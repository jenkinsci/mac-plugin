package fr.jenkins.plugins.mac.MacCloud

import fr.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: _(Messages.Cloud_JenkinsUrl()), field: 'jenkinsUrl') {
    f.textbox()
}
f.entry(title: 'arguments', field: 'entryPointArguments') {
    f.textarea()
}
