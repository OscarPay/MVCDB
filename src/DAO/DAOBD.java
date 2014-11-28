/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

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
        System.out.println("la lista de cosas"+host+port+user+password+nameBD);
        initData(host, port, user, password, nameBD);
        try {
            System.out.println("Entro al trycatch");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
              Class.forName("com.mysql.jdbc.Driver").newInstance();
String x="jdbc:mysql://" + this.host + ":" + this.port + "/" + this.nameDB+
                    this.user+ this.password;
            System.out.println(x);
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.nameDB,
                    this.user, this.password);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {// handle the error          
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
      
            System.out.println("entre");
            System.out.println("coneccion"+connection);
        return connection;
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) { // Si no esta cerrada, se cierra
                    connection.close();
                }
            } catch (SQLException ex) {
                System.out.println(DAOBD.class.getName() + " " + ex.getMessage());
            }
        }
    }

    public void addElement(T elemento) throws SQLException{
        try {
            System.out.println(this.connection);
           
            Statement statement = this.getConnection().createStatement();
            //Sentencia en SQL para agregar elementos a la tabla           
            statement.executeUpdate("INSERT INTO " + (elemento.getClass().getSimpleName()).toLowerCase()
                    + " VALUES ('" + elemento.toString()
                    + "')");
            JOptionPane.showMessageDialog(null, "Se ha registrado Exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            statement.close();
            this.closeConnection(connection);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "No se registró el elemento");
        }
        
    }

    public void deleteElement(T elemento) throws SQLException {
        String claveElemento = obtenerClaveElemento(elemento);
        try {
            Statement statement = this.getConnection().createStatement();
            statement.execute("DELETE FROM " + elemento.getClass().getName()
                    + " WHERE " + claveElemento);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public List<T> getAll(String nameTable){
        List elementos = new ArrayList();
        try {

            PreparedStatement consulta;
            consulta = this.getConnection().prepareStatement("SELECT * FROM "
                    + nameTable);
            ResultSet resultadoDeConsulta = consulta.executeQuery();
            while (resultadoDeConsulta.next()) {
                elementos.add(obtenerElementoDeTabla(resultadoDeConsulta));
            }
            resultadoDeConsulta.close();
            consulta.close();
            this.closeConnection(connection);
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "No se pudo consultar el elemento\n"
                    + e);
        }
        return elementos;
    }

    public abstract int getIdElemento(T elemento);

    public abstract String obtenerClaveElemento(T elemento);

    public abstract Object obtenerElementoDeTabla(ResultSet resultadoDeConsulta) ;
    
    public abstract boolean updateElement(T elemento, String condicion) throws SQLException;

    public abstract T findElement(String condicion) throws SQLException;
    
}
