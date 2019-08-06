package fr.jenkins.plugins.mac.ssh

import java.util.logging.Level
import java.util.logging.Logger

import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.trilead.ssh2.Connection

import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.ssh.connection.SSHClientFactory
import fr.jenkins.plugins.mac.ssh.connection.SSHClientFactoryConfiguration
import fr.jenkins.plugins.mac.util.Constants
import hudson.util.Secret
import jenkins.model.Jenkins

/**
 * Contains all availables methods to execute ssh command for the Mac plugin
 * @author Mathieu DELROCQ
 *
 */
class SSHCommand {

    private static final Logger LOGGER = Logger.getLogger(SSHCommand.name)

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
    static MacUser createUserOnMac(String label, MacHost macHost) throws Exception {
        try {
            Connection connection = SSHClientFactory.getSshClient(new SSHClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
            context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
            readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
            MacUser user = generateUser(label)
            String result = SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.CREATE_USER, user.username, user.password))
            LOGGER.log(Level.FINE, result)
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
    static void deleteUserOnMac(String username, MacHost macHost) throws Exception {
        try {
            Connection connection = SSHClientFactory.getSshClient(new SSHClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
            context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
            readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.DELETE_USER, username)))
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
    static boolean jnlpConnect(MacHost macHost, MacUser user, String jenkinsUrl, String slaveSecret) throws SSHCommandException {
        jenkinsUrl = StringUtils.isNotEmpty(jenkinsUrl) ? jenkinsUrl : Jenkins.get().getRootUrl()
        if(!jenkinsUrl.endsWith("/")) {
            jenkinsUrl += "/"
        }
        String remotingUrl = jenkinsUrl + Constants.REMOTING_JAR_PATH
        try {
            Connection connection = SSHClientFactory.getUserClient(user.username, user.password, macHost.host,
                    macHost.port, macHost.connectionTimeout, macHost.readTimeout, macHost.kexTimeout)
            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.GET_REMOTING_JAR, remotingUrl)))
            String result = SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.LAUNCH_JNLP, jenkinsUrl, user.username, slaveSecret))
            LOGGER.log(Level.FINE, result)
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
    private static MacUser generateUser(String label) throws Exception {
        String password = RandomStringUtils.random(10, true, true);
        String username = String.format(Constants.USERNAME_PATTERN, label, RandomStringUtils.random(5, true, true).toLowerCase())
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

    /**
     * List all users on a mac host for a label
     * @param macHost
     * @param label
     * @return true if exist, false if not
     */
    @Restricted(NoExternalUse)
    static List<String> listLabelUsers(MacHost macHost, String label) throws SSHCommandException{
        try {
            Connection connection = SSHClientFactory.getSshClient(new SSHClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
            context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
            readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
            String result = SSHCommandLauncher.executeCommand(connection, true, String.format(Constants.LIST_USERS, label+"_jenkins_"))
            LOGGER.log(Level.FINE, result)
            if(result.contains("Executed command")) return new ArrayList()
            return result.split(Constants.REGEX_NEW_LINE) as List
        } catch(Exception e) {
            String message = String.format(SSHCommandException.LIST_USERS_ERROR_MESSAGE, macHost.host)
            LOGGER.log(Level.SEVERE, message, e)
            throw new SSHCommandException(message, e)
        }
    }
}