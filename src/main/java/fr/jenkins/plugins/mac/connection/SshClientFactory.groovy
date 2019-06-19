package fr.jenkins.plugins.mac.connection

import com.cloudbees.plugins.credentials.Credentials
import com.jcabi.ssh.Shell
import com.jcabi.ssh.Ssh
import com.jcabi.ssh.SshByPassword

import fr.jenkins.plugins.mac.config.MacPluginConfiguration
import fr.jenkins.plugins.mac.utils.FormUtils
import hudson.model.ModelObject
import jenkins.model.Jenkins

/**
 * Factory of SSH Connection to a Mac
 * @author Mathieu DELROCQ
 *
 */
class SshClientFactory {
    
    static Shell getSshClient(SshClientFactoryConfiguration conf = new SshClientFactoryConfiguration()) {
        String host = conf.host ?: MacPluginConfiguration.host
        def context = conf.context ?: Jenkins.get()
        def credentialsId = conf.credentialsId ?: MacPluginConfiguration.credentialsId
    }
    
    private Shell getClient() {
        
    }
}
