package fr.jenkins.plugins.mac.connection

import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.jcabi.ssh.Shell
import com.jcabi.ssh.Ssh
import com.jcabi.ssh.SshByPassword

import fr.jenkins.plugins.mac.config.MacPluginConfiguration
import fr.jenkins.plugins.mac.utils.CredentialsUtils
import fr.jenkins.plugins.mac.utils.FormUtils
import hudson.model.ModelObject
import jenkins.model.Jenkins

/**
 * Factory of SSH Connection to a Mac
 * @author Mathieu DELROCQ
 *
 */
class SshClientFactory {

    /**
     * Get the SSH client to the Mac
     * @param conf
     * @return Shell
     */
    @Restricted(NoExternalUse)
    static Shell getSshClient(SshClientFactoryConfiguration conf = new SshClientFactoryConfiguration()) {
        String host = conf.host ?: MacPluginConfiguration.host
        Integer port = conf.port ?: MacPluginConfiguration.port
        def context = conf.context ?: Jenkins.get()
        def credentialsId = conf.credentialsId ?: MacPluginConfiguration.credentialsId
        def credentials = CredentialsUtils.findCredentials(FormUtils.getUri(host), credentialsId, context)
        return getClient(credentials, host, port)
    }

    /**
     * Return the ssh client to the Mac
     * @param credentials
     * @param host
     * @param port
     * @return Shell
     */
    @Restricted(NoExternalUse)
    private static Shell getClient(StandardCredentials credentials, String host, Integer port) {
        Shell shell = null
        def adr = InetAddress.getByName(host).toString().split(" / ")[1]
        if(credentials instanceof StandardUsernamePasswordCredentials) {
            StandardUsernamePasswordCredentials usernamePasswordCredentials = credentials
            shell = new SshByPassword(adr, port.intValue(),
                    usernamePasswordCredentials.getUsername(), usernamePasswordCredentials.getPassword().getPlainText())
        }
        return shell
    }
}