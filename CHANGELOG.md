# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [v1.0-rc1] - -/-/2019
### Fixed

### Added


## [v1.0-beta] - 10/09/2019
### Fixed
- Change packages to fr.edf.jenkins.plugin.*
- Close unclosed ssh connections
- Removal of unused files
- Correct detection of agents in provisioning
- Removal of empty logs
- Convert timeouts in second in cloud configuration
- Corrections on logs and exception management
- Logger in SSHCommandLauncher

### Added
- Add idle minutes in cloud configuration
- Add agent connection timeout in Mac Host configuration
- Help tooltips in configuration
- Hardset of max retry to connect an agent
- Fast node provisioning strategy
- Disabling Mac host if it cannot connect
- Possibility to change order of Mac Host in configuration
- Partitioning users folder on the Mac with chmod 700



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