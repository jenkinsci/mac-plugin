# Mac Plugin
[![Build Status](https://ci.jenkins.io/job/Plugins/job/mac-plugin/job/master/65/badge/icon)](https://ci.jenkins.io/job/Plugins/job/mac-plugin/job/master/65/)
[![Coverage Status](https://coveralls.io/repos/github/jenkinsci/mac-plugin/badge.svg?branch=master)](https://coveralls.io/github/jenkinsci/mac-plugin?branch=master)
[![DepShield Badge](https://depshield.sonatype.org/badges/jenkinsci/mac-plugin/depshield.svg)](https://depshield.github.io)

A good utility to build yours IOS apps, this plugin create MacOs agents for yours builds.

It can stock your Keychains file on Jenkins and send it to the MacOs Nodes.
## Features

- [x] Allow to configure a Mac as Jenkins slave
- [x] Run multiples builds on a single Mac
- [x] Isolates each construction from each other
- [x] Run builds on a cloud of Macs
- [x] Configure environment variables
- [x] Stock keychain file as credentials on Jenkins
- [x] Inject keychain on Node filesystem
- [x] Clean all files created after each build

This plugin has been tested against macOS 10.14 Mojave and macOS 10.15 Catalina , although theoretically it should work with older version as long as it supports sysadminctl command.

## Requirements

**Restart MacOs after configuration change**

### Enable SSH for all users
Go to System Preferences -> Sharing, and enable Remote Login for All users :

<img src="https://zupimages.net/up/19/47/q7yq.png" width="500"/>

### SSH configuration
In /etc/ssh/sshd_config file, uncomment and update values of parameters MaxAuthTries, MaxSessions, ClientAliveInterval and ClientAliveCountMax to your need.

example of configuration for 10 Jenkins and 1 Mac with 10 users allowed :

- MaxAuthTries 10
- MaxSessions 100
- ClientAliveInterval 30
- ClientAliveCountMax 150

For more informations about sshd_config consult the
[Official Documentation](https://man.openbsd.org/sshd_config)

### Configure a Jenkins User
Create an user on the Mac with administrator privileges. It will be your connection user for Mac Plugin Global configuration.

Add sudo NOPASSWD to this user in /etc/sudoers :
[see how to configure sudo without password](https://www.robertshell.com/blog/2016/12/3/use-sudo-command-osx-without-password)

To maximize security, you can configure it only for "chmod" and "sysadminctl" command used by the plugin :

`[USERNAME] ALL = NOPASSWD: /usr/sbin/sysadminctl -addUser mac-?????????? -password ??????????, /usr/sbin/sysadminctl -deleteUser mac-??????????, /bin/chmod -R 700 /Users/mac-??????????/`

## Plugin configuration
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
Since 1.1.0, you can set environment variables on Mac host. Theses variables will be set on the Node and will be accessible in the build.

<img src="https://zupimages.net/up/19/50/i14g.png" width="650"/>

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

## Contact
Any question ? You can ask it on the [Gitter room](https://gitter.im/jenkinsci/mac-plugin) or open an issue.
