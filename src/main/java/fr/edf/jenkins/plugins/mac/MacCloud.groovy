package fr.edf.jenkins.plugins.mac

import java.util.logging.Level
import java.util.logging.Logger

import org.antlr.v4.runtime.misc.Nullable
import org.apache.commons.collections.CollectionUtils
import org.kohsuke.stapler.DataBoundConstructor

import fr.edf.jenkins.plugins.mac.connector.MacComputerConnector
import fr.edf.jenkins.plugins.mac.planned.PlannedNodeBuilderFactory
import fr.edf.jenkins.plugins.mac.provisioning.InProvisioning
import fr.edf.jenkins.plugins.mac.ssh.SSHCommand
import fr.edf.jenkins.plugins.mac.ssh.SSHCommandException
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.Label
import hudson.slaves.Cloud
import hudson.slaves.NodeProvisioner.PlannedNode

class MacCloud extends Cloud {

    private static final Logger LOGGER = Logger.getLogger(MacCloud.name)

    List<MacHost> macHosts = new ArrayList()
    MacComputerConnector connector
    Integer idleMinutes

    @DataBoundConstructor
    MacCloud(String name, List<MacHost> macHosts, MacComputerConnector connector, Integer idleMinutes) {
        super(name)
        this.macHosts = macHosts
        this.connector = connector
        this.idleMinutes = idleMinutes
    }

    static @Nullable getMacClouds() {
        return all().get(MacCloud)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized Collection<PlannedNode> provision(Label label, int excessWorkload) {
        try {
            List<MacHost> labelMacHosts = getMacHosts(label)
            if(CollectionUtils.isEmpty(labelMacHosts)) {
                LOGGER.log(Level.WARNING, "No host is configured for the label {0}", label.toString())
                return Collections.emptyList()
            }
            final List<PlannedNode> r = new ArrayList<>()
            Set<String> allInProvisioning = InProvisioning.getAllInProvisioning(label)
            LOGGER.log(Level.FINE, "In provisioning : {0}", allInProvisioning.size())
            int toBeProvisioned = Math.max(0, excessWorkload - allInProvisioning.size())
            LOGGER.log(Level.INFO, "Excess workload after pending Mac agents: {0}", toBeProvisioned)
            if(toBeProvisioned > 0) {
                MacHost macHost = chooseMacHost(labelMacHosts)
                r.add(PlannedNodeBuilderFactory.createInstance().cloud(this).host(macHost).build())
            }
            return r
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage())
            LOGGER.log(Level.FINEST, "Exception : ", e)
            return Collections.emptyList()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean canProvision(Label label) {
        boolean canProvision = macHosts.find {!it.disabled} != null
        if(!canProvision) {
            LOGGER.log(Level.WARNING, "The Mac Cloud {0} is disabled", this.name)
        }
        return canProvision
    }

    /**
     * Return all MacHost available for the given label.<br>
     * Multiples MacHost can have the same label.
     *
     * @param label
     * @return All MacHosts of this cloud not disabled and matched to the given label
     */
    public List<MacHost> getMacHosts(Label label) {
        try {
            return macHosts.findAll {
                !it.disabled && label.matches((Collection) it.getLabelSet())
            }
        } catch(Exception e) {
            String message = String.format("An error occured when trying to find hosts with label %s", label.toString())
            LOGGER.log(Level.WARNING, message)
            LOGGER.log(Level.FINEST, "Exception : ", e)
            return Collections.emptyList()
        }
    }

    /**
     * Return a Mac Host available <br/>
     * It must not be disabled and must have some users left to create <br/>
     * If this method cannot connect to the mac via SSH, it mark it as disabled after the max retry number
     * @param labelMacHosts
     * @return MacHost
     * @throws Exception
     */
    private MacHost chooseMacHost(List<MacHost> labelMacHosts) throws Exception {
        MacHost hostChoosen = labelMacHosts.find {
            if(it.disabled) {
                return false
            }
            int nbTries = 0
            while(true) {
                try {
                    int existingUsers = SSHCommand.listUsers(it).size()
                    return existingUsers < it.maxUsers
                } catch(SSHCommandException sshe) {
                    nbTries ++
                    if(nbTries < it.maxTries) {
                        continue
                    } else {
                        LOGGER.log(Level.INFO, "Disabling Mac Host {0}", it.host)
                        it.disabled = true
                        return false
                    }
                }
            }
        }
        if(null == hostChoosen) throw new Exception("Unable to find a mac host available")
        return hostChoosen
    }

    @Extension
    static class DescriptorImpl extends Descriptor<Cloud> {

        /**
         * {@inheritDoc}
         */
        @Override
        String getDisplayName() {
            return Messages.Cloud_DisplayName()
        }
    }
}
