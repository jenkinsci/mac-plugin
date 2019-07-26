package fr.jenkins.plugins.mac.slave;

import java.util.concurrent.atomic.AtomicBoolean

import org.apache.commons.lang.Validate
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.cause.MacOfflineCause
import fr.jenkins.plugins.mac.ssh.SSHCommand
import groovy.util.logging.Slf4j
import hudson.Extension
import hudson.model.Computer
import hudson.model.Descriptor
import hudson.model.Slave
import hudson.model.TaskListener
import hudson.model.Node.Mode
import hudson.model.Slave.SlaveDescriptor
import hudson.slaves.AbstractCloudSlave
import hudson.slaves.Cloud
import hudson.slaves.CloudRetentionStrategy
import hudson.slaves.ComputerLauncher
import hudson.slaves.RetentionStrategy
import jenkins.model.Jenkins

@Slf4j
class MacSlave extends AbstractCloudSlave {

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
        Collections.EMPTY_LIST)
        this.cloudId = cloudId
        setUserId(user.username)
    }

    private static RetentionStrategy buildRetentionStrategy() {
        return new CloudRetentionStrategy(10)
    }

    @Override
    boolean isAcceptingTasks() {
        return acceptingTasks == null || acceptingTasks.get()
    }

    @Override
    String getDisplayName() {
        if (cloudId != null) {
            return getNodeName() + " on " + cloudId;
        }
        return getNodeName()
    }

    @Override
    MacComputer createComputer() {
        return new MacComputer(this)
    }

    @Override
    @Restricted(NoExternalUse)
    void _terminate(final TaskListener listener) {
        try {
            final Computer computer = toComputer()
            if (computer != null) {
                computer.disconnect(new MacOfflineCause())
                log.info("Disconnected computer for node '" + name + "'.")
            }
        } catch (Exception ex) {
            log.error("Can't disconnect computer for node '" + name + "' due to exception:", ex)
        }
        Computer.threadPoolForRemoting.submit({
            ->
            synchronized(this) {}
        });

        try {
            Jenkins.get().removeNode(this)
            SSHCommand.deleteUserOnMac(this.cloudId, this.name)
            log.info("Removed Node for node '" + name + "'.")
        } catch (IOException ex) {
            log.info("Failed to remove Node for node '" + name + "' due to exception:", ex)
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
