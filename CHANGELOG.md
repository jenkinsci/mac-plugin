# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [v1.3.0] - 14/06/2021

### Added
* Allow to run commands with the user before the agent start

### Changed
* Upgrade parent version to 4.16

## [v1.2.2] - 03/10/2020

### Changed
* Upgrade parent version to 4.7
* Upgrade Jenkins version to 2.235.1

### Fixed
* Fix [JENKINS-54686](https://issues.jenkins-ci.org/browse/JENKINS-54686) - (#13) @mat1e

## [v1.2.1] - 05/03/2020

### Fixed
- Correct missing CSRF

## [v1.2.0] - 02/03/2020

### Added
- Verification of SSH Host Key [SECURITY-1662]
- Collapse Configuration by default

### Changed
- Parent version

### Fixed
- Missing permission + CSRF [SECURITY-1761]

## [v1.1.0] - 10/12/2019

### Added
- Upload of a keychain file on Mac
- Environment variables for Nodes

### Fixed
- Bug on Agent connection timeout property
- Configuration page of Nodes
- Node label

### Changed
- Dependencies management with jenkinsci bom

### Removed
- Unused UI file
- Unneeded dependencies

## [v1.0.2] - 28/11/2019

### Added
- Instruction to restart MacOs after configuration
- Possibility to change port value (was hardsetted before)

### Fixed
- SonarQube alerts

### Changed
- Update Javadocs with news values

### Removed
- unused imports
- unused variables

## [v1.0.1] - 21/11/2019

### Added
- Features descriptions in README
- Description of required configuration of the Mac in README
- Contact section in README
- Add recommendation in help files of plugin configuration
- add the `<url>` field in pom.xml

### Fixed
- Connection loss between 2 ssh commands

### Changed
- Default values for ssh in Mac host configuration
- Rework ssh connection strategy to open a connection by ssh command
- Remove synchronized from executeCommand method
- Update tests impacted

### Removed
- unused imports
- comments of old source code

## [v1.0.0] - 22/10/2019

### Added
- Jenkinsfile
- LICENSE

### Changed
- Udpated core version from 2.150.3 to 2.176.3
- Updated README.md
- The label is now on the hosts and not on the cloud
- Create user moved in launch method
- Wait after sysadminctl commands
- Unit provision

### Removed
- Comments for users groups commands
- kex timeout has been removed from configuration and hard setted to 0

### Fixed
- Exceed of user creation on a Mac
- The labels on nodes always appears

## [v1.0-beta] - 10/09/2019
### Added
- Add idle minutes in cloud configuration
- Add agent connection timeout in Mac Host configuration
- Help tooltips in configuration
- Hardset of max retry to connect an agent
- Fast node provisioning strategy
- Disabling Mac host if it cannot connect
- Possibility to change order of Mac Host in configuration
- Partitioning users folder on the Mac with chmod 700

### Changed
- Artifact id from mac-plugin to mac-agent-plugin
- Change packages to fr.edf.jenkins.plugin.*
- Convert timeouts in second in cloud configuration
- Logger in SSHCommandLauncher

### Removed
- Removal of empty logs
- Removal of unused files

### Fixed
- Close unclosed ssh connections
- Correct detection of agents in provisioning
- Corrections on logs and exception management

## [v1.0-alpha] - 09/08/2019
### Added
- Maven configuration
- Configuration of a Mac cloud
- Configuration of Mac host and test connection
- Define label
- Connect Mac agent with JNLP
- Launch a run on a mac cloud
- Create and connect an agent for a build
- Deletion of the agent after the build
- Custom logger