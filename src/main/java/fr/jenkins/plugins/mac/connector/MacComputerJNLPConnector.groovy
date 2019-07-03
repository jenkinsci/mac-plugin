package fr.jenkins.plugins.mac.connector


import org.jenkinsci.Symbol

import hudson.Extension
import hudson.model.Descriptor

class MacComputerJNLPConnector extends MacComputerConnector {
    
    @Extension @Symbol("jnlp")
    public static final class DescriptorImpl extends Descriptor<MacComputerConnector> {
        @Override
        public String getDisplayName() {
            return "Connect with JNLP";
        }
    }
}
