package fr.jenkins.plugins.mac.utils

import static com.cloudbees.plugins.credentials.CredentialsMatchers.anyOf
import static com.cloudbees.plugins.credentials.CredentialsMatchers.instanceOf
import static com.cloudbees.plugins.credentials.domains.URIRequirementBuilder.fromUri

import javax.validation.constraints.NotNull

import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardListBoxModel
import fr.jenkins.plugins.mac.connection.SshClientFactory
import fr.jenkins.plugins.mac.connection.SshClientFactoryConfiguration
import hudson.model.Item
import hudson.model.ModelObject
import hudson.security.ACL
import hudson.util.FormValidation
import hudson.util.ListBoxModel
import jenkins.model.Jenkins

/**
 * Utilities for Jenkins UI
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
    static FormValidation validateHost(@NotNull final String host) {
        try {
            if(host) {
                InetAddress inetAddress = InetAddress.getByName(host)
            }
            return FormValidation.ok()
        } catch(UnknownHostException uhe) {
            return FormValidation.error("The given host is not valid")
        } catch(SecurityException se) {
            return FormValidation.error("Cannot validate the host due to security restriction")
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
     * @param serverUrl
     * @param credentialsId
     * @param context
     * @return FormValidation
     */
    static FormValidation verifyCredential(final String host, final String credentialsId, final ModelObject context) {
        try {
            SshClientFactory.getSshClient(
                    new SshClientFactoryConfiguration(credentialsId: credentialsId, context: context,
                    host: host))
            return FormValidation.ok("Connection succeed")
        } catch(Exception e) {
            return FormValidation.error(e.getMessage())
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
    static ListBoxModel newCredentialsItemsListBoxModel(final String host,
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
                anyOf(instanceOf(StandardCredentials)))
    }
}
