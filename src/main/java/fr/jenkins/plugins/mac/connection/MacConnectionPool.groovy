package fr.jenkins.plugins.mac.connection

import com.trilead.ssh2.Session

import fr.jenkins.plugins.mac.MacCloud

@Singleton
class MacConnectionPool {
    
    Map<MacCloud, Session> connectionMap
    
    
}
