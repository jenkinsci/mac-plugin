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
    /**"mac-%s"*/
    public static final String USERNAME_PATTERN = "mac-%s"

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

    /** chmod -R u=rwx,g=rx,o=r /Users/%s/ */
    public static final String CHANGE_RIGHTS_ON_USER = "sudo chmod -R 700 /Users/%s/"

    /** curl --retry 10 --verbose %s > remoting.jar */
    public static final String GET_REMOTING_JAR = "curl --retry 5 --retry-delay 10 --verbose %s > remoting.jar"

    /** java -jar remoting.jar -jnlpUrl %scomputer/%s/slave-agent.jnlp -secret %s */
    public static final String LAUNCH_JNLP = "java -jar remoting.jar -jnlpUrl %scomputer/%s/slave-agent.jnlp -secret %s"

    /** dscl . list /Users | grep -v ^_ | grep %s */
    public static final String CHECK_USER_EXIST = "dscl . list /Users | grep -v ^_ | grep %s"

    /** dscl . list /Users | grep -v ^_ | grep %s */
    public static final String LIST_USERS = "dscl . list /Users | grep -v ^_ | grep %s"

    //regex
    public static final String REGEX_NEW_LINE = "\\r?\\n|\\r"

    //Keychain
    /** temp/keychains/ */
    public static final String KEYCHAIN_FOLDER = "keychains/"

    /**  */
    public static final String KEYCHAIN_DESTINATION_FOLDER = ""

    //     Command for grouping users on a mac (not used but keep for potential evol)
    //    /** sudo dseditgroup -o create %s */
    //    public static final String CREATE_GROUP = "sudo dseditgroup -o create %s"
    //
    //    /** sudo dseditgroup -o delete %s */
    //    public static final String DELETE_GROUP = "sudo dseditgroup -o delete %s"
    //
    //    /** sudo dseditgroup -o edit -a %s %s */
    //    public static final String ADD_USER_TO_GROUP = "sudo dseditgroup -o edit -a %s %s"
    //
    //    /** sudo dseditgroup -o edit -d %s %s */
    //    public static final String REMOVE_USER_FROM_GROUP = "sudo dseditgroup -o edit -d %s %s"
    //
    //    /** dseditgroup -o checkmember -m %s %s */
    //    public static final String CHECK_USER_ADDED_TO_GROUP = "sudo dseditgroup -o checkmember -m %s %s"
    //
    //    /** chown -R %s:%s /Users/%s/ */
    //    public static final String ASSIGN_USER_FOLDER_TO_GROUP = "sudo chown -R %s:%s /Users/%s/"
    //
    //    /** sudo dseditgroup -o read %s */
    //    public static final String CHECK_GROUP_EXIST = "sudo dseditgroup -o read %s"
}
