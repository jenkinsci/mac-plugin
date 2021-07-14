package fr.edf.jenkins.plugins.mac.ssh

/**
 * Exception of SSH Command on Mac plugin
 * @author Mathieu DELROCQ
 *
 */
class SSHCommandException extends Exception {

    /** Cannot create MacUser on host %s */
    public static final String CREATE_MAC_USER_ERROR_MESSAGE = "Cannot create MacUser on host %s"

    /** An error occured while deleting user %s on mac %s */
    public static final String DELETE_MAC_USER_ERROR_MESSAGE = "An error occured while deleting user %s on mac %s"

    /** Cannot connect Mac %s with user %s to jenkins with JNLP */
    public static final String JNLP_CONNECTION_ERROR_MESSAGE = "Cannot connect Mac %s with user %s to jenkins with JNLP"

    /** Cannot get existing users on mac %s */
    public static final String LIST_USERS_ERROR_MESSAGE = "Cannot get existing users on mac %s : %s"

    /** Cannot transfertkeychain file %s on mac %s */
    public static final String TRANSFER_KEYCHAIN_ERROR_MESSAGE = "Cannot transfer keychain file on mac %s : %s"

    /** Cannot transfer host file %s on mac %s */
    public static final String TRANSFERT_HOST_FILE_ERROR_MESSAGE = "Cannot transfer hostFile file on mac %s : %s"

    /** An error occred when running entry point command %s on the mac %s */
    public static final String PRELAUNCHCOMMAND_ERROR_MESSAGE = "An error occred when running pre-launch command %s on the mac %s with the user %s"

    /**
     * Constructor with message and cause
     * @param message
     * @param cause
     */
    SSHCommandException(String message, Exception cause) {
        super(message, cause)
    }
}
