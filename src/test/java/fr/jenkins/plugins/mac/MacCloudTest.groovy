package fr.jenkins.plugins.mac

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import fr.jenkins.plugins.mac.test.builders.MacPojoBuilder
import fr.jenkins.plugins.mac.connector.MacComputerConnector
import fr.jenkins.plugins.mac.connector.MacComputerJNLPConnector
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.BuildListener
import hudson.model.Cause
import hudson.model.FreeStyleProject
import hudson.model.Job
import hudson.model.Label
import hudson.util.OneShotEvent
import spock.lang.Specification

class MacCloudTest extends Specification {

    @Rule
    JenkinsRule jenkinsRule

    def "should create cloud"() {
        setup:
        MacCloud cloud = new MacCloud("test", MacPojoBuilder.buildMacHost(), MacPojoBuilder.buildConnector(jenkinsRule), "testLabel", false)

        when:
        jenkinsRule.jenkins.clouds.add(cloud)

        then:
        notThrown Exception
        jenkinsRule.jenkins.clouds.size() == 1
        cloud == jenkinsRule.jenkins.getCloud("test")
    }

    def "should call provision method"() {
        setup:
        MacCloud cloud = new MacCloud("test", MacPojoBuilder.buildMacHost(), MacPojoBuilder.buildConnector(jenkinsRule), "testLabel", false)
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
