package fr.jenkins.plugins.mac

import java.util.concurrent.CompletableFuture

import org.antlr.v4.runtime.misc.Nullable
import org.apache.commons.lang.StringUtils
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse
import org.kohsuke.stapler.DataBoundConstructor

import com.google.common.base.Throwables

import fr.jenkins.plugins.mac.connector.MacComputerConnector
import fr.jenkins.plugins.mac.slave.MacTransientNode
import fr.jenkins.plugins.mac.ssh.SSHCommand
import fr.jenkins.plugins.mac.ssh.SSHCommandException
import groovy.util.logging.Slf4j
import hudson.Extension
import hudson.model.Computer
import hudson.model.Descriptor
import hudson.model.Label
import hudson.model.Queue
import hudson.model.Node
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
    transient Set<LabelAtom> labelSet
    Boolean disabled

    @DataBoundConstructor
    MacCloud(String name, MacHost macHost, MacComputerConnector connector, String labelString, Boolean disabled) {
        super(name)
        this.macHost = macHost
        this.connector = connector
        this.disabled = disabled
        this.labelString = labelString
        labelSet = Label.parse(StringUtils.defaultIfEmpty(labelString, ""))
    }

    static @Nullable getMacClouds() {
        return all().get(MacCloud)
    }

    @Override
    synchronized Collection<PlannedNode> provision(Label label, int excessWorkload) {
        final List<PlannedNode> r = new ArrayList<>();
        MacCloud cloud = this
        MacUser user = null

        try {
            user = SSHCommand.createUserOnMac(cloud.macHost)
            final CompletableFuture<Node> plannedNode = new CompletableFuture<>()
            r.add(new PlannedNode(user.username, plannedNode, excessWorkload))
            createSlave(cloud, user, plannedNode)
            return r
        }catch (Exception e) {
//            if(null != user) {
//                try {
//                    SSHCommand.deleteUserOnMac(cloud.name, user.username)
//                } catch(SSHCommandException sshe) {
//                    log.error(sshe.getMessage(), sshe)
//                }
//            }
            log.error(e.getMessage(), e)
            return Collections.emptyList()
        }
    }

    @Restricted(NoExternalUse)
    private MacTransientNode createSlave(MacCloud cloud, MacUser user, CompletableFuture plannedNode) {
        MacTransientNode slave = null
        int timeout = 0
            Computer.threadPoolForRemoting.submit({ ->
                Queue.withLock({ ->
                    try {
                        ComputerLauncher launcher = cloud.connector.createLauncher(cloud.macHost, user)
                        slave = new MacTransientNode(cloud.name, cloud.labelString, user, launcher)
                        plannedNode.complete(slave)
                        Jenkins.get().addNode(slave)
                        connector.connect(slave)
                        Thread.sleep(5000L)
                    } catch (Exception e) {
                        plannedNode.completeExceptionally(e)
                        if(null != slave) {
                            slave.terminate(log)
                        }
                        throw Throwables.propagate(e);
                    }
                });
            });
        return slave
    }

    @Override
    boolean canProvision(Label label) {
        return !disabled
    }

    @Extension
    static class DescriptorImpl extends Descriptor<Cloud> {

        @Override
        String getDisplayName() {
            return Messages.Cloud_DisplayName()
        }
    }
}
