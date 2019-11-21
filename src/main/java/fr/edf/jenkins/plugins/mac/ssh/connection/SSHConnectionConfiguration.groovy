package fr.edf.jenkins.plugins.mac.ssh.connection

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
}

class SSHGlobalConnectionConfiguration extends SSHConnectionConfiguration {
    String credentialsId
    ModelObject context
}

class SSHUserConnectionConfiguration extends SSHConnectionConfiguration {
    String username
    Secret password
}
