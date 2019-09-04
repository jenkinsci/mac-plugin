package fr.jenkins.plugins.mac.slave

import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

import hudson.model.Computer
import hudson.model.TaskListener
import hudson.slaves.ComputerListener
import hudson.slaves.OfflineCause

public class MacComputerListener extends ComputerListener {

    private static final Logger LOGGER = Logger.getLogger(MacComputerListener.name)

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLaunchFailure(Computer c, TaskListener taskListener) throws IOException, InterruptedException {
        if(c instanceof MacComputer) {
            MacComputer macComputer = (MacComputer) c
            LOGGER.log(Level.WARNING, "Mac Agent {0} failed to launch and will be removed", macComputer.getName())
            macComputer.getNode()._terminate(taskListener)
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOnline(Computer c, TaskListener listener) {
        if(c instanceof MacComputer) {
            MacComputer macComputer = (MacComputer) c
            LOGGER.log(Level.FINE, "Mac Agent {} is now online", macComputer.name)
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOffline(Computer c, OfflineCause cause) {
        if(c instanceof MacComputer) {
            MacComputer macComputer = (MacComputer) c
            LOGGER.log(Level.FINE, "Mac Agent {} is now offline", macComputer.name)
        }
    }
}
