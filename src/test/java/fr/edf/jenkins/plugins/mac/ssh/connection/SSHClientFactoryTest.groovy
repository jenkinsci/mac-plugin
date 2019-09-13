package fr.edf.jenkins.plugins.mac.ssh.connection

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.trilead.ssh2.Connection

import fr.edf.jenkins.plugins.mac.ssh.connection.SSHClientFactory
import fr.edf.jenkins.plugins.mac.util.CredentialsUtils
import hudson.util.Secret
import spock.lang.Specification

class SshClientFactoryTest extends Specification {

    @Rule
    JenkinsRule jenkins = new JenkinsRule()

    def "getSshClient with empty parameter should not throww exception"() {

        setup:
        Connection conn = Mock(Connection)
        StandardCredentials cred = Mock(StandardCredentials)
        GroovyStub(CredentialsUtils, global:true)
        CredentialsUtils.findCredentials(*_) >> cred
        GroovySpy(SSHClientFactory, global:true)
        SSHClientFactory.getClient(cred, null, 22, 0, 0, 0) >> conn

        when:
        Connection result = SSHClientFactory.getSshClient()

        then:
        notThrown Exception
        result == conn
    }

    def "should inform non mandatory null parameters" () {
        setup:
        String host = "host"
        Connection conn = Mock(Connection)
        GroovySpy(SSHClientFactory, global:true)
        SSHClientFactory.getClient(_, host, 22, 0, 0, 0) >> conn

        when:
        Connection result = SSHClientFactory.getUserClient("username", Secret.fromString("password"), host, null, null, null, null)

        then:
        notThrown Exception
        result == conn
    }
}
