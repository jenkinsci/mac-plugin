package fr.edf.jenkins.plugins.mac


import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter
import org.kohsuke.stapler.AncestorInPath
import org.kohsuke.stapler.QueryParameter
import org.kohsuke.stapler.verb.POST

import fr.edf.jenkins.plugins.mac.util.FormUtils

import hudson.Extension
import hudson.model.Describable
import hudson.model.Descriptor
import hudson.model.Item
import jenkins.model.Jenkins
import hudson.util.ListBoxModel

class MacHostFile implements Describable<MacHostFile> {

    String hostFileCredentialsId
    String hostPath

    @DataBoundConstructor
    MacHostFile(String hostFileCredentialsId, String hostPath) {
        this.hostFileCredentialsId = hostFileCredentialsId
        this.hostPath = hostPath
    }

    @DataBoundSetter
    setHostFileCredentialsId(String hostFileCredentialsId) {
        this.hostFileCredentialsId = hostFileCredentialsId
    }

    @DataBoundSetter
    setHostPath(String hostPath) {
        this.hostPath = hostPath
    }

    @Override
    public Descriptor<MacHostFile> getDescriptor() {
        return Jenkins.get().getDescriptorOrDie(this.getClass())
    }

    @Extension
    static class DescriptorImpl extends Descriptor<MacHostFile> {
        /**
         * {@inheritDoc}
         */
        @Override
        String getDisplayName() {
            return Messages.HostFile_DisplayName()
        }


        /**
         * Return ListBoxModel of existing secret files
         * @param credentialsId
         * @param context
         * @return ListBoxModel
         */
        @POST
        ListBoxModel doFillHostFileCredentialsIdItems(@QueryParameter String fileCredentialsId, @AncestorInPath Item ancestor) {
            return FormUtils.newFileCredentialsItemsListBoxModel(fileCredentialsId, ancestor)
        }
    }
}
