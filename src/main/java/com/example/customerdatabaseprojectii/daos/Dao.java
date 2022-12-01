package com.example.customerdatabaseprojectii.daos;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;

public interface Dao<T> {

     boolean dbInsert(T item) throws SQLException, IOException;

     ObservableList<T> getAllFromDB() throws SQLException;

     boolean updateDB(T item) throws SQLException;

     String deleteFromDB(T item) throws SQLException;

}
