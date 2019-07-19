package fr.jenkins.plugins.mac.ssh.connection

import hudson.model.ModelObject

/**
 * All properties needed for SshClientFactory
 * @author Mathieu DELROCQ
 *
 */
class SSHClientFactoryConfiguration {
    String host
    String credentialsId
    Integer port
    ModelObject context
    Integer connectionTimeout
    Integer readTimeout
    Integer kexTimeout
}
