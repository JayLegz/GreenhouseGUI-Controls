import java.io.Serializable;

public class ThermostatDay extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public ThermostatDay(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        greenhouseControls.setVariable("Thermostat", "Day"); //SetVariable method in GreenhouseControls
        gui.writeToOutput(toString()); // Output the description
    }

    @Override
    public String toString() {
        return "Thermostat on day setting";
    }
}
