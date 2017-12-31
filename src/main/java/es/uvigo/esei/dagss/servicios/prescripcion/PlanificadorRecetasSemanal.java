/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dagss.servicios.prescripcion;

import es.uvigo.esei.dagss.dominio.entidades.EstadoReceta;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;

@Stateless
public class PlanificadorRecetasSemanal implements PlanificadorRecetas{
    
    private final int TIEMPO_CALCULO = 7;

    @Override
    public List<Receta> planificar(Prescripcion prescripcion) {
        List<Receta> recipes = new ArrayList<>();
        
        // Calcular el rango de días entre dos fechas
        int totalDias = getTotalDias(
                prescripcion.getFechaInicio(),
                prescripcion.getFechaFin());
        
        // Calcular el número de días que dura una caja de medicamento
        int totalDiasPorCaja = 
                (int) Math.ceil(prescripcion.getMedicamento().getNumeroDosis()/prescripcion.getDosis());
        
        // Calcular el numero total de recetas
        int totalRecetas = (int) Math.ceil((double)totalDias/totalDiasPorCaja);
        
        Date inicioValidez = prescripcion.getFechaInicio();
        Date finValidez = calcularFecha(TIEMPO_CALCULO + totalDiasPorCaja, inicioValidez);
        
        for (int i=0; i<totalRecetas; i++) {
            recipes.add(
                    new Receta(
                            prescripcion,           // prescripcion
                            1,                      // cantidad
                            inicioValidez,          // fecha de inicio de validez
                            finValidez,             // fecha de fin de validez
                            EstadoReceta.GENERADA));// estado GENERADA por defecto
            
            inicioValidez = calcularFecha(-TIEMPO_CALCULO - totalDiasPorCaja, finValidez);
            finValidez = calcularFecha(TIEMPO_CALCULO, finValidez);
        }
        
        return recipes;
    }
    
    /**
     * Obtener el numero dedias entre dos fechas
     * @param inicio Fecha inicial
     * @param fin Fecha final
     * @return Numero de dias entre la fecha inicial y la fecha final
     */
    private int getTotalDias(Date inicio, Date fin) {
        if (inicio.after(fin)) {
            throw new IllegalArgumentException("End date should be grater or equals to start date");
        }

        long inicioDateTime = inicio.getTime();
        long finDateTime = fin.getTime();
        long milPerDay = 1000*60*60*24; 

        // sumar uno para incluir el dia actual
        return (int) ((finDateTime - inicioDateTime) / milPerDay) + 1;
    }
    
    /**
     * Decrementa o incrementa un numero de dias determinado a una fecha
     * @param dias Numero de dias a incrementar/decrementar
     * @param date Fecha de partida
     * @return Fecha de partida con un incremento/decremeto definido por "dias"
     */
    private Date calcularFecha(int dias, Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(GregorianCalendar.DAY_OF_YEAR, dias);
        return calendar.getTime();
    }
        
}
