package fr.jenkins.plugins.mac.MacHost

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)
def c = namespace(lib.CredentialsTagLib)

f.entry(title: Messages.Host_Disabled(), field:'disabled') {
    f.checkbox()
}

f.entry(title:Messages.Cloud_Labels(), field:'labelString') {
    f.textbox()
}

f.entry(title: Messages.Host_MaxTries(), field: 'maxTries') {
    f.number(clazz: 'required', min: 1, default: 5)
}

f.entry(title: _(Messages.Host_Host()), field: 'host') {
    f.textbox(clazz: 'required')
}

f.entry(title: _(Messages.Host_Port()), field: 'port') {
    f.number(clazz: 'required', default: 22, min: 1)
}

f.entry(title: _(Messages.Host_MaxUsers()), field: 'maxUsers') {
    f.number(clazz: 'required', min: 1)
}

f.entry(title: _(Messages.Host_Credentials()), field: 'credentialsId') {
    c.select(context: app, includeUser: false, expressionAllowed: false)
}

f.entry(title: _(Messages.Host_ConnectionTimeout()), field: 'connectionTimeout') {
    f.number(clazz: 'required', default: 15, min: 5)
}

f.entry(title: _(Messages.Host_ReadTimeout()), field: 'readTimeout') {
    f.number(clazz: 'required', default: 60, min: 30)
}

//f.entry(title: _(Messages.Host_KexTimeout()), field: 'kexTimeout') {
//    f.number(clazz: 'required', default: 0, min: 0)
//}

f.entry(title: _(Messages.Host_AgentConnectionTimeout()), field: 'agentConnectionTimeout') {
    f.number(clazz: 'required', default: 15, min: 15)
}

f.block() {
    f.validateButton(
            title: _(Messages.Host_TestConnection()),
            progress: _('Testing...'),
            method: 'verifyConnection',
            with: 'host,port,credentialsId,connectionTimeout,readTimeout'
            )
}