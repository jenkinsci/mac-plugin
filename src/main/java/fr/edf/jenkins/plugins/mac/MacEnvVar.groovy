package fr.edf.jenkins.plugins.mac


import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import hudson.Extension
import hudson.model.Describable
import hudson.model.Descriptor
import jenkins.model.Jenkins

class MacEnvVar implements Describable<MacEnvVar> {

    String key
    String value

    @DataBoundConstructor
    MacEnvVar(String key, String value) {
        this.key = key
        this.value = value
    }

    @DataBoundSetter
    setKey(String key) {
        this.key = key
    }

    @DataBoundSetter
    setValue(String value) {
        this.value = value
    }

    @Override
    public Descriptor<MacEnvVar> getDescriptor() {
        return Jenkins.get().getDescriptorOrDie(this.getClass())
    }

    @Extension
    static class DescriptorImpl extends Descriptor<MacEnvVar> {
        /**
         * {@inheritDoc}
         */
        @Override
        String getDisplayName() {
            return Messages.EnvVar_DisplayName()
        }
    }
}
