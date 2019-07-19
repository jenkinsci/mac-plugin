package fr.jenkins.plugins.mac

import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.trilead.ssh2.Connection

import fr.jenkins.plugins.mac.connection.SshClientFactory
import fr.jenkins.plugins.mac.connection.SshClientFactoryConfiguration
import fr.jenkins.plugins.mac.connector.MacComputerJNLPConnector
import fr.jenkins.plugins.mac.util.Constants
import fr.jenkins.plugins.mac.util.SshUtils
import groovy.util.logging.Slf4j
import hudson.slaves.ComputerLauncher
import hudson.util.Secret
import jenkins.model.Jenkins

@Slf4j
class MacProvisionService {

    @Restricted(NoExternalUse)
    static MacUser createUserOnMac(MacHost macHost) {
        Connection connection = SshClientFactory.getSshClient(new SshClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
            context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
            readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
        MacUser user = generateUser()
        log.info(SshUtils.executeCommand(connection, false, String.format(Constants.CREATE_USER, user.username, user.password)))
        connection.close()
        return user
    }

    @Restricted(NoExternalUse)
    static boolean deleteUserOnMac(String cloudId, String username) {
        MacCloud cloud = Jenkins.get().getCloud(cloudId)
        MacHost macHost = cloud.getMacHost()
        try {
            Connection connection = SshClientFactory.getSshClient(new SshClientFactoryConfiguration(credentialsId: macHost.credentialsId, port: macHost.port,
                context: Jenkins.get(), host: macHost.host, connectionTimeout: macHost.connectionTimeout,
                readTimeout: macHost.readTimeout, kexTimeout: macHost.kexTimeout))
            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.DELETE_USER, username)))
            connection.close()
            return true
        } catch (Exception e) {
            log.error("Error while deleting user " + username + " on mac " + macHost.host)
            return false
        }
    }

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
            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.GET_REMOTING_JAR, remotingUrl)))
            log.info(SshUtils.executeCommand(connection, false, String.format(Constants.LAUNCH_JNLP, jenkinsUrl, user.username, slaveSecret)))
            connection.close()
            return true
        } catch(Exception e) {
            log.error("Cannot connect Mac " + macHost.host + " with user " + user.username + " to jenkins with JNLP")
            return false
        }
    }

    @Restricted(NoExternalUse)
    static private MacUser generateUser() {
        String password = RandomStringUtils.random(10, true, true);
        String username = String.format(Constants.USERNAME_PATTERN, RandomStringUtils.random(5, true, true).toLowerCase())
        String workdir = String.format("/Users/%s/", username)
        return new MacUser(username:username, password:Secret.fromString(password), workdir:workdir)
    }
}