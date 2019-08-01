package fr.jenkins.plugins.mac.connector


import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.slave.MacSlave
import hudson.model.AbstractDescribableImpl
import hudson.model.TaskListener
import hudson.slaves.ComputerLauncher

abstract class MacComputerConnector extends AbstractDescribableImpl<MacComputerConnector> {

    /**
     * Build and return the Launcher for a given connector
     * @param host
     * @param user
     * @return computer launcher
     * @throws IOException
     * @throws InterruptedException
     */
    protected abstract ComputerLauncher createLauncher(MacHost host, MacUser user) throws IOException, InterruptedException

}
