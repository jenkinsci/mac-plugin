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
    public boolean isAcceptingTasks() {
        return acceptingTasks == null || acceptingTasks.get();
    }
    
    @Override
    public String getDisplayName() {
        if (cloudId != null) {
            return getNodeName() + " on " + cloudId;
        }
        return getNodeName();
    }

    @Override
    public MacComputer createComputer() {
        return new MacComputer(this)
    }
    
    @Extension
    public static final class MacTransientNodeDescriptor extends SlaveDescriptor {

        @Override
        public String getDisplayName() {
            return "Mac Agent";
        }

        @Override
        public boolean isInstantiable() {
            return false;
        }
    }
}
