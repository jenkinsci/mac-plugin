package fr.jenkins.plugins.mac.connector


import java.util.logging.Level

import javax.annotation.CheckForNull

import org.apache.commons.lang.StringUtils
import org.jenkinsci.Symbol
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import com.google.common.base.Joiner

import fr.jenkins.plugins.mac.MacCloud
import fr.jenkins.plugins.mac.MacUser
import groovy.util.logging.Slf4j
import hudson.EnvVars
import hudson.Extension
import hudson.model.Descriptor
import hudson.model.TaskListener
import hudson.slaves.ComputerLauncher
import hudson.slaves.JNLPLauncher
import hudson.slaves.NodeProperty
import hudson.util.LogTaskListener
import jenkins.model.Jenkins

@Slf4j
class MacComputerJNLPConnector extends MacComputerConnector {
    
    private static final TaskListener LOGGER_LISTENER = new LogTaskListener(log, Level.FINER)
    private final JNLPLauncher jnlpLauncher
    private String jenkinsUrl
    private String[] entryPointArguments

    @DataBoundConstructor
    public MacComputerJNLPConnector(JNLPLauncher jnlpLauncher) {
        this.jnlpLauncher = jnlpLauncher
    }

    public String getJenkinsUrl() {
        return jenkinsUrl 
    }

    @DataBoundSetter
    public void setJenkinsUrl(String jenkinsUrl){ this.jenkinsUrl = jenkinsUrl }

    public JNLPLauncher getJnlpLauncher() {
        return jnlpLauncher
    }
    
    @CheckForNull
    public String[] getEntryPointArguments(){
        return entryPointArguments;
    }

    @CheckForNull
    public String getEntryPointArgumentsString() {
        if (entryPointArguments == null) return null;
        return Joiner.on("\n").join(entryPointArguments);
    }

    @DataBoundSetter
    public void setEntryPointArgumentsString(String entryPointArgumentsString) {
        if(StringUtils.isEmpty(entryPointArgumentsString)) {
            setEntryPointArguments(new String[0])
        }else {
            setEntryPointArguments(Arrays.stream(entryPointArgumentsString.split("\n")).filter({value ->
                StringUtils.isNotEmpty(value)}).toArray({size -> new String[size]}))
        }
    }

    public void setEntryPointArguments(String[] entryPointArguments) {
        if (entryPointArguments == null || entryPointArguments.length == 0) {
            this.entryPointArguments = null;
        } else {
            this.entryPointArguments = entryPointArguments;
        }
    }


    @Restricted(NoExternalUse.class)
    public enum ArgumentVariables {
        NodeName("NODE_NAME", "The name assigned to this node"), //
        Secret("JNLP_SECRET",
                "The secret that must be passed to slave.jar's -secret argument to pass JNLP authentication."), //
        JenkinsUrl("JENKINS_URL", "The Jenkins root URL."), //
        TunnelArgument("TUNNEL_ARG",
                "If a JNLP tunnel has been specified then this evaluates to '-tunnel', otherwise it evaluates to the empty string"), //
        TunnelValue("TUNNEL", "The JNLP tunnel value");
        private final String name
        private final String description

        ArgumentVariables(String name, String description) {
            this.name = name
            this.description = description
        }

        public String getName() {
            return name
        }

        public String getDescription() {
            return description
        }
    }

    private EnvVars calculateVariablesForVariableSubstitution(final String nodeName, final String secret,
            final String jnlpTunnel, final String jenkinsUrl) throws IOException, InterruptedException {
        final EnvVars knownVariables = new EnvVars()
        final Jenkins j = Jenkins.getInstance()
        addEnvVars(knownVariables, j.getGlobalNodeProperties())
        for (final ArgumentVariables v : ArgumentVariables.values()) {
            // This switch statement MUST handle all possible
            // values of v.
            String argValue
            switch (v) {
                case ArgumentVariables.JenkinsUrl :
                    argValue = jenkinsUrl
                    break
                case ArgumentVariables.TunnelArgument :
                    argValue = StringUtils.isNotBlank(jnlpTunnel) ? "-tunnel" : ""
                    break
                case ArgumentVariables.TunnelValue :
                    argValue = jnlpTunnel
                    break
                case ArgumentVariables.Secret :
                    argValue = secret
                    break
                case ArgumentVariables.NodeName :
                    argValue = nodeName
                    break
                default :
                    final String msg = "Internal code error: Switch statement is missing \"case " + v.name()
                            + " : argValue = ... ; break;\" code."
                    // If this line throws an exception then it's because
                    // someone has added a new variable to the enum without
                    // adding code above to handle it.
                    // The two have to be kept in step in order to
                    // ensure that the help text stays in step.
                    throw new RuntimeException(msg);
            }
            addEnvVar(knownVariables, v.getName(), argValue)
        }
        return knownVariables
    }

    private static void addEnvVars(final EnvVars vars, final Iterable<? extends NodeProperty<?>> nodeProperties)
            throws IOException, InterruptedException {
        if (nodeProperties != null) {
            for (final NodeProperty<?> nodeProperty : nodeProperties) {
                nodeProperty.buildEnvVars(vars, LOGGER_LISTENER)
            }
        }
    }

    private static void addEnvVar(final EnvVars vars, final String name, final Object valueOrNull) {
        vars.put(name, valueOrNull == null ? "" : valueOrNull.toString())
    }

    @Extension @Symbol("jnlp")
    public static final class DescriptorImpl extends Descriptor<MacComputerConnector> {

        public Collection<ArgumentVariables> getEntryPointArgumentVariables() {
            return Arrays.asList(ArgumentVariables.values());
        }
        
        @Override
        public String getDisplayName() {
            return "Connect with JNLP"
        }
    }

    @Override
    protected ComputerLauncher createLauncher(MacCloud cloud, MacUser user) throws IOException, InterruptedException {
        return new JNLPLauncher(true)
    }
}
