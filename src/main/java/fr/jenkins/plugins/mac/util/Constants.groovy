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
    public static final String REMOTING_JAR_PATH = "jnlpJars/remoting.jar"
    /** workdir */
    public static final String WORKDIR="/Users/%s/"
    // Forms
    /** "-----------" */
    public static final String EMPTY_LIST_BOX_NAME = "-----------"
    /** "" */
    public static final String EMPTY_LIST_BOX_VALUE = ""

    // SSH Commands
    /** "whoami" */
    public static final String WHOAMI = "whoami"
    /** create mac user */
    public static final String CREATE_USER = "sudo sysadminctl -addUser %s -fullName %s -password %s"
    /** connect mac user */
    public static final String CONNECT_USER = "sudo su - %s"
    /** home directory */
    public static final String HOME_DIRECTORY = "cd ~"
    /** delete user */
    public static final String DELETE_USER = "sudo sysadminctl -deleteUser %s"
    /** download remoting.jar */
    public static final String GET_REMOTING_JAR = "sudo curl %s -o /Users/%s/remoting.jar"
}
