package fr.jenkins.plugins.mac.strategy

import static java.util.concurrent.TimeUnit.MINUTES

import java.util.logging.Level
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

import fr.jenkins.plugins.mac.slave.MacComputer
import fr.jenkins.plugins.mac.slave.MacSlave
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
class MacRetentionStrategy extends RetentionStrategy<MacComputer> {

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
                log.info("Disconnecting {}", c.getName())
                done(c)
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
                MacSlave node = c.getNode()
                if (node != null) {
                    node._terminate(c.getListener())
                }
            });
        });
    }
}
