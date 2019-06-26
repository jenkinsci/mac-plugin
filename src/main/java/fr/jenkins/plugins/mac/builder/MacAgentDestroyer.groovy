package fr.jenkins.plugins.mac.builder

import org.kohsuke.stapler.DataBoundConstructor

import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.BuildListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Publisher
import hudson.tasks.Recorder

class MacAgentDestroyer extends Recorder {

    @DataBoundConstructor
    MacAgentDestroyer() {
    }
    
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

    }

    @Extension
    static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        String getDisplayName() {
            return "Stop and destroy Mac agent";
        }
    }
}
