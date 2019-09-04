package fr.jenkins.plugins.mac

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import fr.jenkins.plugins.mac.test.builders.MacPojoBuilder
import hudson.model.FreeStyleProject
import hudson.model.Label
import hudson.slaves.Cloud
import spock.lang.Specification

class MacCloudTest extends Specification {

    @Rule
    JenkinsRule jenkinsRule

    def "should create cloud"() {
        setup:
        MacCloud cloud = new MacCloud("test", MacPojoBuilder.buildMacHost(), MacPojoBuilder.buildConnector(jenkinsRule), "testLabel", new Integer(1))

        when:
        jenkinsRule.jenkins.clouds.add(cloud)

        then:
        notThrown Exception
        jenkinsRule.jenkins.clouds.size() == 1
        cloud == jenkinsRule.jenkins.getCloud("test")
    }

    def "should call provision method"() {
        setup:
        MacCloud cloud = new MacCloud("test", MacPojoBuilder.buildMacHost(), MacPojoBuilder.buildConnector(jenkinsRule), "testLabel", new Integer(1))
        jenkinsRule.jenkins.clouds.add(cloud)
        FreeStyleProject project = jenkinsRule.createFreeStyleProject("test")
        project.setAssignedLabel(Label.parse("testLabel").getAt(0))

        when:
        boolean isBuilt = project.scheduleBuild2(1)
        //TODO wait until MacCloud.provision is called
        then:
        notThrown Exception
        isBuilt == true
    }
    
}
