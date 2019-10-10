package fr.edf.jenkins.plugins.mac.ssh

import java.util.logging.Level
import java.util.logging.Logger

import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.trilead.ssh2.Connection

import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.ssh.connection.SSHClientFactory
import fr.edf.jenkins.plugins.mac.ssh.connection.SSHClientFactoryConfiguration
import fr.edf.jenkins.plugins.mac.util.Constants
import hudson.util.Secret
import jenkins.model.Jenkins

/**
 * Contains all availables methods to execute ssh command for the Mac plugin
 * @author Mathieu DELROCQ
 *
 */
class SSHCommand {

    static final Logger LOGGER = Logger.getLogger(SSHCommand.name)

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
    static MacUser createUserOnMac(MacHost macHost, MacUser user) throws Exception {
        Connection connection = null
        try {
            connection = SSHClientFactory.getSshClient(new SSHClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
            context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
            readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
//            String groupname = user.username
            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, true, String.format(Constants.CREATE_USER, user.username, user.password)))
            Thread.sleep(5000)
            if(!isUserExist(connection, user.username)) {
                throw new Exception(String.format("The user %s wasn't created after verification", user.username))
            }
//            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.CREATE_GROUP, groupname)))
//            if(!isGroupExist(connection, groupname)) {
//                throw new Exception(String.format("The group %s wasn't created after verification", user.username))
//            }
//            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.ADD_USER_TO_GROUP, user.username, groupname)))
//            if(!isUserAssignedToGroup(connection, user.username, groupname)) {
//                throw new Exception(String.format("The user %s wasn't assigned to the group %s after verification", user.username, groupname))
//            }
//            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.ASSIGN_USER_FOLDER_TO_GROUP, user.username, groupname, user.username)))
            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, true, String.format(Constants.CHANGE_RIGHTS_ON_USER, user.username)))
            LOGGER.log(Level.FINE, "The User {0} has been CREATED on Mac {1}", user.username, macHost.host)
            connection.close()
            return user
        } catch(Exception e) {
            if(null != connection) connection.close()
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
        Connection connection = null
        String groupname = username
        try {
            connection = SSHClientFactory.getSshClient(new SSHClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
            context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
            readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
//            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.REMOVE_USER_FROM_GROUP, username, groupname)))
//            if(isUserAssignedToGroup(connection, username, groupname)) {
//                LOGGER.log(Level.WARNING, String.format("The user %s is still assigned to the group %s after verification", username, groupname))
//            }
            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, true, String.format(Constants.DELETE_USER, username)))
            Thread.sleep(5000)
            if(isUserExist(connection, username)) {
                throw new Exception(String.format("The user %s still exist after verification", username))
            }
//            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.DELETE_GROUP, groupname)))
//            if(isGroupExist(connection, groupname)) {
//                throw new Exception(String.format("The group %s still exist after verification", groupname))
//            }
            LOGGER.log(Level.FINE, "The User {0} has been DELETED from Mac {1}", username, macHost.host)
            connection.close()
        } catch (Exception e) {
            if(null != connection) connection.close()
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
        Connection connection = null
        try {
            connection = SSHClientFactory.getUserClient(user.username, user.password, macHost.host,
                    macHost.port, macHost.connectionTimeout, macHost.readTimeout, macHost.kexTimeout)
            LOGGER.log(Level.FINE, SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.GET_REMOTING_JAR, remotingUrl)))
            String result = SSHCommandLauncher.executeCommand(connection, false, String.format(Constants.LAUNCH_JNLP, jenkinsUrl, user.username, slaveSecret))
            LOGGER.log(Level.FINE, result)
            connection.close()
            return true
        } catch(Exception e) {
            if(null != connection) connection.close()
            final String message = String.format(SSHCommandException.JNLP_CONNECTION_ERROR_MESSAGE, macHost.host, user.username)
            LOGGER.log(Level.WARNING, message)
            LOGGER.log(Level.FINEST, "Exception : ", e)
            throw new SSHCommandException(message, e)
        }
    }

    /**
     * Generate a Mac user with the pattern in Constants
     * @return a MacUser
     */
    @Restricted(NoExternalUse)
    static MacUser generateUser() throws Exception {
        String password = RandomStringUtils.random(10, true, true);
        String username = String.format(Constants.USERNAME_PATTERN, RandomStringUtils.random(10, true, true).toLowerCase())
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

//    /**
//     * Verify if a group exist on the Mac
//     * @param connection
//     * @param groupname
//     * @return true if exist, false if not
//     */
//    @Restricted(NoExternalUse)
//    private static boolean isGroupExist(Connection connection, String groupname) {
//        String result = SSHCommandLauncher.executeCommand(connection, true, String.format(Constants.CHECK_GROUP_EXIST, groupname))
//        return result.contains(groupname)
//    }

//    /**
//     * Verify if an user was assigned to a group
//     * @param connection
//     * @param username
//     * @param groupname
//     * @return true if assigned, false if not
//     */
//    @Restricted(NoExternalUse)
//    private static boolean isUserAssignedToGroup(Connection connection, String username, String groupname) {
//        String result = SSHCommandLauncher.executeCommand(connection, true, String.format(Constants.CHECK_USER_ADDED_TO_GROUP, username, groupname))
//        return result.contains("yes") && result.contains("is a member of")
//    }

    /**
     * List all users on a mac host for a label
     * @param macHost
     * @param label
     * @return true if exist, false if not
     */
    @Restricted(NoExternalUse)
    static List<String> listUsers(MacHost macHost) throws SSHCommandException {
        Connection connection = null
        try {
            connection = SSHClientFactory.getSshClient(new SSHClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
            context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
            readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
            String result = SSHCommandLauncher.executeCommand(connection, true, String.format(Constants.LIST_USERS, Constants.USERNAME_PATTERN.substring(0, Constants.USERNAME_PATTERN.lastIndexOf("%"))))
            LOGGER.log(Level.FINE, result)
            connection.close()
            if(StringUtils.isEmpty(result)) return new ArrayList()
            return result.split(Constants.REGEX_NEW_LINE) as List
        } catch(Exception e) {
            if(null != connection) connection.close()
            String message = String.format(SSHCommandException.LIST_USERS_ERROR_MESSAGE, macHost.host, e.getMessage())
            LOGGER.log(Level.WARNING, message)
            LOGGER.log(Level.FINEST, "Exception : ", e)
            throw new SSHCommandException(message, e)
        }
    }
}