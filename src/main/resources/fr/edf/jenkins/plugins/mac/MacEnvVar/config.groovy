package fr.jenkins.plugins.mac.MacEnvVar

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title:Messages.EnvVar_Key(), field:'key') {
    f.textbox()
}

f.entry(title: Messages.EnvVar_Value(), field: 'value') {
    f.textbox()
}