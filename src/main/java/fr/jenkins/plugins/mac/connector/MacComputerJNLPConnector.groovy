package fr.jenkins.plugins.mac.connector


import java.util.logging.Level

import javax.annotation.CheckForNull

import org.apache.commons.lang.StringUtils
import org.jenkinsci.Symbol
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import com.google.common.base.Joiner

import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacUser
import groovy.util.logging.Slf4j
import hudson.EnvVars
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.TaskListener
import hudson.slaves.ComputerLauncher
import hudson.slaves.JNLPLauncher
import hudson.slaves.NodeProperty
import hudson.util.LogTaskListener
import jenkins.model.Jenkins

@Slf4j
class MacComputerJNLPConnector extends MacComputerConnector {
    
    private static final TaskListener LOGGER_LISTENER = new LogTaskListener(log, Level.FINER)
    private final JNLPLauncher jnlpLauncher
    private String jenkinsUrl

    @DataBoundConstructor
    public MacComputerJNLPConnector(JNLPLauncher jnlpLauncher) {
        this.jnlpLauncher = jnlpLauncher
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
    protected ComputerLauncher createLauncher(MacCloud cloud, MacUser user) throws IOException, InterruptedException {
        return new JNLPLauncher(true)
    }
}
