package ocaddon.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import ocaddon.item.ItemCooler;
import ocaddon.sim.ThermalSimulation;

public class CoolerEnvironment extends AbstractManagedEnvironment {
    public final ItemCooler cooler;

    public CoolerEnvironment(ItemCooler cooler) {
        this.cooler = cooler;
        setNode(Network.newNode(this, Visibility.Network).withComponent("cooler").create());
    }

    @Callback(doc = "function():number -- Airflow of this fan in CFM.")
    public Object[] getAirflow(Context c, Arguments a) { return new Object[]{cooler.airflow}; }

    @Callback(doc = "function():number -- Current computer temperature in C.")
    public Object[] getTemperature(Context c, Arguments a) {
        return new Object[]{ThermalSimulation.temperatureOf(c.node().address())};
    }
}
