package fr.jenkins.plugins.mac.MacCloud

import fr.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title: Messages.Cloud_Name(), field:'name') {
    f.textbox(default:'mac')
}
f.entry(title: Messages.Cloud_Disabled(), field:'disabled') {
    f.checkbox()
}

f.entry(title:Messages.Host_Title()) {
    f.repeatableHeteroProperty(
            field:"macHosts",
            addCaption: "Add Mac Host",
            oneEach:"true",
            repeatableDeleteButton:'true'
            )
}

f.section(title:Messages.Cloud_AgentsProperties()) {
    f.entry(title:Messages.Cloud_Labels(), field:'labelString') {
        f.textbox()
    }
    f.dropdownDescriptorSelector(title:'Connect method', field:'connector')
}
