# web-backups

Intelligent incremental backup system for web servers

To build executable jar-file, and setup cron, follow this steps:
1. Compile the Java file into a .class file using the "javac" command: with -cp flag containing all jar files and -d flag for package
2. Create a shell script that runs the compiled .class file using the "java" command: <br><br>
  #!/bin/sh<br>
  java MyJavaFile<br><br>
and save the shell script in a location that is accessible to the cron daemon
3. Open the crontab file for editing by running the following command: crontab -e
4. Add a line to the crontab file that specifies the schedule and command for running the shell script. 
5. Save and close the crontab file. The cron job will start running at the specified schedule.
6. Make sure the shell script is executable by running the following command: <br><br>
  chmod +x /path/to/run-java-file.sh
<br><br>
* To run the CLI application:
* Compile all sources with the Main.java file as startup class via intelliJ IDE.
* For full functionality, the structure for saving data must be:<br>
   **rootDir** - directory defined in **config.toml**, in the root dir there needs to be directory **backups** created. It has to contain file **sites_enabled.txt**<br>
   **backups** - directory contains all sites which are kept locally. i.e., **backups/site2**, **backups/xyz**, where site2 and xyz are the site names.
 each site has following directories:<br>
   **full/** and <br>
   **incremental/**<br><br>
 **full/** folder has to contain folders with full-backup periods as name (for example 7/ 14 /30 if these are the configured periods) that are specified in the config file (so the auto can be run) - this holds for local site, while for the remote repository they are created automatically
<br><br>
access a feature using wb command [params] [flag]<br><br>
doe details run **wb** or **wb --help**.
