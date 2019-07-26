package fr.jenkins.plugins.mac.ssh

import java.nio.file.Path

import org.apache.sshd.common.NamedFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.UserAuth
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.auth.password.UserAuthPasswordFactory
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.session.ServerSession
import org.apache.sshd.server.shell.ProcessShellFactory
import org.junit.Rule
import org.junit.Test
import org.jvnet.hudson.test.JenkinsRule

import com.trilead.ssh2.Connection

import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.ssh.connection.SSHClientFactory
import spock.lang.Specification

class SSHCommandTest extends Specification {

    @Rule
    JenkinsRule jenkins = new JenkinsRule()

    def "createUserOnMac should return user without exception"() {
        setup:
        SshServer server = defineSshServer()
        MacHost macHost = Mock(MacHost)
        Connection conn = Mock(Connection)
        GroovySpy(SSHClientFactory, global:true)
        SSHClientFactory.getSshClient(*_) >> conn
        GroovySpy(SSHCommandLauncher, global:true)
        SSHCommandLauncher.executeCommand(conn, false, _) >> "OK"

        when:
        MacUser user = SSHCommand.createUserOnMac(macHost)

        then:
        notThrown Exception
        user != null
    }

    def "createUserOnMac should throw exception"() {
    }

    def "deleteUserOnMac should works"() {
    }

    def "deleteUserOnMac should throw exception"() {
    }

    def "jnlpConnect should works"() {
    }

    def "jnlpConnect should throw exception"() {
    }

    private SshServer defineSshServer() {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(1009)
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>()
        userAuthFactories.add(new UserAuthPasswordFactory())
        sshd.setShellFactory(new ProcessShellFactory());
        sshd.setUserAuthFactories(userAuthFactories)
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            public boolean authenticate(String username, String password, ServerSession session) {
                return "tomek".equals(username) && "123".equals(password);
            }
        });
    sshd.start()
    return sshd
    }
}
