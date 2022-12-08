package com.example.customerdatabaseprojectii.daos;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

public interface Dao<T> {

     boolean dbInsert(T item) throws SQLException, IOException;

     ObservableList<T> getAllFromDB() throws SQLException;

     boolean updateDB(T item) throws SQLException, MalformedURLException;

     String deleteFromDB(T item) throws SQLException;

}
