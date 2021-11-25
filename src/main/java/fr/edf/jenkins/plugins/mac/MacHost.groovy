package fr.edf.jenkins.plugins.mac

import org.apache.commons.lang.StringUtils
import org.kohsuke.stapler.AncestorInPath
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter
import org.kohsuke.stapler.QueryParameter
import org.kohsuke.stapler.verb.POST

import fr.edf.jenkins.plugins.mac.ssh.key.verifiers.MacHostKeyVerifier
import fr.edf.jenkins.plugins.mac.util.Constants
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
    Boolean uploadKeychain = Boolean.FALSE
    String labelString
    String fileCredentialsId
    List<String> preLaunchCommandsList = new ArrayList<>()
    List<MacEnvVar> envVars = new ArrayList<>()
    MacHostKeyVerifier macHostKeyVerifier
    transient Set<LabelAtom> labelSet
    String userManagementTool

    @DataBoundConstructor
    MacHost(String host, String credentialsId, Integer port, Integer maxUsers, Integer connectionTimeout, Integer readTimeout, Integer agentConnectionTimeout,
    Boolean disabled, Integer maxTries, String labelString, Boolean uploadKeychain, String fileCredentialsId, List<MacEnvVar> envVars, String key,
    String preLaunchCommands, String userManagementTool) {
        this.host = host
        this.credentialsId = credentialsId
        this.port = port
        this.maxUsers = maxUsers
        this.connectionTimeout = connectionTimeout
        this.readTimeout = readTimeout
        this.kexTimeout = Integer.valueOf(0)
        this.agentConnectionTimeout = agentConnectionTimeout
        this.disabled = disabled
        this.maxTries = maxTries
        this.labelString = labelString
        this.envVars = envVars
        this.uploadKeychain = uploadKeychain ?: Boolean.FALSE
        this.fileCredentialsId = fileCredentialsId
        this.macHostKeyVerifier = new MacHostKeyVerifier(key)
        this.preLaunchCommandsList = buildPreLaunchCommands(preLaunchCommands)
        this.userManagementTool = userManagementTool
        labelSet = Label.parse(StringUtils.defaultIfEmpty(labelString, ""))
    }

    String getKey() {
        null != this.macHostKeyVerifier ? macHostKeyVerifier.getKey() : ""
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

    @DataBoundSetter
    void setUploadKeychain(Boolean uploadKeychain= Boolean.FALSE) {
        this.uploadKeychain = uploadKeychain
    }

    @DataBoundSetter
    void setFileCredentialsId(String fileCredentialsId) {
        this.fileCredentialsId = fileCredentialsId
    }

    String getPreLaunchCommands() {
        return String.join("\n", preLaunchCommandsList)
    }

    @DataBoundSetter
    void setPreLaunchCommands(String preLaunchCommandsString) {
        this.preLaunchCommandsList = buildPreLaunchCommands(preLaunchCommandsString)
    }

    String getUserManagementTool() {
        return userManagementTool
    }

    @DataBoundSetter
    void setUserManagementTool(String userManagementTool) {
        this.userManagementTool = userManagementTool
    }

    /**
     * Check null or empty and build an array with '\n' separator
     *
     * @param entryPointCmdString
     * @return An array of command
     */
    private List<String> buildPreLaunchCommands(String preLaunchCommandsString) {
        List<String> preLaunchCommandList = new ArrayList<>()
        if(preLaunchCommandsString?.trim()) {
            String[] cmdArray = preLaunchCommandsString.split("\\r?\\n|\\r")
            String cmd = null
            for(int i=0;i<cmdArray.length;i++) {
                cmd = cmdArray[i]
                if (cmd?.trim()) {
                    preLaunchCommandList.add(cmd)
                }
            }
        }
        return preLaunchCommandList;
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
        @POST
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
        @POST
        ListBoxModel doFillCredentialsIdItems(@QueryParameter String host,
                @QueryParameter String credentialsId, @AncestorInPath Item ancestor) {
            return FormUtils.newMacHostCredentialsItemsListBoxModel(host, credentialsId, ancestor)
        }

        /**
         * Return ListBoxModel of existing keychains
         * @param credentialsId
         * @param context
         * @return ListBoxModel
         */
        @POST
        ListBoxModel doFillFileCredentialsIdItems(@QueryParameter String fileCredentialsId, @AncestorInPath Item ancestor) {
            return FormUtils.newFileCredentialsItemsListBoxModel(fileCredentialsId, ancestor)
        }

        /**
         * Return ListBoxModel of UserManagementToolItems
         * @return ListBoxModel
         */
        @POST
        ListBoxModel doFillUserManagementToolItems() {
            ListBoxModel listbox =  new ListBoxModel()
            listbox.add(Constants.SYSADMINCTL)
            listbox.add(Constants.DSCL)
            return listbox
        }

        /**
         * Verify the connection to the Mac machine 
         * @param host
         * @param port
         * @param credentialsId
         * @param context
         * @return ok if connection, ko if error
         */
        @POST
        FormValidation doVerifyConnection(@QueryParameter String host, @QueryParameter Integer port,
                @QueryParameter String credentialsId, @QueryParameter String key, @AncestorInPath Item context) {
            return FormUtils.verifyConnection(host, port, credentialsId, key, context)
        }

        /**
         * Check the validity of the given key
         * @param key
         * @return ok if valid, error with exception message if not
         */
        @POST
        public FormValidation doCheckKey(@QueryParameter String key) {
            return FormUtils.verifyHostKey(key)
        }
    }
}
