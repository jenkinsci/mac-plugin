package fr.edf.jenkins.plugins.mac.ssh.connection

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.trilead.ssh2.Connection

import fr.edf.jenkins.plugins.mac.ssh.key.verifiers.MacHostKeyVerifier
import fr.edf.jenkins.plugins.mac.test.builders.MacPojoBuilder
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

    def "should return connection for user" () {
        setup:
        String host = "host"
        Connection conn = Mock(Connection)
        GroovySpy(SSHConnectionFactory, global:true)
        MacHostKeyVerifier hostKeyVerifier = Mock(MacHostKeyVerifier)
        SSHConnectionFactory.getConnection(_, host, _, _, _, _, hostKeyVerifier) >> conn

        when:
        Connection result = SSHConnectionFactory.getSshConnection(new SSHUserConnectionConfiguration(
            username: "username", password: Secret.fromString("password"),
            host: host, port: null, connectionTimeout: null, readTimeout: null, kexTimeout: null, macHostKeyVerifier: hostKeyVerifier))

        then:
        notThrown Exception
        result == conn
    }
    
    def "should return connection for global" () {
        setup:
        String host = "host"
        StandardUsernamePasswordCredentials c = new UsernamePasswordCredentialsImpl(
            CredentialsScope.SYSTEM,
            null,
            null,
            "username",
            "password"
        )
        Connection conn = Mock(Connection)
        GroovySpy(SSHConnectionFactory, global:true)
        MacHostKeyVerifier hostKeyVerifier = Mock(MacHostKeyVerifier)
        SSHConnectionFactory.getConnection(c, host, _, _, _, _, hostKeyVerifier) >> conn
        GroovyStub(CredentialsUtils, global:true)
        CredentialsUtils.findCredentials(host, _, _) >> c
        
        when:
        Connection result = SSHConnectionFactory.getSshConnection(new SSHGlobalConnectionConfiguration(
            credentialsId: host, host: host, port: null, connectionTimeout: null, readTimeout: null, kexTimeout: null, macHostKeyVerifier: hostKeyVerifier))

        then:
        notThrown Exception
        result == conn
    }
}
