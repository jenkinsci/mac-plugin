package fr.edf.jenkins.plugins.mac.ssh.key.verifiers

import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor

import com.trilead.ssh2.ServerHostKeyVerifier

import hudson.Extension
import hudson.model.Descriptor

class NonVerifyingMacHostKeyVerifier extends MacHostKeyVerifier implements ServerHostKeyVerifier {

    @DataBoundConstructor
    NonVerifyingMacHostKeyVerifier() {
        super()
    }

    @Override
    public String[] getAlgorithms() {
        return null;
    }

    @Override
    boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
        return true
    }

    @Extension @Symbol("nonVerifying")
    static final class DescriptorImpl extends Descriptor<MacHostKeyVerifier> {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Do not verify host key"
        }
    }
}
