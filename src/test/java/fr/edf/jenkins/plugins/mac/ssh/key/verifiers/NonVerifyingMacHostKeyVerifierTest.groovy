package fr.edf.jenkins.plugins.mac.ssh.key.verifiers

import fr.edf.jenkins.plugins.mac.ssh.key.MacHostKey
import spock.lang.Specification

class NonVerifyingMacHostKeyVerifierTest extends Specification {
    
    def "verifyServerHostKey always return true"() {
        given:
        String key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZw=="
        String serverKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCdwKhjiI1XdQtvJYYv603pkn4u8ZNv86YLZqsB9+sdiNU3P2IbItY2R/5MmTtiN8cOF4sv4ga8VQmmyF1WVy5NRWxEXPjhaxKnKH2grk/i7QxDHI0fvjP6v/tAZRmPgikPceQfdB1W0dVsrAAeimSy1MiAwdDV/4tKfKArcTefdQ=="
        String hostname = "test"
        int port = 22
        MacHostKey macHostKey = ManuallyProvidedMacHostKeyVerifier.parseKey(serverKey)
        NonVerifyingMacHostKeyVerifier verifier = new NonVerifyingMacHostKeyVerifier()
        
        when:
        boolean result = verifier.verifyServerHostKey(hostname, port, macHostKey.algorithm, macHostKey.key)
        assert result == true
        macHostKey = ManuallyProvidedMacHostKeyVerifier.parseKey(key)
        result = verifier.verifyServerHostKey(hostname, port, macHostKey.algorithm, macHostKey.key)
        assert result == true
        
        then:
        result == true
        
    }
}
