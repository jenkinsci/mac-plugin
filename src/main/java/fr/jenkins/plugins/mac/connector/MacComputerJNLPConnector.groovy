package fr.jenkins.plugins.mac.connector


import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.slave.MacTransientNode
import fr.jenkins.plugins.mac.ssh.SSHCommand
import fr.jenkins.plugins.mac.ssh.SSHCommandException
import groovy.util.logging.Slf4j
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.TaskListener
import hudson.slaves.ComputerLauncher
import hudson.slaves.JNLPLauncher
import hudson.slaves.SlaveComputer
import jenkins.slaves.JnlpSlaveAgentProtocol

@Slf4j
class MacComputerJNLPConnector extends MacComputerConnector {

    private String jenkinsUrl

    @DataBoundConstructor
    public MacComputerJNLPConnector(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl
    }

    @DataBoundSetter
    public void setJenkinsUrl(String jenkinsUrl){
        this.jenkinsUrl = jenkinsUrl
    }

    public String getJenkinsUrl() {
        return jenkinsUrl
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
    protected void connect(MacTransientNode slave) throws Exception {
        JnlpSlaveAgentProtocol.SLAVE_SECRET.mac(slave.name)
        MacJNLPLauncher launcher = (MacJNLPLauncher) slave.launcher
        launcher.launch(slave.computer, (TaskListener) TaskListener.NULL)
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
        void launch(SlaveComputer computer, TaskListener listener) {
            try {
                SSHCommand.jnlpConnect(host, user, jenkinsUrl, computer.getJnlpMac())
            } catch(SSHCommandException e) {
                throw e
            }
        }

        @Override
        boolean isLaunchSupported() {
            return true
        }
    }
}
