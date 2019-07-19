package fr.jenkins.plugins.mac.util

/**
 * All constants values of mac-plugin.<br>
 * Contains SSH commands for research.
 * @author Mathieu DELROCQ
 *
 */
class Constants {

    // Forms
    /** "-----------" */
    public static final String EMPTY_LIST_BOX_NAME = "-----------"
    /** "" */
    public static final String EMPTY_LIST_BOX_VALUE = ""

    //Username pattern
    /**"jenkins_%s"*/
    public static final String USERNAME_PATTERN = "jenkins_%s"

    //Path of remoting.jar
    /** jnlpJars/remoting.jar */
    public static final String REMOTING_JAR_PATH = "jnlpJars/remoting.jar"

    // SSH Commands
    /** "whoami" */
    public static final String WHOAMI = "whoami"
    /** "sudo sysadminctl -addUser %s -password %s" */
    public static final String CREATE_USER = "sudo sysadminctl -addUser %s -password %s"
    /** "sudo sysadminctl -deleteUser %s" */
    public static final String DELETE_USER = "sudo sysadminctl -deleteUser %s"
    /** "sudo curl %s -o /Users/%s/slave.jar" */
    public static final String GET_REMOTING_JAR = "curl %s -o ~/remoting.jar"
    /** "java -jar remoting.jar -jnlpUrl %scomputer/%s/slave-agent.jnlp -secret %s" */
    public static final String LAUNCH_JNLP = 'java -jar remoting.jar -jnlpUrl %scomputer/%s/slave-agent.jnlp -secret %s'
}
