package fr.edf.jenkins.plugins.mac.util

import static com.cloudbees.plugins.credentials.CredentialsMatchers.anyOf
import static com.cloudbees.plugins.credentials.CredentialsMatchers.instanceOf
import static com.cloudbees.plugins.credentials.domains.URIRequirementBuilder.fromUri

import org.acegisecurity.AccessDeniedException
import org.antlr.v4.runtime.misc.NotNull
import org.jenkinsci.plugins.plaincredentials.FileCredentials
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardListBoxModel
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials

import fr.edf.jenkins.plugins.mac.Messages
import fr.edf.jenkins.plugins.mac.ssh.SSHCommand
import fr.edf.jenkins.plugins.mac.ssh.connection.SSHGlobalConnectionConfiguration
import fr.edf.jenkins.plugins.mac.ssh.key.verifiers.MacHostKeyVerifier
import fr.edf.jenkins.plugins.mac.ssh.key.verifiers.MacHostKeyVerifierException
import hudson.model.Item
import hudson.model.ModelObject
import hudson.security.ACL
import hudson.util.FormValidation
import hudson.util.ListBoxModel
import jenkins.model.Jenkins

/**
 * Utilities for Jenkins UI Forms
 * @author Mathieu DELROCQ
 *
 */
class FormUtils {
    /**
     * Transform the host value to an URI
     * @param String host
     * @return URI
     */
    static URI getUri(@NotNull String host) {
        if (!(host.startsWith("http://") || host.startsWith("https://"))) {
            host = "http://" + host
        }
        if (!host.endsWith("/")) {
            host += "/"
        }
        try {
            return new URI(host)
        } catch(Exception e) {
            return null
        }
    }

    /**
     * Validate the given host
     * @see InetAddress.getByName()
     * @param host
     * @return FormValidation
     */
    @Restricted(NoExternalUse)
    static FormValidation validateHost(@NotNull final String host) {
        try {
            Jenkins.get().checkPermission(Jenkins.ADMINISTER)
            if(host) {
                InetAddress.getByName(host)
            }
            return FormValidation.ok()
        } catch(UnknownHostException uhe) {
            return FormValidation.error(Messages._Host_HostInvalid().toString())
        } catch(SecurityException se) {
            return FormValidation.error(Messages._Host_SecurityRestriction().toString())
        } catch(AccessDeniedException ex) {
            return FormValidation.error(ex.getMessage())
        }
    }

    /**
     * Return an error message if the input value is empty
     * @param value
     * @param error
     * @return
     */
    static FormValidation validateNotEmpty(@NotNull final String value, @NotNull final String error) {
        if (!value) {
            return FormValidation.error(error)
        }
        return FormValidation.ok()
    }

    /**
     * Return FormValidation to verify the connection to GitLab with the given url and credentialsId
     * @param host
     * @param port
     * @param credentialsId
     * @param context
     * @return FormValidation
     */
    @Restricted(NoExternalUse)
    static FormValidation verifyConnection(final String host, final Integer port,
            final String credentialsId, final String key, final ModelObject context) {
        try {
            Jenkins.get().checkPermission(Jenkins.ADMINISTER)
            MacHostKeyVerifier verifier = new MacHostKeyVerifier(key)
            String result = SSHCommand.checkConnection(new SSHGlobalConnectionConfiguration(credentialsId: credentialsId, port: port,
            context: context, host: host, connectionTimeout: 30,
            readTimeout: 30, kexTimeout: 0, macHostKeyVerifier: verifier))
            return FormValidation.ok(Messages._Host_ConnectionSucceeded(result).toString())
        } catch(Exception e) {
            return FormValidation.error(Messages._Host_ConnectionFailed(e.getMessage()).toString())
        }
    }

    /**
     * Return a ListBoxModel with credentials accesibles by ancestor
     * @param host
     * @param credentialsId
     * @param item
     * @return ListBoxModel
     */
    @Restricted(NoExternalUse)
    static ListBoxModel newMacHostCredentialsItemsListBoxModel(final String host,
            final String credentialsId,
            final Item ancestor) {
        // Ref: https://github.com/jenkinsci/credentials-plugin/blob/master/docs/consumer.adoc
        boolean noContextNotAdmin = ancestor == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER)
        boolean contextNoPerm = ancestor != null && !ancestor.hasPermission(Item.EXTENDED_READ) &&
                !ancestor.hasPermission(CredentialsProvider.USE_ITEM)

        if (noContextNotAdmin || contextNoPerm) {
            return new StandardListBoxModel().includeCurrentValue(credentialsId)
        }
        //noinspection GroovyAssignabilityCheck
        return new StandardListBoxModel()
                .includeEmptyValue()
                .includeMatchingAs(ACL.SYSTEM,
                ancestor ?: Jenkins.get(),
                StandardCredentials,
                fromUri(getUri(host).toString()).build(),
                anyOf(instanceOf(StandardUsernamePasswordCredentials)))
    }

    /**
     * Return a ListBoxModel with credentials accesibles by ancestor
     * @param host
     * @param credentialsId
     * @param item
     * @return ListBoxModel
     */
    @Restricted(NoExternalUse)
    static ListBoxModel newFileCredentialsItemsListBoxModel(final String keychainFileId,
            final Item ancestor) {
        // Ref: https://github.com/jenkinsci/credentials-plugin/blob/master/docs/consumer.adoc
        boolean noContextNotAdmin = ancestor == null && !Jenkins.get().hasPermission(Jenkins.ADMINISTER)
        boolean contextNoPerm = ancestor != null && !ancestor.hasPermission(Item.EXTENDED_READ) &&
                !ancestor.hasPermission(CredentialsProvider.USE_ITEM)

        if (noContextNotAdmin || contextNoPerm) {
            return new StandardListBoxModel().includeCurrentValue(keychainFileId)
        }
        //noinspection GroovyAssignabilityCheck
        return new StandardListBoxModel()
                .includeEmptyValue()
                .includeMatchingAs(ACL.SYSTEM,
                ancestor ?: Jenkins.get(),
                FileCredentials,
                fromUri(getUri(Jenkins.get().getRootUrl()).toString()).build(),
                anyOf(instanceOf(FileCredentials)))
    }

    /**
     * Check the validity of the given key
     * @param key
     * @return ok if valid, error with exception message if not
     */
    @Restricted(NoExternalUse)
    static FormValidation verifyHostKey(String key) {
        try {
            Jenkins.get().checkPermission(Jenkins.ADMINISTER)
            MacHostKeyVerifier.parseKey(key)
            return FormValidation.ok()
        } catch (MacHostKeyVerifierException|IllegalArgumentException|AccessDeniedException ex) {
            return FormValidation.error(ex.getMessage())
        }
    }
}
