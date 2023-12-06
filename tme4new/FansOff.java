import java.io.Serializable;

public class FansOff extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public FansOff(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        greenhouseControls.setVariable("Fans", false); //SetVariable method in tme4.GreenhouseControls
        gui.writeToOutput(toString()); // Output the description
    }

    @Override
    public String toString() {
        return "Fans are off";
    }
}
