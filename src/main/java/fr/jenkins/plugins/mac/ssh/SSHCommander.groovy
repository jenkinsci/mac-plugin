package fr.jenkins.plugins.mac.ssh

import org.antlr.v4.runtime.misc.NotNull
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.trilead.ssh2.ChannelCondition
import com.trilead.ssh2.Connection
import com.trilead.ssh2.Session

import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.connector.MacComputerJNLPConnector
import fr.jenkins.plugins.mac.ssh.connection.SshClientFactory
import fr.jenkins.plugins.mac.ssh.connection.SshClientFactoryConfiguration
import fr.jenkins.plugins.mac.util.Constants
import groovy.util.logging.Slf4j
import hudson.slaves.ComputerLauncher
import hudson.util.Secret
import jenkins.model.Jenkins

/**
 * Contains all availables methods to execute ssh command for the Mac plugin
 * @author Mathieu DELROCQ
 *
 */
@Slf4j
class SSHCommander {

    
    /**
     * Check the given connection to ssh with the command whoami
     * @param connection
     * @return the user connected, or null if error
     */
    @Restricted(NoExternalUse)
    static String checkConnection(Connection connection) {
        return SSHCommandLauncher.executeCommand(connection, false,  Constants.WHOAMI)
    }

    /**
     * Create an user with the command sysadminctl
     * @param macHost
     * @return a MacUser
     */
    @Restricted(NoExternalUse)
    static MacUser createUserOnMac(MacHost macHost) {
        Connection connection = SshClientFactory.getSshClient(new SshClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
            context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
            readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
        MacUser user = generateUser()
        log.info(SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.CREATE_USER, user.username, user.password)))
        connection.close()
        return user
    }

    /**
     * Delete the given user in parameter
     * @param cloudId
     * @param username
     * @return true if user is deleted, false if an error occured
     */
    @Restricted(NoExternalUse)
    static boolean deleteUserOnMac(String cloudId, String username) {
        MacCloud cloud = Jenkins.get().getCloud(cloudId)
        MacHost macHost = cloud.getMacHost()
        try {
            Connection connection = SshClientFactory.getSshClient(new SshClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
                context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
                readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
            log.info(SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.DELETE_USER, username)))
            connection.close()
            return true
        } catch (Exception e) {
            log.error("Error while deleting user " + username + " on mac " + macHost.host)
            return false
        }
    }

    /**
     * Get the slave.jar on Jnekins and connect the slave to JNLP
     * @param macHost
     * @param user
     * @param jnlpConnector
     * @param slaveSecret
     * @return true if connection succeed, false otherwise
     */
    @Restricted(NoExternalUse)
    static boolean jnlpConnect(MacHost macHost, MacUser user, MacComputerJNLPConnector jnlpConnector, String slaveSecret) {
        String jenkinsUrl = StringUtils.isNotEmpty(jnlpConnector.jenkinsUrl) ? jnlpConnector.jenkinsUrl : Jenkins.get().getRootUrl()
        if(!jenkinsUrl.endsWith("/")) {
            jenkinsUrl += "/"
        }
        String remotingUrl = jenkinsUrl + Constants.REMOTING_JAR_PATH
        try {
            Connection connection = SshClientFactory.getUserConnection(user.username, user.password, macHost.host,
                macHost.port, macHost.connectionTimeout, macHost.readTimeout, macHost.kexTimeout)
            log.info(SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.GET_REMOTING_JAR, remotingUrl)))
            log.info(SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.LAUNCH_JNLP, jenkinsUrl, user.username, slaveSecret)))
            connection.close()
            return true
        } catch(Exception e) {
            log.error("Cannot connect Mac " + macHost.host + " with user " + user.username + " to jenkins with JNLP")
            return false
        }
    }

    /**
     * Generate a Mac user with the pattern in Constants
     * @return a MacUser
     */
    @Restricted(NoExternalUse)
    private static MacUser generateUser() {
        String password = RandomStringUtils.random(10, true, true);
        String username = String.format(Constants.USERNAME_PATTERN, RandomStringUtils.random(5, true, true).toLowerCase())
        String workdir = String.format("/Users/%s/", username)
        return new MacUser(username:username, password:Secret.fromString(password), workdir:workdir)
    }
}