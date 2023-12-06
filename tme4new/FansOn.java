import java.io.Serializable;

public class FansOn extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public FansOn(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        greenhouseControls.setVariable("Fans", true); //SetVariable method in tme4.GreenhouseControls
        gui.writeToOutput(toString()); // Output the description
    }

    @Override
    public String toString() {
        return "Fans are on";
    }
}
