package com.example.customerdatabaseprojectii.daos;

import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface Dao<T> {

     boolean dbInsert(T item) throws SQLException;

     ObservableList<T> getAllFromDB() throws SQLException;

     boolean updateDB(T item) throws SQLException;

     String deleteFromDB(T item) throws SQLException;

}
