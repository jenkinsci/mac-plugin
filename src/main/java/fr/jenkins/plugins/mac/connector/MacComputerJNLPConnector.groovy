package fr.jenkins.plugins.mac.connector


import java.time.Instant

import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.slave.MacComputer
import fr.jenkins.plugins.mac.ssh.SSHCommand
import fr.jenkins.plugins.mac.ssh.SSHCommandException
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.TaskListener
import hudson.slaves.ComputerLauncher
import hudson.slaves.JNLPLauncher
import hudson.slaves.SlaveComputer

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

    /**
     * {@inheritDoc}
     */
    @Override
    protected ComputerLauncher createLauncher(MacHost host, MacUser user) throws IOException, InterruptedException {
        return new MacJNLPLauncher(host, user, jenkinsUrl)
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

        /**
         * {@inheritDoc}
         */
        @Override
        void launch(SlaveComputer computer, TaskListener listener) {
            MacComputer macComputer = (MacComputer) computer
            SSHCommand.jnlpConnect(host, user, jenkinsUrl, computer.getJnlpMac())
            long currentTimestamp = Instant.now().toEpochMilli()
            while(!macComputer.connecting) {
                if((Instant.now().toEpochMilli() - currentTimestamp) > host.connectionTimeout) {
                    throw new Exception("Connection timeout for the computer " + computer.name)
                }
            }
        }

        @Override
        boolean isLaunchSupported() {
            return true
        }
    }
}
