package fr.jenkins.plugins.mac.connector

import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl

import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacUser
import hudson.Extension
import hudson.model.Descriptor
import hudson.plugins.sshslaves.SSHLauncher
import hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy
import hudson.slaves.ComputerLauncher

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

    String getJvmOptions() {
        return jvmOptions;
    }

    @DataBoundSetter
    void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    String getJavaPath() {
        return javaPath;
    }

    @DataBoundSetter
    void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    String getPrefixStartSlaveCmd() {
        return prefixStartSlaveCmd;
    }

    @DataBoundSetter
    void setPrefixStartSlaveCmd(String prefixStartSlaveCmd) {
        this.prefixStartSlaveCmd = prefixStartSlaveCmd;
    }

    String getSuffixStartSlaveCmd() {
        return suffixStartSlaveCmd;
    }

    @DataBoundSetter
    void setSuffixStartSlaveCmd(String suffixStartSlaveCmd) {
        this.suffixStartSlaveCmd = suffixStartSlaveCmd;
    }

    @Extension @Symbol("ssh")
    static final class DescriptorImpl extends Descriptor<MacComputerConnector> {
        @Override
        String getDisplayName() {
            return "Connect with SSH";
        }
    }

    @Override
    protected ComputerLauncher createLauncher(MacCloud cloud, MacUser user) throws IOException, InterruptedException {
        return new MacSSHLauncher(cloud.macHost.host, cloud.macHost.port, user, jvmOptions, javaPath, prefixStartSlaveCmd, suffixStartSlaveCmd, cloud.macHost.readTimeout, 5, 3000)
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
}
