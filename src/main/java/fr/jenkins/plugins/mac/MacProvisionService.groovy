package fr.jenkins.plugins.mac

import org.apache.commons.lang.RandomStringUtils

import com.trilead.ssh2.Session

import fr.jenkins.plugins.mac.connector.MacComputerConnector
import fr.jenkins.plugins.mac.util.Constants
import hudson.slaves.ComputerLauncher
import hudson.util.Secret
import jenkins.model.Jenkins

@Singleton
class MacProvisionService {

    Map<String, List> connectionMap = new HashMap()

    public MacUser generateUser() {
        String password = RandomStringUtils.random(10, true, true);
        String username = String.format(Constants.USERNAME_PATTERN, RandomStringUtils.random(5, true, true).toLowerCase())
        String workdir = String.format("/Users/%s/", username)
        return new MacUser(username:username, password:Secret.fromString(password), workdir:workdir)
    }

    public void provisionAgent(MacCloud cloud, MacUser user) {
        ComputerLauncher launcher = cloud.connector.createLauncher(cloud, user)
        MacTransientNode node = new MacTransientNode(cloud.name, cloud.labels, user, launcher)
        if(!connectionMap.containsKey(cloud.name)) {
            connectionMap.put(cloud.name, new ArrayList())
        }
        connectionMap.get(cloud.name).add(node.name)
        Jenkins.get().addNode(node)
    }
}