package fr.edf.jenkins.plugins.mac.slave

import hudson.ExtensionList
import hudson.ExtensionPoint

/**
 * A factory of {@link MacComputer} instances.
 */
abstract class MacComputerFactory implements ExtensionPoint {
    /**
     * Returns all registered implementations of {@link MacComputerFactory}.
     * @return all registered implementations of {@link MacComputerFactory}.
     */
    public static ExtensionList<MacComputerFactory> all() {
        return ExtensionList.lookup(MacComputerFactory.class)
    }

    /**
     * Returns a new instance of {@link MacComputer}.
     * @return a new instance of {@link MacComputer}.
     */
    public static MacComputer createInstance(MacSlave slave) {
        for (MacComputerFactory factory: all()) {
            MacComputer macComputer = factory.newInstance(slave)
            if (macComputer != null) {
                return macComputer
            }
        }
        return new MacComputer(slave);
    }

    /**
     * Creates a new instance of {@link MacComputer}.
     * @return a new instance of {@link MacComputer}.
     */
    public abstract MacComputer newInstance(MacSlave slave)
}
