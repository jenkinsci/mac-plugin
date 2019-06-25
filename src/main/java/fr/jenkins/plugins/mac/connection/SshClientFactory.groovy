package fr.jenkins.plugins.mac.connection

import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.trilead.ssh2.Connection
import com.trilead.ssh2.Session

import fr.jenkins.plugins.mac.config.MacPluginConfiguration
import fr.jenkins.plugins.mac.util.CredentialsUtils
import fr.jenkins.plugins.mac.util.FormUtils
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
    static Connection getSshClient(SshClientFactoryConfiguration conf = new SshClientFactoryConfiguration()) {
        String host = conf.host ?: MacPluginConfiguration.host
        Integer port = conf.port ?: MacPluginConfiguration.port
        Integer connectionTimeout = null != conf.connectionTimeout ? conf.connectionTimeout : MacPluginConfiguration.connectionTimeout
        Integer readTimeout = null != conf.readTimeout ? conf.readTimeout : MacPluginConfiguration.readTimeout
        Integer kexTimeout = null != conf.kexTimeout ? conf.kexTimeout : MacPluginConfiguration.kexTimeout
        def context = conf.context ?: Jenkins.get()
        def credentialsId = conf.credentialsId ?: MacPluginConfiguration.credentialsId
        def credentials = CredentialsUtils.findCredentials(FormUtils.getUri(host), credentialsId, context)
        return getClient(credentials, host, port, connectionTimeout, readTimeout, kexTimeout)
    }

    /**
     * Return the ssh client to the Mac
     * @param credentials
     * @param host
     * @param port
     * @return Shell
     */
    @Restricted(NoExternalUse)
    private static Connection getClient(final StandardCredentials credentials, final String host, final Integer port,
            final Integer connectionTimeout, final Integer readTimeout, final Integer kexTimeout) {
        Session sess = null;
        String adr = InetAddress.getByName(host).toString().split("/")[1]
        Connection conn = new Connection(adr)
        if(credentials instanceof StandardUsernamePasswordCredentials) {
            StandardUsernamePasswordCredentials usernamePasswordCredentials = credentials
            conn.connect(null, connectionTimeout.intValue(), readTimeout.intValue(), kexTimeout.intValue())
            conn.authenticateWithPassword(usernamePasswordCredentials.getUsername(), usernamePasswordCredentials.getPassword().getPlainText())
        }
        return conn
    }
}