package fr.edf.jenkins.plugins.mac.connector


import java.time.Instant

import org.apache.commons.lang.exception.ExceptionUtils
import org.jenkinsci.Symbol
import org.jenkinsci.plugins.plaincredentials.FileCredentials
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.slave.MacComputer
import fr.edf.jenkins.plugins.mac.ssh.SSHCommand
import fr.edf.jenkins.plugins.mac.util.CredentialsUtils
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.TaskListener
import hudson.slaves.ComputerLauncher
import hudson.slaves.JNLPLauncher
import hudson.slaves.SlaveComputer
import jenkins.model.Jenkins

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
                SSHCommand.createUserOnMac(host, user)
                if(host.uploadKeychain && host.fileCredentialsId != null) {
                    FileCredentials fileCredentials = CredentialsUtils.findFileCredentials(host.fileCredentialsId, Jenkins.get())
                    SSHCommand.uploadKeychain(host, user, fileCredentials)
                }
                if(host.preLaunchCommandsList) {
                    listener.logger.print("Launching entry point cmd")
                    SSHCommand.launchPreLaunchCommand(host, user)
                }
                if (host.hostFiles) {
                    host.hostFiles.each { hostFile ->
                        FileCredentials fileCredentials = CredentialsUtils.findFileCredentials(hostFile.hostFileCredentialsId, Jenkins.get())
                        SSHCommand.uploadHostFile(host, user, fileCredentials, hostFile.hostPath)
                    }
                }
                SSHCommand.jnlpConnect(host, user, jenkinsUrl, computer.getJnlpMac())
            }catch(Exception e) {
                launched = false
                String message = String.format("Error while connecting computer %s due to error %s ",
                        computer.name, ExceptionUtils.getStackTrace(e))
                listener.error(message)
                throw new InterruptedException(message)
            }
            long currentTimestamp = Instant.now().toEpochMilli()
            while(!macComputer.isOnline()) {
                if (macComputer == null) {
                    launched = false
                    String message = "Node was deleted, computer is null"
                    listener.error(message)
                    throw new IOException(message)
                }
                if (macComputer.isOnline()) {
                    break;
                }
                if((Instant.now().toEpochMilli() - currentTimestamp) > host.agentConnectionTimeout.multiply(1000).intValue()) {
                    launched = false
                    String message = toString().format("Connection timeout for the computer %s", computer.name)
                    listener.error(message)
                    throw new InterruptedException(message)
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
