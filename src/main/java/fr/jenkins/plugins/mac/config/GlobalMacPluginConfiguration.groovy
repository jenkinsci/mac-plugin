package fr.jenkins.plugins.mac.config

import javax.annotation.Nullable

import org.kohsuke.stapler.StaplerRequest
import hudson.Extension
import hudson.model.Descriptor.FormException
import jenkins.model.GlobalConfiguration
import net.sf.json.JSONObject

/**
 * 
 * @author Mathieu DELROCQ
 *
 */
@Extension
class GlobalMacPluginConfiguration extends GlobalConfiguration {

    List<MacPluginConfiguration> macPluginConfigs
    
    GlobalMacPluginConfiguration() {
        load()
    }
    
    @Override
    boolean configure(StaplerRequest req, final JSONObject json) throws FormException {
        macPluginConfigs = req.bindJSONToList(MacPluginConfiguration.class, json.get('macPluginConfigs'))
        save()
        return super.configure(req, json)
    }

    List<MacPluginConfiguration> getMacPluginConfigs() {
        return macPluginConfigs
    }

    void setMacPluginConfigs(List<MacPluginConfiguration> macPluginConfigs) {
        this.macPluginConfigs = macPluginConfigs
    }

    static @Nullable getGlobalMacPluginConfiguration() {
        return all().get(GlobalMacPluginConfiguration)
    }
}
