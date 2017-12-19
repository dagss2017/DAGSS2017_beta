/*
 Proyecto Java EE, DAGSS-2014
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class MedicamentoDAO extends GenericoDAO<Medicamento> {

    // Completar aqui
    public List<Medicamento> buscarPorFiltro(String patron) {
        TypedQuery query = em.createQuery(
               "SELECT m FROM Medicamento AS m "
                       + "WHERE (m.nombre LIKE :patron) "
                       + "ORDER BY m.nombre ", Medicamento.class);
        query.setParameter("patron","%"+patron+"%");
        return query.getResultList();
    }
}
