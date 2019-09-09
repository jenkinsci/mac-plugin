package fr.jenkins.plugins.mac.MacCloud

import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: Messages.Cloud_Name(), field:'name') {
    f.textbox(default:'mac')
}

f.entry(title:Messages.Host_Title()) {
    f.repeatableHeteroProperty(
            field:"macHosts",
            hasHeader: "true",
            addCaption: "Add Mac Host",
            oneEach:"false",
            repeatableDeleteButton:'true'
            )
}

f.section(title:Messages.Cloud_AgentsProperties()) {
    f.entry(title:Messages.Cloud_Labels(), field:'labelString') {
        f.textbox()
    }
    f.entry(title:Messages.Cloud_IdleMinutes(), field:'idleMinutes') {
        f.number(clazz: 'required', min: 1, default: 1)
    }
    f.dropdownDescriptorSelector(title:'Connect method', field:'connector')
}
