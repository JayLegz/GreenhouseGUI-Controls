import java.io.Serializable;

public class LightOff extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public LightOff(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        greenhouseControls.setVariable("Light", false); //SetVariable method in tme4.GreenhouseControls
        gui.writeToOutput(toString()); // Output the description
    }

    @Override
    public String toString() {
        return "Light is off";
    }
}
