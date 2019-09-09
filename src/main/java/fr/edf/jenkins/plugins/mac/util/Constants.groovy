package fr.edf.jenkins.plugins.mac.util

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
    public static final String USERNAME_PATTERN = "%s_jenkins_%s"
    
    //Workdir pattern
    /** /Users/%s/ */
    public static final String WORKDIR_PATTERN = "/Users/%s/"
    
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
    public static final String LAUNCH_JNLP = "java -jar remoting.jar -jnlpUrl %scomputer/%s/slave-agent.jnlp -secret %s"
    /** dscl . list /Users | grep -v ^_ | grep %s */
    public static final String CHECK_USER_EXIST = "dscl . list /Users | grep -v ^_ | grep %s"
    /** list all users on mac */
    public static final String LIST_USERS = "dscl . list /Users | grep -v ^_ | grep %s"
    
    //regex
    public static final String REGEX_NEW_LINE = "\\r?\\n|\\r"
}
