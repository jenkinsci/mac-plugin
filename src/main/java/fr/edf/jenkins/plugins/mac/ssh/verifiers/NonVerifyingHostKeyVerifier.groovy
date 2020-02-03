package fr.edf.jenkins.plugins.mac.ssh.verifiers

import org.jenkinsci.Symbol

import com.trilead.ssh2.ServerHostKeyVerifier

import hudson.Extension
import hudson.model.Descriptor

class NonVerifyingHostKeyVerifier extends MacHostKeyVerifier implements ServerHostKeyVerifier {
    
    @Override
    public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
        return true
    }
    
    @Extension @Symbol("nonVerifying")
    public static final class DescriptorImpl extends Descriptor<MacHostKeyVerifier> {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Non verifying host key"
        }
    }
}
