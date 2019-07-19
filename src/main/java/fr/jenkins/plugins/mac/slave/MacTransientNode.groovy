package fr.jenkins.plugins.mac.slave

import java.util.concurrent.atomic.AtomicBoolean

import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.cause.MacOfflineCause
import fr.jenkins.plugins.mac.ssh.SSHCommander
import fr.jenkins.plugins.mac.strategy.MacRetentionStrategy
import groovy.util.logging.Slf4j
import hudson.Extension
import hudson.model.Computer
import hudson.model.Slave
import hudson.model.TaskListener
import hudson.model.Node.Mode
import hudson.model.Slave.SlaveDescriptor
import hudson.slaves.Cloud
import hudson.slaves.ComputerLauncher
import hudson.slaves.RetentionStrategy
import jenkins.model.Jenkins

@Slf4j
class MacTransientNode extends Slave {
    
    final String cloudId
    AtomicBoolean acceptingTasks = new AtomicBoolean(true);

    MacTransientNode(String cloudId, String labels, MacUser user, ComputerLauncher launcher) {
        super(user.username, user.workdir, launcher)
        this.cloudId = cloudId
        setUserId(user.username)
        setNumExecutors(1)
        setMode(Mode.EXCLUSIVE)
        setLabelString(labels)
        setNodeProperties(Collections.EMPTY_LIST)
        setNodeDescription("Agent Mac for the user " + user.username)
        setRetentionStrategy(new MacRetentionStrategy(10L))
    }
    
    @Override
    boolean isAcceptingTasks() {
        return acceptingTasks == null || acceptingTasks.get();
    }
    
    @Override
    String getDisplayName() {
        if (cloudId != null) {
            return getNodeName() + " on " + cloudId;
        }
        return getNodeName();
    }

    @Override
    MacComputer createComputer() {
        return new MacComputer(this)
    }

    @Restricted(NoExternalUse)
    void terminate(final TaskListener listener) {
        try {
            final Computer computer = toComputer();
            if (computer != null) {
                computer.disconnect(new MacOfflineCause());
                log.info("Disconnected computer for node '" + name + "'.");
            }
        } catch (Exception ex) {
            log.error("Can't disconnect computer for node '" + name + "' due to exception:", ex);
        }
        Computer.threadPoolForRemoting.submit({ ->
            synchronized(this) {}
        });

        try {
            Jenkins.get().removeNode(this);
            SSHCommander.deleteUserOnMac(this.cloudId, this.name)
            log.info("Removed Node for node '" + name + "'.");
        } catch (IOException ex) {
            log.info("Failed to remove Node for node '" + name + "' due to exception:", ex);
        }
    }
    
    public MacCloud getCloud() {
        if (cloudId == null) return null;
        final Cloud cloud = Jenkins.get().getCloud(cloudId);

        if (cloud == null) {
            throw new RuntimeException("Failed to retrieve Cloud " + cloudId);
        }

        if (!(cloud instanceof MacCloud)) {
            throw new RuntimeException(cloudId + " is not a MacCloud, it's a " + cloud.getClass().toString());
        }

        return (MacCloud) cloud;
    }
    
    @Extension
    static final class MacTransientNodeDescriptor extends SlaveDescriptor {

        @Override
        String getDisplayName() {
            return "Mac Agent";
        }

        @Override
        boolean isInstantiable() {
            return false;
        }
    }
}
