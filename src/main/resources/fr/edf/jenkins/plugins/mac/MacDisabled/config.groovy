import fr.edf.jenkins.plugins.mac.Messages

def f = namespace(lib.FormTagLib)

f.entry(title:Messages.MacDisabled_Disable(), field:'disabledByChoice') {
    f.checkbox(default:false)
}
f.invisibleEntry() {
    f.checkbox(field:'disabledBySystem')
}
f.invisibleEntry() {
    f.textbox(field:'whenDisabledBySystemString')
}
f.invisibleEntry() {
    f.textbox(field:'whenReEnableBySystemString')
}
f.invisibleEntry() {
    f.textbox(field:'reasonWhyDisabledBySystem')
}
f.invisibleEntry() {
    f.textbox(field:'exceptionWhenDisabledBySystemString')
}