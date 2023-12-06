import java.io.Serializable;

public class WindowMalfunction extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public WindowMalfunction(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() throws ControllerException {
        gui.writeToOutput(toString());
        greenhouseControls.removeEvent(this);
        greenhouseControls.setVariable("WindowOk", false); //SetVar Method in GreenhouseControls.
        greenhouseControls.setVariable("ErrorCode", 1); //SetVar Method in GreenhouseControls.
        gui.shutdownGreenhouseControls("A Window Malfunction Detected!");
    }

    @Override
    public String toString() {
        return "A Window Malfunction is Detected";
    }
}
