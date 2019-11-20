package fr.edf.jenkins.plugins.mac.provisioning

import javax.annotation.CheckForNull
import javax.annotation.Nonnull

import hudson.ExtensionList
import hudson.ExtensionPoint
import hudson.model.Label

abstract class InProvisioning implements ExtensionPoint {

    /**
     * Returns the agents names in provisioning according to all implementations of this extension point for the given label.
     *
     * @param label the {@link Label} being checked.
     * @return the agents names in provisioning according to all implementations of this extension point for the given label.
     */
    @Nonnull
    public static Set<String> getAllInProvisioning(@CheckForNull Label label) {
        return all().collect{ it.getInProvisioning(label) }.collectMany([] as HashSet){ it }
    }

    public static ExtensionList<InProvisioning> all() {
        return ExtensionList.lookup(InProvisioning)
    }

    /**
     * Returns the agents in provisioning for the current label.
     *
     * @param label The label being checked
     * @return The agents names in provisioning for the current label.
     */
    @Nonnull
    public abstract Set<String> getInProvisioning(Label label)
}
