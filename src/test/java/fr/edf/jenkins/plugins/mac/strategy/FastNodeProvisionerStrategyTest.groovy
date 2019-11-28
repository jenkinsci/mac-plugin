package fr.edf.jenkins.plugins.mac.strategy

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import fr.edf.jenkins.plugins.mac.MacCloud
import fr.edf.jenkins.plugins.mac.test.builders.MacPojoBuilder
import hudson.model.Computer
import hudson.model.Label
import hudson.model.LoadStatistics.LoadStatisticsSnapshot
import hudson.slaves.NodeProvisioner.PlannedNode
import hudson.slaves.NodeProvisioner.StrategyDecision
import hudson.slaves.NodeProvisioner.StrategyState
import spock.lang.Specification

public class FastNodeProvisionerStrategyTest extends Specification {

    @Rule
    JenkinsRule jenkins = new JenkinsRule()
    
    def "should not throw exception even if it cannot connect to the mac"() {
        setup:
        final List<PlannedNode> r = new ArrayList<>()
        LoadStatisticsSnapshot snapshot = buildSnapshot()
        StrategyState state = GroovyStub(StrategyState) {
            getLabel() >> Label.parse("label").getAt(0)
            getSnapshot() >> snapshot
        }
        MacCloud cloud = new MacCloud("test", MacPojoBuilder.buildMacHost(), MacPojoBuilder.buildConnector(jenkins), new Integer(1))
//        Cannot add a Mock of MacCloud in JenkinsRule...
//        GroovySpy(constructorArgs: ["test", MacPojoBuilder.buildMacHost(), MacPojoBuilder.buildConnector(jenkins), "testLabel", new Integer(1)]) {
//            canProvision("label") >> true
//            provision("label", _) >> r
//        }
        jenkins.jenkins.clouds.add(cloud)
        
        when:
        FastNodeProvisionerStrategy provisioner = new FastNodeProvisionerStrategy()
        StrategyDecision decision = provisioner.apply(state)
        
        then:
        notThrown Exception
        decision == StrategyDecision.CONSULT_REMAINING_STRATEGIES
    }
    
    def "should not throw exception and don't find any matched label"() {
        setup:
        final List<PlannedNode> r = new ArrayList<>()
        LoadStatisticsSnapshot snapshot = buildSnapshot()
        StrategyState state = GroovyStub(StrategyState) {
            getLabel() >> Label.parse("label").getAt(0)
            getSnapshot() >> snapshot
        }
        MacCloud cloud = new MacCloud("test", MacPojoBuilder.buildMacHost(), MacPojoBuilder.buildConnector(jenkins), new Integer(1))
//        Cannot add a Mock of MacCloud in JenkinsRule...
//        GroovySpy(constructorArgs: ["test", MacPojoBuilder.buildMacHost(), MacPojoBuilder.buildConnector(jenkins), "testLabel", new Integer(1)]) {
//            canProvision("label") >> true
//            provision("label", _) >> r
//        }
        jenkins.jenkins.clouds.add(cloud)
        
        when:
        FastNodeProvisionerStrategy provisioner = new FastNodeProvisionerStrategy()
        StrategyDecision decision = provisioner.apply(state)
        
        then:
        notThrown Exception
        decision == StrategyDecision.CONSULT_REMAINING_STRATEGIES
    }
    
    private LoadStatisticsSnapshot buildSnapshot() {
        LoadStatisticsSnapshot.Builder builder = new LoadStatisticsSnapshot.Builder()
        builder.withQueueLength(1)
        builder.with((Computer) null)
        
        return builder.build()
    }
}
