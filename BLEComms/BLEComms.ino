#include <string.h>
#include <SoftwareSerial.h>
SoftwareSerial BTSerial (2,3);

int DEFAULT_COLOR[3] = {255, 255, 255};

int red_light_pin= 11;
int green_light_pin = 10;
int blue_light_pin = 9;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);
  pinMode(red_light_pin, OUTPUT);
  pinMode(green_light_pin, OUTPUT);
  pinMode(blue_light_pin, OUTPUT);
  setLight(DEFAULT_COLOR);

  Serial.println("Init done");
}

void loop() {
  if(BTSerial.available()){
    readAndSetLight();
  }
}

void readAndSetLight() {
  int rgb[3] = {0, 0, 0};
  int error = readValue(rgb);

  if (error) {
    Serial.println("Error");
    setLight(DEFAULT_COLOR);
  } else {
    setLight(rgb);
  }
  
}

void setLight(int rgb[]) {
    Serial.print("R: ");
    Serial.println(rgb[0]);
    Serial.print("G: ");
    Serial.println(rgb[1]);
    Serial.print("B: ");
    Serial.println(rgb[2]);

  analogWrite(red_light_pin, rgb[0]);
  analogWrite(green_light_pin, rgb[1]);
  analogWrite(blue_light_pin, rgb[2]);
}

int readValue(int rgb[]) {
  char buffer[16] = {0};
  int index = 0;
  int returnCode = BTSerial.readBytesUntil('}', buffer, 16);
  if(returnCode == 0) {
    return 1;
  }

  if (buffer[index] != '{') {
    return 1;
  }
  index++;
  
  char c = buffer[index];
  int i = 0;
  int total = 0;
  while(((c >= '0' && c <= '9') || c == ',') && i<3 && total<11) {
  
    if(c == ',') {
      i++; 
    } else if(rgb[i] > 255) {
      return 1; 
    } else {
      rgb[i] = rgb[i]*10 + c-'0';
    }
    
    total++;
    index++;
    c = buffer[index];
  }

  if (i != 2) {
    return 1;
  }
  
  return 0;
}


void flushSerial() {
  while(Serial.available() > 0) {
    char t = Serial.read();
  }
}
