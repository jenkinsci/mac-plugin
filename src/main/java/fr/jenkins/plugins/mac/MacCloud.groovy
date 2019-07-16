package fr.jenkins.plugins.mac

import java.util.concurrent.CompletableFuture

import javax.annotation.CheckForNull

import org.antlr.v4.runtime.misc.Nullable
import org.apache.commons.lang.StringUtils
import org.kohsuke.stapler.DataBoundConstructor

import com.google.common.base.Throwables
import com.trilead.ssh2.Connection

import fr.jenkins.plugins.mac.connection.SshClientFactory
import fr.jenkins.plugins.mac.connection.SshClientFactoryConfiguration
import fr.jenkins.plugins.mac.connector.MacComputerConnector
import fr.jenkins.plugins.mac.util.Constants
import fr.jenkins.plugins.mac.util.SshUtils
import groovy.util.logging.Slf4j
import hudson.AbortException
import hudson.Extension
import hudson.Util
import hudson.model.Computer
import hudson.model.Descriptor
import hudson.model.Label
import hudson.model.Node
import hudson.model.TaskListener
import hudson.model.labels.LabelAtom
import hudson.slaves.Cloud
import hudson.slaves.ComputerLauncher
import hudson.slaves.NodeProvisioner
import hudson.slaves.NodeProvisioner.PlannedNode
import jenkins.model.Jenkins

@Slf4j
class MacCloud extends Cloud {

    MacHost macHost
    MacComputerConnector connector
    String labelString
    transient Set<LabelAtom> labelSet;

    @DataBoundConstructor
    MacCloud(String name, MacHost macHost, MacComputerConnector connector, String labelString) {
        super(name)
        this.macHost = macHost
        this.connector = connector
        this.labelString = labelString
        labelSet = Label.parse(StringUtils.defaultIfEmpty(labelString, ""))
    }

    MacHost getMacHost() {
        return macHost;
    }

    Set<LabelAtom> getLabelSet() {
        return labelSet;
    }

    void setLabelSet(Set<LabelAtom> labelSet) {
        this.labelSet = labelSet;
    }

    static @Nullable getMacClouds() {
        return all().get(MacCloud)
    }

    @Override
    Collection<PlannedNode> provision(Label label, int excessWorkload) {
        final List<PlannedNode> r = new ArrayList<>();
        Connection connection = null
        MacCloud cloud = this
        try {
            String jenkinsUrl = /*Jenkins.get().getRootUrl()*/ "http://10.31.195.86:8080/jenkins/"
            String remotingUrl = jenkinsUrl + Constants.REMOTING_JAR_PATH
            connection = SshClientFactory.getSshClient(new SshClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
                        context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
                        readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
            MacUser user = MacProvisionService.getInstance().generateUser()
            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.CREATE_USER, user.username, user.password)))
            connection.close()
            final CompletableFuture<Node> plannedNode = new CompletableFuture<>()
            r.add(new PlannedNode(user.username, plannedNode, macHost.maxUsers))
            final Runnable taskToCreateNewSlave = new Runnable() {
                @Override
                public void run() {
                    MacTransientNode slave = null;
                    try {
                        ComputerLauncher launcher = cloud.connector.createLauncher(cloud, user)
                        slave = new MacTransientNode(cloud.name, cloud.labelString, user, launcher)
                        plannedNode.complete(slave)

                        // On provisioning completion, let's trigger NodeProvisioner
                        Jenkins.get().addNode(slave)

                    } catch (Exception ex) {
                        log.error("Error in provisioning; user='{}' for cloud='{}'",
                                user.username, getDisplayName(), ex)
                        plannedNode.completeExceptionally(ex)
                        throw Throwables.propagate(ex)
                    }
                }
            };
            Computer.threadPoolForRemoting.submit(taskToCreateNewSlave);
            connection = SshClientFactory.getUserConnection(user.username, user.password, macHost.host,
                macHost.port, macHost.connectionTimeout, macHost.readTimeout, macHost.kexTimeout)
            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.GET_REMOTING_JAR, remotingUrl)))
            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.LAUNCH_JNLP, jenkinsUrl, user.username, user.username)))
            connection.close()
            return r
        }catch (Exception e) {
            log.error(e.getMessage(), e)
            if (null != connection) connection.close()
            return Collections.emptyList()
        }
    }

    @Override
    boolean canProvision(Label label) {
        // TODO Auto-generated method stub
        return true;
    }

    @Extension
    static class DescriptorImpl extends Descriptor<Cloud> {

        @Override
        String getDisplayName() {
            return Messages.Cloud_DisplayName()
        }
    }
}
