import java.io.Serializable;

public class WaterOff extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public WaterOff(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        greenhouseControls.setVariable("Water", false); //SetVariable method in GreenhouseControls
        gui.writeToOutput(toString()); // Output the description
    }

    @Override
    public String toString() {
        return "Water is off";
    }
}
