package fr.edf.jenkins.plugins.mac.util

import org.jenkinsci.plugins.plaincredentials.FileCredentials
import org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.SecretBytes
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.domains.HostnameSpecification
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl

import hudson.model.FileParameterValue.FileItemImpl
import jenkins.model.Jenkins
import spock.lang.Specification

class CredentialsUtilsTest extends Specification {

    @Rule
    public JenkinsRule r = new JenkinsRule()

    def "findCredentials should return credentials with id"() {
        setup:
        Domain domain = new Domain("example", "test domain", Arrays.asList(new HostnameSpecification("example.org", null)))
        StandardUsernamePasswordCredentials c1 = new UsernamePasswordCredentialsImpl(
                CredentialsScope.SYSTEM,
                "test1",
                null,
                "username",
                "password"
                )
        StandardUsernamePasswordCredentials c2 = new UsernamePasswordCredentialsImpl(
                CredentialsScope.SYSTEM,
                "test2",
                null,
                "username",
                "password"
                )
        CredentialsProvider.lookupStores(r.jenkins).iterator().next().addDomain(domain, c1)
        CredentialsProvider.lookupStores(r.jenkins).iterator().next().addDomain(domain, c2)

        when:
        StandardCredentials result = CredentialsUtils.findCredentials("example.org", "test1", Jenkins.get())

        then:
        assert result != null
        assert result instanceof StandardUsernamePasswordCredentials
        assert result.id == "test1"
    }

    def "findCredentials should throw IllegalArgumentException because id does not exist"() {
        setup:
        Domain domain = new Domain("example", "test domain", Arrays.asList(new HostnameSpecification("example.org", null)))
        StandardUsernamePasswordCredentials c1 = new UsernamePasswordCredentialsImpl(
                CredentialsScope.SYSTEM,
                "test1",
                null,
                "username",
                "password"
                )
        StandardUsernamePasswordCredentials c2 = new UsernamePasswordCredentialsImpl(
                CredentialsScope.SYSTEM,
                "test2",
                null,
                "username",
                "password"
                )
        CredentialsProvider.lookupStores(r.jenkins).iterator().next().addDomain(domain, c1)
        CredentialsProvider.lookupStores(r.jenkins).iterator().next().addDomain(domain, c2)

        when:
        StandardCredentials result = CredentialsUtils.findCredentials("example.org", "inexistentId", Jenkins.get())

        then:
        IllegalArgumentException iae = thrown()
    }

    def "findFileCredentials should return credentials with id"() {
        setup:
        Domain domain = new Domain("example", "test domain", Arrays.asList(new HostnameSpecification(Jenkins.get().getRootUrl(), null)))
        FileCredentials c1 = new FileCredentialsImpl(
            CredentialsScope.SYSTEM,
            "keychain1",
            "keychain1",
            new FileItemImpl(new File("src/test/resources/test.keychain-db")),
            null,
            new SecretBytes(true, new String("test").bytes)
            )
        StandardUsernamePasswordCredentials c2 = new UsernamePasswordCredentialsImpl(
                CredentialsScope.SYSTEM,
                "test2",
                null,
                "username",
                "password"
                )
        boolean test = CredentialsProvider.lookupStores(r.jenkins).iterator().next().addDomain(domain, c1)
        CredentialsProvider.lookupStores(r.jenkins).iterator().next().addDomain(domain, c2)

        when:
        FileCredentials result = CredentialsUtils.findFileCredentials("keychain1", Jenkins.get())

        then:
        assert result != null
        assert result instanceof FileCredentials
        assert result.id == "keychain1"
    }

    def "findKeychain should throw IllegalArgumentException because there is no KeychainFileCredentials"() {
        setup:
        Domain domain = new Domain("example", "test domain", Arrays.asList(new HostnameSpecification(Jenkins.get().getRootUrl(), null)))
        StandardUsernamePasswordCredentials c1 = new UsernamePasswordCredentialsImpl(
                CredentialsScope.SYSTEM,
                "keychain1",
                null,
                "username",
                "password"
                )
        StandardUsernamePasswordCredentials c2 = new UsernamePasswordCredentialsImpl(
                CredentialsScope.SYSTEM,
                "test2",
                null,
                "username",
                "password"
                )
        CredentialsProvider.lookupStores(r.jenkins).iterator().next().addDomain(domain, c1)
        CredentialsProvider.lookupStores(r.jenkins).iterator().next().addDomain(domain, c2)

        when:
        FileCredentials result = CredentialsUtils.findFileCredentials("keychain1", Jenkins.get())

        then:
        IllegalArgumentException iae = thrown()
    }
}
