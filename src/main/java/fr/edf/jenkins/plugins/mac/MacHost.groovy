package fr.edf.jenkins.plugins.mac

import org.apache.commons.lang.StringUtils
import org.kohsuke.stapler.AncestorInPath
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter
import org.kohsuke.stapler.QueryParameter

import fr.edf.jenkins.plugins.mac.util.FormUtils
import hudson.Extension
import hudson.model.Describable
import hudson.model.Descriptor
import hudson.model.Item
import hudson.model.Label
import hudson.model.labels.LabelAtom
import hudson.util.FormValidation
import hudson.util.ListBoxModel
import hudson.util.FormValidation.Kind
import jenkins.model.Jenkins

/**
 * Configuration of a Mac SSH Connection
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
    Integer agentConnectionTimeout
    Integer maxTries
    Boolean disabled
    String labelString
    List<MacEnvVar> envVars = new ArrayList()
    transient Set<LabelAtom> labelSet

    @DataBoundConstructor
    MacHost(String host, String credentialsId, Integer port, Integer maxUsers,
    Integer connectionTimeout, Integer readTimeout, Integer kexTimeout, Boolean disabled, Integer maxTries, String labelString, Integer agentConnectionTimeout, List<MacEnvVar> envVars) {
        this.host = host
        this.credentialsId = credentialsId
        this.port = port
        this.maxUsers = maxUsers
        this.connectionTimeout = connectionTimeout
        this.readTimeout = readTimeout
        this.kexTimeout = new Integer(0)
        this.agentConnectionTimeout = agentConnectionTimeout
        this.disabled = disabled
        this.maxTries = maxTries
        this.labelString = labelString
        this.envVars = envVars
        labelSet = Label.parse(StringUtils.defaultIfEmpty(labelString, ""))
    }

    @DataBoundSetter
    void setHost(String host) {
        this.host = host
    }

    @DataBoundSetter
    void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }

    @DataBoundSetter
    void setPort(Integer port) {
        this.port = port
    }

    @DataBoundSetter
    void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers
    }

    @DataBoundSetter
    void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout
    }

    @DataBoundSetter
    void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout
    }

    @DataBoundSetter
    void setKexTimeout(Integer kexTimeout) {
        this.kexTimeout = kexTimeout
    }

    @DataBoundSetter
    void setAgentConnectionTimeout(Integer agentConnectionTimeout) {
        this.agentConnectionTimeout = agentConnectionTimeout
    }

    @DataBoundSetter
    void setDisabled(Boolean disabled) {
        this.disabled = disabled
    }

    @DataBoundSetter
    void setMaxTries(Integer maxTries) {
        this.maxTries = maxTries
    }

    @DataBoundSetter
    void setLabelString(String labelString) {
        this.labelString = labelString
    }

    @DataBoundSetter
    void setEnvVars(List<MacEnvVar> envVars) {
        this.envVars = envVars
    }

    @Override
    Descriptor<MacHost> getDescriptor() {
        return Jenkins.get().getDescriptorOrDie(this.getClass())
    }

    Set<LabelAtom> getLabelSet() {
        return Label.parse(StringUtils.defaultIfEmpty(this.labelString, ""))
    }

    /**
     * Descriptor of a Mac Host for Jenkins UI
     * @see src\main\resources\fr\jenkins\plugins\mac\MacHost\config.groovy
     * @author mathieu.delrocq
     *
     */
    @Extension
    static class DescriptorImpl extends Descriptor<MacHost> {

        /**
         * {@inheritDoc}
         */
        @Override
        String getDisplayName() {
            return Messages.Host_DisplayName()
        }

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
         * @param port
         * @param credentialsId
         * @param context
         * @return ok if connection, ko if error
         */
        FormValidation doVerifyConnection(@QueryParameter String host, @QueryParameter Integer port,
                @QueryParameter String credentialsId, @AncestorInPath Item context) {
            return FormUtils.verifyCredential(host, port, credentialsId, context)
        }
    }
}
