package ami.proj.lightmediator;

public class Store {
    private static final int DEFAULT_TIMECAP_VALUE = 60;

    public static final String[] timecapValues = new String[] {
            "5",
            "10",
            "15",
            "30",
            "45",
            "60",
            "75",
            "90",
            "120",
            "240",
            "300"
    };
    private int timecapValue = Store.DEFAULT_TIMECAP_VALUE;

    private LightInterface lightInterface = null;


    public LightInterface getLightInterface() {return lightInterface;}
    public void setLightInterface(LightInterface lightInterface) {this.lightInterface = lightInterface;}

    public String[] getTimecapValues() {
        return timecapValues;
    }
    public int getTimecapValue() {
        return timecapValue;
    }
    public int getTimecapValue(int id) {
        return Integer.parseInt(Store.timecapValues[id]);
    }
    public void setTimecapValue(int id) {
        this.timecapValue = getTimecapValue(id);
    }

    private static final Store store = new Store();
    public static Store getInstance() {return store;}
}
