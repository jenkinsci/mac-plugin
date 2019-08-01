package fr.jenkins.plugins.mac.slave;

import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
import java.util.logging.Logger

import org.jenkinsci.plugins.durabletask.executors.OnceRetentionStrategy
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.cause.MacOfflineCause
import fr.jenkins.plugins.mac.ssh.SSHCommand
import hudson.Extension
import hudson.model.Computer
import hudson.model.Slave
import hudson.model.TaskListener
import hudson.model.Node.Mode
import hudson.model.Slave.SlaveDescriptor
import hudson.slaves.AbstractCloudSlave
import hudson.slaves.Cloud
import hudson.slaves.ComputerLauncher
import hudson.slaves.RetentionStrategy
import jenkins.model.Jenkins

class MacSlave extends AbstractCloudSlave {

    private static final Logger LOGGER = Logger.getLogger(MacSlave.name)

    final String cloudId
    AtomicBoolean acceptingTasks = new AtomicBoolean(true)

    MacSlave(String cloudId, String labels, MacUser user, ComputerLauncher launcher) {
        super(
        user.username,
        "Agent Mac for the user " + user.username,
        user.workdir,
        1,
        Mode.EXCLUSIVE,
        labels,
        launcher,
        buildRetentionStrategy(),
        Collections.EMPTY_LIST
        )
        this.cloudId = cloudId
        setUserId(user.username)
    }

    private static RetentionStrategy buildRetentionStrategy() {
        return new OnceRetentionStrategy(1)
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
                SSHCommand.deleteUserOnMac(this.cloudId, this.name)
                computer.disconnect(new MacOfflineCause())
                LOGGER.log(Level.FINE, "Disconnected computer for node '{0}'.", name)
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Can't disconnect computer for node '{0}' due to exception: {1}", [name, ex.getMessage()])
        }
        try {
            Jenkins.get().removeNode(this)
            LOGGER.log(Level.FINE, "Removed Node for node '{0}'.", name)
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to remove Node for node '" + name + "' due to exception:", ex)
        }
    }

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

        @Override
        String getDisplayName() {
            return "Mac Agent"
        }

        @Override
        boolean isInstantiable() {
            return false
        }
    }
}
