/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prueba;

import DAO.DAOBD;
import DAO.DAOBD;
import Prueba.Candidato;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Lalo
 */
public class Dao extends DAOBD {


    @Override
    public String obtenerCondicionElemento(Object elemento) {

        int idCandidato = ((Candidato) elemento).getID();
        String condicion = "candidato_id = " + idCandidato;

        return condicion;
    }

    @Override
    public Object obtenerElementoDeTabla(ResultSet resultadoDeConsulta) {
        try {
            return new Candidato(resultadoDeConsulta.getInt("candidato_id"),
                    resultadoDeConsulta.getString("nombre"), resultadoDeConsulta.getInt("num_votos"));
        } catch (SQLException ex) {
            System.out.println("ERROR EN LA LEÍDA DE LA BD.");
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateElement(Object elemento, String condicion) throws SQLException {

        this.establishConnection();

        Candidato elementoAmodificar = (Candidato) elemento;

        Statement sentencia = this.getConnection().createStatement();

        int actualizaCandidato = sentencia.
                executeUpdate("UPDATE mvcdb.candidato SET "
                        + "`candidato_id` = '" + elementoAmodificar.getID() + "'"
                        + ",`nombre` = '" + elementoAmodificar.getNombre() + "'"
                        + ",`num_votos` = '" + elementoAmodificar.getNumVotos() + "'"
                        + " WHERE " + condicion);

        return (actualizaCandidato != 0);
    }

    @Override
    public Object findElement(String nombreTabla, String condicion) throws SQLException {
        this.establishConnection();
        String query = "SELECT * FROM " + nombreTabla + " WHERE " + condicion;

        Statement sentenciaBuscaliente = this.getConnection().createStatement();
        ResultSet busquedaCliente = sentenciaBuscaliente.executeQuery(query);
        busquedaCliente.next();

        Candidato unCandidato = new Candidato(busquedaCliente.getInt("candidato_id"),
                busquedaCliente.getString("nombre"),
                busquedaCliente.getInt("num_votos"));

        return unCandidato;
    }

}
