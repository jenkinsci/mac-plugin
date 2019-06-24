package fr.jenkins.plugins.mac.connection

import hudson.model.ModelObject

/**
 * All properties needed for SshClientFactory
 * @author Mathieu DELROCQ
 *
 */
class SshClientFactoryConfiguration {
    String host
    String credentialsId
    Integer port
    ModelObject context
    Integer connectionTimeout
    Integer readTimeout
    Integer kexTimeout
}
