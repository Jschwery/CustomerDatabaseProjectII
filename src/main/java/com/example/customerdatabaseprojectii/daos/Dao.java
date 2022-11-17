package com.example.customerdatabaseprojectii.daos;

import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface Dao<T> {

     String dbInsert(T item) throws SQLException;

     ObservableList<T> getAllFromDB() throws SQLException;

     String updateDB(T item) throws SQLException;

     String deleteFromDB(T item) throws SQLException;

}
