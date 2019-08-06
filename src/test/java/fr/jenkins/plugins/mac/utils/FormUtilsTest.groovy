package fr.jenkins.plugins.mac.utils

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import fr.jenkins.plugins.mac.Messages
import fr.jenkins.plugins.mac.util.FormUtils
import hudson.util.FormValidation
import hudson.util.ListBoxModel
import spock.lang.Specification

class FormUtilsTest extends Specification {
    
    @Rule
    JenkinsRule jenkins
    
    def "getUri with jenkins URL should not throw exception"() {
        setup:
        String url = jenkins.getURL().toString()
        when:
        URI jenkinsUri = FormUtils.getUri(url)
        then:
        notThrown Exception
        assert null != jenkinsUri
    }
    
    def "getUri should return URI"() {
        setup:
        String url = "http://localhost"
        when:
        URI uri = FormUtils.getUri(url)
        then:
        notThrown Exception
        assert null != uri
        assert uri.getHost() == "localhost"
    }
    
    def "validate host with jenkins url should not throw exception"() {
        setup:
        URI uri = jenkins.getURL().toURI()
        when:
        FormValidation result = FormUtils.validateHost(uri.getHost())
        then:
        notThrown Exception
        assert result == FormValidation.ok()
    }
    
    def "validate host with given url should not throw Exception but return invalid host"() {
        setup:
        URI uri = FormUtils.getUri("http://test.test")
        when:
        FormValidation result = FormUtils.validateHost(uri.getHost())
        then:
        notThrown Exception
        assert result.getMessage() == Messages._Host_HostInvalid().toString()
    }
    
    def "validate not empty should return ok"() {
        setup:
        String value = "test"
        String error = "error"
        when:
        FormValidation result = FormUtils.validateNotEmpty(value, error)
        then:
        notThrown Exception
        assert result == FormValidation.ok()
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
        assert result1.getMessage() == error
        assert result2.getMessage() == error
    }
    
    def "newCredentialsItemsListBoxModel should not throw exception and should not return null"() {
        
    }
}
 