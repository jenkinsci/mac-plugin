package fr.jenkins.plugins.mac

import javax.annotation.CheckForNull

import org.antlr.v4.runtime.misc.Nullable
import org.kohsuke.stapler.DataBoundConstructor

import fr.jenkins.plugins.mac.connector.MacComputerConnector
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.Label
import hudson.slaves.Cloud
import hudson.slaves.ComputerLauncher
import hudson.slaves.NodeProvisioner.PlannedNode

class MacCloud extends Cloud {

    MacHost macHost
    MacComputerConnector connector
    String labels

    @DataBoundConstructor
    MacCloud(String name, MacHost macHost, MacComputerConnector connector, String labels) {
        super(name)
        this.macHost = macHost
        this.connector = connector
        this.labels = labels
    }

    public MacHost getMacHost() {
        return macHost;
    }

    static @Nullable getMacClouds() {
        return all().get(MacCloud)
    }

    @Override
    public Collection<PlannedNode> provision(Label label, int excessWorkload) {
//        ComputerLauncher launcher = connector.createLauncher(this, user)
//        MacTransientNode node = new MacTransientNode(cloud.name, user, launcher)
//        if(!connectionMap.containsKey(cloud.name)) {
//            connectionMap.put(cloud.name, new ArrayList())
//        }
//        connectionMap.get(cloud.name).add(node.name)
//        Jenkins.get().addNode(node)
    }

    @Override
    public boolean canProvision(Label label) {
        // TODO Auto-generated method stub
        return false;
    }

    @Extension
    static class DescriptorImpl extends Descriptor<Cloud> {

        @Override
        public String getDisplayName() {
            return Messages.Cloud_DisplayName()
        }
    }
}
