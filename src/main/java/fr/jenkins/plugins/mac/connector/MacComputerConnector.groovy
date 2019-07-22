package fr.jenkins.plugins.mac.connector


import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.slave.MacTransientNode
import hudson.model.AbstractDescribableImpl
import hudson.model.TaskListener
import hudson.slaves.ComputerLauncher

abstract class MacComputerConnector extends AbstractDescribableImpl<MacComputerConnector> {
    
    protected abstract ComputerLauncher createLauncher(MacHost host, MacUser user) throws IOException, InterruptedException
    
    protected abstract void connect(MacTransientNode slave) throws Exception

}
