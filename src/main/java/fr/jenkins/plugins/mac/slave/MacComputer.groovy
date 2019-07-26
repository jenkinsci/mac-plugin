package fr.jenkins.plugins.mac.slave

import java.util.logging.Level

import javax.annotation.CheckForNull

import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.google.common.base.Objects

import fr.jenkins.plugins.mac.MacCloud
import groovy.util.logging.Slf4j
import hudson.EnvVars
import hudson.model.Executor
import hudson.model.Queue
import hudson.slaves.AbstractCloudComputer

@Slf4j
class MacComputer extends AbstractCloudComputer<MacSlave> {

    MacComputer(MacSlave node) {
        super(node)
    }

    @CheckForNull
    @Override
    MacSlave getNode() {
        return (MacSlave) super.getNode()
    }

    @CheckForNull
    public MacCloud getCloud() {
        final MacSlave node = getNode();
        return node == null ? null : node.getCloud()
    }

    @CheckForNull
    String getUserId() {
        final MacSlave node = getNode()
        return node == null ? null : node.getUserId()
    }

    @CheckForNull
    String getCloudId() {
        final MacSlave node = getNode()
        return node == null ? null : node.getCloudId()
    }

    @Override
    public void taskAccepted(Executor executor, Queue.Task task) {
        super.taskAccepted(executor, task);
        log.info("Computer " + this + " taskAccepted")
    }

    @Override
    public void taskCompleted(Executor executor, Queue.Task task, long durationMS) {
        log.info("Computer " + this + " taskCompleted")
        super.taskCompleted(executor, task, durationMS)
    }

    @Override
    public void taskCompletedWithProblems(Executor executor, Queue.Task task, long durationMS, Throwable problems) {
        super.taskCompletedWithProblems(executor, task, durationMS, problems)
        log.info("Computer " + this + " taskCompletedWithProblems")
    }

    @Override
    @Restricted(NoExternalUse.class)
    public EnvVars getEnvironment() throws IOException, InterruptedException {
        EnvVars variables = super.getEnvironment()
        variables.put("MAC_USER_ID", getUserId())
        final MacCloud cloud = getCloud()
        if (cloud != null) {
            variables.put("JENKINS_CLOUD_ID", cloud.name);
            String macHost = cloud.macHost.host
            variables.put("MAC_HOST", macHost)
        }
        return variables;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", super.getName())
                .add("slave", getNode())
                .toString()
    }
}
