[back](../README.md)

# Appendix A

## Java version

I have used Java 11 to test the following. I cannot guarantee other versions of Java will work.

(Update, Sep 2024: Tested for a Windows PC: it is possible to use a more recent version of Java. However, this involves using `java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.awt.windows=ALL-UNNAMED ProgName` to run your program once the class files have been created with javac. The reasons for this are discussed on the JOGL forum. I have not tested this on a Mac. May be easier to stick to using Java 11.)

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

[back](../README.md)
