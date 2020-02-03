package fr.edf.jenkins.plugins.mac.ssh.key.verifiers.ManualProvidedMacHostKeyVerifier

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: _(Messages.ManualKeyProvidedHostKeyVerifier_HostKey()), field:'key') {
    f.textarea(checkMethod: 'doCheckKey')
}
