package fr.edf.jenkins.plugins.mac.test.builders

import org.jvnet.hudson.test.JenkinsRule

import fr.edf.jenkins.plugins.mac.MacCloud
import fr.edf.jenkins.plugins.mac.MacEnvVar
import fr.edf.jenkins.plugins.mac.MacHost
import fr.edf.jenkins.plugins.mac.MacUser
import fr.edf.jenkins.plugins.mac.connector.MacComputerConnector
import fr.edf.jenkins.plugins.mac.connector.MacComputerJNLPConnector
import fr.edf.jenkins.plugins.mac.slave.MacSlave
import fr.edf.jenkins.plugins.mac.ssh.key.verifiers.MacHostKeyVerifier
import hudson.util.Secret

class MacPojoBuilder {

    static List<MacHost> buildMacHost() {
        MacHost host = new MacHost(
                'localhost', //host
                '1', //credentialsId
                0, //port
                10, //maxUsers
                5, //connectionTimeout
                5, //readTimeout
                15, //agentConnectionTimeout
                false, //disabled
                5, //maxTries
                "testLabel", //label
                Boolean.FALSE, //
                null ,
                buildEnvVars(), //envVars
                "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZw==", //macHostKeyVerifier
                "ls \n pwd \n whoami", //preLaunchCommands
                [] // host files
                )
        List<MacHost> hostList = new ArrayList()
        hostList.add(host)
        return hostList
    }

    static MacComputerConnector buildConnector(JenkinsRule jenkinsRule) {
        return new MacComputerJNLPConnector(jenkinsRule.getURL().toString())
    }

    static MacUser buildUser() {
        return new MacUser("toto", Secret.fromString("root"), "/Users/toto")
    }

    static List<MacEnvVar> buildEnvVars() {
        List<MacEnvVar> envVars = new ArrayList()
        envVars.add(new MacEnvVar("TEST1", "test1"))
        envVars.add(new MacEnvVar("TEST2", "test2"))
        return envVars
    }

    static MacCloud buildMacCloud(List<MacHost> hosts, MacComputerConnector connector) {
        return new MacCloud("test", hosts, connector, new Integer(1))
    }
    static MacSlave buildMacSlave(String cloudId, MacUser user, MacHost host, MacComputerConnector connector) {
        return new MacSlave(cloudId,"testLabel", user, host, connector.createLauncher(host, user), new Integer(1), Collections.EMPTY_LIST)
    }

    static MacHostKeyVerifier buildMacHostKeyVerifier() {
        String key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCqGKukO1De7zhZj6+H0qtjTkVxwTCpvKe4eCZ0FPqri0cb2JZfXJ/DgYSF6vUpwmJG8wVQZKjeGcjDOL5UlsuusFncCzWBQ7RKNUSesmQRMSGkVb1/3j+skZ6UtW+5u09lHNsj6tQ51s1SPrCBkedbNf0Tp0GbMJDyR4e9T04ZZw=="
        return new MacHostKeyVerifier(key)
    }
}
