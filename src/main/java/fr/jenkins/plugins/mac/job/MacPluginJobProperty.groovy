package fr.jenkins.plugins.mac.job

import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import fr.jenkins.plugins.mac.config.GlobalMacPluginConfiguration
import fr.jenkins.plugins.mac.config.MacPluginConfiguration
import fr.jenkins.plugins.mac.util.FormUtils
import hudson.Extension
import hudson.model.AbstractProject
import hudson.model.Job
import hudson.model.JobProperty
import hudson.model.JobPropertyDescriptor
import hudson.util.ListBoxModel
import jenkins.model.OptionalJobProperty
import jenkins.model.OptionalJobProperty.OptionalJobPropertyDescriptor

/**
 * Properties in Job configuration for mac-plugin
 * @author Mathieu DELROCQ
 *
 */
class MacPluginJobProperty extends JobProperty<Job<?, ?>> {

    String macConfigHost

    @DataBoundConstructor
    MacPluginJobProperty(String macConfigHost) {
        this.macConfigHost = macConfigHost
    }

    public String getMacConfigHost() {
        return macConfigHost;
    }

    @DataBoundSetter
    public void setMacConfigHost(String macConfigHost) {
        this.macConfigHost = macConfigHost;
    }

    @Extension
    static class DescriptorImpl extends JobPropertyDescriptor {

        /**
         * Fill the Mac configured in the global configuration in the dropdown list
         * @return ListBoxModel
         */
        ListBoxModel doFillServerUrlItems() {
            GlobalMacPluginConfiguration globalConf = GlobalMacPluginConfiguration.getGlobalMacPluginConfiguration()
            ListBoxModel listBox = FormUtils.newListBoxModel(
                    {it.host.toString()},
                    {it.host.toString()},
                    globalConf.getMacPluginConfigs())
            return listBox
        }
    }
}
