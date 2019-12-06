package fr.edf.jenkins.plugins.mac.slave

import java.util.logging.Level
import java.util.logging.Logger

import javax.annotation.CheckForNull

import com.google.common.base.Objects

import fr.edf.jenkins.plugins.mac.MacCloud
import fr.edf.jenkins.plugins.mac.MacHost
import hudson.model.Executor
import hudson.model.Queue
import hudson.slaves.AbstractCloudComputer

class MacComputer extends AbstractCloudComputer<MacSlave> {

    private static final Logger LOGGER = Logger.getLogger(MacComputer.name)

    MacComputer(MacSlave node) {
        super(node)
    }

    /**
     * {@inheritDoc}
     */
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

    @CheckForNull
    MacHost getMacHost() {
        final MacSlave node = getNode()
        return node == null ? null : node.getMacHost()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void taskAccepted(Executor executor, Queue.Task task) {
        super.taskAccepted(executor, task)
        Queue.Executable exec = executor.getCurrentExecutable()
        LOGGER.log(Level.FINE, "Computer {0} accepted task {1}", this, exec)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void taskCompleted(Executor executor, Queue.Task task, long durationMS) {
        Queue.Executable exec = executor.getCurrentExecutable()
        LOGGER.log(Level.FINE, "Computer {0} completed task {1}", this, exec)

        // May take the agent offline and remove it, in which case getNode()
        // above would return null and we'd not find our MacSlave anymore.
        super.taskCompleted(executor, task, durationMS)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void taskCompletedWithProblems(Executor executor, Queue.Task task, long durationMS, Throwable problems) {
        Queue.Executable exec = executor.getCurrentExecutable()
        LOGGER.log(Level.FINE, "Computer {0} completed task {1} with problems", this, exec)
        super.taskCompletedWithProblems(executor, task, durationMS, problems)
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", super.getName())
                .add("slave", getNode())
                .toString()
    }
}
