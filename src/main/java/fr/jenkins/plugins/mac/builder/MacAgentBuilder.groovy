package fr.jenkins.plugins.mac.builder


import hudson.DescriptorExtensionList
import hudson.Extension
import hudson.FilePath
import hudson.Launcher
import hudson.model.AbstractProject
import hudson.model.Run
import hudson.model.TaskListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Builder
import jenkins.tasks.SimpleBuildStep

/**
 * 
 * @author Mathieu DELROCQ
 *
 */
class MacAgentBuilder extends Builder implements SimpleBuildStep {

    @Override
    public void perform(Run run, FilePath workspace, Launcher launcher, TaskListener listener)
    throws InterruptedException, IOException {
        // TODO Auto-generated method stub
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true
        }

        @Override
        public String getDisplayName() {
            return "Start/Stop Mac agents"
        }
    }
}
