package fr.edf.jenkins.plugins.mac

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import fr.edf.jenkins.plugins.mac.test.builders.MacPojoBuilder
import spock.lang.Specification

class MacHostTest extends Specification {

    @Rule
    JenkinsRule jenkinsRule

    def "Should build and set preLaunch commands to MacHost"() {
        setup:
        MacHost host = MacPojoBuilder.buildMacHost().get(0)
        final String newLine = "\n"
        String preLaunchCommandsString = "ls"+newLine+"whoami"+newLine+"ifconfig";

        when:
        host.setPreLaunchCommands(preLaunchCommandsString)

        then:
        notThrown Exception
        host.getPreLaunchCommands().contentEquals(preLaunchCommandsString)
        host.preLaunchCommandsList.size() == 3
        host.preLaunchCommandsList.get(0).contentEquals("ls")
        host.preLaunchCommandsList.get(1).contentEquals("whoami")
        host.preLaunchCommandsList.get(2).contentEquals("ifconfig")
    }

    def "Should build and set preLaunch commands to MacHost without empty line"() {
        setup:
        MacHost host = MacPojoBuilder.buildMacHost().get(0)
        final String newLine = "\n"
        String preLaunchCommandsString = "ls"+newLine+"whoami"+newLine+"         "+newLine+"ifconfig";

        when:
        host.setPreLaunchCommands(preLaunchCommandsString)

        then:
        notThrown Exception
        !host.getPreLaunchCommands().contentEquals(preLaunchCommandsString)
        host.preLaunchCommandsList.size() == 3
        host.preLaunchCommandsList.get(0).contentEquals("ls")
        host.preLaunchCommandsList.get(1).contentEquals("whoami")
        host.preLaunchCommandsList.get(2).contentEquals("ifconfig")
    }

    def "preLaunch commands should not be null if string is empty"() {
        setup:
        MacHost host = MacPojoBuilder.buildMacHost().get(0)
        final String newLine = "\n"
        String preLaunchCommandsString = "";

        when:
        host.setPreLaunchCommands(preLaunchCommandsString)

        then:
        notThrown Exception
        host.getPreLaunchCommands().isEmpty()
        host.preLaunchCommandsList != null
        host.preLaunchCommandsList.isEmpty()
    }

    def "preLaunch commands should not be null if string is null"() {
        setup:
        MacHost host = MacPojoBuilder.buildMacHost().get(0)
        final String newLine = "\n"
        String preLaunchCommandsString = "";

        when:
        host.setPreLaunchCommands(preLaunchCommandsString)

        then:
        notThrown Exception
        host.getPreLaunchCommands().isEmpty()
        host.preLaunchCommandsList != null
        host.preLaunchCommandsList.isEmpty()
    }

    def "preLaunch commands should not be null if string is blank"() {
        setup:
        MacHost host = MacPojoBuilder.buildMacHost().get(0)
        final String newLine = "\n"
        String preLaunchCommandsString = "                   \n                 ";

        when:
        host.setPreLaunchCommands(preLaunchCommandsString)

        then:
        notThrown Exception
        host.getPreLaunchCommands().isEmpty()
        host.preLaunchCommandsList != null
        host.preLaunchCommandsList.isEmpty()
    }

    def "get preLaunchCommand should not return null"() {
        setup:
        MacHost host = MacPojoBuilder.buildMacHost().get(0)
        String preLaunchCommandsString = "";

        when:
        host.setPreLaunchCommands(preLaunchCommandsString)

        then:
        notThrown Exception
        host.getPreLaunchCommands().isEmpty()
    }
}
