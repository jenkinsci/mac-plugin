package fr.edf.jenkins.plugins.mac.ssh.connection

import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.trilead.ssh2.Connection

import fr.edf.jenkins.plugins.mac.ssh.verifiers.MacHostKeyVerifier
import fr.edf.jenkins.plugins.mac.ssh.verifiers.NonVerifyingHostKeyVerifier
import fr.edf.jenkins.plugins.mac.util.CredentialsUtils
import jenkins.model.Jenkins

/**
 * Factory of SSH Connection
 * @author Mathieu DELROCQ
 *
 */
class SSHConnectionFactory {

    /**
     * Dispatch request on the good method of connection
     * @param conf
     * @return com.trilead.ssh2.Connection
     */
    static Connection getSshConnection(SSHConnectionConfiguration conf) {
        if(conf instanceof SSHGlobalConnectionConfiguration) {
            return getGlobalSshConnection(conf)
        }
        if(conf instanceof SSHUserConnectionConfiguration) {
            return getUserSshConnection(conf)
        }
        return null
    }

    /**
     * Get the SSH client to the Mac for global user
     * @param conf
     * @return com.trilead.ssh2.Connection
     */
    @Restricted(NoExternalUse)
    private static Connection getGlobalSshConnection(SSHGlobalConnectionConfiguration conf = new SSHGlobalConnectionConfiguration()) {
        String host = conf.host
        Integer port = conf.port ?: new Integer(22)
        Integer connectionTimeout = conf.connectionTimeout ?: new Integer(5)
        Integer readTimeout = conf.readTimeout ?: new Integer(60)
        Integer kexTimeout = conf.kexTimeout ?: new Integer(0)
        def context = conf.context ?: Jenkins.get()
        def credentialsId = conf.credentialsId ?: null
        MacHostKeyVerifier hostKeyVerifier = conf.hostKeyVerifier ?: new NonVerifyingHostKeyVerifier()
        def credentials = CredentialsUtils.findCredentials(host, credentialsId, context)
        return getConnection(credentials, host, port, connectionTimeout, readTimeout, kexTimeout, hostKeyVerifier)
    }

    /**
     * Generate a transient credential with the given user and password and return a connection
     * @param conf
     * @return com.trilead.ssh2.Connection
     */
    @Restricted(NoExternalUse)
    private static Connection getUserSshConnection(SSHUserConnectionConfiguration conf) {
        String host = conf.host
        Integer port = conf.port ?: new Integer(22)
        Integer connectionTimeout = conf.connectionTimeout ?: new Integer(0)
        Integer readTimeout = conf.readTimeout ?: new Integer(0)
        Integer kexTimeout = conf.kexTimeout ?: new Integer(0)
        MacHostKeyVerifier hostKeyVerifier = conf.hostKeyVerifier ?: new NonVerifyingHostKeyVerifier()
        UsernamePasswordCredentials credentials =  new UsernamePasswordCredentialsImpl(
                CredentialsScope.SYSTEM,
                "global",
                null,
                conf.username,
                conf.password.getPlainText()
                );
        return getConnection(credentials, host, port, connectionTimeout, readTimeout, kexTimeout, hostKeyVerifier)
    }

    /**
     * Return the ssh client to the Mac
     * @param credentials
     * @param host
     * @param port
     * @param connectionTimeout
     * @param readTimeout
     * @param kexTimeout
     * @return com.trilead.ssh2.Connection
     */
    @Restricted(NoExternalUse)
    private static Connection getConnection(final StandardCredentials credentials, final String host, final Integer port,
            final Integer connectionTimeout, final Integer readTimeout, final Integer kexTimeout, MacHostKeyVerifier hostKeyVerifier) {
        String adr = InetAddress.getByName(host).toString().split("/")[1]
        Connection conn = new Connection(adr, port.intValue())
        if(credentials instanceof StandardUsernamePasswordCredentials) {
            StandardUsernamePasswordCredentials usernamePasswordCredentials = credentials
            conn.connect(hostKeyVerifier, connectionTimeout.multiply(1000).intValue(), readTimeout.multiply(1000).intValue(), kexTimeout.multiply(1000).intValue())
            conn.authenticateWithPassword(usernamePasswordCredentials.getUsername(), usernamePasswordCredentials.getPassword().getPlainText())
        }
        return conn
    }
}