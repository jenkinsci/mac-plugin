package fr.jenkins.plugins.mac.ssh

import org.apache.sshd.common.SshException
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import com.trilead.ssh2.Connection

import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.ssh.connection.SSHClientFactory
import fr.jenkins.plugins.mac.util.Constants
import hudson.util.Secret
import spock.lang.Specification

class SSHCommandTest extends Specification {

    @Rule
    JenkinsRule jenkins

    // TODO : Get NoClassDefFoundException
//    def "createUserOnMac should not throw exception"() {
//        setup:
//        String label = "label"
//        MacHost macHost = Mock(MacHost)
//        Connection conn = Mock(Connection)
//        GroovySpy(SSHClientFactory, global:true)
//        1 * SSHClientFactory.getSshClient(*_) >> conn
//        GroovySpy(SSHCommandLauncher, global:true)
//        GroovySpy(SSHCommand, global:true)
//        1 * SSHCommandLauncher.executeCommand(conn, false, _) >> "OK"
//        1 * SSHCommand.isUserExist(conn, _) >> true
//        when:
//        MacUser user = SSHCommand.createUserOnMac(label, macHost)
//
//        then:
//        notThrown Exception
//        user != null
//    }

    def "createUserOnMac should throw exception because user does not exist after creation"() {
        setup:
        String label = "label"
        MacHost macHost = Mock(MacHost)
        Connection conn = Mock(Connection)
        
        GroovySpy(SSHClientFactory, global:true)
        1 * SSHClientFactory.getSshClient(*_) >> conn
        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(conn, false, _) >> "OK"
        1 * SSHCommandLauncher.executeCommand(conn, true, _) >> "OK"

        when:
        MacUser user = SSHCommand.createUserOnMac(label, macHost)

        then:
        SSHCommandException e = thrown()
        e.getMessage().contains("Cannot create MacUser on host")
        user == null
    }

    def "deleteUserOnMac should works"() {
        setup:
        String username = "mac_user_test"
        MacHost macHost = Mock(MacHost)
        Connection conn = Mock(Connection)
        GroovySpy(SSHClientFactory, global:true)
        1 * SSHClientFactory.getSshClient(*_) >> conn
        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(conn, false, _) >> "OK"
        1 * SSHCommandLauncher.executeCommand(conn, true, _) >> ""

        when:
        SSHCommand.deleteUserOnMac(username, macHost)

        then:
        notThrown Exception
    }

    def "deleteUserOnMac should return exception because user always exist after command"() {
        setup:
        String username = "mac_user_test"
        MacHost macHost = Mock(MacHost)
        Connection conn = Mock(Connection)
        GroovySpy(SSHClientFactory, global:true)
        1 * SSHClientFactory.getSshClient(*_) >> conn
        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(conn, false, _) >> "OK"
        1 * SSHCommandLauncher.executeCommand(conn, true, _) >> username

        when:
        SSHCommand.deleteUserOnMac(username, macHost)

        then:
        SSHCommandException e = thrown()
        e.getMessage().contains("An error occured while deleting user " + username)
    }

    def "jnlpConnect should works"() {
        setup:
        MacUser user = Mock(MacUser)
        MacHost macHost = Mock(MacHost)
        String slaveSecret = "secret"
        Connection conn = Mock(Connection)
        GroovySpy(SSHClientFactory, global:true)
        1 * SSHClientFactory.getUserClient(*_) >> conn
        GroovySpy(SSHCommandLauncher, global:true)
        2 * SSHCommandLauncher.executeCommand(conn, false, _) >> "OK"

        when:
        SSHCommand.jnlpConnect(macHost, user, null, slaveSecret)

        then:
        notThrown Exception
    }

    def "jnlpConnect should throw exception"() {
        setup:
        MacUser user = new MacUser("test", Secret.fromString("password"), "workdir")
        MacHost macHost = new MacHost("host", "credentialsId", 0, 1, 5, 5, 5, false)
        String slaveSecret = "secret"
        Connection conn = Mock(Connection)
        GroovySpy(SSHClientFactory, global:true)
        1 * SSHClientFactory.getUserClient(*_) >> conn

        when:
        SSHCommand.jnlpConnect(macHost, user, null, slaveSecret)

        then:
        SSHCommandException e = thrown()
        e.getMessage().contains("Cannot connect Mac " + macHost.host + " with user " + user.username + " to jenkins with JNLP")
    }

    def "listLabelUsers should works without exception"() {
        setup:
        String label = "label"
        MacHost macHost = new MacHost("host", "credentialsId", 0, 1, 5, 5, 5, false)
        Connection conn = Mock(Connection)
        GroovySpy(SSHClientFactory, global:true)
        1 * SSHClientFactory.getSshClient(*_) >> conn
        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(conn, true, String.format(Constants.LIST_USERS, label+"_jenkins_")) >> ""

        when:
        SSHCommand.listLabelUsers(macHost, label)

        then:
        notThrown Exception
    }
}
