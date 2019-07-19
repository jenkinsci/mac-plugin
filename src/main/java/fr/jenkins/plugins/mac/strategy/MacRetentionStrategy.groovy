package fr.jenkins.plugins.mac.strategy

import static java.util.concurrent.TimeUnit.MINUTES

import java.util.logging.Level
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

import fr.jenkins.plugins.mac.slave.MacComputer
import fr.jenkins.plugins.mac.slave.MacTransientNode
import groovy.util.logging.Slf4j
import hudson.Extension
import hudson.model.Computer
import hudson.model.Descriptor
import hudson.model.Executor
import hudson.model.ExecutorListener
import hudson.model.Queue
import hudson.model.Queue.Task
import hudson.slaves.RetentionStrategy
import hudson.util.FormValidation

@Slf4j
class MacRetentionStrategy extends RetentionStrategy<MacComputer> implements ExecutorListener {

    static Long DEFAULT_IDLEMINUTES = 10
    private Long idleDelay = DEFAULT_IDLEMINUTES
    
    MacRetentionStrategy(Long idleDelay) {
        this.idleDelay = idleDelay
    }
    
    @Override
    long check(MacComputer c) {
        if(c.isIdle()) {
            final long idleMilliseconds = System.currentTimeMillis() - c.getIdleStartMilliseconds();
            if (idleMilliseconds > MINUTES.toMillis(getIdleDelay())) {
                log.info("Disconnecting {}", c.getName());
                done(c);
            }
        }
        return 1;
    }
    
    long getIdleDelay() {
        return this.idleDelay.longValue()
    }
    
    private void done(final MacComputer c) {
        c.setAcceptingTasks(false); // just in case
        Computer.threadPoolForRemoting.submit({ ->
            Queue.withLock({ ->
                MacTransientNode node = c.getNode();
                if (node != null) {
                    node.terminate(c.getListener());
                }
            });
        });
    }

    @Override
    public void taskAccepted(Executor executor, Task task) {
    }

    @Override
    public void taskCompleted(Executor executor, Task task, long duration) {
        final MacComputer c = (MacComputer) executor.getOwner();
        Queue.Executable exec = executor.getCurrentExecutable();
        log.info("terminating {} since {} seems to be finished", c.getName(), exec);
        done(c);
    }

    @Override
    public void taskCompletedWithProblems(Executor executor, Task task, long duration, Throwable error) {
        final MacComputer c = (MacComputer) executor.getOwner();
        log.error(String.format("%s returned an error : %s", c.getName(), error.getMessage()), error)
        taskCompleted(executor, task, duration);
    }
}
