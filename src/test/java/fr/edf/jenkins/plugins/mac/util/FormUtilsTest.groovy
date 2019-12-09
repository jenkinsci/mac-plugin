package fr.edf.jenkins.plugins.mac.util

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import fr.edf.jenkins.plugins.mac.Messages
import hudson.util.FormValidation
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


    def "newMacHostCredentialsItemsListBoxModel should not throw exception and should not return null"() {
        setup:
        String host = "test.host"
        String credentialsId = "credentials"

        when:
        ListBoxModel list = FormUtils.newMacHostCredentialsItemsListBoxModel(host, credentialsId, null)

        then:
        notThrown Exception
        list != null
    }

    def "newKeychainFileItemsListBoxModel should not throw exception and should not return null"() {
        setup:
        String keychainFileId = "credentials"

        when:
        ListBoxModel list = FormUtils.newFileCredentialsItemsListBoxModel(keychainFileId, null)

        then:
        notThrown Exception
        list != null
    }
}
