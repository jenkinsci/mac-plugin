package fr.jenkins.plugins.mac.slave

import javax.annotation.CheckForNull

import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.google.common.base.Objects

import fr.jenkins.plugins.mac.MacCloud
import groovy.util.logging.Slf4j
import hudson.EnvVars
import hudson.slaves.SlaveComputer

@Slf4j
class MacComputer extends SlaveComputer {
    
    MacComputer(MacTransientNode node) {
        super(node)
    }
    
    @CheckForNull
    @Override
    MacTransientNode getNode() {
        return (MacTransientNode) super.getNode()
    }
    
    @CheckForNull
    public MacCloud getCloud() {
        final MacTransientNode node = getNode();
        return node == null ? null : node.getCloud();
    }
    
    @CheckForNull
    String getUserId() {
        final MacTransientNode node = getNode()
        return node == null ? null : node.getUserId()
    }
    
    @CheckForNull
    String getCloudId() {
        final MacTransientNode node = getNode()
        return node == null ? null : node.getCloudId()
    }

    @Override
    @Restricted(NoExternalUse.class)
    public EnvVars getEnvironment() throws IOException, InterruptedException {
        EnvVars variables = super.getEnvironment();
        variables.put("MAC_USER_ID", getUserId());
        final MacCloud cloud = getCloud();
        if (cloud != null) {
            variables.put("JENKINS_CLOUD_ID", cloud.name);
            String macHost = cloud.macHost.host
            variables.put("MAC_HOST", macHost);
        }
        return variables;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", super.getName())
                .add("slave", getNode())
                .toString();
    }
}
