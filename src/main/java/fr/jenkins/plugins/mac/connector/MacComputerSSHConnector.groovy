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

    public String getJvmOptions() {
        return jvmOptions;
    }

    @DataBoundSetter
    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public String getJavaPath() {
        return javaPath;
    }

    @DataBoundSetter
    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getPrefixStartSlaveCmd() {
        return prefixStartSlaveCmd;
    }

    @DataBoundSetter
    public void setPrefixStartSlaveCmd(String prefixStartSlaveCmd) {
        this.prefixStartSlaveCmd = prefixStartSlaveCmd;
    }

    public String getSuffixStartSlaveCmd() {
        return suffixStartSlaveCmd;
    }

    @DataBoundSetter
    public void setSuffixStartSlaveCmd(String suffixStartSlaveCmd) {
        this.suffixStartSlaveCmd = suffixStartSlaveCmd;
    }

    @Extension @Symbol("ssh")
    public static final class DescriptorImpl extends Descriptor<MacComputerConnector> {
        @Override
        public String getDisplayName() {
            return "Connect with SSH";
        }
    }

    @Override
    protected ComputerLauncher createLauncher(MacCloud cloud, MacUser user) throws IOException, InterruptedException {
        return new MacSSHLauncher(cloud.macHost.host, cloud.macHost.port, user.username, user.password.plainText, jvmOptions, javaPath, prefixStartSlaveCmd, suffixStartSlaveCmd, cloud.macHost.readTimeout, 5, 3000)
    }

    private static class MacSSHLauncher extends SSHLauncher {
        private String user
        private String password

        public MacSSHLauncher(String host, int port, String user, String password, String jvmOptions,
        String javaPath, String prefixStartSlaveCmd, String suffixStartSlaveCmd, Integer launchTimeoutSeconds,
        Integer maxNumRetries, Integer retryWaitTime) {
            super(host, port, user, jvmOptions, javaPath, prefixStartSlaveCmd,
            suffixStartSlaveCmd, launchTimeoutSeconds, maxNumRetries, retryWaitTime, null)
            this.user = user
            this.password = password
        }

        @Override
        public StandardUsernameCredentials getCredentials() {
            return new UsernamePasswordCredentialsImpl(CredentialsScope.SYSTEM, user,
                    "private credentials for mac ssh agent", user, password)
        }
    }
}
