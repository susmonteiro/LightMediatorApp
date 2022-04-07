package ami.proj.lightmediator;

public class UsersListView {

    private final int circleId;

    private final int color;

    private final String text1;

    private final String text2;

    private final String time;

    public UsersListView(int CircleId, int Color, String Text1, String Text2, String Time) {
        circleId = CircleId;
        text1 = Text1;
        text2 = Text2;
        color = Color;
        time = Time;
    }

    public int getCircleId() {
        return circleId;
    }

    public int getColor() {
        return color;
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }

    public String getTime() { return time; }

}