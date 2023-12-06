import java.io.Serializable;

public class PowerOut extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public PowerOut(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        gui.writeToOutput(toString());
        greenhouseControls.removeEvent(this);
        greenhouseControls.setVariable("PowerOn", false); //SetVar Method in GreenhouseControls
        greenhouseControls.setVariable("ErrorCode", 2); //SetVar Method in GreenhouseControls
        gui.shutdownGreenhouseControls("A Power Outage!");
    }


    @Override
    public String toString() {
        return "Error Code 2: A Power Outage is Detected";
    }
}

