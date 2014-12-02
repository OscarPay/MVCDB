package DAO;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import monitor.Controlador_Pool;
import monitor.DatosBD;

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
    private Controlador_Pool pool = new Controlador_Pool();


    public DAOBD() {
        pool.iniciarPool();
    }

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
            
            DatosBD unaConexion = pool.pedirConexion();
            System.out.println("Información que acaba de setear: "+unaConexion);
            String puerto = String.valueOf(unaConexion.getPuerto());
            //System.out.println("Contraseña: "+unaConexion.getPassword());
            //Falta que se arregle lo de la contraseña.
            initData(unaConexion.getIp(), puerto,
                    unaConexion.getUsuario(), unaConexion.getPassword(),
                    unaConexion.getNombreBD());
        }

        try {
            //Aquí establece la conexión:
            MysqlDataSource source = new MysqlDataSource();
            
            String url = "jdbc:mysql://" + this.getHost()
                    + ":" + this.getPort() + "/"
                    + this.getNameDB();
            
            source.setURL(url);
            source.setUser(this.user);
            source.setPassword(this.password);
            
            this.setConnection(source.getConnection());
            //this.setConnection(DriverManager.getConnection(url, this.getUser(), this.getPassword()));
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
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) { // Si no esta cerrada, se cierra;
                    connection.close();
                    connection = null;
                }
            } catch (SQLException ex) {
                System.out.println(DAOBD.class.getName() + " " + ex.getMessage());
            }
        }
    }

    /**
     * Agrega un elemento a la base de datos, ya sea candidato o usuario,
     * CUIDADO que es un genérico y tienen que implementar el metodo toString()
     * para que devuelva los valores en formato de MYSQL.
     *
     * @param elemento
     * @throws SQLException
     */
    
    
    public void addElement(T elemento) throws SQLException {
        try {
            this.establishConnection();

            Statement statement = this.getConnection().createStatement();
            //Sentencia en SQL para agregar elementos a la tabla   
            String query = "INSERT INTO "
                    + (elemento.getClass().getSimpleName()).toLowerCase()
                    + " VALUES (" + elemento.toString() + ")";
            //System.out.println(query);

            statement.executeUpdate(query);
            
            statement.close();
            statement = null;
            this.closeConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "No se registró el elemento");
        }

    }

    /**
     * Elimina un elemento de la base de Datos
     *
     * @param elemento
     * @throws SQLException
     */
    public void deleteElement(T elemento) throws SQLException {
        String claveElemento = obtenerCondicionElemento(elemento);
        try {
            this.establishConnection();
            Statement statement = this.getConnection().createStatement();
            statement.execute("DELETE FROM " + elemento.getClass().getSimpleName().toLowerCase()
                    + " WHERE " + claveElemento);
            statement.close();
            statement = null;
            this.closeConnection();
            
        } catch (SQLException ex) {
            System.out.println("Error en borrar" + ex);
        }
    }

    /**
     * Obtiene todos los elementos de una tabla, ya sea usuario o candidato
     *
     * @param nameTable
     * @return
     */
    public ArrayList<T> getAllFromTable(String nameTable) {
        ArrayList<T> elementos = new ArrayList<>();
        try {

            this.establishConnection();

            PreparedStatement consulta;
            consulta = this.getConnection().prepareStatement("SELECT * FROM "
                    + nameTable);
            ResultSet resultadoDeConsulta = consulta.executeQuery();
            while (resultadoDeConsulta.next()) {
                elementos.add((T) obtenerElementoDeTabla(resultadoDeConsulta));
            }
            resultadoDeConsulta.close();
            consulta.close();
            consulta = null;
            this.closeConnection();
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "No se pudo consultar el elemento\n");
            e.printStackTrace();
        }
        return elementos;
    }

    /**
     * Este método devolverá la condición de la tabla a la que se tendrá que
     * acceder para borrar un elemento.
     *
     * @param elemento será un objeto de cualquier tipo.
     * @return la condición de la tabla en donde se borrará.
     */
    public abstract String obtenerCondicionElemento(T elemento);

    public abstract Object obtenerElementoDeTabla(ResultSet resultadoDeConsulta);

    public abstract boolean updateElement(T elemento, String condicion) throws SQLException;

    public abstract T findElement(String nombreTabla, String condicion) throws SQLException;

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
