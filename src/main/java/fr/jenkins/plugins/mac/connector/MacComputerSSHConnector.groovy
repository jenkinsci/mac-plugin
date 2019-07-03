package fr.jenkins.plugins.mac.connector

import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import com.thoughtworks.xstream.InitializationException

import hudson.Extension
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import hudson.remoting.Channel
import hudson.remoting.Which
import jenkins.model.Jenkins

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
}
