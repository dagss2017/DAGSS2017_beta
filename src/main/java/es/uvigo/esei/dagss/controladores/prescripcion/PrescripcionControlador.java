/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dagss.controladores.prescripcion;

import es.uvigo.esei.dagss.controladores.administrador.GestionMedicosControlador;
import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.controladores.medico.MedicoControlador;
import es.uvigo.esei.dagss.dominio.daos.MedicamentoDAO;
import es.uvigo.esei.dagss.dominio.daos.MedicoDAO;
import es.uvigo.esei.dagss.dominio.daos.PacienteDAO;
import es.uvigo.esei.dagss.dominio.daos.PrescripcionDAO;
import es.uvigo.esei.dagss.dominio.daos.RecetaDAO;
import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author uxiog
 */
@Named(value = "prescripcionControlador")
@SessionScoped
public class PrescripcionControlador implements Serializable{
    
    Prescripcion prescripcionActual;
    List<Prescripcion> prescripciones;
    Medico medicoActual;
    Paciente pacienteActual;

    @Inject
    private AutenticacionControlador autenticacionControlador;

    @Inject
    private PrescripcionDAO prescripcionDAO;

    @Inject
    private PacienteDAO pacienteDAO;
    
    @Inject
    private MedicamentoDAO medicamentoDAO;
    
    @Inject
    private MedicoDAO medicoDAO;
    
    @Inject
    private RecetaDAO recetaDAO;
    
    /**
     * Creates a new instance of PrescripcionControlador
     */
    public PrescripcionControlador() {
    }
    
    @PostConstruct
    public void inicializar() {
    }

    public Prescripcion getPrescripcionActual() {
        return prescripcionActual;
    }

    public void setMedicoActual(Medico medico){
        this.medicoActual = medico;
    }
    
     public void setPacienteActual(Paciente paciente){
        this.pacienteActual = paciente;
    }
    
    public void setPrescripcionActual(Prescripcion prescripcionActual) {
        this.prescripcionActual = prescripcionActual;
    }
    
    public void setPrescripcionesPaciente(Paciente paciente){
        this.prescripciones = prescripcionDAO.buscarPorPaciente(paciente);
    }

    public List<Prescripcion> getListPrescripcionesPaciente() {
        return prescripciones;
    }
    
    public void doVer(Prescripcion prescripcion) {
        this.prescripcionActual = prescripcion;   // Otra alternativa: volver a refrescarlos desde el DAO
    }
    
    public void doNuevo() {
        prescripcionActual = new Prescripcion(); // Prescripcion
        prescripcionActual.setMedico(medicoActual);
        prescripcionActual.setPaciente(pacienteActual);
        prescripcionActual.setFechaInicio( Calendar.getInstance().getTime());
    }
    
       private boolean fechasValidas() {
        return (prescripcionActual.getFechaInicio().before(prescripcionActual.getFechaFin()));
    }
    
    public void onSelect(Medicamento medicamento, String typeOfSelection, String indexes){
        System.out.println("OnSelect:" + medicamento + " typeOfSelection: " + typeOfSelection + " indexes: " + indexes);
        prescripcionActual.setMedicamento(medicamento);
    }
       
    public void doGuardarNuevo() {
        if (fechasValidas()) {
            // Crea  nuevo
            prescripcionActual = prescripcionDAO.crear(prescripcionActual);
            
            // Actualiza lista 
            this.prescripciones = prescripcionDAO.buscarPorPaciente(prescripcionActual.getPaciente());
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "La fecha de finalización de la prescripción debe ser posterior a la fecha de inicio", ""));
        }
    }
}
