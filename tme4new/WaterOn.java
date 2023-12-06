import java.io.Serializable;

public class WaterOn extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public WaterOn(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        greenhouseControls.setVariable("Water", true); //SetVariable method in GreenhouseControls
        gui.writeToOutput(toString()); // Output the description
    }

    @Override
    public String toString() {
        return "Water is on";
    }
}
