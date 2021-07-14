package fr.edf.jenkins.plugins.mac.MacHost

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)
def c = namespace(lib.CredentialsTagLib)


f.entry(title: _(Messages.Host_Host()), field: 'host') {
    f.textbox(clazz: 'required', checkMethod: 'post')
}

f.entry(title: Messages.Host_Disabled(), field:'disabled') {
    f.checkbox()
}

f.advanced(title:Messages.Host_Details()) {

    f.entry(title:Messages.Cloud_Labels(), field:'labelString') {
        f.textbox()
    }

    f.entry(title: Messages.Host_MaxTries(), field: 'maxTries') {
        f.number(clazz: 'required', min: 1, default: 5)
    }

    f.entry(title: _(Messages.Host_Port()), field: 'port') {
        f.number(clazz: 'required', default: 22, min: 1)
    }

    f.entry(title: _(Messages.MacHostKeyVerifier_HostKey()), field:'key') {
        f.textarea(clazz: 'required', checkMethod: 'post')
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

    f.entry(title: _(Messages.Host_AgentConnectionTimeout()), field: 'agentConnectionTimeout') {
        f.number(clazz: 'required', default: 15, min: 15)
    }

    f.block() {
        f.validateButton(
                title: _(Messages.Host_TestConnection()),
                progress: _('Testing...'),
                method: 'verifyConnection',
                with: 'host,port,credentialsId,key'
                )
    }

    f.optionalBlock(title: _(Messages.Keychain_Title()), field: 'uploadKeychain',
    checked: null != instance ? instance.uploadKeychain : false, inline: 'true') {
        f.entry(title:_(Messages.Keychain_DisplayName()), field:"fileCredentialsId") {
            c.select(context: app, includeUser: false, expressionAllowed: false)
        }
    }

    f.entry(title:Messages.Host_PreLaunchCommand(), field:'preLaunchCommands') {
        f.expandableTextbox()
    }

    f.entry(title: _(Messages.EnvVar_Title())) {
        f.repeatableHeteroProperty(
                field:'envVars',
                hasHeader: 'true',
                addCaption: Messages.EnvVar_Add(),
                deleteCaption:Messages.EnvVar_Delete(),
                oneEach:'false',
                repeatableDeleteButton:'true'
        )
    }

	f.entry(title: _(Messages.Host_File())) {
		f.repeatableHeteroProperty( field:'hostFiles',
                hasHeader: 'true',
                addCaption: Messages.HostFile_Add(),
                deleteCaption:Messages.HostFile_Delete(),
                oneEach:'false',
                repeatableDeleteButton:'true'
        )
	}
}
