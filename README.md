#Scheduling Desktop Application

####Author: Jared Schwery
####Email: jsch454@wgu.edu

##Build Info
####IntelliJ IDEA 2021.2.3 (Community Edition)

####Java JDK 17.0.1
####JavaFX-SDK-19.0
####Application Version 0.0.1

##Purpose:
The purpose of this application is to provide a software company with the ability to maintain client appointment schedules across multiple different regions. 
The company has a heavy French user base, so in order to fulfill the needs 
of the majority of the customers needs, the application has the ability to be translated into French, either manually or automatically by user location.


##Directions
###<ins>First</ins><br> 
####Login into the application, you can either user the default login with<br> Username: admin<br>Password: admin
####You can also create a new account via the *Create Account* link located in the bottom right of the login. This account information will then be saved into the database for future use.

###<ins>Second</ins><br>
####After logging into the application you will be prompted if you have an appointment scheduled within 15 minutes. 
####The landing page will display a *tableview* of all the appointments currently stored in the database, there are tabs along the top to sort by: <ins>Week, Month, or All.</ins>
####There is a *combobox* and button on each of the different pages to switch back and forth. On the *Appointments* page you can select an appointment by clicking on a row within the table and then have the option to: Add, Update or Delete an appointment.
####If *Update* is selected, a new window will be generated and filled with the selected appointments current data, if you change any fields within the new window the appointment will be saved to the database and displayed within the table.
####If *Add* is selected, a new window will be generated with blank fields, however the *AppointmentID* will be disabled and auto-generated, if submit is clicked, the appointment will be saved to the database and displayed in th table.
####If *Delete* is clicked and there is an appointment selected then the appointment will be removed from the database, and the table.

###<ins>Third</ins><br>
####Once you make it to the customers page you will be presented with a tableview of all the customers within the database. 
####The same rules apply to the *Add, Update, and Delete* within the Customers scene as the Appointments

###<ins>Fourth</ins><br>
####Within the reports page 

