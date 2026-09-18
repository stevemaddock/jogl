[main menu](../README.md)

# Appendix A

## A1. Setting up

### Overview

1. Download the correct version of JOGL - this is the same for PC and Mac users. (I haven't been able to test for Linux users, but would welcome feedback from anyone who is a Linux user.)
2. Install the correct parts of the downloaded file - the aim here is to put the jar file(s) in a folder such that when you compile and run files they can be correctly linked.
3. Compile and run the first test program.

I'll go through each of these stages in more detail, first for Windows PC users, then for Mac users.

---

## A2. Windows PC users

I'll start with installing JOGL and then running programs from the command line. Then, I'll describe how to configure Visual Studio Code if you prefer to use that.

### Step 1: Download JOGL

- Visit the jogamp site: ([Direct link](https://jogamp.org/))
- Find the heading 'Builds/Downloads'
- Click on the link for 2.6.0 'fat' which takes you to a list of jar and zip files to download
- Download 'jogamp-fat.jar' ([Direct link](https://jogamp.org/deployment/jogamp-current/fat/))

### Step 2: Installation

The aim here is to place the 'jogamp-fat.jar' file in a location on your PC where it can be linked to from the Java programs you will develop. I'm choosing to put it at c:\jogl26 to give a short path that indicates which version of jogl is being used. You can put it where you like on your system (e.g. in the same folder that you will develop your java programs in) but you will then need to adjust the instructions given below.

The jogamp-fat.jar wraps everything that is needed in one file, rather than dealing with lots of different files. (Some discourage use of jogamp-fat.jar and instead say you should use the jar files for your specific system, i.e. PC or Mac - I've given details about this below for those who are interested.)

### Step 3: Running programs

Decide where you will develop your Java and JOGL programs, e.g. c:\com3503 or c:\com4503 or c:\com6503 or c:\modules\com3503 or however you have organised files for the different modules you take.

Open a command prompt window in your working folder, e.g. if you are working on the programs in ch2_initial then that is the folder you need to be in. (Type 'cmd' in the folder line at the top of the file viewer or in the windows search box. Note this is **not** a Windows PowerShell.).

Now that you have opened a command line window, you are ready to compile and run programs with javac and java, respectively. As an example, from Chapter 2 of the example jogl code for the module, i.e. folder ch2_initial:

`javac -cp c:/jogl26/jogamp-fat.jar;. A01.java`

The -cp argument is short for classpath and tells the java compiler where to find relevant classes that are required for the program. These are wrapped in the jogamp-fat.jar file. The '.' is important as it says that there are classes in the current folder - this is needed so that javac and java know to look in the current folder for the programs you have just compiled.

`java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED -cp c:/jogl26/jogamp-fat.jar;. A01`

The --add-exports settings mean we can use JOGL with more recent versions of Java (which have increased policing measures related to access of classes within a jar file so need to be told to ignore these extra measures).

This is a lot to type every time you run a program. Instead, you can create a batch file or use a system like Visual Studio Code.

> [!TIP]
> You can check which version of Java you are using, by typing 'javac -version' and 'java -version' at the Windows command window prompt.

#### Batch file

Create a text file called jc.bat which contains the following (on two lines):

```bat
del *.class
javac -cp c:/jogl26/jogamp-fat.jar;. %*
```

This can be used to compile your java programs, e.g. `jc A01.java`. The %* means multiple parameters can be supplied to the batch file. As we are only using one, %1 could be used instead. I've chosen to use del *.class so that all your program files are recompiled. An alternative is to use a build manager like Maven or Gradle to control which files need recompiling.

Create a second text file called j.bat that contains the following (all on one long line - scroll right to see the full line):

```bat
java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED -cp c:/jogl26/jogamp-fat.jar;. %*
```

The program can then be run using `j A01`. Again, %* could be replaced by %1. Note I have used the path to where I have stored the jogamp-fat.jar file on my system.

In later chapters more jar files may be needed, e.g. we will create a gmaths package in chapter 5 and this can then be wrapped into a jar file which can then be added to the list of jar files in the above commands. e.g. name.jar;name2.jar;name3.jar.

#### Visual Studio Code for Windows PC users

This is slightly more complicated.

Drag the folder you are working in into Visual Studio Code, e.g. drag the folder ch2_initial into Visual Studio Code.

Click on one of the main program files, e.g. A01.java. This will create a Java Projects view in the bottom left hand corner of the window.

Click on the three horizontal dots next to `JAVA PROJECTS` (hover over this to see the dots) and select Configure Classpath. Select the Libraries option. Click on '+ Add Library...'.

Navigate to wherever you put the jar files for JOGL (e.g. in C:\jog26). Select jogamp-fat.jar.

Then click on 'Apply Settings'.

Next, click on the left hand icon menu option that contains a picture of a bug on top of a triangle. This is the option to Run and Debug. In the pop-up, below the Run and Debug button, there is the option 'create a launch.json file'. Click on this. In the pop-up, select 'Java' as the debugger. This will then create a launch.json file for your project, which is added to your list of files.

Open the launch.json file. For each program listed in the launch.json file, you need to add an extra line in the configuration. As an example, the following (A01.java is part of the ch2_initial folder you dragged into Visual Studio Code):

```json
{
    "type": "java",
    "name": "A01",
    "request": "launch",
    "mainClass": "A01",
    "projectName": "ch2_initial_85e1897d"
},
```

becomes

```json
{
    "type": "java",
    "name": "A01",
    "request": "launch",
    "mainClass": "A01",
    "projectName": "ch2_initial_85e1897d",
    "vmArgs": "--add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED"
},
```

The configuration for each of A01, A02 and A03 should be updated in the same way. (Typically, we will have only one main java file in each folder, so this approach will suffice. When there are multiple programs, it would be better to set it once for all of them, which should be possible although I haven't tried this yet.) Then save the launch.json file. (Note: your projectName will be different - it is automatically set up by Visual Studio Code and will contain a random string attached to the main name.)

Returning to the JAVA PROJECTS window, you can now click on the symbol of a bug over a triangle which is next to 'ch2_initial' when you hover over it with your mouse. Clicking this will run the program. The pop-up gives you the option of running A01, A02 or A03. If there was only one main program in the folder, it would run automatically.

---

## A3. Mac users

### Mac Step 1: Download JOGL

Same as Windows PC above

### Mac Step 2: Installation

The downloaded file 'jogamp-fat.jar' needs to be placed somewhere in the file system. I chose /users/stevemaddock/jogl26/jogamp-fat.jar. You would replace 'stevemaddock' with the name you use on your Mac. You can also put it somewhere else on your system (e.g. in the same folder that you will develop your java programs in) but you will then need to adjust the instructions given below.

### Mac Step 3: Running programs

Decide where you will develop your Java and JOGL programs, i.e. which folder in the folder hierarchy on your system.

Open a terminal in the same folder as your java programs, e.g. if you are working on the programs in ch2_initial then that is the folder you need to be in.

Now you are ready to compile and run programs with javac and java, respectively. As an example, from Chapter 2 of the example jogl code for the module, i.e. ch2_initial:

`javac -cp /users/stevemaddock/jogl26/jogamp-fat.jar:. A01.java`

The -cp argument is short for classpath and tells the java compiler where to find relevant classes that are required for the program. These are wrapped in the jogamp-fat.jar file. The '.' is important as it says that there are classes in the current folder - this is needed so that javac and java know to look in the current folder for the programs you have just compiled.

`java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED -cp /users/stevemaddock/jogl26/jogamp-fat.jar:. A01`

The --add-exports settings mean we can use JOGL with more recent versions of Java (which have increased policing measures related to access of classes within a jar file so need to be told to ignore these extra measures).

This is a lot to type every time you run a program. Instead, you can create a shell script or use a system like Visual Studio Code.

> [!TIP]
> You can check which version of Java you are using, by typing 'javac -version' and 'java -version' at the command window prompt.

#### Shell script

Create a text file called jc.sh which contains the following (on two lines):

```bash
rm *.class
javac -cp /users/stevemaddock/jogl26/jogamp-fat.jar:. $1
```

The $1 is the parameter supplied to the shell script. I've chosen to use rm *.class so that all your program files are recompiled. An alternative is to use a build manager like Maven or Gradle to control which files need recompiling.

Before you can use this shell script, you need to make it executable:

```bash
chmod u+x jc.sh
```

This will make the shell script e**x**ecutable for you the '**u**ser'. Be careful you don't make it executable to others. (You should read up on the chmod command if you are unsure what it does.)

You can check the permissions for the file by using `ls -al` which will list all the files in the current folder as well as all their permissions settings.

You can now compile your java programs using `./jc.sh A01.java`.

Create a second text file called j.sh that contains the following (all on one long line - scroll right to see the full line):

```bash
java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED -cp /users/stevemaddock/jogl26/jogamp-fat.jar:. $1
```

Note I have used the path to where I have stored the jogamp-fat.jar file on my system.

Again change the executable permissions: `chmod u+x j.sh`

The program can then be run using `./j.sh A01`.

---

#### Visual Studio Code for Mac users

The instructions are the same as for Windows PC above (with adjustments for the path where jogamp-fat.jar is stored).

---

## A4. jogamp-fat.jar

The JogAmp project discourages the use of jogamp-fat.jar because it increases deployment size, removes valuable metadata for bug reporting, and adds unnecessary native library files for unsupported platforms.

An alternative is to download 'jogamp-all-platforms.7z' by following the zip link under 'Builds / Downloads, 2.6.0' at <https://jogamp.org/>. This is then unzipped to find the relevant jar files for your system. The two important ones for us are jogl-all.jar and gluegen-rt.jar. In addition, gluegen-rt-natives-windows-amd64.jar and jogl-all-natives-windows-amd64.jar are required for windows PC users (irrespective of whether you have intel, amd or nvidia hardware). Similarly, for mac users, you also need gluegen-rt-natives-macosx-universal.jar and jogl-all-natives-macosx-universal.jar. With these all in a jar folder, the configuration instructions above would replace jogamp-fat.jar with jogl-all.jar and gluegen-rt.jar only.
