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
import fr.jenkins.plugins.mac.ssh.connection.SSHClientFactory
import fr.jenkins.plugins.mac.ssh.connection.SSHClientFactoryConfiguration
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
class SSHCommand {

    
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
    static MacUser createUserOnMac(MacHost macHost) throws Exception {
        try {
            Connection connection = SSHClientFactory.getSshClient(new SSHClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
                context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
                readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
            MacUser user = generateUser()
            log.info(SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.CREATE_USER, user.username, user.password)))
            if(!isUserExist(connection, user.username))
                throw new Exception(String.format("The user %s wasn't created after verification", user.username))
            connection.close()
            return user
        } catch(Exception e) {
            final String message = String.format(SSHCommandException.CREATE_MAC_USER_ERROR_MESSAGE, macHost.host)
            throw new SSHCommandException(message, e)
        }
    }

    /**
     * Delete the given user in parameter
     * @param cloudId
     * @param username
     * @return true if user is deleted, false if an error occured
     */
    @Restricted(NoExternalUse)
    static void deleteUserOnMac(String cloudId, String username) throws Exception {
        MacCloud cloud = Jenkins.get().getCloud(cloudId)
        MacHost macHost = cloud.getMacHost()
        try {
            Connection connection = SSHClientFactory.getSshClient(new SSHClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
                context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
                readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
            log.info(SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.DELETE_USER, username)))
            if(isUserExist(connection, username))
                throw new Exception(String.format("The user %s still exist after verification", username))
            connection.close()
        } catch (Exception e) {
            final String message = String.format(SSHCommandException.DELETE_MAC_USER_ERROR_MESSAGE, username, macHost.host)
            throw new SSHCommandException(message, e)
        }
    }

    /**
     * Get the slave.jar on Jenkins and connect the slave to JNLP
     * @param macHost
     * @param user
     * @param jnlpConnector
     * @param slaveSecret
     * @return true if connection succeed, false otherwise
     */
    @Restricted(NoExternalUse)
    static boolean jnlpConnect(MacHost macHost, MacUser user, String jenkinsUrl, String slaveSecret) throws Exception {
        jenkinsUrl = StringUtils.isNotEmpty(jenkinsUrl) ? jenkinsUrl : Jenkins.get().getRootUrl()
        if(!jenkinsUrl.endsWith("/")) {
            jenkinsUrl += "/"
        }
        String remotingUrl = jenkinsUrl + Constants.REMOTING_JAR_PATH
        try {
            Connection connection = SSHClientFactory.getUserClient(user.username, user.password, macHost.host,
                macHost.port, macHost.connectionTimeout, macHost.readTimeout, macHost.kexTimeout)
            log.info(SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.GET_REMOTING_JAR, remotingUrl)))
            String result = SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.LAUNCH_JNLP, jenkinsUrl, user.username, slaveSecret))
            log.info(result)
            connection.close()
            return true
        } catch(Exception e) {
            final String message = String.format(SSHCommandException.JNLP_CONNECTION_ERROR_MESSAGE, macHost.host, user.username)
            throw new SSHCommandException(message, e)
        }
    }
    
    /**
     * Generate a Mac user with the pattern in Constants
     * @return a MacUser
     */
    @Restricted(NoExternalUse)
    private static MacUser generateUser() throws Exception {
        String password = RandomStringUtils.random(10, true, true);
        String username = String.format(Constants.USERNAME_PATTERN, RandomStringUtils.random(5, true, true).toLowerCase())
        String workdir = String.format(Constants.WORKDIR_PATTERN, username)
        return new MacUser(username:username, password:Secret.fromString(password), workdir:workdir)
    }

    /**
     * Verify if an user exist on the Mac
     * @param connection
     * @param username
     * @return true if exist, false if not
     */
    @Restricted(NoExternalUse)
    private static boolean isUserExist(Connection connection, String username) {
        String result = SSHCommandLauncher.executeCommand(connection, true, String.format(Constants.CHECK_USER_EXIST, username))
        return result.trim() == username
    }
}