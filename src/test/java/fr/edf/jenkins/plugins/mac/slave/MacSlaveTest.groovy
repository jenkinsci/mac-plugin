package fr.edf.jenkins.plugins.mac.slave

import fr.edf.jenkins.plugins.mac.MacCloud
import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.connector.MacComputerJNLPConnector
import fr.edf.jenkins.plugins.mac.slave.MacSlave
import fr.edf.jenkins.plugins.mac.ssh.SSHCommand
import fr.edf.jenkins.plugins.mac.test.builders.MacPojoBuilder
import hudson.model.TaskListener

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import spock.lang.Specification

class MacSlaveTest extends Specification {

    @Rule
    JenkinsRule jenkinsRule

    def "should create slave"() {
        setup:
        List<MacHost> macHosts = MacPojoBuilder.buildMacHost()
        MacComputerJNLPConnector connector = MacPojoBuilder.buildConnector(jenkinsRule)
        MacUser user = MacPojoBuilder.buildUser()
        MacCloud cloud = new MacCloud("test", macHosts, connector, new Integer(1))
        MacSlave slave = new MacSlave("test","testLabel", user, macHosts.get(0), connector.createLauncher(macHosts.get(0), user), new Integer(1))

        when:
        jenkinsRule.jenkins.get().clouds.add(cloud)
        jenkinsRule.jenkins.get().addNode(slave)

        then:
        notThrown Exception
        jenkinsRule.jenkins.get().getNode(slave.name) == slave
    }

    def "should terminate and remove slave"() {
        setup:
        List<MacHost> macHosts = MacPojoBuilder.buildMacHost()
        MacComputerJNLPConnector connector = MacPojoBuilder.buildConnector(jenkinsRule)
        GroovyStub(SSHCommand, global:true) {
            deleteUserOnMac("toto", macHosts.get(0)) >> "ok"
        }
        MacUser user = MacPojoBuilder.buildUser()
        MacCloud cloud = new MacCloud("test", macHosts, connector, new Integer(1))
        MacSlave slave = new MacSlave("test","testLabel", user, macHosts.get(0), connector.createLauncher(macHosts.get(0), user), new Integer(1))

        when:
        jenkinsRule.jenkins.get().clouds.add(cloud)
        jenkinsRule.jenkins.get().addNode(slave)
        assert jenkinsRule.jenkins.get().getNode(slave.name) == slave
        slave.terminate()

        then:
        notThrown Exception
        jenkinsRule.jenkins.get().getNode(slave.name) == null
    }

    def "should return the mac cloud of the slave"() {
        setup:
        List<MacHost> macHosts = MacPojoBuilder.buildMacHost()
        MacComputerJNLPConnector connector = MacPojoBuilder.buildConnector(jenkinsRule)
        MacUser user = MacPojoBuilder.buildUser()
        MacCloud cloud = new MacCloud("test", macHosts, connector, new Integer(1))
        MacSlave slave = new MacSlave("test","testLabel", user, macHosts.get(0), connector.createLauncher(macHosts.get(0), user), new Integer(1))

        when:
        jenkinsRule.jenkins.get().clouds.add(cloud)
        jenkinsRule.jenkins.get().addNode(slave)
        assert jenkinsRule.jenkins.get().getNode(slave.name) == slave
        MacCloud result = slave.getCloud()
        
        then:
        notThrown Exception
        result == cloud
    }

    def "should return node name on mac cloud"() {
        setup:
        List<MacHost> macHosts = MacPojoBuilder.buildMacHost()
        MacComputerJNLPConnector connector = MacPojoBuilder.buildConnector(jenkinsRule)
        MacUser user = MacPojoBuilder.buildUser()
        MacCloud cloud = new MacCloud("test", macHosts, connector, new Integer(1))
        MacSlave slave = new MacSlave("test","testLabel", user, macHosts.get(0), connector.createLauncher(macHosts.get(0), user), new Integer(1))

        when:
        jenkinsRule.jenkins.get().clouds.add(cloud)
        jenkinsRule.jenkins.get().addNode(slave)
        assert jenkinsRule.jenkins.get().getNode(slave.name) == slave
        String result = slave.displayName

        then:
        notThrown Exception
        result == user.username + " on " + cloud.name
    }
    
    def "should return node name equals username"() {
        setup:
        List<MacHost> macHosts = MacPojoBuilder.buildMacHost()
        MacComputerJNLPConnector connector = MacPojoBuilder.buildConnector(jenkinsRule)
        MacUser user = MacPojoBuilder.buildUser()
        MacSlave slave = new MacSlave(null,"testLabel", user, macHosts.get(0), connector.createLauncher(macHosts.get(0), user), new Integer(1))

        when:
        jenkinsRule.jenkins.get().addNode(slave)
        assert jenkinsRule.jenkins.get().getNode(slave.name) == slave
        String result = slave.displayName

        then:
        notThrown Exception
        result == user.username
    }
}
