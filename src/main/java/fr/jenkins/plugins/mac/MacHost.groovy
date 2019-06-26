package fr.jenkins.plugins.mac

import javax.annotation.Nullable

import org.kohsuke.stapler.AncestorInPath
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

import fr.jenkins.plugins.mac.util.FormUtils
import hudson.Extension
import hudson.model.Describable
import hudson.model.Descriptor
import hudson.model.Item
import hudson.model.ItemGroup
import hudson.util.FormValidation
import hudson.util.FormValidation.Kind
import hudson.util.ListBoxModel
import jenkins.model.Jenkins

/**
 * Iteration of the Global configuration of mac-plugin
 * @author Mathieu DELROCQ
 *
 */
class MacHost implements Describable<MacHost> {

    String host
    String credentialsId
    Integer port
    Integer maxUsers
    Integer connectionTimeout
    Integer readTimeout
    Integer kexTimeout

    @DataBoundConstructor
    MacHost(String host, String credentialsId, Integer port, Integer maxUsers,
    Integer connectionTimeout, Integer readTimeout, Integer kexTimeout) {
        this.host = host
        this.credentialsId = credentialsId
        this.port = port
        this.maxUsers = maxUsers
        this.connectionTimeout = connectionTimeout
        this.readTimeout = readTimeout
        this.kexTimeout = kexTimeout
    }
    /**
     * Return the current instance of MacPluginConfiguration
     * @return
     */
    static @Nullable MacHost getMacHost() {
        return MacCloud.macCloud?.find { true }
    }
    static @Nullable String getHost() {
        getMacHost()?.@host
    }

    static @Nullable String getCredentialsId() {
        getMacHost()?.@credentialsId
    }

    static @Nullable Integer getPort() {
        getMacHost()?.@port
    }

    static @Nullable Integer getMaxUsers() {
        getMacHost()?.@maxUsers
    }

    static @Nullable Integer getConnectionTimeout() {
        getMacHost()?.@connectionTimeout
    }

    static @Nullable Integer getReadTimeout() {
        getMacHost()?.@readTimeout
    }

    static @Nullable Integer getKexTimeout() {
        getMacHost()?.@kexTimeout
    }

    @Override
    Descriptor<MacHost> getDescriptor() {
        return Jenkins.get().getDescriptorOrDie(this.getClass())
    }

    /**
     * Descriptor of an iteration of the global configuration
     * @see src\main\resources\fr\jenkins\plugins\mac\config\MacPluginConfiguration\config.groovy
     * @author mathieu.delrocq
     *
     */
    @Extension
    static class DescriptorImpl extends Descriptor<MacHost> {

        /**
         * Check if the value of host field is correct
         * @param value
         * @return FormValidation
         */
        FormValidation doCheckHost(@QueryParameter String value) {
            def validation = FormUtils.validateHost(value)
            if (validation.kind == Kind.OK) {
                validation = FormUtils.validateNotEmpty(value, Messages.Host_HostRequired())
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
        FormValidation doVerifyConnection(@QueryParameter String host, @QueryParameter Integer port,
                @QueryParameter String credentialsId, @QueryParameter Integer connectionTimeout,
                @QueryParameter Integer readTimeout, @QueryParameter Integer kexTimeout, @AncestorInPath Item context) {
            return FormUtils.verifyCredential(host, port, credentialsId, connectionTimeout, readTimeout, kexTimeout, context)
        }
    }
}
