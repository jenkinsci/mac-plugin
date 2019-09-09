package fr.edf.jenkins.plugins.mac.connector


import fr.edf.jenkins.plugins.mac.MacCloud
import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.slave.MacSlave
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
