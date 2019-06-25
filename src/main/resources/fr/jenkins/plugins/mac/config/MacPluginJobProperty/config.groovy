package fr.jenkins.plugins.mac.config.MacPluginConfiguration

import fr.jenkins.plugins.mac.config.MacPluginConfiguration
import fr.jenkins.plugins.mac.config.Messages

def f = namespace(lib.FormTagLib)
def c = namespace(lib.CredentialsTagLib)

f.section(title: Messages.JobConfiguration_Title()) {
    f.entry(title:Messages.JobConfiguration_MacList(), field:'macConfig') {
        f.select()
    }
}