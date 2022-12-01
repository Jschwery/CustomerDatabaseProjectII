#<ins>*Cool* Software Company Scheduling Desktop Application</ins>

####Author: Jared Schwery
####Email: jsch454@wgu.edu
####Date 11/28/2022

##Build Info
####IntelliJ IDEA 2021.2.3 (Community Edition)

####Java JDK 17.0.1
####JavaFX-SDK-19.0
####mysql-connector-java-8.0.30
####Application Version 0.0.1


##Purpose:
The purpose of this application is to provide a software company with the ability to maintain client appointment schedules across multiple different regions. 
The company has a heavy French user base, so in order to fulfill the needs 
of the majority of the customers needs, the application has the ability to be translated into French, either manually or automatically by user location.

##Directions
###<ins>First</ins><br> 
####Login into the application, you can either use the default login with<br> Username: admin<br>Password: admin
####You can also create a new account via the *Create Account* link located in the bottom right of the login. This account information will then be saved into the database for future use.

###<ins>Second</ins><br>
####After logging into the application you will be prompted if you have an appointment scheduled within 15 minutes. 
####The landing page will display a *tableview* of all the appointments currently stored in the database, there are tabs along the top to sort by: <ins>Week, Month, or All.</ins>
####There is a *combo-box* and button on each of the different pages to switch back and forth. On the *Appointments* page you can select an appointment by clicking on a row within the table and then have the option to: Add, Update or Delete an appointment.
####If *Update* is selected, a new window will be generated and filled with the selected appointments current data, if you change any fields within the new window the appointment will be saved to the database and displayed within the table.
####If *Add* is selected, a new window will be generated with blank fields, however the *AppointmentID* will be disabled and auto-generated, if submit is clicked, the appointment will be saved to the database and displayed in th table.
####If *Delete* is clicked and there is an appointment selected then the appointment will be removed from the database, and the table.

###<ins>Third</ins><br>
####Once you make it to the customers page you will be presented with a tableview of all the customers within the database. 
####The same rules apply to the *Add, Update, and Delete* within the Customers scene as the Appointments

###<ins>Fourth</ins><br>
####Within the reports page there are two tabs:
####Contact Schedule - Allows the user to filter the schedule of each contact with a drop-down combo-box
####Appointment Type / Month - Which allows the user to enter the type of appointment or appointment month and hit 'enter' to filter the results.
####After (or before) the results  are filtered, the user can click on a row from the table and click on the *select* button. This will display two options:
####Total Type - finds the total appointments with the type that has been selected
#### Total Month - finds the total number of appointments within the month that has been selected
###Part A3F -  Located at the top of the page with there is a combo-box with prompt-text 'UserID'. 
####This combo-box is filled with the IDs of users that have been logged into the 'login_activity.txt' file. Simply select a number which represents the user, and the total number of times the user has logged in will be displayed as the report.
###Questions?
####If you have any questions or find any bugs, please feel free to message me at the email jsch454@wgu.edu :)
