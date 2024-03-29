package fr.edf.jenkins.plugins.mac

import java.util.concurrent.TimeUnit

import javax.annotation.Nonnull
import javax.annotation.Nullable

import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter
import org.kohsuke.stapler.QueryParameter
import org.kohsuke.stapler.verb.POST

import hudson.Extension
import hudson.Functions
import hudson.Util
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import hudson.util.FormValidation

public class MacDisabled extends AbstractDescribableImpl<MacDisabled> implements Serializable {

    static final long serialVersionUID = 1L

    boolean disabledByChoice = false

    transient boolean disabledBySystem
    transient long nanotimeWhenDisabledBySystem
    transient long nanotimeWhenReEnableBySystem
    transient String reasonWhyDisabledBySystem
    transient Throwable exceptionWhenDisabledBySystem

    // Persistence functionality

    @DataBoundConstructor
    public MacDisabled() {
        // Sonar happy
    }

    /**
     * Set a WindowsHost as disabled
     * 
     * @param disabledByChoice : boolean
     */
    @DataBoundSetter
    void setDisabledByChoice(final boolean disabledByChoice) {
        this.disabledByChoice = disabledByChoice
    }

    // Internal use functionality

    /**
     * Called from owning classes to record a problem that will cause
     * {@link #isDisabled()} to return true for a period.
     * 
     * @param reasonGiven            Human-readable String stating why.
     * @param durationInMilliseconds Length of time, in milliseconds, the
     *                               disablement should continue.
     * @param exception              Optional exception.
     */
    @Restricted(NoExternalUse.class)
    void disableBySystem(@Nonnull final String reasonGiven, final long durationInMilliseconds,
            @Nullable final Throwable exception) {
        final long durationInNanoseconds = TimeUnit.MILLISECONDS.toNanos(durationInMilliseconds)
        final long now = readTimeNowInNanoseconds()
        disabledBySystem = true
        nanotimeWhenDisabledBySystem = now
        nanotimeWhenReEnableBySystem = now + durationInNanoseconds
        reasonWhyDisabledBySystem = reasonGiven
        exceptionWhenDisabledBySystem = exception
    }

    /**
     * Indicates if we are currently disabled for any reason (either the user has
     * ticked the disable box or {@link #disableBySystem(String, long, Throwable)}
     * has been called recently).
     * 
     * @return true if we are currently disabled.
     */
    @Restricted(NoExternalUse.class)
    boolean isDisabled() {
        return isDisabledByChoice() || isDisabledBySystem()
    }

    boolean isDisabledBySystem() {
        if (disabledBySystem) {
            final long now = readTimeNowInNanoseconds()
            final long disabledTimeRemaining = nanotimeWhenReEnableBySystem - now
            if (disabledTimeRemaining > 0) {
                return true
            }
            disabledBySystem = false
            nanotimeWhenDisabledBySystem = 0L
            nanotimeWhenReEnableBySystem = 0L
            reasonWhyDisabledBySystem = null
            exceptionWhenDisabledBySystem = null
        }
        return false
    }

    /** @return How long ago this was disabled by the system, e.g. "3 min 0 sec". */
    String getWhenDisabledBySystemString() {
        if (!isDisabledBySystem()) {
            return ""
        }
        final long now = readTimeNowInNanoseconds()
        final long howLongAgoInNanoseconds = now - nanotimeWhenDisabledBySystem
        final long howLongAgoInMilliseconds = TimeUnit.NANOSECONDS.toMillis(howLongAgoInNanoseconds)
        return Util.getTimeSpanString(howLongAgoInMilliseconds)
    }

    /**
     * @return How long ago this will remain disabled by the system, e.g. "2 min 0
     *         sec".
     */
    String getWhenReEnableBySystemString() {
        final long now = readTimeNowInNanoseconds()
        if (!isDisabledBySystem()) {
            return ""
        }
        final long howSoonInNanoseconds = nanotimeWhenReEnableBySystem - now
        final long howSoonInMilliseconds = TimeUnit.NANOSECONDS.toMillis(howSoonInNanoseconds)
        return Util.getTimeSpanString(howSoonInMilliseconds)
    }

    String getReasonWhyDisabledBySystem() {
        if (!isDisabledBySystem()) {
            return ""
        }
        return reasonWhyDisabledBySystem
    }

    String getExceptionWhenDisabledBySystemString() {
        if (!isDisabledBySystem() || exceptionWhenDisabledBySystem == null) {
            return ""
        }
        return Functions.printThrowable(exceptionWhenDisabledBySystem)
    }

    @Extension
    static class DescriptorImpl extends Descriptor<MacDisabled> {

        @POST
        FormValidation doCheckDisabledByChoice(@QueryParameter boolean disabledByChoice,
                @QueryParameter boolean disabledBySystem, @QueryParameter String whenDisabledBySystemString,
                @QueryParameter String whenReEnableBySystemString, @QueryParameter String reasonWhyDisabledBySystem,
                @QueryParameter String exceptionWhenDisabledBySystemString) {
            if (disabledByChoice) {
                return FormValidation.warning("Note: Disabled.")
            }
            if (disabledBySystem) {
                final String reason = Util.fixNull(reasonWhyDisabledBySystem)
                final String disabledAgo = Util.fixNull(whenDisabledBySystemString)
                final String enableWhen = Util.fixNull(whenReEnableBySystemString)
                final String exception = Util.fixNull(exceptionWhenDisabledBySystemString)
                if (!reason.isEmpty() && !disabledAgo.isEmpty() && !enableWhen.isEmpty()) {
                    final StringBuilder html = new StringBuilder()
                    html.append("Note: Disabled ")
                    html.append(Util.escape(disabledAgo))
                    html.append(" ago due to error.")
                    html.append("  Will re-enable in ")
                    html.append(Util.escape(enableWhen))
                    html.append(".")
                    html.append("<br/>Reason: ")
                    html.append(Util.escape(reason))
                    if (!exception.isEmpty()) {
                        html.append(" <a href='#' class='showDetails'>")
                        html.append(Messages.MacDisabled_ShowDetails())
                        html.append("</a><pre style='display:none'>")
                        html.append(Util.escape(exception))
                        html.append("</pre>")
                    }
                    return FormValidation.warningWithMarkup(html.toString())
                }
            }
            return FormValidation.ok()
        }
    }

    @Override
    String toString() {
        final boolean ByChoice = getDisabledByChoice()
        final boolean bySystem = isDisabledBySystem()
        if (bySystem) {
            final String ago = getWhenDisabledBySystemString()
            final String until = getWhenReEnableBySystemString()
            final String why = getReasonWhyDisabledBySystem()
            if (ByChoice) {
                return "ByChoice,BySystem," + ago + "," + until + "," + why
            }
            return "BySystem," + ago + "," + until + "," + why
        }
        if (ByChoice) {
            return "ByChoice"
        }
        return "No"
    }

    // Test accessor
    @Restricted(NoExternalUse.class)
    protected long readTimeNowInNanoseconds() {
        return System.nanoTime()
    }

    /**
     * Makes Spotbug happy by avoiding SE_TRANSIENT_FIELD_NOT_RESTORED
     * 
     * @return this
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        disabledBySystem = false
        nanotimeWhenDisabledBySystem = 0L
        nanotimeWhenReEnableBySystem = 0L
        reasonWhyDisabledBySystem = null
        exceptionWhenDisabledBySystem = null
        return this
    }
}
