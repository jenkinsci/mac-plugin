package fr.jenkins.plugins.mac.config

import javax.annotation.Nullable

import org.kohsuke.stapler.AncestorInPath
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

import fr.jenkins.plugins.mac.utils.FormUtils
import hudson.Extension
import hudson.model.Describable
import hudson.model.Descriptor
import hudson.model.Item
import hudson.model.ItemGroup
import hudson.util.FormValidation
import hudson.util.FormValidation.Kind
import hudson.util.ListBoxModel
import jenkins.model.Jenkins

class MacPluginConfiguration implements Describable<MacPluginConfiguration> {

    String host
    String credentialsId
    Integer port
    Integer maxUsers

    @DataBoundConstructor
    MacPluginConfiguration(String host, String credentialsId, int port, int maxUsers) {
        this.host = host
        this.credentialsId = credentialsId
        this.port = port
        this.maxUsers = maxUsers
    }

    static @Nullable MacPluginConfiguration getMacPluginConfigs() {
        return GlobalMacPluginConfiguration.globalMacPluginConfiguration.macPluginConfigs?.find { true }
    }
    static @Nullable String getHost() {
        getMacPluginConfigs()?.host
    }

    static @Nullable String getCredentialsId() {
        getMacPluginConfigs()?.@credentialsId
    }

    static @Nullable Integer getPort() {
        getMacPluginConfigs()?.@port
    }

    static @Nullable Integer getMaxUsers() {
        getMacPluginConfigs()?.@maxUsers
    }

    @Override
    Descriptor<MacPluginConfiguration> getDescriptor() {
        return Jenkins.get().getDescriptorOrDie(this.getClass())
    }

    /**
     * Descriptor of an iteration of the global configuration
     * @see src\main\resources\fr\jenkins\plugins\mac\config\MacPluginConfiguration\config.groovy
     * @author mathieu.delrocq
     *
     */
    @Extension
    static class DescriptorImpl extends Descriptor<MacPluginConfiguration> {

        /**
         * Check if the value of host field is correct
         * @param value
         * @return FormValidation
         */
        FormValidation doCheckHost(@QueryParameter String value) {
            def validation = FormUtils.validateHost(value)
            if (validation.kind == Kind.OK) {
                validation = FormUtils.validateNotEmpty(value, Messages.Configuration_HostRequired())
            }
            return validation
        }

        /**
         * Return ListBoxModel of existing credentials
         * @param host
         * @param credentialsId
         * @param context
         * @return ListBoxModel
         */
        ListBoxModel doFillCredentialsIdItems(@QueryParameter String host,
                @QueryParameter String credentialsId, @AncestorInPath Item ancestor) {
            return FormUtils.newCredentialsItemsListBoxModel(host, credentialsId, ancestor)
        }

        /**
         * Verify the connection to the Mac machine 
         * @param host
         * @param credentialsId
         * @param context
         * @return ok if connection, ko if error
         */
        FormValidation verifyConnection(@QueryParameter String host,
                @QueryParameter String credentialsId, @AncestorInPath Item context) {
            return FormUtils.verifyCredential(host, credentialsId, context)
        }
    }
}
