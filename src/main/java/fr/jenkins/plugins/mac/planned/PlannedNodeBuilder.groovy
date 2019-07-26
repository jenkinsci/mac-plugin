package fr.jenkins.plugins.mac.planned;

import fr.jenkins.plugins.mac.MacCloud;
import fr.jenkins.plugins.mac.MacHost;
import fr.jenkins.plugins.mac.MacUser;
import hudson.model.Label;
import hudson.slaves.NodeProvisioner;

/**
 * A builder of {@link hudson.slaves.NodeProvisioner.PlannedNode} implementations for Kubernetes.
 * Can be subclassed to provide alternative implementations of {@link hudson.slaves.NodeProvisioner.PlannedNode}.
 */
/**
 * @author MD5C715N
 *
 */
public abstract class PlannedNodeBuilder {
    protected MacCloud cloud;
    protected MacUser user;
    protected MacHost macHost;
    protected Label label;
    protected int numExecutors = 1;

    /**
     * @param cloud the {@link KubernetesCloud} instance to use.
     * @return the current builder.
     */
    public PlannedNodeBuilder cloud(MacCloud cloud) {
        this.cloud = cloud;
        return this;
    }

    /**
     * @param host the {@link PodTemplate} instance to use.
     * @return the current builder.
     */
    public PlannedNodeBuilder host(MacHost host) {
        this.macHost = host;
        return this;
    }

    /**
     * @param label the {@link Label} to use.
     * @return the current builder.
     */
    public PlannedNodeBuilder label(Label label) {
        this.label = label;
        return this;
    }

    /**
     * @param numExecutors the number of executors.
     * @return the current builder.
     */
    public PlannedNodeBuilder numExecutors(int numExecutors) {
        this.numExecutors = numExecutors;
        return this;
    }

    /**
     * Builds the {@link hudson.slaves.NodeProvisioner.PlannedNode} instance based
     * on the given inputs.
     * 
     * @return a {@link hudson.slaves.NodeProvisioner.PlannedNode} configured from
     *         this builder.
     */
    public abstract NodeProvisioner.PlannedNode build();
}
