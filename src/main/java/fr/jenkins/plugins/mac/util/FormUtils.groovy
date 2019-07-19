package fr.jenkins.plugins.mac.util

import static com.cloudbees.plugins.credentials.CredentialsMatchers.anyOf
import static com.cloudbees.plugins.credentials.CredentialsMatchers.instanceOf
import static com.cloudbees.plugins.credentials.domains.URIRequirementBuilder.fromUri
import static fr.jenkins.plugins.mac.util.Constants.WHOAMI

import org.antlr.v4.runtime.misc.NotNull
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardListBoxModel
import com.trilead.ssh2.Connection
import com.trilead.ssh2.Session

import fr.jenkins.plugins.mac.Messages
import fr.jenkins.plugins.mac.ssh.SSHCommander
import fr.jenkins.plugins.mac.ssh.connection.SSHClientFactory
import fr.jenkins.plugins.mac.ssh.connection.SSHClientFactoryConfiguration
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
    static FormValidation validateHost(@NotNull final String host) {
        try {
            if(host) {
                InetAddress inetAddress = InetAddress.getByName(host)
            }
            return FormValidation.ok()
        } catch(UnknownHostException uhe) {
            return FormValidation.error(Messages._Host_HostInvalid().toString())
        } catch(SecurityException se) {
            return FormValidation.error(Messages._Host_SecurityRestriction().toString())
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
    @Restricted(NoExternalUse)
    static FormValidation verifyCredential(final String host, final Integer port,
            final String credentialsId, final Integer connectionTimeout,
            final Integer readTimeout, final Integer kexTimeout, final ModelObject context) {
        Connection connection = null
        try {
            connection = SSHClientFactory.getSshClient(
                    new SSHClientFactoryConfiguration(credentialsId: credentialsId, port: port,
                    context: context, host: host, connectionTimeout: connectionTimeout,
                    readTimeout: readTimeout, kexTimeout: kexTimeout))
            String result = SSHCommander.checkConnection(connection)
            connection.close()
            return FormValidation.ok(Messages._Host_ConnectionSucceeded(result).toString())
        } catch(Exception e) {
            if(null != connection) connection.close()
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
    
    /**
     * Return ListBoxModel with filled item and empty option
     * @param nameSelector
     * @param valueSelector
     * @param items
     * @return ListBoxModel
     */
    static ListBoxModel newListBoxModel(Closure<String> nameSelector, Closure<String> valueSelector, List items) {
        def listBoxModel = newListBoxModelWithEmptyOption()
        items.each { item ->
            listBoxModel.add(nameSelector(item), valueSelector(item))
        }
        return listBoxModel
    }

    /**
     * Return an empty ListBoxModel with empty option
     * @param nameSelector
     * @param valueSelector
     * @param items
     * @return ListBoxModel
     */
    static ListBoxModel newListBoxModelWithEmptyOption() {
        def listBoxModel = new ListBoxModel()
        listBoxModel.add(Constants.EMPTY_LIST_BOX_NAME, Constants.EMPTY_LIST_BOX_VALUE)
        return listBoxModel
    }
}
