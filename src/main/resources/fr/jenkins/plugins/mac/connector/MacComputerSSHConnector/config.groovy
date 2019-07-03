package fr.jenkins.plugins.mac.MacCloud

import fr.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: _(Messages.Cloud_JavaPath()), field: 'javaPath') {
    f.textbox()
}

f.entry(title: _(Messages.Cloud_JvmOptions()), field: 'jvmOptions') {
    f.textbox()
}

f.entry(title: _(Messages.Cloud_PrefixStartSlaveCmd()), field: 'prefixStartSlaveCmd') {
    f.textbox()
}

f.entry(title: _(Messages.Cloud_SuffixStartSlaveCmd()), field: 'suffixStartSlaveCmd') {
    f.textbox()
}
