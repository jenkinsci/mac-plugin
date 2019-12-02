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

import fr.edf.jenkins.plugins.mac.util.Constants
import hudson.Extension
import jenkins.model.Jenkins

class KeychainFileCredentials extends BaseStandardCredentials {

    String fileName
    String filePath

    public String getFileName() {
        return this.fileName
    }
    
    public String getFilePath() {
        return this.filePath
    }

    @Restricted(NoExternalUse)
    void writeKeychain(File output) throws IOException {
        FileUtils.writeByteArrayToFile(output, this.secretBytes.getPlainData())
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
        StringBuilder filePath = new StringBuilder(Jenkins.get().getRootDir())
        filePath.append(Constants.KEYCHAIN_FOLDER)
        filePath.append(this.id)
        filePath.append("/")
        this.filePath = filePath.toString()
        file.write(new File(this.filePath + this.fileName))
        
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
