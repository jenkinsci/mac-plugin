package fr.jenkins.plugins.mac.config.MacPluginConfiguration

import fr.jenkins.plugins.mac.config.MacPluginConfiguration
import fr.jenkins.plugins.mac.config.Messages

def f = namespace(lib.FormTagLib)
def c = namespace(lib.CredentialsTagLib)

f.section(title: Messages.Configuration_Title()) {

    f.entry(title: _(Messages.Configuration_Host()), field: 'host') {
        f.textbox(clazz: 'required')
    }

    f.entry(title: _(Messages.Configuration_Port()), field: 'port') {
        f.number(clazz: 'required', default: 22, min: 1)
    }

    f.entry(title: _(Messages.Configuration_MaxUsers()), field: 'maxUsers') {
        f.number(clazz: 'required', min: 1)
    }

    f.entry(title: _(Messages.Configuration_Credentials()), field: 'credentialsId') {
        c.select(context: app, includeUser: false, expressionAllowed: false)
    }

    f.entry(title: _(Messages.Configuration_ConnectionTimeout()), field: 'connectionTimeout') {
        f.number(clazz: 'required', default: 0, min: 0)
    }

    f.entry(title: _(Messages.Configuration_ReadTimeout()), field: 'readTimeout') {
        f.number(clazz: 'required', default: 0, min: 0)
    }

    f.entry(title: _(Messages.Configuration_KexTimeout()), field: 'kexTimeout') {
        f.number(clazz: 'required', default: 0, min: 0)
    }

    f.block() {
        f.validateButton(
                title: _(Messages.Configuration_TestConnection()),
                progress: _('Testing...'),
                method: 'verifyConnection',
                with: 'host,port,credentialsId,connectionTimeout,readTimeout,kexTimeout'
                )
    }
}
