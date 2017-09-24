# Play-Logback-Groovy

This is a module that allows you to configure your logback logger with a groovy script instead of a xml file. By default, 
play just allow you to use xml to avoid the inclusion of the groovy dependency. 

### Usage

You have add this dependency to your project:

```scala
//for play 2.5
libraryDependencies += "com.github.tzimisce012" %% "play-logback-groovy" % "0.2.5" 
``` 

```scala
//for play 2.6
libraryDependencies += "com.github.tzimisce012" %% "play-logback-groovy" % "0.2.6" 
``` 

Also, you will have to disable the PlayLogback module as explained [here](https://www.playframework.com/documentation/2.6.x/SettingsLogger#using-a-custom-logging-framework)

```scala
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLogback)
```

Now you can put your `logback.groovy` file in your conf folder. Also, you still can use the xml configuration with this 
module enabled. 