package fr.edf.jenkins.plugins.mac.ssh

import org.jenkinsci.plugins.plaincredentials.FileCredentials
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.ssh.connection.SSHGlobalConnectionConfiguration
import fr.edf.jenkins.plugins.mac.test.builders.MacPojoBuilder
import fr.edf.jenkins.plugins.mac.util.Constants
import hudson.util.Secret
import spock.lang.Specification

class SSHCommandTest extends Specification {

    @Rule
    JenkinsRule jenkins

    def "createUserOnMac should not throw exception"() {
        setup:
        String label = "label"
        MacHost macHost = Mock(MacHost)
        MacUser user = SSHCommand.generateUser()
        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.CREATE_USER, user.username, user.password.getPlainText())) >> "OK"
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.CHECK_USER_EXIST, user.username)) >> user.username
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.CHANGE_RIGHTS_ON_USER, user.username)) >> "OK"

        when:
        SSHCommand.createUserOnMac(macHost, user)

        then:
        notThrown Exception
    }

    def "createUserOnMac should throw exception because user does not exist after creation"() {
        setup:
        String label = "label"
        MacHost macHost = Mock(MacHost)
        MacUser user = SSHCommand.generateUser()

        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.CREATE_USER, user.username, user.password)) >> "OK"
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.CHECK_USER_EXIST, user.username)) >> ""

        when:
        SSHCommand.createUserOnMac(macHost, user)

        then:
        SSHCommandException e = thrown()
        e.getMessage().contains("Cannot create MacUser on host")
    }

    def "deleteUserOnMac should works"() {
        setup:
        String username = "mac_user_test"
        MacHost macHost = Mock(MacHost)
        GroovySpy(SSHCommandLauncher, global:true)
        2 * SSHCommandLauncher.executeCommand(_, true, _) >> "OK"

        when:
        SSHCommand.deleteUserOnMac(username, macHost)

        then:
        notThrown Exception
    }

    def "deleteUserOnMac should return exception because user still exist after command"() {
        setup:
        String username = "mac_user_test"
        MacHost macHost = Mock(MacHost)
        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.DELETE_USER, username)) >> "OK"
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.CHECK_USER_EXIST, username)) >> username

        when:
        SSHCommand.deleteUserOnMac(username, macHost)

        then:
        SSHCommandException e = thrown()
        e.getMessage().contains("An error occured while deleting user " + username)
        e.getCause().getMessage().contains("The user " + username + " still exist after verification")
    }


    def "jnlpConnect should works"() {
        setup:
        MacUser user = SSHCommand.generateUser()
        MacHost macHost = Mock(MacHost)
        String slaveSecret = "secret"
        GroovySpy(SSHCommandLauncher, global:true)
        2 * SSHCommandLauncher.executeCommand(_, false, _) >> "OK"

        when:
        SSHCommand.jnlpConnect(macHost, user, null, slaveSecret)

        then:
        notThrown Exception
    }

    def "jnlpConnect should throw exception"() {
        setup:
        MacUser user = MacPojoBuilder.buildUser()
        MacHost macHost = MacPojoBuilder.buildMacHost().get(0)
        String slaveSecret = "secret"

        when:
        SSHCommand.jnlpConnect(macHost, user, null, slaveSecret)

        then:
        SSHCommandException e = thrown()
        e.getMessage().contains("Cannot connect Mac " + macHost.host + " with user " + user.username + " to jenkins with JNLP")
    }

    def "listLabelUsers should works and return empty list"() {
        setup:
        MacHost macHost = MacPojoBuilder.buildMacHost().get(0)
        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.LIST_USERS, Constants.USERNAME_PATTERN.substring(0, Constants.USERNAME_PATTERN.lastIndexOf("%")))) >> ""

        when:
        List result = SSHCommand.listUsers(macHost)

        then:
        notThrown Exception
        result.isEmpty()
    }

    def "listLabelUsers should works and return list of user"() {
        setup:
        MacHost macHost = MacPojoBuilder.buildMacHost().get(0)
        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.LIST_USERS, Constants.USERNAME_PATTERN.substring(0, Constants.USERNAME_PATTERN.lastIndexOf("%")))) >> "user1\ruser2"

        when:
        List result = SSHCommand.listUsers(macHost)

        then:
        notThrown Exception
        result.size() == 2
    }
    
    def "listLabelUsers should throw SSHCommandException"() {
        setup:
        MacHost macHost = MacPojoBuilder.buildMacHost().get(0)
        GroovySpy(SSHCommandLauncher, global:true)
        1 * SSHCommandLauncher.executeCommand(_, true, String.format(Constants.LIST_USERS, Constants.USERNAME_PATTERN.substring(0, Constants.USERNAME_PATTERN.lastIndexOf("%")))) >> { throw new Exception("Error") }

        when:
        List result = SSHCommand.listUsers(macHost)

        then:
        SSHCommandException sshe = thrown()
    }

    def "checkConnection should works without exception"() {
        setup:
        SSHGlobalConnectionConfiguration connectionConfig = Mock(SSHGlobalConnectionConfiguration)
        GroovySpy(SSHCommandLauncher, global:true)
        1* SSHCommandLauncher.executeCommand(connectionConfig, false,  Constants.WHOAMI) >> "ok"

        when:
        SSHCommand.checkConnection(connectionConfig)

        then:
        notThrown Exception
    }
    
    def "uploadKeychain should work without exception"() {
        setup:
        MacUser user = new MacUser("test", Secret.fromString("password"), "workdir")
        MacHost host = Mock(MacHost)
        String fileDir = String.format(Constants.KEYCHAIN_DESTINATION_FOLDER, user.username)
        InputStream content = Mock(InputStream)
        String fileName = "keychain"
        FileCredentials keychainFile = Stub(FileCredentials) {
            getContent() >> content
            getFileName() >> fileName
        }
        GroovySpy(SSHCommandLauncher, global:true)
        1* SSHCommandLauncher.executeCommand(_, true, String.format(Constants.CREATE_DIR, fileDir)) >> "ok"
        1* SSHCommandLauncher.sendFile(_, content, fileName, fileDir) >> {}

        when:
        SSHCommand.uploadKeychain(host, user, keychainFile)

        then:
        notThrown Exception
    }
    
    def "uploadKeychain should throw SSHCommandException"() {
        setup:
        MacUser user = new MacUser("test", Secret.fromString("password"), "workdir")
        MacHost host = Mock(MacHost)
        String fileDir = String.format(Constants.KEYCHAIN_DESTINATION_FOLDER, user.username)
        InputStream content = Mock(InputStream)
        String fileName = "keychain"
        FileCredentials keychainFile = Stub(FileCredentials) {
            getContent() >> content
            getFileName() >> fileName
        }
        GroovySpy(SSHCommandLauncher, global:true)
        1* SSHCommandLauncher.executeCommand(_, true, String.format(Constants.CREATE_DIR, fileDir)) >> "ok"
        1* SSHCommandLauncher.sendFile(_, content, fileName, fileDir) >> new Exception("failed to send file")

        when:
        SSHCommand.uploadKeychain(host, user, keychainFile)

        then:
        SSHCommandException sshe = thrown()
        sshe.getMessage().contains("Cannot transfert keychain file")
    }
}
