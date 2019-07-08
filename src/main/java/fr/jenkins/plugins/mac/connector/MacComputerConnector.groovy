package fr.jenkins.plugins.mac.connector


import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import hudson.model.AbstractDescribableImpl
import hudson.model.TaskListener
import hudson.slaves.ComputerLauncher

abstract class MacComputerConnector extends AbstractDescribableImpl<MacComputerConnector> {
    
    protected abstract ComputerLauncher createLauncher(MacCloud cloud, MacUser user) throws IOException, InterruptedException;

}
