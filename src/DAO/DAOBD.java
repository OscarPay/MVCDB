/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Oscar
 * @param <T>
 */

public abstract class DAOBD<T> {

    String host;
    String port;
    String user;
    String password;
    String nameDB;    
    Connection connection = null;
    
    private void initData(String host, String port, String user, String password, String nameBD) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.nameDB = nameBD;
    }

    public void establishConnection(String host, String port, String user, String password, String nameBD) {
        initData(host, port, user, password, nameBD);
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.nameDB,
                this.user, this.password);
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {// handle the error          
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return connection;
    }

    public void closeConnection(Connection connection) {
            if (connection != null) {
                try {
                    if (!connection.isClosed()) { // Si no esta cerrada, se cierra
                        connection.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(DAOBD.class.getName()+" "+ ex.getMessage());                    
                }
            }        
    }

    public abstract boolean addElement(T elemento) throws SQLException;

    public abstract boolean updateElement(T elemento, String condicion) throws SQLException;

    public abstract void deleteElement(T elemento) throws SQLException;

    public abstract T findElement(String condicion) throws SQLException;    
    
    public abstract List<T> getAll(String nameTable);
        
}
