package fr.edf.jenkins.plugins.mac.ssh.connection

import org.antlr.v4.runtime.misc.NotNull
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.trilead.ssh2.Connection
import com.trilead.ssh2.Session

import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.util.CredentialsUtils
import fr.edf.jenkins.plugins.mac.util.FormUtils
import hudson.util.Secret
import jenkins.model.Jenkins

/**
 * Factory of SSH Connection
 * @author Mathieu DELROCQ
 *
 */
class SSHClientFactory {

    /**
     * Get the SSH client to the Mac
     * @param conf
     * @return com.trilead.ssh2.Connection
     */
    @Restricted(NoExternalUse)
    static Connection getSshClient(SshClientFactoryConfiguration conf = new SshClientFactoryConfiguration()) {
        String host = conf.host
        Integer port = conf.port ?: new Integer(22)
        Integer connectionTimeout = conf.connectionTimeout ?: new Integer(0)
        Integer readTimeout = conf.readTimeout ?: new Integer(0)
        Integer kexTimeout = conf.kexTimeout ?: new Integer(0)
        def context = conf.context ?: Jenkins.get()
        def credentialsId = conf.credentialsId ?: null
        def credentials = CredentialsUtils.findCredentials(host, credentialsId, context)
        return getClient(credentials, host, port, connectionTimeout, readTimeout, kexTimeout)
    }

    /**
     * Generate a transient credential with the given user and password and return a connection
     * @param username
     * @param password
     * @param host
     * @param port
     * @param connectionTimeout
     * @param readTimeout
     * @param kexTimeout
     * @return com.trilead.ssh2.Connection
     */
    static Connection getUserClient(@NotNull final String username, @NotNull final Secret password, @NotNull final String host,
            Integer port, Integer connectionTimeout, Integer readTimeout, Integer kexTimeout) {
        port = port ?: new Integer(22)
        connectionTimeout = connectionTimeout ?: new Integer(0)
        readTimeout = readTimeout ?: new Integer(0)
        kexTimeout = kexTimeout ?: new Integer(0)
        UsernamePasswordCredentials credentials =  new UsernamePasswordCredentialsImpl(
                CredentialsScope.SYSTEM,
                "global",
                null,
                username,
                password.getPlainText()
                );
        return getClient(credentials, host, port, connectionTimeout, readTimeout, kexTimeout)
    }

    /**
     * Return the ssh client to the Mac
     * @param credentials
     * @param host
     * @param port
     * @return com.trilead.ssh2.Connection
     */
    @Restricted(NoExternalUse)
    private static Connection getClient(final StandardCredentials credentials, final String host, final Integer port,
            final Integer connectionTimeout, final Integer readTimeout, final Integer kexTimeout) {
        Session sess = null;
        String adr = InetAddress.getByName(host).toString().split("/")[1]
        Connection conn = new Connection(adr)
        if(credentials instanceof StandardUsernamePasswordCredentials) {
            StandardUsernamePasswordCredentials usernamePasswordCredentials = credentials
            conn.connect(null, connectionTimeout.multiply(1000).intValue(), readTimeout.multiply(1000).intValue(), kexTimeout.multiply(1000).intValue())
            conn.authenticateWithPassword(usernamePasswordCredentials.getUsername(), usernamePasswordCredentials.getPassword().getPlainText())
        }
        return conn
    }
}