package fr.edf.jenkins.plugins.mac.util

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull

import org.jenkinsci.plugins.plaincredentials.FileCredentials

import com.cloudbees.plugins.credentials.CredentialsMatchers
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder

import hudson.model.ModelObject
import hudson.security.ACL

/**
 * Utilities for credentials
 * @author Mathieu DELROCQ
 */
class CredentialsUtils {

    /**
     * Retrieve credentials with domain, id and context
     * @param domain
     * @param credentialsId
     * @param context
     * @return StandardCredentials
     */
    static StandardCredentials findCredentials(String host, String credentialsId, ModelObject context) {
        checkNotNull(host)
        URI domain = FormUtils.getUri(host)
        checkNotNull(credentialsId)
        checkNotNull(context)
        checkNotNull(domain)
        checkArgument(!credentialsId.isEmpty())

        List<StandardCredentials> lookupCredentials = CredentialsProvider.lookupCredentials(
                StandardCredentials,
                context,
                ACL.SYSTEM,
                URIRequirementBuilder.fromUri(domain.toString()).build())

        def credentials = CredentialsMatchers.firstOrNull(lookupCredentials, CredentialsMatchers.withId(credentialsId))
        checkArgument(credentials != null)
        return credentials
    }
    
    /**
     * Retrieve keychain credentials with id
     * @param credentialsId
     * @param context
     * @return StandardCredentials
     */
    static FileCredentials findFileCredentials(String credentialsId, ModelObject context) {
        checkNotNull(credentialsId)
        checkNotNull(context)
        checkArgument(!credentialsId.isEmpty())

        List<FileCredentials> lookupCredentials = CredentialsProvider.lookupCredentials(
                FileCredentials,
                context,
                ACL.SYSTEM,
                null)

        def credentials = CredentialsMatchers.firstOrNull(lookupCredentials, CredentialsMatchers.withId(credentialsId))
        checkArgument(credentials != null)
        return credentials
    }
}
