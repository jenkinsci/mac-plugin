package fr.edf.jenkins.plugins.mac.ssh.verifiers

import org.jenkinsci.Symbol

import com.trilead.ssh2.ServerHostKeyVerifier

import hudson.Extension
import hudson.model.Descriptor

class ProvidedHostKeyVerifier extends MacHostKeyVerifier implements ServerHostKeyVerifier {

    private final byte[] key
    private final String keyAlgorithm

    public ProvidedHostKeyVerifier(final byte[] key, final String keyAlgorithm) {
        this.key = key
        this.keyAlgorithm = keyAlgorithm
    }

    @Override
    public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
        if (keyAlgorithm == null) {
            if (serverHostKeyAlgorithm != null) return false
        } else if (!keyAlgorithm.equals(serverHostKeyAlgorithm))
            return false
        if (!Arrays.equals(key, serverHostKey)) return false
        return true
    }
    
    @Extension @Symbol("providedKey")
    public static final class DescriptorImpl extends Descriptor<MacHostKeyVerifier> {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Provided host key"
        }
    }
}
