package fr.jenkins.plugins.mac.config.GlobalMacPluginConfiguration

import fr.jenkins.plugins.mac.config.Messages

def f = namespace(lib.FormTagLib)
def c = namespace(lib.CredentialsTagLib)

f.section(title:Messages.Configuration_SectionTitle()) {
    f.entry(title:Messages.Configuration_DisplayMacs()) {
        f.repeatableHeteroProperty(
        field:"macPluginConfigs",
        addCaption: "New Mac",
        oneEach:"true",
        repeatableDeleteButton:'true'
        )
    }
}