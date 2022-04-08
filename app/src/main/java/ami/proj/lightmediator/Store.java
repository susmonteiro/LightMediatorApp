package ami.proj.lightmediator;

public class Store {
    private LightInterface lightInterface = null;
    public LightInterface getLightInterface() {return lightInterface;}
    public void setLightInterface(LightInterface lightInterface) {this.lightInterface = lightInterface;}

    private static final Store store = new Store();
    public static Store getInstance() {return store;}
}
