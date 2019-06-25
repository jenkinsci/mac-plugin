package fr.jenkins.plugins.mac.util

import com.trilead.ssh2.Session

import groovy.util.logging.Slf4j

import org.antlr.v4.runtime.misc.NotNull
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils

/**
 * Utilities to run SSH command.
 * @author Mathieu DELROCQ
 *
 */
@Slf4j
class SshUtils {
    
    private final static String UTF8 = "UTF-8"
    
    /**
     * Execute a command on the given session
     * @param session
     * @param command
     * @return output of the command
     * @throws Exception if cannot execute the command or if the command return an error
     */
    static String executeCommand(@NotNull Session session, @NotNull String command) throws Exception {
        session.execCommand(command)
        String error = getStderr(session.getStderr())
        if(StringUtils.isNotEmpty(error)) {
            log.error(error)
            throw new Exception(error)
        }
        return getStdout(session.getStdout())
    }

    /**
     * Get the output as a String
     * @param out
     * @return output
     */
    static String getStdout(@NotNull InputStream out) {
        StringWriter stdout = new StringWriter()
        IOUtils.copy(out, stdout, UTF8)
        return stdout.toString()
    }

    /**
     * Get the error output as a String
     * @param err
     * @return error output
     */
    static String getStderr(@NotNull InputStream err) {
        StringWriter stderr = new StringWriter()
        IOUtils.copy(err, stderr, UTF8)
        return stderr.toString()
    }
}
