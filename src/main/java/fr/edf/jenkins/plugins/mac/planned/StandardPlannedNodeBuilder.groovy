package fr.edf.jenkins.plugins.mac.planned;

import java.util.concurrent.Future;
import java.util.logging.Level
import java.util.logging.Logger

import com.google.common.util.concurrent.Futures

import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.slave.MacSlave
import fr.edf.jenkins.plugins.mac.ssh.SSHCommand
import fr.edf.jenkins.plugins.mac.ssh.SSHCommandException
import hudson.model.Descriptor;
import hudson.slaves.ComputerLauncher
import hudson.slaves.NodeProvisioner

/**
 * The default {@link PlannedNodeBuilder} implementation, in case there is other
 * registered.
 */
public class StandardPlannedNodeBuilder extends PlannedNodeBuilder {

    private static final Logger LOGGER = Logger.getLogger(StandardPlannedNodeBuilder.name)

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeProvisioner.PlannedNode build() {
        Future f;
        MacUser user = null
        try {
            user = SSHCommand.createUserOnMac(label.toString(), macHost)
            ComputerLauncher launcher = cloud.connector.createLauncher(macHost, user)
            MacSlave agent = new MacSlave(cloud.name, label.toString(), user, macHost, launcher, cloud.idleMinutes)
            f = Futures.immediateFuture(agent)
        } catch (IOException | Descriptor.FormException | SSHCommandException e) {
            LOGGER.log(Level.SEVERE, e.getMessage())
            LOGGER.log(Level.FINEST, "Exception : ", e)
            f = Futures.immediateFailedFuture(e)
            if (user != null ) {
                SSHCommand.deleteUserOnMac(user.username)
            }
        }
        return new NodeProvisioner.PlannedNode(macHost.host, f, numExecutors)
    }
}
