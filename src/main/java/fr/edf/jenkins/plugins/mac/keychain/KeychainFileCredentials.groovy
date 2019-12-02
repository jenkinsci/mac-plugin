package fr.edf.jenkins.plugins.mac.keychain

import javax.annotation.CheckForNull

import org.apache.commons.fileupload.FileItem
import org.apache.commons.io.FileUtils
import org.jenkinsci.plugins.plaincredentials.FileCredentials
import org.jenkinsci.plugins.plaincredentials.impl.Messages
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.DoNotUse
import org.kohsuke.accmod.restrictions.NoExternalUse
import org.kohsuke.stapler.DataBoundConstructor

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.SecretBytes
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials.BaseStandardCredentialsDescriptor

import hudson.Extension

class KeychainFileCredentials extends BaseStandardCredentials implements FileCredentials {

    String fileName
    SecretBytes secretBytes

    @Override
    public String getFileName() {
        return this.fileName
    }

    @Restricted(DoNotUse)
    @Override
    public InputStream getContent() throws IOException {
        return null
    }

    @Restricted(NoExternalUse)
    public File getFile() throws IOException {
        return FileUtils.writeByteArrayToFile(new File(), this.secretBytes.getPlainData())
    }

    @DataBoundConstructor
    KeychainFileCredentials(@CheckForNull CredentialsScope scope, @CheckForNull String id,
    @CheckForNull String description, @CheckForNull FileItem file) {
        super(scope, id, description)
        String name = file != null ? file.getName() : ""
        if(!name) {
            throw new IllegalArgumentException("No file provided or resolved.")
        }
        this.fileName = name.replaceFirst("^.+[/\\\\]", "")
        this.secretBytes = SecretBytes.fromBytes(file.get())
    }

    /**
     * Descriptor of KeychainFileCredentials
     */
    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return fr.edf.jenkins.plugins.mac.Messages.Keychain_DisplayName();
        }

    }
}
