/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prueba;

import DAO.DAOBD;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Lalo
 */
public class PruebaDAO {

    public static void main(String[] args) {
        try {
            Candidato unc = new Candidato(1, "pepe", 0);
            Candidato cand = new Candidato(2, "juan", 0);
            DAOBD dao = new Dao();
                      
            dao.addElement(unc);
           
            dao.addElement(cand);
            List lista = dao.getAllFromTable("candidato");
            System.out.println(lista);
            
            System.out.println("-------------");
            System.out.println("Borrando elemento..");
            dao.deleteElement(unc);
            
            System.out.println("Obteniendo todo:");
            List lista2 = dao.getAllFromTable("candidato");
            System.out.println(lista2);
            
            System.out.println("Borrando elemento...");
            dao.addElement(unc);
            System.out.println("\nActualizando un candidato");
            unc.setIdCandidato(7);
            if(dao.updateElement(unc, "`candidato_id` = '1'"))
                System.out.println("Si actualizó.");
            //También sirve con el nombre:
            //dao.updateElement(unc, "`nombre`='Eduardo'");

            List lista3 = dao.getAllFromTable("candidato");
            System.out.println(lista3);

            System.out.println("\nEncontrando un elemento en específico...");
            Candidato c = (Candidato) dao.findElement("candidato", "`candidato_id` = '7'");
            System.out.println(c);
          
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
