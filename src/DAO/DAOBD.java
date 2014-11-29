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
import monitor.ConexionBD;
import monitor.Controlador_Pool;

/**
 *
 * @author Oscar
 * @param <T>
 */
public abstract class DAOBD<T> {

    private String host;
    private String port;
    private String user;
    private String password;
    private String nameDB;
    private Connection connection = null;

    private void initData(String host, String port, String user, String password, String nameBD) {
        this.setHost(host);
        this.setPort(port);
        this.setUser(user);
        this.setPassword(password);
        this.setNameDB(nameBD);
    }

    /**
     * Crea una nueva conexión con la base de datos. Los datos los toma del
     * archivo de configuración, éstos deberán ser correctos, ya que si hay
     * incorrectos entonces no se podrá realizar la conexión.
     *
     */
    public void establishConnection() {

        if (this.connection == null) {
            Controlador_Pool pool = new Controlador_Pool();
            pool.iniciarPool();
            ConexionBD unaConexion = pool.pedirConexion();
            String puerto = String.valueOf(unaConexion.getPORT());

            //Falta que se arregle lo de la contraseña.
            initData(unaConexion.getHOST(), puerto, 
                    unaConexion.getUsuario(), "", 
                    unaConexion.getNombreBD());
        }

        try {
            //Aquí establece la conexión:
            this.setConnection(DriverManager.getConnection("jdbc:mysql://" + this.getHost()
                    + ":" + this.getPort() + "/"
                    + this.getNameDB(), this.getUser(), this.getPassword()));

        } catch (SQLException ex) {// handle the error          
            System.out.println("SQLException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Devuelve la conexión; sino está activa o no se ha inicializado alguna,
     * devuelve nulo.
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {

        //System.out.println("entre");
        //System.out.println("coneccion" + connection);
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

    public void addElement(T elemento) throws SQLException {
        try {
            this.establishConnection();

            Statement statement = this.getConnection().createStatement();
            //Sentencia en SQL para agregar elementos a la tabla           
            statement.executeUpdate("INSERT INTO " + (elemento.getClass().getSimpleName()).toLowerCase()
                    + " VALUES ('" + elemento.toString() + "')");

            JOptionPane.showMessageDialog(null, "Se ha registrado Exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);

            statement.close();
            this.closeConnection(getConnection());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "No se registró el elemento");
        }

    }

    public void deleteElement(T elemento) throws SQLException {
        String claveElemento = obtenerCondicionElemento(elemento);
        try {
            this.establishConnection();
            Statement statement = this.getConnection().createStatement();
            statement.execute("DELETE FROM " + elemento.getClass().getSimpleName().toLowerCase()
                    + " WHERE " + claveElemento);
        } catch (SQLException ex) {
            System.out.println("Error en borrar"+ex);
        }
    }

    public List<T> getAllFromTable(String nameTable) {
        List elementos = new ArrayList();
        try {

            this.establishConnection();

            PreparedStatement consulta;
            consulta = this.getConnection().prepareStatement("SELECT * FROM "
                    + nameTable);
            ResultSet resultadoDeConsulta = consulta.executeQuery();
            while (resultadoDeConsulta.next()) {
                elementos.add(obtenerElementoDeTabla(resultadoDeConsulta));
            }
            resultadoDeConsulta.close();
            consulta.close();
            this.closeConnection(getConnection());
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "No se pudo consultar el elemento\n");
            e.printStackTrace();
        }
        return elementos;
    }


    /**
     * Este método devolverá la condición de la tabla a la que
     * se tendrá que acceder para borrar un elemento.
     * 
     * @param elemento será un objeto de cualquier tipo.
     * @return la condición de la tabla en donde se borrará.
     */
    public abstract String obtenerCondicionElemento(T elemento);

    public abstract Object obtenerElementoDeTabla(ResultSet resultadoDeConsulta);

    public abstract boolean updateElement(T elemento, String condicion) throws SQLException;

    public abstract T findElement(String nombreTabla,String condicion) throws SQLException;

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the nameDB
     */
    public String getNameDB() {
        return nameDB;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param nameDB the nameDB to set
     */
    public void setNameDB(String nameDB) {
        this.nameDB = nameDB;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
