/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dagss.servicios.prescripcion;

import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.util.List;


public interface PlanificadorRecetas {

    /**
     * Planificar recetas en funcion a una prescripcion
     * @param prescripcion Objeto {@link Prescripcion}
     * @return Lista de objetos {@link Receta}
     */
    List<Receta> planificar(Prescripcion prescripcion);
}
