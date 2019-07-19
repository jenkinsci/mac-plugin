package fr.jenkins.plugins.mac

import java.util.concurrent.CompletableFuture

import org.antlr.v4.runtime.misc.Nullable
import org.apache.commons.lang.StringUtils
import org.kohsuke.stapler.DataBoundConstructor

import com.google.common.base.Throwables
import com.trilead.ssh2.Connection

import fr.jenkins.plugins.mac.connector.MacComputerConnector
import fr.jenkins.plugins.mac.connector.MacComputerJNLPConnector
import fr.jenkins.plugins.mac.slave.MacTransientNode
import fr.jenkins.plugins.mac.ssh.SSHCommander
import groovy.util.logging.Slf4j
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.Label
import hudson.model.Node
import hudson.model.labels.LabelAtom
import hudson.slaves.Cloud
import hudson.slaves.ComputerLauncher
import hudson.slaves.NodeProvisioner
import hudson.slaves.NodeProvisioner.PlannedNode
import jenkins.model.Jenkins
import jenkins.slaves.JnlpSlaveAgentProtocol

@Slf4j
class MacCloud extends Cloud {

    MacHost macHost
    MacComputerConnector connector
    String labelString
    transient Set<LabelAtom> labelSet

    @DataBoundConstructor
    MacCloud(String name, MacHost macHost, MacComputerConnector connector, String labelString) {
        super(name)
        this.macHost = macHost
        this.connector = connector
        this.labelString = labelString
        labelSet = Label.parse(StringUtils.defaultIfEmpty(labelString, ""))
    }

    static @Nullable getMacClouds() {
        return all().get(MacCloud)
    }

    @Override
    synchronized Collection<PlannedNode> provision(Label label, int excessWorkload) {
        final List<PlannedNode> r = new ArrayList<>();
        Connection connection = null
        MacCloud cloud = this
        try {
            MacUser user = SSHCommander.createUserOnMac(macHost)
            final CompletableFuture<Node> plannedNode = new CompletableFuture<>()
            r.add(new PlannedNode(user.username, plannedNode, excessWorkload))
            MacTransientNode slave = null
            try {
                ComputerLauncher launcher = cloud.connector.createLauncher(cloud, user)
                slave = new MacTransientNode(cloud.name, cloud.labelString, user, launcher)
                JnlpSlaveAgentProtocol.SLAVE_SECRET.mac(slave.name)
                plannedNode.complete(slave)

                Jenkins.get().addNode(slave)
            } catch (Exception ex) {
                log.error("Error in provisioning; user='{}' for cloud='{}'",
                        user.username, getDisplayName(), ex)
                plannedNode.completeExceptionally(ex)
                throw Throwables.propagate(ex)
            }
            if(connector instanceof MacComputerJNLPConnector) {
                MacComputerJNLPConnector jnlpConnector = (MacComputerJNLPConnector) connector
                SSHCommander.jnlpConnect(macHost, user, connector, slave.getComputer().getJnlpMac())
            }
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
        return true
    }

    @Extension
    static class DescriptorImpl extends Descriptor<Cloud> {

        @Override
        String getDisplayName() {
            return Messages.Cloud_DisplayName()
        }
    }
}
