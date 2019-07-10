package fr.jenkins.plugins.mac.MacCloud

import fr.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: Messages.Cloud_Name(), field:'name') {
    f.textbox(default:'mac')
}

f.property(field:'macHost')

f.section(title:Messages.Cloud_AgentsProperties()) {
    f.entry(title:Messages.Cloud_Labels(), field:'labelString') {
        f.textbox()
    }
    f.dropdownDescriptorSelector(title:'Connect method', field:'connector')
}
