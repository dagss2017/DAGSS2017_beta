/*
 Proyecto Java EE, DAGSS-2016
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Farmacia;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class FarmaciaDAO extends GenericoDAO<Farmacia> {

    public Farmacia buscarPorNIF(String nif) {
        TypedQuery<Farmacia> q = em.createQuery("SELECT f FROM Farmacia AS f "
                                              + "  WHERE f.nif = :nif", Farmacia.class);
        q.setParameter("nif", nif);

        return filtrarResultadoUnico(q);
    }

    public List<Receta> buscarRecetasPorPaciente(String nTarjetaPaciente) {
        return em.createQuery(
                "SELECT r FROM Receta AS r "
                        + "WHERE r.prescripcion.paciente.numeroTarjetaSanitaria = :nTarjetaPaciente", Receta.class)
                .setParameter("nTarjetaPaciente", nTarjetaPaciente)
                .getResultList();
    }
}
