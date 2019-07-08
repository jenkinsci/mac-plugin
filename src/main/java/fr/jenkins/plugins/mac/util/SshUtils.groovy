package fr.jenkins.plugins.mac.util

import org.antlr.v4.runtime.misc.NotNull
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse

import com.trilead.ssh2.ChannelCondition
import com.trilead.ssh2.Connection
import com.trilead.ssh2.Session

import groovy.util.logging.Slf4j

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
    @Restricted(NoExternalUse)
    static String executeCommand(@NotNull Connection conn, @NotNull boolean ignoreError, @NotNull String command) throws Exception {
        Session session = null
        try {
            session = conn.openSession()
            log.info("Executing command " + command)
            session.execCommand(command)
            session.waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5000)
            log.info("Exit SIGNAL : {}", session.getExitSignal())
            log.info("Exit STATUS : {}", null != session.getExitStatus() ? session.getExitStatus().intValue() : null)
            session.close()
            String out = convertInputStream(session.getStdout())
            String err = convertInputStream(session.getStderr())
            log.info(out)
            log.error(err)
            if(!ignoreError && null != session.exitStatus && session.exitStatus.intValue() != 0) {
                String error = StringUtils.isNotEmpty(err) ? err : String.format("Failed to execute command %s", command)
                log.error(error)
                throw new Exception(error)
            }
            return StringUtils.isNotEmpty(out) ? out : String.format("Executed command %s", command)
        } catch(Exception e) {
            if(session != null) session.close()
            throw e
        }
    }

    /**
     * return the string value of an InputStream
     * @param out
     * @return output
     */
    static String convertInputStream(@NotNull InputStream out) {
        StringWriter result = new StringWriter()
        IOUtils.copy(out, result, UTF8)
        return result.toString()
    }

}
