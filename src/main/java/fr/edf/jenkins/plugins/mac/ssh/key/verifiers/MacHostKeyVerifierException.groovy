package fr.edf.jenkins.plugins.mac.ssh.key.verifiers

class MacHostKeyVerifierException extends Exception {

    /**
     * Constructor with message
     * @param message
     */
    MacHostKeyVerifierException(String message) {
        super(message)
    }

    /**
     * Constructor with message and cause
     * @param message
     * @param cause
     */
    MacHostKeyVerifierException(String message, Exception cause) {
        super(message, cause)
    }
}
