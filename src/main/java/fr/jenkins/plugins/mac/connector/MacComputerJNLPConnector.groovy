package fr.jenkins.plugins.mac.connector


import java.util.logging.Level

import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.slave.MacTransientNode
import fr.jenkins.plugins.mac.ssh.SSHCommand
import groovy.util.logging.Slf4j
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.TaskListener
import hudson.slaves.ComputerLauncher
import hudson.slaves.JNLPLauncher
import hudson.slaves.SlaveComputer
import hudson.util.LogTaskListener

@Slf4j
class MacComputerJNLPConnector extends MacComputerConnector {
    
    private static final TaskListener LOGGER_LISTENER = new LogTaskListener(log, Level.FINER)
    private String jenkinsUrl

    @DataBoundConstructor
    public MacComputerJNLPConnector(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl
    }

    @DataBoundSetter
    public void setJenkinsUrl(String jenkinsUrl){
         this.jenkinsUrl = jenkinsUrl 
    }

    @Extension @Symbol("jnlp")
    public static final class DescriptorImpl extends Descriptor<MacComputerConnector> {

        @Override
        public String getDisplayName() {
            return "Connect with JNLP"
        }
    }

    @Override
    protected ComputerLauncher createLauncher(MacHost host, MacUser user) throws IOException, InterruptedException {
        return new MacJNLPLauncher(host, user, jenkinsUrl)
    }

    @Override
    protected void connect(MacHost host, MacUser user, MacTransientNode slave) throws Exception {
        ComputerLauncher launcher = createLauncher(host, user)
        launcher.launch(slave.computer, null)
    }
    
    private static class MacJNLPLauncher extends JNLPLauncher {
        
        MacHost host
        MacUser user
        String jenkinsUrl
        
        MacJNLPLauncher(MacHost host, MacUser user, String jenkinsUrl) {
            super(true)
            this.host = host
            this.user = user
            this.jenkinsUrl = jenkinsUrl
        }
        
        @Override
        public void launch(SlaveComputer computer, TaskListener listener) {
            SSHCommand.jnlpConnect(host, user, jenkinsUrl, computer.getJnlpMac())
        }
        
        @Override
        public boolean isLaunchSupported() {
            return true;
        }
    }
}
