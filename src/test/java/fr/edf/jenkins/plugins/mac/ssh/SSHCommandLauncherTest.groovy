package fr.edf.jenkins.plugins.mac.ssh

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import com.trilead.ssh2.ChannelCondition
import com.trilead.ssh2.Connection
import com.trilead.ssh2.Session

import fr.edf.jenkins.plugins.mac.ssh.connection.SSHConnectionFactory
import fr.edf.jenkins.plugins.mac.ssh.connection.SSHGlobalConnectionConfiguration
import fr.edf.jenkins.plugins.mac.ssh.connection.SSHUserConnectionConfiguration
import spock.lang.Specification

class SSHCommandLauncherTest extends Specification {

    @Rule
    JenkinsRule jenkins

    def "executeCommand should not throw exception"() {
        setup:
        String command = "test command"
        Session session = Stub(Session) {
            execCommand(command) >> null
            waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5000) >> 1
            close() >> null
            getExitSignal() >> "0"
            getExitStatus() >> 0
            getStdout() >> new ByteArrayInputStream(new String("out").getBytes())
            getStderr() >> new ByteArrayInputStream(new String("err").getBytes())
        }
        Connection conn = Stub(Connection) {
            openSession() >> session
        }
        GroovySpy(SSHConnectionFactory, global:true)
        1 * SSHConnectionFactory.getSshConnection(*_) >> conn
        SSHGlobalConnectionConfiguration connectionConfig = Mock(SSHGlobalConnectionConfiguration)
        when:
        String result = SSHCommandLauncher.executeCommand(connectionConfig, false, command)

        then:
        notThrown Exception
        result != null
        result == "out"
    }

    def "executeCommand should throw exception because exit status is 1"() {
        setup:
        String command = "test command"
        Session session = Stub(Session) {
            execCommand(command) >> null
            waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5000) >> 1
            close() >> null
            getExitSignal() >> "Error"
            getExitStatus() >> 1
            getStdout() >> new ByteArrayInputStream(new String("out").getBytes())
            getStderr() >> new ByteArrayInputStream(new String("err").getBytes())
        }

        Connection conn = Stub(Connection) {
            openSession() >> session
        }
        GroovySpy(SSHConnectionFactory, global:true)
        1 * SSHConnectionFactory.getSshConnection(*_) >> conn
        SSHGlobalConnectionConfiguration connectionConfig = Mock(SSHGlobalConnectionConfiguration)
        when:
        String result = SSHCommandLauncher.executeCommand(connectionConfig, false, command)

        then:
        Exception e = thrown()
        e.getMessage() == "Failed to execute command " + command
    }

    def "executeCommand should not throw exception with exit status is 1 because ignore error is true"() {
        setup:
        String command = "test command"
        Session session = Stub(Session) {
            execCommand(command) >> null
            waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5000) >> 1
            close() >> null
            getExitSignal() >> "Error"
            getExitStatus() >> 1
            getStdout() >> new ByteArrayInputStream(new String("").getBytes())
            getStderr() >> new ByteArrayInputStream(new String("err").getBytes())
        }
        Connection conn = Stub(Connection) {
            openSession() >> session
        }
        GroovySpy(SSHConnectionFactory, global:true)
        1 * SSHConnectionFactory.getSshConnection(*_) >> conn
        SSHGlobalConnectionConfiguration connectionConfig = Mock(SSHGlobalConnectionConfiguration)
        when:
        String result = SSHCommandLauncher.executeCommand(connectionConfig, true, command)

        then:
        notThrown Exception
        result == "err"
    }

    def "sendFile should throw Exception because SCPClient cannot find host" () {
        setup:
        SSHUserConnectionConfiguration connectionConfig = Mock(SSHUserConnectionConfiguration)
        Connection conn = Stub(Connection) {
            close() >> {}
        }
        GroovySpy(SSHConnectionFactory, global:true)
        1 * SSHConnectionFactory.getSshConnection(*_) >> conn
        InputStream content = new ByteArrayInputStream(new String().getBytes())
        String fileName = "fileName"
        String outputDir = "outputDir"

        when:
        SSHCommandLauncher.sendFile(connectionConfig, content, fileName, outputDir)

        then:
        Exception e = thrown()
    }

    def "executeMultipleCommands should not throw exception"() {
        setup:
        String command = "test command"
        Session session = Stub(Session) {
            execCommand(command) >> null
            waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5000) >> 1
            close() >> null
            getExitSignal() >> "0"
            getExitStatus() >> 0
            getStdout() >> new ByteArrayInputStream(new String("out").getBytes())
            getStderr() >> new ByteArrayInputStream(new String("err").getBytes())
        }
        Connection conn = Stub(Connection) {
            openSession() >> session
            close() >> null
        }
        GroovySpy(SSHConnectionFactory, global:true)
        1 * SSHConnectionFactory.getSshConnection(*_) >> conn
        SSHGlobalConnectionConfiguration connectionConfig = Mock(SSHGlobalConnectionConfiguration)
        String ls = "ls"
        String whoami = "whoami"
        List<String> commands = Arrays.asList(ls, whoami)
        GroovySpy(SSHCommandLauncher, global:true)

        when:
        SSHCommandLauncher.executeMultipleCommands(connectionConfig, true, commands)

        then:
        1 * SSHCommandLauncher.executeCommandWithConnection(conn, false, ls)
        1 * SSHCommandLauncher.executeCommandWithConnection(conn, false, whoami)
        notThrown Exception
    }

    def "executeMultipleCommands should throw exception and stop at the first command ignoreError is false and exit status is 1"() {
        setup:
        String command = "test command"
        Session session = Stub(Session) {
            execCommand(command) >> null
            waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5000) >> 1
            close() >> null
            getExitSignal() >> "0"
            getExitStatus() >> 1
            getStdout() >> new ByteArrayInputStream(new String("out").getBytes())
            getStderr() >> new ByteArrayInputStream(new String("err").getBytes())
        }
        Connection conn = Stub(Connection) {
            openSession() >> session
            close() >> null
        }
        GroovySpy(SSHConnectionFactory, global:true)
        1 * SSHConnectionFactory.getSshConnection(*_) >> conn
        SSHGlobalConnectionConfiguration connectionConfig = Mock(SSHGlobalConnectionConfiguration)
        String ls = "ls"
        String whoami = "whoami"
        List<String> commands = Arrays.asList(ls, whoami)
        GroovySpy(SSHCommandLauncher, global:true)

        when:
        SSHCommandLauncher.executeMultipleCommands(connectionConfig, false, commands)

        then:
        1 * SSHCommandLauncher.executeCommandWithConnection(conn, false, ls)
        0 * SSHCommandLauncher.executeCommandWithConnection(conn, false, whoami)
        Exception e = thrown()
    }
    
    def "executeMultipleCommands should not throw exception and launch both commands with exit status 1 and ignore error true"() {
        setup:
        String command = "test command"
        Session session = Stub(Session) {
            execCommand(command) >> null
            waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5000) >> 1
            close() >> null
            getExitSignal() >> "0"
            getExitStatus() >> 1
            getStdout() >> new ByteArrayInputStream(new String("out").getBytes())
            getStderr() >> new ByteArrayInputStream(new String("err").getBytes())
        }
        Connection conn = Stub(Connection) {
            openSession() >> session
            close() >> null
        }
        GroovySpy(SSHConnectionFactory, global:true)
        1 * SSHConnectionFactory.getSshConnection(*_) >> conn
        SSHGlobalConnectionConfiguration connectionConfig = Mock(SSHGlobalConnectionConfiguration)
        String ls = "ls"
        String whoami = "whoami"
        List<String> commands = Arrays.asList(ls, whoami)
        GroovySpy(SSHCommandLauncher, global:true)

        when:
        SSHCommandLauncher.executeMultipleCommands(connectionConfig, true, commands)

        then:
        1 * SSHCommandLauncher.executeCommandWithConnection(conn, false, ls)
        1 * SSHCommandLauncher.executeCommandWithConnection(conn, false, whoami)
        notThrown Exception
    }
}
