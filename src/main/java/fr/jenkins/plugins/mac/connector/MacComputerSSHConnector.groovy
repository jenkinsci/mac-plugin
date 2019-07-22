package fr.jenkins.plugins.mac.connector

import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl

import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.slave.MacTransientNode
import hudson.Extension
import hudson.model.Descriptor
import hudson.plugins.sshslaves.SSHLauncher
import hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy
import hudson.slaves.ComputerLauncher
import hudson.util.LogTaskListener

import java.util.logging.Level
import java.util.logging.Logger

class MacComputerSSHConnector extends MacComputerConnector {

    String jvmOptions
    String javaPath
    String prefixStartSlaveCmd
    String suffixStartSlaveCmd

    @DataBoundConstructor
    MacComputerSSHConnector(String jvmOptions, String javaPath,
    String prefixStartSlaveCmd, String suffixStartSlaveCmd) {
        this.jvmOptions = jvmOptions
        this.javaPath = javaPath
        this.prefixStartSlaveCmd = prefixStartSlaveCmd
        this.suffixStartSlaveCmd = suffixStartSlaveCmd
    }

    @DataBoundSetter
    void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions
    }

    @DataBoundSetter
    void setJavaPath(String javaPath) {
        this.javaPath = javaPath
    }

    @DataBoundSetter
    void setPrefixStartSlaveCmd(String prefixStartSlaveCmd) {
        this.prefixStartSlaveCmd = prefixStartSlaveCmd
    }

    @DataBoundSetter
    void setSuffixStartSlaveCmd(String suffixStartSlaveCmd) {
        this.suffixStartSlaveCmd = suffixStartSlaveCmd
    }

    @Extension @Symbol("ssh")
    static final class DescriptorImpl extends Descriptor<MacComputerConnector> {
        @Override
        String getDisplayName() {
            return "Connect with SSH"
        }
    }

    @Override
    protected ComputerLauncher createLauncher(MacHost macHost, MacUser user) throws IOException, InterruptedException {
        return new MacSSHLauncher(macHost.host, macHost.port, user, jvmOptions, javaPath, prefixStartSlaveCmd, suffixStartSlaveCmd, macHost.readTimeout, 5, 3000)
    }

    private static class MacSSHLauncher extends SSHLauncher {
        String user
        String password

        MacSSHLauncher(String host, int port, MacUser user, String jvmOptions,
        String javaPath, String prefixStartSlaveCmd, String suffixStartSlaveCmd, Integer launchTimeoutSeconds,
        Integer maxNumRetries, Integer retryWaitTime) {
            super(host, port, user.getUsername(), jvmOptions, javaPath, prefixStartSlaveCmd,
            suffixStartSlaveCmd, launchTimeoutSeconds, maxNumRetries, retryWaitTime, new NonVerifyingKeyVerificationStrategy())
            //            super(host, port, user)
            this.workDir = user.getWorkdir()
            this.user = user.getUsername()
            this.password = user.getPassword()
        }

        @Override
        StandardUsernameCredentials getCredentials() {
            return new UsernamePasswordCredentialsImpl(CredentialsScope.SYSTEM, user,
                    "private credentials for mac ssh agent", user, password)
        }
    }

    @Override
    protected void connect(MacHost host, MacUser user, MacTransientNode slave) throws Exception {
        ComputerLauncher launcher = createLauncher(host, user)
        launcher.launch(slave.computer, new LogTaskListener(new Logger(), Level.FINE))
    }
}
