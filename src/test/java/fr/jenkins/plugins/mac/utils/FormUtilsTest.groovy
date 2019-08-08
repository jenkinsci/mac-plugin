package fr.jenkins.plugins.mac.utils

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import com.trilead.ssh2.Connection

import fr.jenkins.plugins.mac.Messages
import fr.jenkins.plugins.mac.ssh.SSHCommand
import fr.jenkins.plugins.mac.ssh.connection.SSHClientFactory
import fr.jenkins.plugins.mac.util.FormUtils
import hudson.model.Item
import hudson.util.FormValidation
import hudson.util.FormValidation.Kind
import hudson.util.ListBoxModel
import spock.lang.Specification

class FormUtilsTest extends Specification {

    @Rule
    JenkinsRule jenkinsRule

    def "getUri with jenkins URL should not throw exception"() {
        setup:
        String url = jenkinsRule.getURL().toString()
        when:
        URI jenkinsUri = FormUtils.getUri(url)
        then:
        notThrown Exception
        null != jenkinsUri
    }

    def "getUri should return URI"() {
        setup:
        String url = "http://localhost"
        when:
        URI uri = FormUtils.getUri(url)
        then:
        notThrown Exception
        null != uri
        uri.getHost() == "localhost"
    }
    
    def "validate host with jenkins url should not throw exception"() {
        setup:
        URI uri = jenkinsRule.getURL().toURI()
        when:
        FormValidation result = FormUtils.validateHost(uri.getHost())
        then:
        notThrown Exception
        result == FormValidation.ok()
    }

    def "validate host with given url should not throw Exception but return invalid host"() {
        setup:
        URI uri = FormUtils.getUri("http://test.test")
        when:
        FormValidation result = FormUtils.validateHost(uri.getHost())
        then:
        notThrown Exception
        result.getMessage() == Messages._Host_HostInvalid().toString()
    }

    def "validate not empty should return ok"() {
        setup:
        String value = "test"
        String error = "error"
        when:
        FormValidation result = FormUtils.validateNotEmpty(value, error)
        then:
        notThrown Exception
        result == FormValidation.ok()
    }

    def "validate not empty should return error if input is null or empty"() {
        setup:
        String value = ""
        String value2 = null
        String error = "error"
        when:
        FormValidation result1 = FormUtils.validateNotEmpty(value, error)
        FormValidation result2 = FormUtils.validateNotEmpty(value2, error)
        then:
        notThrown Exception
        result1.getMessage() == error
        result2.getMessage() == error
    }
    

    // TODO : NoClassDefFound Error on SSHCommand
//    def "verifyCredential should not throw exception"() {
//        setup:
//        String username = "test spock"
//        Connection conn = Mock(Connection)
//        GroovySpy(SSHClientFactory, global:true)
//        GroovySpy(SSHCommand, global:true)
//        1 * SSHClientFactory.getSshClient(*_) >> conn
//        1 * SSHCommand.checkConnection(conn) >> username
//
//        when:
//        FormValidation result = FormUtils.verifyCredential("host", 0, "credentialsId", 5, 5, 5, jenkinsRule.jenkins.get())
//
//        then:
//        result.kind == Kind.OK
//        result.getMessage() == Messages._Host_ConnectionSucceeded(username).toString()
//    }

    def "newCredentialsItemsListBoxModel should not throw exception and should not return null"() {
        setup:
        String host = "test.host"
        String credentialsId = "credentials"
        
        when:
        ListBoxModel list = FormUtils.newCredentialsItemsListBoxModel(host, credentialsId, null)
        
        then:
        notThrown Exception
        list != null
    }
}
