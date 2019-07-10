package fr.jenkins.plugins.mac.cause

import hudson.slaves.OfflineCause

class MacOfflineCause extends OfflineCause {
    
    @Override
    public String toString() {
        return "Remove Mac user";
    }
}
