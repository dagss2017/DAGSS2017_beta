package es.uvigo.esei.dagss.servicios.prescripcion;

import es.uvigo.esei.dagss.dominio.daos.RecetaDAO;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class PrescripcionServicio {
    
    public static final int PLANIFICADOR_SEMANAL = 0;
    
    private PlanificadorRecetas planificadorRecetas;
    
    @Inject
    private RecetaDAO recetaDAO;

    public PrescripcionServicio() {
    }
        
    public void crearPlanificador(int tipoPlanificador) {
        if(tipoPlanificador==PLANIFICADOR_SEMANAL) {
            planificadorRecetas = new PlanificadorRecetasSemanal();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    public void planificar(Prescripcion prescripcion) {
        List<Receta> listaRecetas = planificadorRecetas.planificar(prescripcion);
        for (Receta r: listaRecetas) {
            recetaDAO.crear(r);
        }
    }
    
}
