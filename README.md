# Mac Plugin
[![Build Status](https://travis-ci.org/jenkinsci/mac-plugin.svg?branch=master)](https://travis-ci.org/jenkinsci/mac-plugin)
[![Coverage Status](https://coveralls.io/repos/github/jenkinsci/mac-plugin/badge.svg?branch=master)](https://coveralls.io/github/jenkinsci/mac-plugin?branch=master)
[![DepShield Badge](https://depshield.sonatype.org/badges/jenkinsci/mac-plugin/depshield.svg)](https://depshield.github.io)

This plugin allows to configure a cloud of Mac in Jenkins configuration.

Like docker and kubernetes plugins does, you can configure your builds to run on a cloud of Mac.

All builds are independent and there is a restricted space dedicated to read and write to this single build. This space is removed at the end of the build so that no traces remain on the machine.

### Plugin configuration
In jenkins global configuration, add a new Mac Cloud :

<img src="https://image.noelshack.com/fichiers/2019/32/4/1565272720-addmaccloud.png" width="200"/>

Configure fields of Mac Cloud :

<img src="https://image.noelshack.com/fichiers/2019/42/1/1571074130-cloud-config.png" width="750"/>

Select JNLP for the connector and refer your Jenkins URL. This URL must be accessible by outside, localhost is not working.

Add a new Mac Host and fill the properties in the fields :

<img src="https://image.noelshack.com/fichiers/2019/42/1/1571074130-host-config.png" width="750"/>

The number of simultaneous builds on the same Mac Host depends of the property "Max users".
More you have Mac Hosts configured, more you can build simultaneous on many machines.
**For best usage I recommend a limit of 3.**

The supported credentials for now is User and Password.
Put an account of your mac with **sudo NOPASSWORD configured**.

After it refers the label of your agent.
Select JNLP for the connector and refer your Jenkins URL. This URL must be accessible by outside, localhost is not working.

In a project configuration, refers the label :

<img src="https://image.noelshack.com/fichiers/2019/42/1/1571074794-job-label-config.png" width="750"/>

### Logs configuration
You can define a custom LOGGER to log every output of the plugin on the same place.
To do it, go to System logs in the Jenkins configuration :

<img src="https://image.noelshack.com/fichiers/2019/32/4/1565272759-logs-system.png" width="400"/>

Configure the Logger of the plugin :
<img src="https://image.noelshack.com/fichiers/2019/42/1/1571074130-custom-log-config.png" width="750"/>

Save your configuration.

### Execution
After configuration, when you run a job with a Mac Cloud label, it will create a jenkins agent on the mac you setted as host and run the build on it.

You can see it on the home page of Jenkins :

<img src="https://image.noelshack.com/fichiers/2019/42/1/1571074130-build-capture.png" width="300"/>


