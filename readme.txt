
Live application can be found at http://simple.cs.pomona.edu:3000/

Installation instructions for Text Simplication Tool on Windows Machine

To set up database MySQL : 

1. Download MySQL community edition larger file to your desktop.
2. Follow the default set up to proceed wih the installations.
3. After installation your MySQL ini file will be found in similar path - "C:\ProgramData\MySQL\MySQL Server 8.0" 
4. For the above path select the folder MySQL Server 8.0 and check the properties. 
   Under properties - security tab you will need to give the folder NETWORK SERVICE (full control) privilege for your MySQL. 
5. Restart your MySQL80 from services.msc
6. Download database.zip from http://www.cs.pomona.edu/~dkauchak/forKankana/ for scripts and local files
7. Open MySQL command line tool and create a database named "analytics"
8. Open MySQL Workbench tool and load the create_analytics.sql script by using the analytics database.
9. Open MySQL command line tool and create a database named "simplification"
10. Place the MRCONSO.RFF and vocab_cs file in path "C:\ProgramData\MySQL\MySQL Server 8.0\Uploads" 
    and make appropriate changes in the load_UMLS.sql and load_unigram_mysql.sql files. 
11. Use "simplification" database for the load_UMLS.sql script by making changes to script file like changing database name and path of the MRCONSO file using MySQL Workbench tool
12. Use "simplification" database for the load_unigram_mysql.sql by making changes to script file like changing database name and path of the vocab_cs file using MySQL Workbench tool
13. If you encounter error  "The MySQL server is running with the --secure-file-priv option so it cannot execute this statement' 
    Then, open your my.ini mentioned in Step 3 and modify to secure-file-priv=''
    Restart the MySQL server
14. Again try Step 11 and 12 and if you observe "Error 2013: Connection timed out error"
    Then open MySQL Workbench tool and go to Edit - preferences - click on SQL Editor and increase the
    DBMS connection read timeout interval to 450
    DBMS connection timeout interval to 450. 
    These changes should solve most of the errors you encounter while loading the file. 
15. Replace "load data local infile" with "load data infile" in the load_UMLS.sql and load_unigram_mysql.sql

Setting Up Interface Application :

1. Install eclipse IDE
2. Check out project from Git as a General Project.
3. Download jars from http://www.cs.pomona.edu/~dkauchak/forKankana/ (models, lib)
4. Copy them into the lib directory and the models directory respectively.
5. Update paths in pom.xml to point to your local machine for jars
    - There are three hardcoded dependencies to local libraries you'll need to change them with local ones.
6. Edit edu.pomona.simplification.Preferences and change MODEL_DIR directory path and change MySQL username and password
7. Update paths and MySql connection variables (schema url, username, password) in Preferences.java file
8. Run pom.xml as maven install (Right click->Run As -> maven install)
9. Run pom.xml as maven build (Right click->Run As -> maven build) with goal spring-boot:run
10. Open localhost:8080 on your browser to run the interface application

Running the Code : 

1. To reflect the changes made in JS and CSS you need to Hard Refresh the browser or use incognito mode as the browser caches your previous version. 
2. To reflect the changes to the backend (i.e., the Java code), you will need to restart the server.











	
