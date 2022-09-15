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

    // Advanced properties default values
    /** -Xms64m -Xmx128m */
    public static final String AGENT_JVM_DEFAULT_PARAMETERS = "-Xms64m -Xmx128m"

    //Username pattern
    /**"mac-[random_string]"*/
    public static final String USERNAME_PATTERN = "mac-%s"

    //Workdir pattern
    /** /Users/[username]/ */
    public static final String WORKDIR_PATTERN = "/Users/%s/"

    //Path of remoting.jar
    /** jnlpJars/remoting.jar */
    public static final String REMOTING_JAR_PATH = "jnlpJars/remoting.jar"

    //Group on MacOs
    /** staff */
    public static final String STAFF_GROUP = "staff"

    // SSH Commands
    /** "whoami" */
    public static final String WHOAMI = "whoami"

    //User Management Tool
    /** sysadminctl */
    public static final String SYSADMINCTL = "sysadminctl"

    /** dscl */
    public static final String DSCL = "dscl"

    /** "sudo sysadminctl -addUser [username] -password [password]" */
    public static final String CREATE_USER = "sudo sysadminctl -addUser %s -password %s"

    /** "sudo ssysadminctl -deleteUser [username]" */
    public static final String DELETE_USER = "sudo sysadminctl -deleteUser %s"

    /** && */
    public static final String COMMAND_JOINER = " && "

    /** sudo dscl . -create /Users/[username] */
    public static final String CREATE_USER_DSCL = "sudo dscl . -create /Users/%s"

    /** sudo dscl . -create /Users/[username] UserShell /bin/zsh */
    public static final String CREATE_USER_SHELL_DSCL = "sudo dscl . -create /Users/%s UserShell /bin/zsh"

    /** sudo dscl . -create /Users/[username] UniqueID \$(echo `dscl . -list /Users UniqueID | awk '{ print \$2 }' | sort -g | tail -n1` + 1 | bc) */
    public static final String CREATE_USER_UID_DSCL = "sudo dscl . -create /Users/%s UniqueID \$(echo `dscl . -list /Users UniqueID | awk '{ print \$2 }' | sort -g | tail -n1` + 1 | bc)"

    /** sudo dscl . -create /Users/[username] PrimaryGroupID 20 */
    public static final String CREATE_USER_PRIMARYGROUPID_DSCL = "sudo dscl . -create /Users/%s PrimaryGroupID 20"

    /** sudo cp -R /System/Library/User\\ Template/Non_localized /Users/[username] && sudo cp -R /System/Library/User\\ Template/English.lproj /Users/[username] */
    public static final String CREATE_USER_HOMEDIR = "sudo cp -R /System/Library/User\\ Template/Non_localized /Users/%s && sudo cp -R /System/Library/User\\ Template/English.lproj /Users/%s"

    /** sudo dscl . -create /Users/[username] NFSHomeDirectory /Users/[username] */
    public static final String CREATE_USER_NFSHOMEDIR = "sudo dscl . -create /Users/%s NFSHomeDirectory /Users/%s"

    /** sudo chown -R [username]:staff /Users/[username] */
    public static final String CHOWN_USER_DIR = "sudo chown -R %s:$STAFF_GROUP /Users/%s"

    /** sudo dscl . -passwd /Users/[username] [password] */
    public static final String CREATE_USER_PASSWORD_DSCL = "sudo dscl . -passwd /Users/%s %s"

    /** sudo pkill -u %s */
    public static final String KILL_ALL_USER_PROCESSES = "sudo pkill -u %s"

    /** sudo dscl . -delete /Users/[username] */
    public static final String DELETE_USER_DSCL = "sudo dscl . -delete /Users/%s"

    /** sudo rm -rf /Users/[username] */
    public static final String DELETE_USER_HOMEDIR = "sudo rm -rf /Users/%s"

    /** "sudo chmod -R u=rwx,g=rx,o=r /Users/[username]/" */
    public static final String CHANGE_RIGHTS_ON_USER = "sudo chmod -R 700 /Users/%s/"

    /** "sudo mkdir /Users/[username] */
    public static final String CREATE_HOME_FOLDER = "sudo mkdir /Users/%s"

    /** "sudo chown [username]:[groupname] /Users/[username] */
    public static final String CHANGE_HOME_OWNER = "sudo chown %s:%s /Users/%s"

    /** curl --retry 10 --verbose [remoting.jar_url] > remoting.jar */
    public static final String GET_REMOTING_JAR = "curl --retry 5 --retry-delay 10 --verbose %s > remoting.jar"

    /** java [agent_jvm_parameters] -jar remoting.jar -jnlpUrl [jenkins_url]computer/[computer_name]/slave-agent.jnlp -secret [secret] */
    public static final String LAUNCH_JNLP = "java %s -jar remoting.jar -jnlpUrl %scomputer/%s/slave-agent.jnlp -secret %s"

    /** dscl . list /Users | grep -v ^_ | grep [username] */
    public static final String CHECK_USER_EXIST = "dscl . list /Users | grep -v ^_ | grep %s"

    /** dscl . list /Users | grep -v ^_ | grep [username_pattern] */
    public static final String LIST_USERS = "dscl . list /Users | grep -v ^_ | grep %s"

    /** mkdir [dir_name] */
    public static final String CREATE_DIR = "mkdir -p %s"

    //regex
    public static final String REGEX_NEW_LINE = "\\r?\\n|\\r"

    //Keychain
    /** keychains/ */
    public static final String KEYCHAIN_FOLDER = "keychains/"

    /** /Users/%s/Library/Keychains/ */
    public static final String KEYCHAIN_DESTINATION_FOLDER = "/Users/%s/Library/Keychains/"

    /** /Users/%s/ */
    public static final String HOST_FILE_DESTINATION_BASE_FOLDER = "/Users/%s/"

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
