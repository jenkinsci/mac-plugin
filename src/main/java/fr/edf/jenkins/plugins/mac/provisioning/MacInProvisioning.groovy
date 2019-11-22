package fr.edf.jenkins.plugins.mac.provisioning

import java.util.logging.Logger

import javax.annotation.CheckForNull

import fr.edf.jenkins.plugins.mac.slave.MacSlave
import hudson.Extension
import hudson.model.Computer
import hudson.model.Label
import hudson.model.Node

@Extension
class MacInProvisioning extends InProvisioning {

    private static boolean isNotAcceptingTasks(Node n) {
        Computer computer = n.toComputer()
        return computer != null && (computer.isLaunchSupported() 
                || !computer.isAcceptingTasks() // Launcher hasn't been called yet
                || !n.isAcceptingTasks()) // node is not ready yet
    }

    @Override
    public Set<String> getInProvisioning(@CheckForNull Label label) {
        if (label != null) {
            return label.getNodes().stream()
                    .filter { node ->
                        node instanceof MacSlave
                    }
                    .filter { node ->
                        MacInProvisioning.isNotAcceptingTasks(node)
                    }
                    .collect([] as HashSet){it.name}
        } else {
            return Collections.emptySet();
        }
    }
}
