package fr.edf.jenkins.plugins.mac

import hudson.util.Secret

class MacUser {
    
    String username
    Secret password
    String workdir
    
    MacUser(String username, Secret password, String workdir) {
        this.username = username
        this.password = password
        this.workdir = workdir
    }
    
    private MacUser() {
    }
}
