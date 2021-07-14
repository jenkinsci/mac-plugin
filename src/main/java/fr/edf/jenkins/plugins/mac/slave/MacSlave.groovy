package fr.edf.jenkins.plugins.mac.slave;

import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
import java.util.logging.Logger

import org.jenkinsci.plugins.durabletask.executors.OnceRetentionStrategy
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import fr.edf.jenkins.plugins.mac.MacCloud
import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.cause.MacOfflineCause
import fr.edf.jenkins.plugins.mac.ssh.SSHCommand
import hudson.Extension
import hudson.model.Computer
import hudson.model.TaskListener
import hudson.model.Node.Mode
import hudson.model.Slave.SlaveDescriptor
import hudson.slaves.AbstractCloudSlave
import hudson.slaves.Cloud
import hudson.slaves.ComputerLauncher
import hudson.slaves.NodeProperty
import hudson.slaves.RetentionStrategy
import jenkins.model.Jenkins

class MacSlave extends AbstractCloudSlave {

    private static final Logger LOGGER = Logger.getLogger(MacSlave.name)

    final String cloudId
    final MacHost macHost
    AtomicBoolean acceptingTasks = new AtomicBoolean(true)
    Boolean debugMode

    MacSlave(String cloudId, String labels, MacUser user, MacHost macHost, ComputerLauncher launcher, Integer idleMinutes, List<? extends NodeProperty<?>> nodeProperties, Boolean debugMode = false) {
        super(
        user.username,
        "Agent Mac for the user " + user.username,
        user.workdir,
        1,
        Mode.EXCLUSIVE,
        labels,
        launcher,
        buildRetentionStrategy(idleMinutes),
        nodeProperties
        )
        this.cloudId = cloudId
        this.macHost = macHost
        this.debugMode = debugMode
        setUserId(user.username)
    }

    /**
     * Return the retention strategy used for this MacSlave
     * @param idleMinutes
     * @return OnceRetentionStrategy
     */
    private static RetentionStrategy buildRetentionStrategy(Integer idleMinutes) {
        return new OnceRetentionStrategy(idleMinutes.intValue())
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isAcceptingTasks() {
        return acceptingTasks == null || acceptingTasks.get()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getDisplayName() {
        if (cloudId != null) {
            return getNodeName() + " on " + cloudId;
        }
        return getNodeName()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    MacComputer createComputer() {
        return MacComputerFactory.createInstance(this)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Restricted(NoExternalUse)
    void _terminate(final TaskListener listener) {
        try {
            final Computer computer = toComputer()
            if (computer != null) {
                computer.disconnect(new MacOfflineCause())
                LOGGER.log(Level.FINE, "Disconnected computer for node {0}.", name)
            }
        } catch (Exception e) {
            String message = String.format("Can't disconnect computer for node %s", name)
            LOGGER.log(Level.SEVERE, message, e)
            listener.error(message)
        }

        if (this.debugMode != true) {
            try {
                SSHCommand.deleteUserOnMac(this.name, this.macHost)
            } catch (Exception e) {
                String message = String.format("Failed to remove user %s on mac %s due to exception : %s", this.name, this.macHost.host, e.message)
                LOGGER.log(Level.SEVERE, message, e)
                listener.fatalError(message)
            }
        }
    }

    /**
     * Retrieve the cloud attached to this MacSlave
     * @return MacCloud
     */
    public MacCloud getCloud() {
        if (cloudId == null) return null;
        final Cloud cloud = Jenkins.get().getCloud(cloudId)

        if (cloud == null) {
            throw new RuntimeException("Failed to retrieve Cloud " + cloudId)
        }

        if (!(cloud instanceof MacCloud)) {
            throw new RuntimeException(cloudId + " is not a MacCloud, it's a " + cloud.getClass().toString())
        }

        return (MacCloud) cloud
    }

    @Extension
    static final class MacSlaveDescriptor extends SlaveDescriptor {

        /**
         * {@inheritDoc}
         */
        @Override
        String getDisplayName() {
            return "Mac Agent"
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean isInstantiable() {
            return false
        }
    }
}
