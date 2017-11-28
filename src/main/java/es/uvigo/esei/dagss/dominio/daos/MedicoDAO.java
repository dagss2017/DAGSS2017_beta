/*
 Proyecto Java EE, DAGSS-2014
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Cita;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class MedicoDAO extends GenericoDAO<Medico> {

    public Medico buscarPorDNI(String dni) {
        TypedQuery<Medico> q = em.createQuery("SELECT m FROM Medico AS m "
                                            + "  WHERE m.dni = :dni", Medico.class);
        q.setParameter("dni", dni);

        return filtrarResultadoUnico(q);
    }

    public Medico buscarPorNumeroColegiado(String numeroColegiado) {
        TypedQuery<Medico> q = em.createQuery("SELECT m FROM Medico AS m "
                                            + "  WHERE m.numeroColegiado = :numeroColegiado", Medico.class);
        q.setParameter("numeroColegiado", numeroColegiado);
        
        return filtrarResultadoUnico(q);
    }

    public List<Medico> buscarPorNombre(String patron) {
        TypedQuery<Medico> q = em.createQuery("SELECT m FROM Medico AS m "
                + "  WHERE (m.nombre LIKE :patron) OR "
                + "        (m.apellidos LIKE :patron)", Medico.class);
        q.setParameter("patron","%"+patron+"%");        
        return q.getResultList();
    }

    // Completar aqui
     public List<Cita> buscarCitasPorPaciente(Medico medico) {
        Date now = new Date();       
        /* + "LEFT JOIN Paciente AS p ON c.paciente.dni=p.dni "*/
        TypedQuery query = em.createQuery(
               "SELECT c FROM Cita AS c "
                       + "WHERE c.medico.dni = :dniMedico AND "
                       + "c.fecha >= :today "
                       + "ORDER BY c.hora", Cita.class);
        query.setParameter("dniMedico", medico.getDni());
        query.setParameter("today",now,TemporalType.DATE);

        return query.getResultList();
    }
     
}
