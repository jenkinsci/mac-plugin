package fr.jenkins.plugins.mac.util

/**
 * All constants values of mac-plugin.<br>
 * Contains SSH commands for research.
 * @author Mathieu DELROCQ
 *
 */
class Constants {
    //Path of remoting.jar
    /** jnlpJars/remoting.jar */
    public static final String REMOTING_JAR_PATH = "jnlpJars/slave.jar"
    // Forms
    /** "-----------" */
    public static final String EMPTY_LIST_BOX_NAME = "-----------"
    /** "" */
    public static final String EMPTY_LIST_BOX_VALUE = ""
    
    //Username pattern
    /**"jenkins_%s"*/
    public static final String USERNAME_PATTERN = "jenkins_%s"
    //workdir pattern
    /**"/Users/%s/"*/
    public static final String WORKDIR_PATTERN = "/Users/%s/"

    // SSH Commands
    /** "whoami" */
    public static final String WHOAMI = "whoami"
    /** "sudo sysadminctl -addUser %s -password %s" */
    public static final String CREATE_USER = "sudo sysadminctl -addUser %s -password %s"
    /** "sudo su - %s" */
    public static final String CONNECT_USER = "sudo su - %s"
    /** "cd ~" */
    public static final String HOME_DIRECTORY = "cd ~"
    /** "sudo sysadminctl -deleteUser %s" */
    public static final String DELETE_USER = "sudo sysadminctl -deleteUser %s"
    /** "sudo curl %s -o /Users/%s/slave.jar" */
    public static final String GET_REMOTING_JAR = "curl %s -o ~/slave.jar"
    //TODO : A variabiliser
//    public static final String LAUNCH_JNLP = 'java -jar agent.jar -jnlpUrl http://10.31.195.86:8080/jenkins/computer/jenkins_1a2so/slave-agent.jnlp -workDir "/Users/jenkins_1a2so/"'
    public static final String LAUNCH_JNLP = 'java -jar slave.jar -jnlpUrl %scomputer/%s/slave-agent.jnlp -workDir "/Users/%s/"'
}
