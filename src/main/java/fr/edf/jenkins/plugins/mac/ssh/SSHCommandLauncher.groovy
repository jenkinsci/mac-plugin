package fr.edf.jenkins.plugins.mac.ssh

import java.util.logging.Level
import java.util.logging.Logger

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
 * Runner of SSH command.
 * @author Mathieu DELROCQ
 *
 */
protected class SSHCommandLauncher {

    private static final Logger LOGGER = Logger.getLogger(SSHCommandLauncher.name)
    
    final static String UTF8 = "UTF-8"

    /**
     * Execute a command with the given connection
     * @param conn
     * @param ignoreError
     * @param command
     * @return
     * @throws Exception if cannot execute the command or if the command return an error
     */
    @Restricted(NoExternalUse)
    synchronized static String executeCommand(@NotNull Connection conn, @NotNull boolean ignoreError, @NotNull String command) throws Exception {
        Session session = null
        try {
            session = conn.openSession()
            LOGGER.log(Level.FINE, "Executing command {0}", command)
            session.execCommand(command)
            session.waitForCondition(ChannelCondition.EXIT_STATUS | ChannelCondition.EXIT_SIGNAL, 5000)
            LOGGER.log(Level.FINEST, "Exit SIGNAL : {0}", session.getExitSignal())
            LOGGER.log(Level.FINEST,"Exit STATUS : {0}", null != session.getExitStatus() ? session.getExitStatus().intValue() : null)
            session.close()
            String out = convertInputStream(session.getStdout())
            String err = convertInputStream(session.getStderr())
            LOGGER.log(Level.FINEST, out)
            LOGGER.log(Level.FINEST,err)
            if(!ignoreError && null != session.exitStatus && session.exitStatus.intValue() != 0) {
                String error = String.format("Failed to execute command %s", command)
                LOGGER.log(Level.SEVERE, error)
                throw new Exception(error)
            }
            LOGGER.log(Level.FINE, "Executed command {0} with exit status {1}", command, null != session.exitStatus ? session.exitStatus.intValue() : null)
            return StringUtils.isNotEmpty(out) ? out : StringUtils.isNotEmpty(err) ? err : ""
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
    private static String convertInputStream(@NotNull InputStream out) {
        StringWriter result = new StringWriter()
        IOUtils.copy(out, result, UTF8)
        return result.toString()
    }
}