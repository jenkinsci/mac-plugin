package fr.jenkins.plugins.mac.slave

import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacHost
import fr.jenkins.plugins.mac.MacUser
import fr.jenkins.plugins.mac.connector.MacComputerJNLPConnector
import fr.jenkins.plugins.mac.test.builders.MacPojoBuilder

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import spock.lang.Specification

class MacSlaveTest extends Specification {
    //TODO : MacSlaveTest
    @Rule
    JenkinsRule jenkinsRule
    
    def "should create slave"() {
        setup:
        List<MacHost> macHosts = MacPojoBuilder.buildMacHost()
        MacComputerJNLPConnector connector = MacPojoBuilder.buildConnector(jenkinsRule)
        MacUser user = MacPojoBuilder.buildUser()
        MacCloud cloud = new MacCloud("test", macHosts, connector, "testLabel", false)
        MacSlave slave = new MacSlave("test","testLabel", user, macHosts.get(0), connector.createLauncher(macHosts.get(0), user))
        
        when:
        jenkinsRule.jenkins.get().addNode(slave)
        
        then:
        notThrown Exception
        jenkinsRule.jenkins.get().getNode(slave.name) == slave
    }
}
