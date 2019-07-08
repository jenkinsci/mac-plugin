package fr.jenkins.plugins.mac.builder


import org.kohsuke.stapler.DataBoundConstructor

import com.thoughtworks.xstream.InitializationException
import com.trilead.ssh2.Connection
import com.trilead.ssh2.Session
import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacProvisionService
import fr.jenkins.plugins.mac.MacTransientNode
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.connection.SshClientFactory
import fr.jenkins.plugins.mac.connection.SshClientFactoryConfiguration
import fr.jenkins.plugins.mac.util.Constants
import fr.jenkins.plugins.mac.util.CredentialsUtils
import fr.jenkins.plugins.mac.util.SshUtils
import groovy.util.logging.Slf4j
import hudson.AbortException
import hudson.DescriptorExtensionList
import hudson.Extension
import hudson.FilePath
import hudson.Launcher
import hudson.model.AbstractProject
import hudson.model.Run
import hudson.model.TaskListener
import hudson.remoting.Channel
import hudson.remoting.Which
import hudson.slaves.Cloud
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Builder
import jenkins.model.Jenkins
import jenkins.tasks.SimpleBuildStep

/**
 * Builder of Mac Agent <br>
 * @author Mathieu DELROCQ
 *
 */
@Slf4j
class MacAgentBuilder extends Builder implements SimpleBuildStep {

//    protected static final File remoting = {
//        try {
//            return Which.jarFile(Channel.class);
//        } catch (IOException e) {
//            throw new InitializationException("Failed to resolve path to remoting.jar");
//        }
//    }

    @DataBoundConstructor
    MacAgentBuilder() {
    }

    /**
     * Connect on a Mac with SSH <br>
     * Create an user <br>
     * Connect with the user <br>
     * Launch Agent with JNLP
     */
    @Override
    public void perform(Run run, FilePath workspace, Launcher launcher, TaskListener listener)
    throws InterruptedException, IOException {
        Connection connection = null
        try {
            List<MacCloud> macClouds = Jenkins.get().clouds.findAll { cloud ->
                cloud instanceof MacCloud
            }
            MacCloud cloud = macClouds.get(0)
            MacHost host = cloud.getMacHost()
            String remotingUrl = /*Jenkins.get().getRootUrl()*/ "http://10.31.195.86:8080/jenkins/" + Constants.REMOTING_JAR_PATH
            connection = SshClientFactory.getSshClient(new SshClientFactoryConfiguration(credentialsId: host.credentialsId, port: host.port,
                        context: run.getParent(), host: host.host, connectionTimeout: host.connectionTimeout,
                        readTimeout: host.readTimeout, kexTimeout: host.kexTimeout))
            MacUser user = MacProvisionService.getInstance().generateUser()
            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.CREATE_USER, user.username, user.password)))
            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.GET_REMOTING_JAR, remotingUrl, user.username)))
            MacProvisionService.getInstance().provisionAgent(cloud, user)
//            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.GET_REMOTING_JAR, remotingUrl, Constants.REMOTING_JAR_FILENAME)))
            
            connection.close()
        }catch (Exception e) {
            log.error(e.getMessage(), e)
            if (null != connection) connection.close()
            throw new AbortException(e.getMessage())
        }
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true
        }

        @Override
        public String getDisplayName() {
            return "Start Mac agents"
        }
    }
}
