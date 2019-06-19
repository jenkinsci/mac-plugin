package fr.jenkins.plugins.mac.utils

import javax.validation.constraints.NotNull

import hudson.util.FormValidation

/**
 * 
 * @author Mathieu DELROCQ
 *
 */
class FormUtils {

    /**
     * Transform the host value to an URI
     * @param String host
     * @return URI
     */
    static URI getUri (@NotNull String host) {
        if (!(host.startsWith("http://") || host.startsWith("https://"))) {
            host = "http://" + host
        }
        if (!host.endsWith("/")) {
            host += "/"
        }
        try {
            return new URI(host)
        } catch(Exception e) {
            return null
        }
        
    }

    /**
     * Validate the given host
     * @see InetAddress.getByName()
     * @param host
     * @return FormValidation
     */
    static FormValidation validateHost(@NotNull String host) {
        try {
            if(host) {
                InetAddress inetAddress = InetAddress.getByName(host)
            }
            return FormValidation.ok()
        } catch(UnknownHostException uhe) {
            return FormValidation.error("The given host is not valid")
        } catch(SecurityException se) {
            return FormValidation.error("Cannot validate the host due to security restriction")
        }
    }

    /**
     * Return an error message if the input value is empty
     * @param value
     * @param error
     * @return
     */
    static FormValidation validateNotEmpty(@NotNull String value, @NotNull String error) {
        if (!value) {
            return FormValidation.error(error)
        }
        return FormValidation.ok()
    }
}
