[main menu](../README.md)

# Appendix A

# NEW (for 25/26)

## Setting up

### Overview
1. Download the correct version of JOGL - this is the same for PC and Mac users. I haven't been able to test for Linux users, but would welcome feedback from anyone who is a Linux user.
2. Install the correct parts of the downloaded file - the aim here is to put the jar file(s) in a folder such that when you compile and run files they can be correctly linked.
3. Compile and run the first test program.

I'll go through each of these stages in more detail, first for Windows PC users, then for Mac users. 

## Windows PC users

I'll start with installing JOGL and then running programs from the command line. Then, I'll describe how to configure Visual Studio Code if you prefer to use that.

### Step 1: Download JOGL

- Visit the jogamp site: (https://jogamp.org/)
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

**Batch file**

Create a text file called jc.bat which contains the following (on two lines):

```
del *.class
javac -cp c:/jogl26/jogamp-fat.jar;. %*
```

This can be used to compile your java programs, e.g. `jc A01.java`. The %* means multiple parameters can be supplied to the batch file. As we are only using one, %1 could be used instead. I've chosen to use del *.class so that all your program files are recompiled. An alternative is to use a build manager like Maven or Gradle to control which files need recompiling.

Create a second text file called j.bat that contains the following (all on one long line - scroll right to see the full line):

```
java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED -cp c:/jogl26/jogamp-fat.jar;. %*
```

The program can then be run using `j A01`. Again, %* could be replaced by %1. Note I have used the path to where I have stored the jogamp-fat.jar file on my system.

In later chapters more jar files may be needed, e.g. we will create a gmaths package in chapter 5 and this can then be wrapped into a jar file which can then be added to the list of jar files in the above commands. e.g. name.jar;name2.jar;name3.jar.

**Visual Studio Code**

This is slightly more complicated. 

Drag the folder you are working in into Visual Studio Code, e.g. drag the folder ch2_initial into Visual Studio Code.

Click on one of the main program files, e.g. A01.java. This will create a Java Projects view in the bottom left hand corner of the window. 

Click on the three horizontal dots next to `JAVA PROJECTS` (hover over this to see the dots) and select Configure Classpath. Select the Libraries option. Click on '+ Add Library...'. 

Navigate to wherever you put the jar files for JOGL (e.g. in C:\jog26). Select jogamp-fat.jar.

Then click on 'Apply Settings'.

Next, click on the left hand icon menu option that contains a picture of a bug on top of a triangle. This is the option to Run and Debug. In the pop-up, below the Run and Debug button, there is the option 'create a launch.json file'. Click on this. In the pop-up, select 'Java' as the debugger. This will then create a launch.json file for your project, which is added to your list of files.

Open the launch.json file. For each program listed in the launch.json file, you need to add an extra line in the configuration. As an example, the following (A01.java is part of the ch2_initial folder you dragged into Visual Studio Code):

```
{
    "type": "java",
    "name": "A01",
    "request": "launch",
    "mainClass": "A01",
    "projectName": "ch2_initial_85e1897d"
},
```

becomes

```
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

## Mac users

### Step 1: Download JOGL

Same as Windows PC above

### Step 2: Installation

The downloaded file 'jogamp-fat.jar' needs to be placed somewhere in the file system. I chose /users/stevemaddock/jogl26/jogamp-fat.jar. You would repalce 'stevemaddock' with the name you use on your Mac. You can also put it somewhere else on your system (e.g. in the same folder that you will develop your java programs in) but you will then need to adjust the instructions given below.

### Step 3: Running programs

Decide where you will develop your Java and JOGL programs, i.e. which folder in the folder hierarchy on your system. 

Open a terminal in the same folder as your java programs, e.g. if you are working on the programs in ch2_initial then that is the folder you need to be in.

Now you are ready to compile and run programs with javac and java, respectively. As an example, from Chapter 2 of the example jogl code for the module, i.e. ch2_initial:

`javac -cp /users/stevemaddock/jogl26/jogamp-fat.jar;. A01.java`

The -cp argument is short for classpath and tells the java compiler where to find relevant classes that are required for the program. These are wrapped in the jogamp-fat.jar file. The '.' is important as it says that there are classes in the current folder - this is needed so that javac and java know to look in the current folder for the programs you have just compiled.

`java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED -cp /users/stevemaddock/jogl26/jogamp-fat.jar;. A01`

The --add-exports settings mean we can use JOGL with more recent versions of Java (which have increased policing measures related to access of classes within a jar file so need to be told to ignore these extra measures). 

This is a lot to type every time you run a program. Instead, you can create a shell script or use a system like Visual Studio Code. 

> [!TIP]
> You can check which version of Java you are using, by typing 'javac -version' and 'java -version' at the Windows command window prompt.

**Shell script**

Create a text file called jc.sh which contains the following (on two lines):

```
rm *.class
javac -cp /users/stevemaddock/jogl26/jogamp-fat.jar;. $1
```

The $1 is the parameter supplied to the shell script. I've chosen to use rm *.class so that all your program files are recompiled. An alternative is to use a build manager like Maven or Gradle to control which files need recompiling.

Before you can use this shell script, you need to make it executable:

```
chmod u+x jc.sh
```

This will make the shell script executable for you the '**u**ser'. Be careful you don;t make it executable to others. (You should read up on the chmod command if you are unsure what it does.)

You can check the permissions for the file by using `ls -al` which will list all the files in the current folder as well as all their permission settings.

You can now compile your java programs using `./jc.sh A01.java`.

Create a second text file called j.sh that contains the following (all on one line):

```
java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED -cp /users/stevemaddock/jogl26/jogamp-fat.jar;. %*
```
Note I have used the path to where I have stored the jogamp-fat.jar file on my system.

Again change the executable permissions: `chmod u+x j.sh`

The program can then be run using `./j.sh A01`.

**Visual Studio Code** 

The instructions are the same as for Windows PC above (with adjustments for the path where jogamp-fat.jar is stored).


## jogamp-fat.jar

The JogAmp project discourages the use of jogamp-fat.jar because it increases deployment size, removes valuable metadata for bug reporting, and adds unnecessary native library files for unsupported platforms.

An alternative is to download 'jogamp-all-platforms.7z' by following the zip link under 'Builds / Downloads, 2.6.0' at https://jogamp.org/. This is then unzipped to find the relevant jar files for your system. The two important ones for us are jogl-all.jar and gluegen-rt.jar. In addition, gluegen-rt-natives-windows-amd64.jar and jogl-all-natives-windows-amd64.jar are required for windows PC users (irrespective of whether you have intel, amd or nvidia hardware). Similarly, for mac users, you also need gluegen-rt-natives-macosx-universal.jar and jogl-all-natives-macosx-universal.jar. With these all in a jar folder, the configuration instructions above would replace jogamp-fat.jar with jogl-all.jar and gluegen-rt.jar only.

---

---

---

# OLD

The instructions below have been superceded. They are included here as they have been tried and tested in recent years and do work. However, the instructions given above are much more flexible and mean that recent versions of java can be used.

## Java version

I have used Java 11 to test the following. I cannot guarantee other versions of Java will work.

(Update, Sep 2024: Tested for a Windows PC: it is possible to use a more recent version of Java. However, this involves using

`java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.awt.windows=ALL-UNNAMED ProgName` 

to run your program once the class files have been created with javac. The reasons for this are discussed on the JOGL forum. I have not tested this on a Mac. May be easier to stick to using Java 11.)

## Setting up

A number of steps need to be followed:

1. Download the correct version of JOGL - this is the same for PC and Mac users. I haven't been able to test for Linux users, but would welcome feedback from anyone who is a Linux user.
2. Install the correct parts of the downloaded file - the aim here is to put the jar and lib files in folders such that when you compile and run files they can be correctly linked.
3. Set up your environment so that you can compile and run Java programs and link in the jar and lib files.
4. Compile and run the first test program.

I'll go through each of these stages in more detail, first for Windows PC users, then for Mac users. 

## Windows PC users

This can be done in a number of ways depending on how you intend to compile and run programs (e.g. from the Windows command prompt or using an IDE). We'll look at how to set things up to compile and run programs from the Windows command prompt. (If you are using a code editor like VSC or an IDE, then you will need to work out how to set that up yourself.)

### Step 1: Download JOGL

- Visit the jogamp site: (https://jogamp.org/jogl/www/)
- Follow the link for 'Builds/'
- Find the heading 'Builds/Downloads'
- Click on the link for 2.5.0 'zip' which takes you to a list of .7z files to download
- Download 'jogamp-all-platforms.7z' ([Direct link](https://jogamp.org/deployment/jogamp-current/archive/))

### Step 2: Installation

The aim here is to place relevant files in a location on your PC where they can be linked to from the Java programs you will develop. I'm choosing to put them at c:\jogl25 to give a short path that indicates which version of jogl is being used. You can put them where you like on your system, but you will then need to adjust the instructions given below. (Note: if you are using Visual Studio Code for development, it is a good idea to use a subfolder in the folder where you are developing your Java and OPenGL programs.)

Steps:

- Create the folder 'c:\jogl25'
- Unzip the downloaded file 'jogamp-all-platforms.7z', which will create a subfolder called 'jogamp-all-platforms'
- Copy the subfolder 'jogamp-all-platforms\jar' into 'c:\jogl25' to create 'c:\jogl25\jar' (Note: some of the files are not needed, but it is simpler to just copy everything.)
- Make a subfolder called `c:\jogl25\lib`
- Enter the subfolder 'jogamp-all-platforms\lib'. Now you must only copy relevant files. Within that folder open the 'windows-amd64' folder (yes even if you are on an Intel machine!!). Then copy all the files from here into `c:\jogl25\lib`.

### Step 3: Setting up your environment

You must now add relevant files and folders to the system environment variables 'path' and 'classpath' so that when you compile and run Java programs the relevant files can be linked.

Open a command prompt window. On Windows PCs, you can do this by typing `cmd` in the title bar of a Windows explorer window.

Within the command prompt window, type the following commands:

`set path=c:\jogl25\lib;%path%`

`set classpath=.;c:\jogl25\jar\jogl-all.jar;c:\jogl25\jar\gluegen-rt.jar;%classpath%`

Each of these set commands is on a single line. Do not add extra new lines. Also, there is only a blank space character after the word 'set'. Do not add in any extra blank spaces anywhere else on a line. Each entry in a set command should be separated by a semi-colon and no extra spaces. Make sure the command includes 'jogl-all.jar' and 'gluegen-rt.jar'. The '-' is important in both these names. Also, note the '.' at the beginning of the classpath. This is important.

These set commands temporarily change the system environment variables 'path' and 'classpath'. This means that this only lasts whilst this command prompt window is open. If it is closed, the temporary additions are lost, and would need to be typed again for a new command prompt window. We'll look at alternatives to this in a short while. (Note: if there are any spaces in filenames, because you have chosen a different place to create the jogl folder, then the complete filename should be enclosed in quotes when setting path and classpath.)

To check that the 'classpath' and 'path' variables have been set up correctly, type 'echo %path%' and 'echo %classpath%'

You're now ready for step 4. Before that a note on how to create a batch file for issuing the above commands and a way to add the commands to the system environment variables permanently.

Creating a batch file:

Create a file called `setupjogl25.bat` (this is a text file) using your favourite text editor. Within this file, include the following lines:

`set path=c:\jogl25\lib;%path%`

`set classpath=.;c:\jogl25\jar\jogl-all.jar;c:\jogl25\jar\gluegen-rt.jar;%classpath%`

Store `setupjogl25.bat` in the folder where you will develop your programs. Now, when you open a command prompt window in that folder, you can type 'setupjogl25' and it will carry out the commands stored in the batch file.

Setting up permanent environment variables on a Windows PC (if you have administrator rights):


1. In Windows, the environment variables can be accessed by opening Windows Settings and typing 'Environment Variables' into the search area. This will launch a window where the 'Environment Variables…' button can be selected. In the User Variables section, check for an existing variable called CLASSPATH. If this does not exist, then create a new entry. Select the classpath variable and edit it. Add the full path for each of the previously mentioned jar files:

    `.;c:\jogl25\jar\jogl-all.jar;c:\jogl25\jar\gluegen-rt.jar`
   
    (Note: It is the user environment variables that you are editing. The user environment variables are automatically appended to the system environment variables by the system. If you are the administrator of your machine, you could also edit the system variables if you wish. Note also that the drive name on your machine may be different to C:, so adapt the above list accordingly.)

   (Another note: make sure there are no space characters in the classpath, otherwise your programs will fail to compile.)
2. The lib folder also needs to be made available when compiling, as this folder contains the dll files for JOGL. Navigate to the environment variables again. This time, change (or add) a user variable called PATH. Add the full path of the lib folder to this path variable: c:\jogl25\lib   (Note that your drive name on your machine may be different to C:)

### Step 4: Running your first program

First, make sure you have Java 11 installed on your machine. If not download it and install it first. This is the version I have tested with JOGL. Once downloaded, make sure your system 'path' variable links to the Java 11 install folder. You may need to edit the system environment variable 'path' - see above for instructions on editing the path variable. You could use the approach above to temporarily add it to the system path variable.

(Note: multiple versions of Java can exist on a system, since they are installed in different folders when downloaded. I have at least three different versions of Java on my machine. I sometimes use batch files to make use of specific Java versions to save having to edit the system path variable to alter the Java version used. Example: javac11.bat contains the line "C:\Program Files\Java\jdk-11\bin\javac" %1 which will call the java 11 compiler for the provided filename, e.g. javac11 ProgName. Similarly, you could use the -classpath option ([more details](https://docs.oracle.com/javase/6/docs/technotes/tools/windows/javac.html)) to look up classes rather than alter a classpath variable.)

You can check which version of Java you are using, by typing 'javac -version' and 'java -version' at the Windows command window prompt.

Decide where you will develop your Java and JOGL programs, e.g. c:\com3503 or c:\com4503 or c:\com6503 or c:\modules\com3503 or however you have organised files for the different modules you take. Download and copy all the code files for the tutorial (see Appendix B) to this working folder.

Open a command prompt window in your working folder. Set up the jogl links - see Step 3. Remember, you must do this every time you open a command prompt window, unless you have added the relevant links to the permanent environment variables.

Now that you have opened a command line window and have set up the jogl links, you are ready to run programs. As an example, from Chapter 2 of the downloaded code:

`C:\com3503>javac A01.java`

`C:\com3503>java A01`

(Note: To change drive in a command prompt window you type the drive name followed by a colon, e.g. D:. To change folder, use cd folder_name. To move back a level in the folder hierarchy, use cd ..)

(Optional: You can clean up the lib and jar folders if you wish by deleting all the joal.* and jocl.* files as these are not required. Other files can also be deleted from the jar folder depending on which platform you are working on. For example, if you are working on the windows desktop, then all you need are the following: gluegen.jar, gluegen-rt.jar, gluegen-rt-natives-windows-amd64.jar, gluegen-rt-natives-windows-i586.jar, jogl-all.jar, jogl-all-natives-windows-amd64.jar, jogl-all-natives-windows-i586.jar)

## Mac users

Most of this is similar to PC users, as described above, so I'll keep this relatively short and ask you to refer to the above description for more details.

(Note: It can take (quite) a few seconds to initially compile and run programs on a Mac (probably legacy issues, as Apple moves away from OpenGL). However, after subsequent edits, the compile and run cycle is much quicker.)

### Step 1: Download JOGL

- Visit the jogamp site (https://jogamp.org/jogl/www/)
- Follow the link for 'Builds/'
- Find the heading 'Builds/Downloads'
- Click on the link for 2.5.0 'zip' which takes you to a list of .7z files to download
- Download 'jogamp-all-platforms.7z' ([Direct link](https://jogamp.org/deployment/jogamp-current/archive/))

### Step 2: Installation

The aim here is to place relevant files in a location on your Mac where they can be linked to from the Java programs you will develop. I'm choosing to put them at /Users/steve/jogl25 to give a short path that indicates which version of jogl is being used. You can put them where you like on your system, but you will then need to adjust the instructions given below. For example, you could put them in the same folder that you are developing your Java and JOGL programs.

Steps:

- Create the folder '/Users/steve/jogl25'
- Unzip the downloaded file 'jogamp-all-platforms.7z', which will create a subfolder called 'jogamp-all-platforms'
- Copy the subfolder 'jogamp-all-platforms/jar' into '/Users/steve/jogl25' to create '/Users/steve/jogl25/jar' (Note: some of the files are not needed, but it is simpler to just copy everything.)
- Make a subfolder called '/Users/steve/jogl25/lib'
- Enter the subfolder 'jogamp-all-platforms/lib'. Now you must only copy relevant files. Within that folder open the 'macosx-universal' folder. Then copy all the files from here into '/Users/steve/jogl25/lib'

### Step 3: Setting up your environment

You must now add relevant files and folders to the system environment variables 'path' and 'classpath' so that when you compile and run Java programs the relevant files can be linked.

Open a terminal window.

At the command prompt, type the following commands (updated to reflect wherever you have stored the jogl25 folder on your system):

`export PATH=/Users/steve/jogl25/lib:$PATH`

`export CLASSPATH=.:/Users/steve/jogl25/jar/jogl-all.jar:/Users/steve/jogl25/jar/gluegen-rt.jar:$CLASSPATH`

Each of these export commands is on a single line. Also, there is only a blank space character after the word 'export'. Do not add in any extra blank spaces anywhere else on a line. Each entry in an export command should be separated by a colon and no extra spaces. Make sure the command includes 'jogl-all.jar' and 'gluegen-rt.jar'. The '-' is important in both these names. (Note: if there are any spaces in filenames, then the complete filename should be enclosed in quotes when setting path and classpath.) Also, note the '.' at the beginning of the classpath. This is important.

These commands temporarily change the system environment variables 'PATH' and 'CLASSPATH'. This means that this only lasts whilst this terminal window is open. If it is closed, the temporary additions are lost, and would need to be typed again for a new terminal window. There are alternative ways to add them permanently (e.g. [Intro to shell scripts in Terminal on Mac](https://support.apple.com/en-gb/guide/terminal/apd53500956-7c5b-496b-a362-2845f2aab4bc/mac)). You may want to only use temporary changes to the environment variables so as not to disturb other things in your general system set up.

To check that the 'CLASSPATH' and 'PATH' variables have been set up correctly, type 'echo $PATH' and 'echo $CLASSPATH'

You're now ready for step 4.

### Step 4: Running your first program

First, make sure you have Java installed on your machine. You can check which version of Java you are using, by typing 'javac -version' and 'java -version' at the terminal prompt. I have tested my programs using Java 11.

Decide where you will develop your Java and JOGL programs, e.g. in a folder called com3503 or com4503 or com6503 or ... Download and copy all the code files for the tutorial (see Appendix B) to this working folder.

Open a terminal window in your working folder. Set up the jogl links - see Step 3. Remember, you must do this every time you open a terminal window, unless you have added the relevant links to the permanent environment variables.

Now that you have opened a terminal window and have set up the jogl links, you are ready to run programs. As an example, using Chapter 2 of the downloaded code:

`prompt% javac A01.java`

`prompt% java A01`

You can list all the environment variables on a Mac by typing 'env' at the prompt.

Important: On a Mac, when you run one of the programs in the later chapters, you may get some warnings or a message saying that a shader could not link. Do not worry about this. There appears to be a bug when initially checking that a shader program has linked correctly. It has linked correctly, as attested by the fact that the programs work as expected.

[main menu](../README.md)
