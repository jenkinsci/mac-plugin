package fr.edf.jenkins.plugins.mac.planned

import fr.edf.jenkins.plugins.mac.MacCloud
import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import hudson.slaves.EnvironmentVariablesNodeProperty
import hudson.slaves.NodeProperty
import hudson.slaves.NodeProvisioner;

/**
 * A builder of {@link hudson.slaves.NodeProvisioner.PlannedNode} implementations for Mac.
 * Can be subclassed to provide alternative implementations of {@link hudson.slaves.NodeProvisioner.PlannedNode}.
 */
public abstract class PlannedNodeBuilder {
    protected MacCloud cloud
    protected MacUser user
    protected MacHost macHost
    protected int numExecutors = 1
    protected List<? extends NodeProperty<?>> nodeProperties

    /**
     * @param cloud the {@link MacCloud} instance to use.
     * @return the current builder.
     */
    PlannedNodeBuilder cloud(MacCloud cloud) {
        this.cloud = cloud
        return this
    }

    /**
     * @param host the {@link MacHost} instance to use.
     * @return the current builder.
     */
    PlannedNodeBuilder host(MacHost host) {
        this.macHost = host
        if(host.envVars) {
            this.nodeProperties = new ArrayList()
            nodeProperties.add(new EnvironmentVariablesNodeProperty(host.envVars.collect {
                new EnvironmentVariablesNodeProperty.Entry(it.key, it.value)
            }))
        }
        else {
            this.nodeProperties = Collections.EMPTY_LIST
        }
        return this
    }

    /**
     * @param numExecutors the number of executors.
     * @return the current builder.
     */
    PlannedNodeBuilder numExecutors(int numExecutors) {
        this.numExecutors = numExecutors
        return this
    }

    /**
     * Builds the {@link hudson.slaves.NodeProvisioner.PlannedNode} instance based
     * on the given inputs.
     * 
     * @return a {@link hudson.slaves.NodeProvisioner.PlannedNode} configured from
     *         this builder.
     */
    abstract NodeProvisioner.PlannedNode build()
}
