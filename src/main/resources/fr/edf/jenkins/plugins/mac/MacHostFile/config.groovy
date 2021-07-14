package fr.edf.jenkins.plugins.mac.MacHostFile

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)
def c = namespace(lib.CredentialsTagLib)

f.entry(title:_(Messages.Keychain_DisplayName()), field:"hostFileCredentialsId") {
	c.select(context: app, includeUser: false, expressionAllowed: false)
}

f.entry(title: Messages.HostFile_HostPath(), field: 'hostPath') {
    f.textbox()
}
