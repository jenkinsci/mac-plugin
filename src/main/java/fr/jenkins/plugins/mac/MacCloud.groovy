package fr.jenkins.plugins.mac

import javax.annotation.CheckForNull

import org.antlr.v4.runtime.misc.Nullable
import org.kohsuke.stapler.DataBoundConstructor

import fr.jenkins.plugins.mac.connector.MacComputerConnector
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.Label
import hudson.slaves.Cloud
import hudson.slaves.NodeProvisioner.PlannedNode

class MacCloud extends Cloud {

    MacHost macHost
    MacComputerConnector connector

    @DataBoundConstructor
    MacCloud(String name, MacHost macHost, MacComputerConnector connector) {
        super(name)
        this.macHost = macHost
        this.connector = connector
    }

    public MacHost getMacHost() {
        return macHost;
    }

    static @Nullable getMacClouds() {
        return all().get(MacCloud)
    }

    @Override
    public Collection<PlannedNode> provision(Label label, int excessWorkload) {
        // TODO Auto-generated method stub
        return null;
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
