package ami.proj.lightmediator;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.services.transcribestreaming.model.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TranscribeStreaming implements Serializable {
    private static final Region REGION = Region.EU_WEST_2;
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNELS, AUDIO_FORMAT);
    private static String transcription = "";

    private static ArrayList<User> users;
    private static TranscribeStreamingAsyncClient client;
    private static AudioStreamPublisher publisher;
    private static String lastTranscription = "";
    private static String lastSpeakerLabel = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void streaming() throws ExecutionException, InterruptedException {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "***REMOVED***",
                "***REMOVED***");

        client = TranscribeStreamingAsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(REGION)
                .build();

        publisher = new AudioStreamPublisher(getStreamFromMic());

        CompletableFuture<Void> result = client.startStreamTranscription(getRequest(),
                publisher,
                getResponseHandler());

        result.get();
        client.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<User> getUsers() {
        return users;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getTimes() {
        return users.stream().map(User::getTimeText).collect(Collectors.toList());
    }

    public String getLastTranscription() {
        String transcription = lastTranscription;
        lastTranscription = "";
        return transcription;
    }

    public String getLastSpeakerLabel() { return lastSpeakerLabel; }

    public void close() {
        publisher.subscribe(null);
        client.close();
        System.out.println("The client has stopped");
    }

    public void setUsers(ArrayList<User> listOfUsers) {
        users = listOfUsers;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static User findUserById(int id) {
        if (users == null) return null;
        return users.stream().filter(user -> user.getId() == id).findAny().orElse(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void increaseSpokenTime(int id, double time) {
        User user = findUserById(id);
        if (user != null) System.out.println("Time: " + time + "Total time: " + user.getTimeText());
        if (user != null) user.addSpokenTime(time);
    }

    public String getTranscription() {
        return transcription;
    }

    private static InputStream getStreamFromMic() {

        // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
        return new AudioInputStream(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNELS, AUDIO_FORMAT, bufferSize);
    }

    private static StartStreamTranscriptionRequest getRequest() {
        return StartStreamTranscriptionRequest.builder()
                .languageCode(LanguageCode.EN_US.toString())
                .mediaEncoding(MediaEncoding.PCM)
                .mediaSampleRateHertz(TranscribeStreaming.SAMPLE_RATE)
                .showSpeakerLabel(true)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static StartStreamTranscriptionResponseHandler getResponseHandler() {
        return StartStreamTranscriptionResponseHandler.builder()
                .onResponse(r -> System.out.println("Received Initial response"))
                .onError(e -> {
                    System.out.println(e.getMessage());
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    System.out.println("Error Occurred: " + sw);
                })
                .onComplete(() -> System.out.println("=== All records stream successfully ==="))
                .subscriber(event -> {
                    List<Result> results = ((TranscriptEvent) event).transcript().results();
                    if (results.size() > 0) {
                        if (!results.get(0).alternatives().get(0).transcript().isEmpty()
                                && !results.get(0).isPartial()) {
                            lastSpeakerLabel = results.get(0).alternatives().get(0).items().get(0).speaker();
                            System.out.println(lastSpeakerLabel);
                            transcription += "Speaker " + lastSpeakerLabel + ": ";
                            lastTranscription = results.get(0).alternatives().get(0).transcript();
                            System.out.println(lastTranscription);
                            transcription += lastTranscription + "\n\n";

                            Double totalTime = results.get(0).alternatives().get(0).items()
                                    .stream()
                                    .map(item -> item.endTime() - item.startTime())
                                    .reduce((double) 0, Double::sum);
                            increaseSpokenTime(Integer.parseInt(lastSpeakerLabel), totalTime);
                        }
                    }
                })
                .build();
    }

    private static class AudioStreamPublisher implements Publisher<AudioStream> {
        private final InputStream inputStream;
        private Subscription currentSubscription;

        private AudioStreamPublisher(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void subscribe(Subscriber<? super AudioStream> s) {

            if (this.currentSubscription != null) {
                this.currentSubscription.cancel();
            }
            this.currentSubscription = new SubscriptionImpl(s, inputStream);
            s.onSubscribe(currentSubscription);
        }
    }

    public static class SubscriptionImpl implements Subscription {
        private static final int CHUNK_SIZE_IN_BYTES = 1024;
        private final Subscriber<? super AudioStream> subscriber;
        private final InputStream inputStream;
        private final ExecutorService executor = Executors.newFixedThreadPool(1);
        private final AtomicLong demand = new AtomicLong(0);

        SubscriptionImpl(Subscriber<? super AudioStream> s, InputStream inputStream) {
            this.subscriber = s;
            this.inputStream = inputStream;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void request(long n) {
            if (n <= 0) {
                subscriber.onError(new IllegalArgumentException("Demand must be positive"));
            }

            demand.getAndAdd(n);

            executor.submit(() -> {
                try {
                    do {
                        ByteBuffer audioBuffer = getNextEvent();
                        if (audioBuffer.remaining() > 0) {
                            AudioEvent audioEvent = audioEventFromBuffer(audioBuffer);
                            subscriber.onNext(audioEvent);
                        } else {
                            subscriber.onComplete();
                            break;
                        }
                    } while (demand.decrementAndGet() > 0);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            });
        }

        @Override
        public void cancel() {
            executor.shutdown();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private ByteBuffer getNextEvent() {
            ByteBuffer audioBuffer;
            byte[] audioBytes = new byte[CHUNK_SIZE_IN_BYTES];

            int len;
            try {
                len = inputStream.read(audioBytes);

                if (len <= 0) {
                    audioBuffer = ByteBuffer.allocate(0);
                } else {
                    audioBuffer = ByteBuffer.wrap(audioBytes, 0, len);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            return audioBuffer;
        }

        private AudioEvent audioEventFromBuffer(ByteBuffer bb) {
            return AudioEvent.builder()
                    .audioChunk(SdkBytes.fromByteBuffer(bb))
                    .build();
        }
    }
}
