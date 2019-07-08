package fr.jenkins.plugins.mac

import javax.annotation.CheckForNull

import groovy.util.logging.Slf4j
import hudson.slaves.SlaveComputer

@Slf4j
class MacComputer extends SlaveComputer {
    
    public MacComputer(MacTransientNode node) {
        super(node)
    }
    
    @CheckForNull
    @Override
    public MacTransientNode getNode() {
        return (MacTransientNode) super.getNode()
    }
}
