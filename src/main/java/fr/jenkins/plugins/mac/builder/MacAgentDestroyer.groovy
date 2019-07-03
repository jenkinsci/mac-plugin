package fr.jenkins.plugins.mac.builder

import org.kohsuke.stapler.DataBoundConstructor
import com.trilead.ssh2.Connection
import com.trilead.ssh2.Session
import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.connection.SshClientFactory
import fr.jenkins.plugins.mac.connection.SshClientFactoryConfiguration
import fr.jenkins.plugins.mac.util.Constants
import fr.jenkins.plugins.mac.util.SshUtils
import groovy.util.logging.Slf4j
import hudson.AbortException
import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.BuildListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Publisher
import hudson.tasks.Recorder
import jenkins.model.Jenkins

@Slf4j
class MacAgentDestroyer extends Recorder {

    @DataBoundConstructor
    MacAgentDestroyer() {
    }
    
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Destroy all Mac agents created for the build <br>
     * Delete user created for the agent on the Mac machine <br>
     * Clear temps files created for the builds
     */
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        Connection connection = null
        Session session = null
        try {
            List<MacCloud> macClouds = Jenkins.get().clouds.findAll { cloud ->
                cloud instanceof MacCloud
            }
            MacHost host = macClouds.get(0).getMacHost()
            connection = SshClientFactory.getSshClient(new SshClientFactoryConfiguration(credentialsId: host.credentialsId, port: host.port,
                        context: build, host: host.host, connectionTimeout: host.connectionTimeout,
                        readTimeout: host.readTimeout, kexTimeout: host.kexTimeout))
            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.DELETE_USER, "new_user")))
            connection.close()
        }catch (Exception e) {
            log.error(e.getMessage(), e)
            if (null != connection) connection.close()
            throw new AbortException(e.getMessage())
        }
    }

    @Extension
    static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        String getDisplayName() {
            return "Stop and destroy Mac agent";
        }
    }
}
