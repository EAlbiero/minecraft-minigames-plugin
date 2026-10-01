This project is a Bukkit Minecraft plugin that allows you to pick and choose minigames in a Minecraft server through chat commands.

### Setting up a local server

This project can be run locally for testing using a Spigot server.

1. Download [Spigot]([url](https://getbukkit.org/download/spigot)) from the official website.
2. Create a `Server` directory in your computer and save the Spigot .jar file in there
3. Start the server by going to the `Server` directory and running

   `$ java -jar <SPIGOT-JAR-NAME> nogui`

The first time you run this command Spigot will automatically generate all the server files you need, which may take a few minutes. Subsequent starts will be considerably faster.

### Compiling the plugin

1. Go to `pom.xml` and update the output file configuration to a directory in your computer. For convenience, it is recommended to set this as the `plugins` directory in your Spigot server so that the server automatically runs the new version. This has to be done by specifying the absolute path in

```
<configuration>
  <outputFile>C:\path_to_server\server\plugins\minigames-plugin.jar</outputFile>
</configuration>
```
2. Use Maven to install your project and you should see the `.jar` file in the directory configured in the previous step
