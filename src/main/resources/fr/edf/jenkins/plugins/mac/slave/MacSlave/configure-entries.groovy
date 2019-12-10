package fr.jenkins.plugins.mac.slave.MacSlave

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: Messages.MacSlave_Description(), help: '/help/system-config/master-slave/description.html') {
    f.textbox(field:'nodeDescription')
}

f.entry(title:Messages.MacSlave_Executors(), field:'numExecutors') {
    f.textbox()
}

f.entry(title: Messages.MacSlave_RemoteFSRoot(), field: 'remoteFS') {
    f.textbox()
}

f.slave_mode(name: 'mode', node: it)

f.descriptorList(title: Messages.MacSlave_NodeProperties(), descriptors: h.getNodePropertyDescriptors(descriptor.clazz), field: 'nodeProperties')