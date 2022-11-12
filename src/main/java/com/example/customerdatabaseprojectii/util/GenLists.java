package com.example.customerdatabaseprojectii.util;

import com.example.customerdatabaseprojectii.daos.*;
import com.example.customerdatabaseprojectii.entity.First_Level_Divisions;

import java.sql.SQLException;

public class GenLists {

    public static void populateObservableListsFromDB(){
        try{
            AppointmentsDao.generateAppointmentList();
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Unable to generate Appointment Observable List");
        }
        try{
            ContactsDao.addContactToObservableList();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Unable to generate Contacts Observable List");
        }try{
            CountriesDao.addCountriesToObservableList();
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Unable to generate Countries Observable List");
        }try{
            CustomersDao.addCustomersToObservableListFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Unable to generate Customers Observable List");
        }try{
            First_Level_DivisionsDao.addDivisionToObservableList();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Unable to generate FirstLevelDivisions Observable List");
        }try{
            UsersDao.getAllUsersObservableList();
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Unable to generate Users Observable List");
        }
    }
}
