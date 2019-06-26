package fr.jenkins.plugins.mac

import groovy.util.logging.Slf4j
import hudson.model.Slave

@Slf4j
class MacTransientNode extends Slave {
    final String username
    final MacHost macHost
    final String cloudId
}
