package fr.edf.jenkins.plugins.mac.connector


import java.time.Instant

import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.slave.MacComputer
import fr.edf.jenkins.plugins.mac.ssh.SSHCommand
import fr.edf.jenkins.plugins.mac.ssh.SSHCommandException
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

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Connect with JNLP"
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ComputerLauncher createLauncher(MacHost host, MacUser user) {
        return new MacJNLPLauncher(host, user, jenkinsUrl)
    }

    private static class MacJNLPLauncher extends JNLPLauncher {

        MacHost host
        MacUser user
        String jenkinsUrl
        boolean launched

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
            launched = true
            MacComputer macComputer = (MacComputer) computer
            try {
                SSHCommand.jnlpConnect(host, user, jenkinsUrl, computer.getJnlpMac())
            }catch(SSHCommandException sshe) {
                launched = false
                listener.error("Error while connecting computer " + computer.name)
            }
            long currentTimestamp = Instant.now().toEpochMilli()
            while(!macComputer.isOnline()) {
                if (macComputer == null) {
                    launched = false
                    listener.error("Node was deleted, computer is null");
                }
                if (macComputer.isOnline()) {
                    break;
                }
                if((Instant.now().toEpochMilli() - currentTimestamp) > host.agentConnectionTimeout.multiply(1000).intValue()) {
                    launched = false
                    listener.error("Connection timeout for the computer " + computer.name)
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean isLaunchSupported() {
            return !launched
        }
    }
}
