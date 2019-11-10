# MCJII
A Minecraft Jar Injection Infector

This project is for educational purposes only, I am not responsible for how you use this. I wrote this to further understand injection in Java, and how to use JavaAssist.

This program, when compiled is designed to inject a custom class call in the main method of a minecraft.jar file with the use of JavaAssist.

**Project Libraries:**

https://github.com/zeroturnaround/zt-zip

https://github.com/jboss-javassist/javassist

**How to setup a class file to be injected on runtime?**

**Project Setup:**

Firstly you will need to design your script to be run in a new Java Project. For the purposes of this tutorial I will be explaining how to do this with the use of the Eclipse IDE in mind. Also MCJII folder is refering to a folder containing a compiled jar of this projects source.

**Package Setup:**

Create a new project and call it what ever you'd like.
Next you want to create a class (for example: `MyScript.java` in the default package (**IMPORTANT**) must be in defaultpackage otherwise the call will not work.

**Class Structure:**

Now in this class you've made I suggest extending java.lang.Thread and calling the start method in the constructor of the class.
Since we are extending java.lang.Thread we want to Override the run method which is where we will be running our script that is going to be executed when the Minecraft.jar file is launched. Here is an example layout:

```
//Make sure there is no package me.you.something; up here!

public class MyScript extends Thread
{
  public MyScript()
  {
    start();
  }
  
  @Override
  public void run()
  {
    //You can write all your code in this run method, for example:
    while (true)
    {
      System.out.println("Injection Example");
      try
      {
        Thread.sleep(1000L);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
  }
}
```

**Class Exportation:**

So now that we have our script built, we want to inject it? To do this we must get the class file of this file we just made. Head to your projects workspace and go into the `bin/MyScript.class` we want to copy this file into MCJII folder.

**Getting our jar file to inject to:**

So now we have our class that is going to be injected into a minecraft jar file we actually need to get our jar we want to inject to. So if you have Minecraft installed with a version ready `e.g "1.8.7.jar"` we want to go copy this jar file from our versions folder in `%appdata%/.minecraft/versions/1.8.7/1.8.7.jar` and paste that file into our MCJII folder.

**Config Modification:**

Now we want to head to our MCJII folder and if we don't already create a config.properties file. In this file we want to set the information that the injector uses to inject our script. Here is an example of a config.properties file:

```

//This is the location of the jar file on disc you are injecting to (Must use '/' as a separator)
INPUT_JAR=C:/Users/Josh/Desktop/1.8.7.jar

//Main class location where program enters
INPUT_CLASS_MAIN=net.minecraft.client.main.Main

// This is the method that your class will be called from
INJECTION_METHOD=main

//This is your custom class file in this directory to do what you want
INPUT_CLASS_HACK=MyScript.class
```
You can find this here:
https://github.com/JershDervis/MCJII/blob/master/config.properties

INPUT_JAR=the location of the Minecraft jar you will be injecting your script into, the one that we just copied into our MCJII directory for ease of access.

INPUT_CLASS_MAIN=where the entry point of the programs class package location is the default in a Minecraft jar is `net.minecraft.client.main.Main` so you most likely wont need to change this.

INPUT_CLASS_HACK=the method that your class will be called from

INPUT_CLASS_HACK=the location to your class file script that you wrote earlier, for example I used `MyScript.class`.

**Injection Process:**

I suggest creating a batch file to execute MCJII.jar so you can see if any errors occur. For example:
```
java -jar MCJII.jar
pause
```
Create this in the MCJII folder.

**Reinstallation and Execution Process:**

The program will let you know when it has injected your code and you can then reinstall the `1.8.7.jar` or whatever jar you injected to back into Minecraft, remember to create a profile for it and set the jar to the one you've injected to. Now your script will be run on when Minecraft launches your jar file.
