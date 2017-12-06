/*
 Proyecto Java EE, DAGSS-2016
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class PrescripcionDAO extends GenericoDAO<Prescripcion> {

    public Prescripcion buscarPorIdConRecetas(Long id) {
        TypedQuery<Prescripcion> q = em.createQuery("SELECT p FROM Prescripcion AS p JOIN FETCH p.recetas"
                                                  + "  WHERE p.id = :id", Prescripcion.class);
        q.setParameter("id", id);
        
        return q.getSingleResult();
    }
    
    // Completar aqui 
    public List<Prescripcion> buscarPorPaciente(Paciente paciente){
        Date now = new Date();  
        TypedQuery query = em.createQuery(
               "SELECT p FROM Prescripcion AS p "
                       + "WHERE p.paciente.id = :idpaciente AND "
                       + "p.fechaFin >= :today "
                       + "ORDER BY p.fechaInicio", Prescripcion.class);
        query.setParameter("idpaciente", paciente.getId());
        query.setParameter("today",now,TemporalType.DATE);

        return query.getResultList();
    }
}
