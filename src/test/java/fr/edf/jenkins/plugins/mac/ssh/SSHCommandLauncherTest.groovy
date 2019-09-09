package fr.edf.jenkins.plugins.mac.ssh

import com.trilead.ssh2.ChannelCondition
import com.trilead.ssh2.Connection
import com.trilead.ssh2.Session

import fr.edf.jenkins.plugins.mac.ssh.SSHCommandLauncher
import spock.lang.Specification

class SSHCommandLauncherTest extends Specification {
    
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
        when:
        String result = SSHCommandLauncher.executeCommand(conn, false, command)
        
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
        when:
        String result = SSHCommandLauncher.executeCommand(conn, false, command)
        
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
        when:
        String result = SSHCommandLauncher.executeCommand(conn, true, command)
        
        then:
        notThrown Exception
        result == "Executed command " + command
    }
}
