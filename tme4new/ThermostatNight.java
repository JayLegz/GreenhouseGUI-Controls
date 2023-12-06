import java.io.Serializable;

public class ThermostatNight extends Event implements Serializable, Runnable {
    private GreenhouseControls greenhouseControls;

    public ThermostatNight(long delayTime, GreenhouseControls greenhouseControls) {
        super(delayTime);
        this.greenhouseControls = greenhouseControls;
    }

    @Override
    public void action() {
        greenhouseControls.setVariable("Thermostat", "Night"); //SetVariable method in GreenhouseControls
        gui.writeToOutput(toString()); // Output the description
    }

    @Override
    public String toString() {
        return "Thermostat on night setting";
    }
}
