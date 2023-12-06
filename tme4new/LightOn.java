import java.io.Serializable;

public class LightOn extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public LightOn(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        greenhouseControls.setVariable("Light", true); //SetVariable method in GreenhouseControls
        gui.writeToOutput(toString()); // Output the description
    }

    @Override
    public String toString() {
        return "Light is on";
    }
}
