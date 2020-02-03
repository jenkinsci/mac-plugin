package fr.edf.jenkins.plugins.mac.ssh.key.verifiers

import fr.edf.jenkins.plugins.mac.ssh.key.MacHostKey
import fr.edf.jenkins.plugins.mac.ssh.key.verifiers.ManuallyProvidedMacHostKeyVerifier.DescriptorImpl
import hudson.util.FormValidation
import spock.lang.Specification

class ManuallyProvidedMacHostKeyVerifierTest extends Specification {

    def "doCheckKey with empty parameter should return Key should be 2 parts: algorithm and Base 64 encoded key value."() {
        given:
        String key = ""
        DescriptorImpl desc = new ManuallyProvidedMacHostKeyVerifier.DescriptorImpl()
        
        when:
        FormValidation result = desc.doCheckKey(key)

        then:
        result.getMessage().equals("Key should be 2 parts: algorithm and Base 64 encoded key value.")
    }

    def "doCheckKey should work"() {
        given:
        String key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZw=="
        DescriptorImpl desc = new ManuallyProvidedMacHostKeyVerifier.DescriptorImpl()
        
        when:
        FormValidation result = desc.doCheckKey(key)

        then:
        result == FormValidation.OK
    }

    def "doCheckKey should return Input byte array has wrong 4-byte ending unit"() {
        given:
        String key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZw="
        DescriptorImpl desc = new ManuallyProvidedMacHostKeyVerifier.DescriptorImpl()
        
        when:
        FormValidation result = desc.doCheckKey(key)

        then:
        result.getMessage().equals("Input byte array has wrong 4-byte ending unit")
    }

    def "doCheckKey should return Unexpected key algorithm: ss-rsa"() {
        given:
        String key = "ss-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZw=="
        DescriptorImpl desc = new ManuallyProvidedMacHostKeyVerifier.DescriptorImpl()
        
        when:
        FormValidation result = desc.doCheckKey(key)

        then:
        result.getMessage().equals("Unexpected key algorithm: ss-rsa")
    }

    def "doCheckKey should return Illegal base64 character 3a"() {
        given:
        String key = "ssh-rsa ::::"
        DescriptorImpl desc = new ManuallyProvidedMacHostKeyVerifier.DescriptorImpl()

        when:
        FormValidation result = desc.doCheckKey(key)

        then:
        result.getMessage().equals("Illegal base64 character 3a")
    }

    def "doCheckKey should return Key should be 2 parts: algorithm and Base 64 encoded key value. because of null value"() {
        given:
        String key = null
        DescriptorImpl desc = new ManuallyProvidedMacHostKeyVerifier.DescriptorImpl()

        when:
        FormValidation result = desc.doCheckKey(key)

        then:
        result.getMessage().equals("Key should be 2 parts: algorithm and Base 64 encoded key value.")
    }

    def "verifyServerHostKey should retrurn true"() {
        given:
        String key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZw=="
        String hostname = "test"
        int port = 22
        MacHostKey macHostKey = ManuallyProvidedMacHostKeyVerifier.parseKey(key)
        ManuallyProvidedMacHostKeyVerifier verifier = new ManuallyProvidedMacHostKeyVerifier(key)
        
        when:
        boolean result = verifier.verifyServerHostKey(hostname, port, macHostKey.algorithm, macHostKey.key)
        
        then:
        result == true
    }
    
    def "verifyServerHostKey should return false"() {
        given:
        String key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZw=="
        String serverKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCdwKhjiI1XdQtvJYYv603pkn4u8ZNv86YLZqsB9+sdiNU3P2IbItY2R/5MmTtiN8cOF4sv4ga8VQmmyF1WVy5NRWxEXPjhaxKnKH2grk/i7QxDHI0fvjP6v/tAZRmPgikPceQfdB1W0dVsrAAeimSy1MiAwdDV/4tKfKArcTefdQ=="
        String hostname = "test"
        int port = 22
        MacHostKey macHostKey = ManuallyProvidedMacHostKeyVerifier.parseKey(serverKey)
        ManuallyProvidedMacHostKeyVerifier verifier = new ManuallyProvidedMacHostKeyVerifier(key)
        
        when:
        boolean result = verifier.verifyServerHostKey(hostname, port, macHostKey.algorithm, macHostKey.key)
        
        then:
        result == false
    }
}
