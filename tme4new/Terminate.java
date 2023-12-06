import java.io.Serializable;

public class Terminate extends Event implements Serializable {
    private GreenhouseControls greenhouseControls;

    public Terminate(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        //System.out.println("Debug: Terminate action called"); // Debug statement
        gui.writeToOutput(toString());
        greenhouseControls.terminateOperations();
        //System.exit(0);
    }

    @Override
    public String toString() {
        return "Terminating";
    }
}
