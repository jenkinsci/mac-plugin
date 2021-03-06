package fr.edf.jenkins.plugins.mac.ssh.connection

import fr.edf.jenkins.plugins.mac.ssh.key.verifiers.MacHostKeyVerifier
import hudson.model.ModelObject
import hudson.util.Secret

/**
 * All properties needed for SshClientFactory
 * @author Mathieu DELROCQ
 *
 */
abstract class SSHConnectionConfiguration {
    String host
    Integer port
    Integer connectionTimeout
    Integer readTimeout
    Integer kexTimeout
    MacHostKeyVerifier macHostKeyVerifier
}

/**
 * Specific for global connection
 * @author Mathieu Delrocq
 *
 */
class SSHGlobalConnectionConfiguration extends SSHConnectionConfiguration {
    String credentialsId
    ModelObject context
}

/**
 * Specific for user connection
 * @author Mathieu Delrocq
 *
 */
class SSHUserConnectionConfiguration extends SSHConnectionConfiguration {
    String username
    Secret password
}
