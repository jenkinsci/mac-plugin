package fr.edf.jenkins.plugins.mac.ssh.verifiers

import org.jenkinsci.Symbol

import com.trilead.ssh2.ServerHostKeyVerifier

import fr.edf.jenkins.plugins.mac.connector.MacComputerConnector
import hudson.Extension
import hudson.model.Descriptor

class KnownHostsFileKeyVerifier extends MacHostKeyVerifier implements ServerHostKeyVerifier {
    
    private final hostFile
    
    @Override
    public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
        
    }
    
    @Extension @Symbol("hostsFile")
    public static final class DescriptorImpl extends Descriptor<MacHostKeyVerifier> {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Known hosts file"
        }
    }
}
