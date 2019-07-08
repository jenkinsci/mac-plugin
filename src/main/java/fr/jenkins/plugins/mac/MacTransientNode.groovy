package fr.jenkins.plugins.mac

import java.util.concurrent.atomic.AtomicBoolean

import groovy.util.logging.Slf4j
import hudson.Extension
import hudson.model.Slave
import hudson.model.Node.Mode
import hudson.model.Slave.SlaveDescriptor
import hudson.slaves.ComputerLauncher

@Slf4j
class MacTransientNode extends Slave {
    
    final String cloudId
    AtomicBoolean acceptingTasks = new AtomicBoolean(true);

    MacTransientNode(String cloudId, String labels, MacUser user, ComputerLauncher launcher) {
        super(user.username, user.workdir, launcher)
        this.cloudId = cloudId
        setNumExecutors(1)
        setMode(Mode.EXCLUSIVE)
        setLabelString(labels)
//        setRetentionStrategy(new DockerOnceRetentionStrategy(10));
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
