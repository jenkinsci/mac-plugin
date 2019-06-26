package fr.jenkins.plugins.mac

import org.antlr.v4.runtime.misc.Nullable
import org.kohsuke.stapler.DataBoundConstructor

import hudson.Extension
import hudson.model.Descriptor
import hudson.model.Label
import hudson.slaves.Cloud
import hudson.slaves.NodeProvisioner.PlannedNode

class MacCloud extends Cloud {

    MacHost macHost

    @DataBoundConstructor
    MacCloud(String name, MacHost macHost) {
        super(name)
        this.macHost = macHost
    }

    public MacHost getMacHost() {
        return macHost;
    }

    static @Nullable getMacCloud() {
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
