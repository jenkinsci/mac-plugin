[<img src="https://i.pinimg.com/originals/bc/00/a8/bc00a8bd0a4be6cd29680d02c70f0539.png" width="100" align="right"/>](https://github.com/groupe-edf)

# Mac Plugin
[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins%2Fmac-plugin%2Fmaster)](https://ci.jenkins.io/job/Plugins/job/mac-plugin/job/master/)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/mac.svg?color=blue)](https://plugins.jenkins.io/mac)
[![Join the chat at https://gitter.im/jenkinsci/mac-plugin](https://badges.gitter.im/jenkinsci/mac-plugin.svg)](https://gitter.im/jenkinsci/mac-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A good utility to build yours IOS apps, this plugin create MacOs agents for yours builds.

It can stock your Keychains file on Jenkins and send it to the MacOs Nodes.

## Table of Contents
- [Mac Plugin](#mac-plugin)
	- [Table of Contents](#table-of-contents)
	- [Features](#features)
	- [Requirements](#requirements)
		- [Jenkins](#jenkins)
		- [MacOS](#macos)
			- [Enable SSH for all users](#enable-ssh-for-all-users)
			- [SSH configuration](#ssh-configuration)
			- [Configure a Jenkins User](#configure-a-jenkins-user)
	- [Plugin configuration](#plugin-configuration)
		- [Global Configuration](#global-configuration)
		- [Keychain Managment](#keychain-managment)
		- [Environment variables](#environment-variables)
		- [Host files](#host-files)
		- [Pre-launch commands](#pre-launch-commands)
		- [Web Socket](#web-socket)
		- [User Management Tool](#user-management-tool)
	- [Logs configuration](#logs-configuration)
	- [Execution](#execution)
	- [Troubleshooting](#troubleshooting)
	- [Team](#team)
	- [Contact](#contact)

## Features

- [x] Allow to configure a Mac as Jenkins slave
- [x] Run multiples builds on a single Mac
- [x] Isolates each construction from each other
- [x] Run builds on a cloud of Macs
- [x] Configure environment variables
- [x] Stock keychain file as credentials on Jenkins
- [x] Inject keychain on Node filesystem
- [x] Prepare build environment
- [x] Clean all files created after each build

This plugin has been tested against macOS 10.14 Mojave and macOS 10.15 Catalina , although theoretically it should work with older version as long as it supports sysadminctl command.

## Requirements

### Jenkins

'TCP port for inbound agents' must be enabled in Global Security settings.

If not, WebSocket must be supported by Jenkins and activated in the agents (see [Web Socket](#web-socket))

### MacOS

**Restart MacOs after configuration change**

#### Enable SSH for all users
Go to System Preferences -> Sharing, and enable Remote Login for All users :

<img src="https://zupimages.net/up/19/47/q7yq.png" width="500"/>

#### SSH configuration
In /etc/ssh/sshd_config file, uncomment and update values of parameters MaxAuthTries, MaxSessions, ClientAliveInterval and ClientAliveCountMax to your need.

example of configuration for 10 Jenkins and 1 Mac with 10 users allowed :

- MaxAuthTries 10
- MaxSessions 100
- ClientAliveInterval 30
- ClientAliveCountMax 150

For more informations about sshd_config consult the
[Official Documentation](https://man.openbsd.org/sshd_config)

#### Configure a Jenkins User
Create an user on the Mac with administrator privileges. It will be your connection user for Mac Plugin Global configuration.

Add sudo NOPASSWD to this user in /etc/sudoers :
[see how to configure sudo without password](https://www.robertshell.com/blog/2016/12/3/use-sudo-command-osx-without-password)

To maximize security, you can configure it only for "chmod" and "sysadminctl" command used by the plugin :

`[USERNAME] ALL = NOPASSWD: /usr/sbin/sysadminctl -addUser mac-?????????? -password ??????????, /usr/sbin/sysadminctl -deleteUser mac-??????????, /bin/chmod -R 700 /Users/mac-??????????/`

**Update for v1.4.0+ :**

Since 1.4.0 it is possible to use "dscl" instead of "sysadminctl". To use the full functionnalities of the plugin, here is the new NOPASSWD configuration for the user :

`[USERNAME] ALL = NOPASSWD: /usr/sbin/sysadminctl -addUser mac-?????????? -password ??????????, /usr/sbin/sysadminctl -deleteUser mac-??????????, /bin/chmod -R 700 /Users/mac-??????????/, /usr/sbin/chown mac-??????????\:staff /Users/mac-??????????, /bin/mkdir /Users/mac-??????????, /usr/bin/dscl . -create /Users/mac-??????????, /usr/bin/dscl . -create /Users/mac-?????????? UserShell /bin/zsh, /usr/bin/dscl . -create /Users/mac-?????????? UniqueID ???, /usr/bin/dscl . -create /Users/mac-?????????? PrimaryGroupID 20, /bin/cp -R /System/Library/User\ Template/Non_localized /Users/mac-??????????, /bin/cp -R /System/Library/User\ Template/English.lproj /Users/mac-??????????, /usr/bin/dscl . -create /Users/mac-?????????? NFSHomeDirectory /Users/mac-??????????, /usr/sbin/chown -R mac-??????????\:staff /Users/mac-??????????, /usr/bin/dscl . -passwd /Users/mac-?????????? ??????????, /usr/bin/pkill -u mac-??????????, /usr/bin/dscl . -delete /Users/mac-??????????, /bin/rm -rf /Users/mac-??????????`

## Plugin configuration
### Global Configuration
In jenkins global configuration, add a new Mac Cloud :

<img src="https://www.zupimages.net/up/19/47/e599.png" width="200"/>

Configure fields of Mac Cloud :

<img src="https://zupimages.net/up/19/47/d1i6.png" width="750"/>

Select JNLP for the connector and refer your Jenkins URL. This URL must be accessible by outside, localhost is not working.

Add a new Mac Host and fill the properties in the fields :

<img src="https://zupimages.net/up/19/47/vrte.png" width="750"/>

The number of simultaneous builds on the same Mac Host depends of the property "Max users".
More you have Mac Hosts configured, more you can build simultaneous on many machines.
**The plugin was tested with a limit of 7 users per Mac hosts.**

The supported credentials for now is User and Password.
Put an account of your mac with **sudo NOPASSWORD configured** (see Configure a Jenkins User).

Refer the label of your agent.
Select JNLP for the connector and refer your Jenkins URL. This URL must be accessible by outside, localhost is not working.

In a project configuration, refers the label :

<img src="https://zupimages.net/up/19/47/xyw2.png" width="750"/>

### Keychain Managment
Since v1.1.0, you have the possibility to stock keychain files into Jenkins to inject it in the Jenkins Mac agent.
For this, check "Upload a keychain file" :

<img src="https://zupimages.net/up/19/49/93el.png" width="400"/>

Add a new Secret file credentials. **Prefers to store it as System Credentials to not allow any project to use it directly** :

<img src="https://zupimages.net/up/19/49/xw7u.png" width="750"/>

The Keychain will be send to the Mac agent with SCP in ~/Library/Keychains/ directory before the JNLP connection.

### Environment variables
Since v1.1.0, you can set environment variables on Mac host. Theses variables will be set on the Node and will be accessible in the build.

<img src="https://zupimages.net/up/19/50/i14g.png" width="650"/>

### Host files
You can upload files (defined as Jenkins' credentials file type) to a path relative to the user's home folder

### Pre-launch commands
Since v1.3.0, you can set commands passed to the user before the agent starts.
The field is a multi-line string, and each line match to a command execution.
It is possible to run a script on the Mac with this field.

<img src="https://zupimages.net/up/21/23/05ub.png" width="750"/>

### Web Socket
Since v1.3.1, Mac agents supports [WebSocket](https://www.jenkins.io/blog/2020/02/02/web-socket/).

The option is available in Mac Cloud settings :

<img src="https://zupimages.net/up/21/31/nn4a.png" width="800"/>

### User Management Tool
v1.4.0 include the possibility to choose between "sysadminctl" or "dscl" for the users creation and deletion.

The option is available in Mac Cloud->Mac Host settings :

<img src="https://zupimages.net/up/21/47/zrdb.png" width="800"/>

This functionality has been developed to fix [JENKINS-66374](https://issues.jenkins.io/browse/JENKINS-66374)

sudoers file on the Mac must be updated to add sudo NOPASSWD on all commands needed to create the user with dscl (see [Configure a Jenkins User](#configure-a-jenkins-user)).

## Logs configuration
You can define a custom LOGGER to log every output of the plugin on the same place.
To do it, go to System logs in the Jenkins configuration :

<img src="https://zupimages.net/up/19/47/m7i5.png" width="400"/>

Configure the Logger of the plugin :

<img src="https://zupimages.net/up/19/47/3mkc.png" width="750"/>

Save your configuration.

## Execution
After configuration, when you run a job with a Mac Cloud label, it will create a jenkins agent on the mac you setted as host and run the build on it.

You can see it on the home page of Jenkins :

<img src="https://zupimages.net/up/19/47/fkmf.png" width="300"/>

## Troubleshooting
* Zombie process : Sometimes, "sysadminctl" tool continue to run after task executed. After a while, it can saturate MacOS (in our case we had +1000 process running). To prevent this, a script with the command "killall sysadminctl" has to be run regulary.
* User and homedirs not deleted : Sometimes when an error happens, the users and/or home directories cannot be deleted. This issue can block the others builds because the plugin detect the user like a build in progress and will wait until its deletion. A clean of the users and homedirs starting with "mac-" has to be run regulary.

**Recommendation :**
All Mac used with the plugin has to be rebooted at least one time a week to prevent theses problems. This script can be run during the reboot to clean all uneeded users and process :

```
killall sysadminctl
for user in `/usr/bin/dscl . -list /Users | grep mac-`; do
    /usr/bin/dscl . -delete /Users/$user
done

cd /Users/ && ls | grep mac- | xargs rm –rf
```

Since v1.4.0, it is possible to use dscl over sysadminctl (see [User Management Tool](#user-management-tool)). Theses issues should not happen with dscl.

## Team

Product Owner : [Cloudehard](https://github.com/Cloudehard)

Developer : [mat1e](https://github.com/mat1e)

## Contact
Any question ? You can ask it on the [Gitter room](https://gitter.im/jenkinsci/mac-plugin) or open an issue on the [Jira of Jenkins](https://issues.jenkins-ci.org/secure/Dashboard.jspa).
