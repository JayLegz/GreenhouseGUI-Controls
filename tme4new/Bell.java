import java.io.Serializable;

public class Bell extends Event implements Serializable {
    private GreenhouseControls greenhouseControls;
    private int rings;

    public Bell(long delayTime, int rings, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.rings = rings;
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        if (rings > 0) {
            rings--;
            greenhouseControls.addEvent(new Bell(2000, rings, greenhouseControls));
            gui.writeToOutput(toString());
        }
    }

    @Override
    public String toString() {
        return "Bell Bings!";
    }
}
