package ami.proj.lightmediator;

public class UsersListView {

    private final int circleId;

    private final int color;

    private final String name;

    private String time;

    public UsersListView(int CircleId, int Color, String Name, String Time) {
        circleId = CircleId;
        name = Name;
        color = Color;
        time = Time;
    }

    public int getCircleId() {
        return circleId;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getTime() { return time; }

    public void setTime(String Time) { time = Time; }

}