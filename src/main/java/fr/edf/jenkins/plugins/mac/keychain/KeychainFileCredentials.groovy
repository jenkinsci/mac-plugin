package fr.edf.jenkins.plugins.mac.keychain

import javax.annotation.CheckForNull

import org.apache.commons.fileupload.FileItem
import org.apache.commons.lang.StringUtils
import org.jenkinsci.plugins.plaincredentials.FileCredentials
import org.kohsuke.stapler.DataBoundConstructor

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.SecretBytes
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials.BaseStandardCredentialsDescriptor

import hudson.Extension

class KeychainFileCredentials extends BaseStandardCredentials implements FileCredentials {

    String fileName
    SecretBytes secretBytes
    //String filePath

    public String getFileName() {
        return this.fileName
    }
    
    public String getFilePath() {
        return this.filePath
    }
    
    @Override
    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(secretBytes.getPlainData())
    }

    @DataBoundConstructor
    KeychainFileCredentials(@CheckForNull CredentialsScope scope, @CheckForNull String id,
    @CheckForNull String description, @CheckForNull FileItem file) {
        super(scope, id, description)
        String name = file != null ? file.getName() : ""
        if(StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("No file provided or resolved.")
        }
        this.fileName = name.replaceFirst("^.+[/\\\\]", "")
        this.secretBytes = SecretBytes.fromBytes(file.get())
//        StringBuilder filePath = new StringBuilder(Jenkins.get().getRootDir())
//        filePath.append("/")
//        filePath.append(Constants.KEYCHAIN_FOLDER)
//        filePath.append(this.id)
//        filePath.append("/")
//        this.filePath = filePath.toString()
//        file.write(new File(this.filePath + this.fileName))
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
