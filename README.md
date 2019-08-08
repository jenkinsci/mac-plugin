# mac-agent-plugin

### Plugin configuration
In jenkins global configuration, add a new Mac Cloud :

<img src="https://image.noelshack.com/fichiers/2019/32/4/1565272720-addmaccloud.png" width="200"/>

Add a new Mac Host and fill the properties in the fields :

<img src="https://image.noelshack.com/fichiers/2019/32/4/1565272712-configmachost.png" width="750"/>

The number of simultaneous builds on the same Mac Host depends of the property "Max users".
More you have Mac Hosts configured, more you can build simultaneous on many machines.

The supported credentials for now is User and Password.
Put the "admin" account of your mac (it must have sudo password bybass).

After it refers the label of your agent.
Select JNLP for the connector and refer your Jenkins URL. This URL must be accessible by outside, localhost is not working.

<img src="https://image.noelshack.com/fichiers/2019/32/4/1565272729-agent-properties.png" width="750"/>

In a project configuration, refers the label :

<img src="https://image.noelshack.com/fichiers/2019/32/4/1565272746-project-config.png" width="750"/>

### Logs configuration
You can define a custom LOGGER to log every output of the plugin on the same place.
To do it, go to System logs in the Jenkins configuration :

<img src="https://image.noelshack.com/fichiers/2019/32/4/1565272759-logs-system.png" width="400"/>

Configure the Logger of the plugin :
<img src="https://image.noelshack.com/fichiers/2019/32/4/1565272766-custom-log-mac.png" width="750"/>

Save your configuration.

### Execution
After configuration, when you run a job with a Mac Cloud label, it will create a jenkins agent on the mac you setted as host and run the build on it.

You can see it on the home page of Jenkins :

<img src="https://image.noelshack.com/fichiers/2019/32/4/1565277561-agent-mac-running.png" width="300"/>


