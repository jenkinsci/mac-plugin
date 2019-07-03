package fr.jenkins.plugins.mac

import org.kohsuke.stapler.DataBoundConstructor

import com.trilead.ssh2.Connection
import groovy.util.logging.Slf4j
import hudson.model.Slave
import hudson.slaves.ComputerLauncher

@Slf4j
class MacTransientNode extends Slave {
    final String username
    final String cloudId
    final transient Connection sshConnection;
    
    MacTransientNode(String cloudId, String username, String workdir, Connection sshConnection, ComputerLauncher launcher) {
        super(username, workdir, launcher)
        this.cloudId = cloudId
        this.username = username
        this.sshConnection = sshConnection
    }
    
    
}
