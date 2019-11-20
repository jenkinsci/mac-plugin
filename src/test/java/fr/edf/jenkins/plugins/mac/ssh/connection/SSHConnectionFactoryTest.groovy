package fr.edf.jenkins.plugins.mac.ssh.connection

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.trilead.ssh2.Connection

import fr.edf.jenkins.plugins.mac.ssh.connection.SSHConnectionFactory
import fr.edf.jenkins.plugins.mac.util.CredentialsUtils
import hudson.util.Secret
import spock.lang.Specification

class SSHConnectionFactoryTest extends Specification {

    @Rule
    JenkinsRule jenkins = new JenkinsRule()

    def "getSshConnection with empty parameter should return null"() {

        when:
        Connection result = SSHConnectionFactory.getSshConnection()

        then:
        notThrown Exception
        result == null
    }

    def "should inform non mandatory null parameters" () {
        setup:
        String host = "host"
        Connection conn = Mock(Connection)
        GroovySpy(SSHConnectionFactory, global:true)
        SSHConnectionFactory.getConnection(_, host, 22, 0, 0, 0) >> conn

        when:
        Connection result = SSHConnectionFactory.getSshConnection(new SSHUserConnectionConfiguration(
            username: "username", password: Secret.fromString("password"),
            host: host, port: null, connectionTimeout: null, readTimeout: null, kexTimeout: null))

        then:
        notThrown Exception
        result == conn
    }
}
