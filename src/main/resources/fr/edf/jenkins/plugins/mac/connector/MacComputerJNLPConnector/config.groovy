package fr.edf.jenkins.plugins.mac.MacCloud

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: _(Messages.Connector_WebSocket()), field: 'webSocket') {
    f.checkbox()
}

f.entry(title: _(Messages.Connector_JenkinsUrl()), field: 'jenkinsUrl') {
    f.textbox()
}
