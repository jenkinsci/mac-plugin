package fr.jenkins.plugins.mac

import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import java.util.logging.Logger

import org.antlr.v4.runtime.misc.Nullable
import org.apache.commons.lang.StringUtils
import org.kohsuke.stapler.DataBoundConstructor

import fr.jenkins.plugins.mac.connector.MacComputerConnector
import fr.jenkins.plugins.mac.planned.PlannedNodeBuilderFactory
import fr.jenkins.plugins.mac.provisioning.InProvisioning
import fr.jenkins.plugins.mac.slave.MacSlave
import fr.jenkins.plugins.mac.ssh.SSHCommand
import fr.jenkins.plugins.mac.ssh.SSHCommandException
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.Label
import hudson.model.Node
import hudson.model.labels.LabelAtom
import hudson.slaves.Cloud
import hudson.slaves.NodeProvisioner
import hudson.slaves.NodeProvisioner.PlannedNode
import jenkins.model.Jenkins

class MacCloud extends Cloud {

    private static final Logger LOGGER = Logger.getLogger(MacCloud.name)

    MacHost macHost
    MacComputerConnector connector
    String labelString
    transient Set<LabelAtom> labelSet
    Boolean disabled

    @DataBoundConstructor
    MacCloud(String name, MacHost macHost, MacComputerConnector connector, String labelString, Boolean disabled) {
        super(name)
        this.macHost = macHost
        this.connector = connector
        this.disabled = disabled
        this.labelString = labelString
        labelSet = Label.parse(StringUtils.defaultIfEmpty(labelString, ""))
    }

    static @Nullable getMacClouds() {
        return all().get(MacCloud)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized Collection<PlannedNode> provision(Label label, int excessWorkload) {
        final List<PlannedNode> r = new ArrayList<>();
        MacCloud cloud = this
        Set<String> allInProvisioning = InProvisioning.getAllInProvisioning(label)
        LOGGER.log(Level.FINE, "In provisioning : {}", allInProvisioning.size())
        int toBeProvisioned = Math.max(0, excessWorkload - allInProvisioning.size())
        LOGGER.log(Level.INFO, "Excess workload after pending Mac agents: {0}", toBeProvisioned)
        if(toBeProvisioned > 0) {
            try {
                r.add(PlannedNodeBuilderFactory.createInstance().cloud(this).host(macHost).label(label).build())
                return r
            }catch (Exception e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e)
                return Collections.emptyList()
            }
        }
        return Collections.emptyList()
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    boolean canProvision(Label label) {
        return !disabled
    }
    
    @Extension
    static class DescriptorImpl extends Descriptor<Cloud> {

        @Override
        String getDisplayName() {
            return Messages.Cloud_DisplayName()
        }
    }
}
