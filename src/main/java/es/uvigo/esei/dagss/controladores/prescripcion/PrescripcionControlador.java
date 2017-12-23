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
import java.util.ArrayList;
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
    List<Medicamento> medicamentos;
    String filtroMedicamentos;
    
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
    @Inject
    private MedicoControlador medicoControlador;
    
    /**
     * Creates a new instance of PrescripcionControlador
     */
    public PrescripcionControlador() {
    }
    
    @PostConstruct
    public void inicializar() {
        this.filtroMedicamentos="";
        this.medicoActual = medicoControlador.getMedicoActual();
        this.pacienteActual = medicoControlador.getCitaActual().getPaciente();
        setPrescripcionesPaciente(medicoControlador.getCitaActual().getPaciente());
        medicamentos = new ArrayList<Medicamento>();
    }
    
    
    /**
     * Valor para el patrón de búsqueda de medicamentos.
     * La búsqueda se aplica al campo Nombre de medicamento
     * @return el patrón a buscar como String
     */
    public String getFiltroMedicamentos(){
        return this.filtroMedicamentos;
    }
    
    /**
     * Devuelve la Prescripción Actual
     * @return Prescripcion actual como Prescripcion
     */
    public Prescripcion getPrescripcionActual() {
        return prescripcionActual;
    }
    
    /**
     * Guarda el patrón de búsqueda de medicamentos.
     * La búsqueda se aplica al campo Nombre de Medicamento
     * @param filtro patrón de búsqueda como String
     */
    public void setFiltroMedicamentos(String filtro){
        this.filtroMedicamentos = filtro;
    }
    
    /**
     * Establece el médico actual
     * @param medico médico loggueado como Medico
     */
    public void setMedicoActual(Medico medico){
        this.medicoActual = medico;
    }
    
    /**
     * Establece el Paciente actual para el cual se cargan sus prescripciones
     * @param paciente Paciente actual como Paciente
     */
    public void setPacienteActual(Paciente paciente){
        this.pacienteActual = paciente;
    }
    
    /**
     * Establece la Prescripción actual 
     * @param prescripcionActual Prescripcion Actual como Prescripcion
     */
    public void setPrescripcionActual(Prescripcion prescripcionActual) {
        this.prescripcionActual = prescripcionActual;
    }
    
    /**
     * Recupera una lista de las precripciones en vigor del Paciente actual
     * @param paciente Paciente actual como Paciente
     */
    public void setPrescripcionesPaciente(Paciente paciente){
        this.prescripciones = prescripcionDAO.buscarPorPaciente(paciente);
    }
    
    /**
     * Devuelve la lista de prescipciones en vigor para el paciente actual
     * @return lista de prescripciones como List(Prescripciones)
     */
    public List<Prescripcion> getListPrescripcionesPaciente() {
        return prescripciones;
    }
      
    /**
     * Comprueba si la fecha de Fin es posterior a la fecha de inicio
     * @return True si las fechas son válidas, FALSE en caso contrario
     */
    private boolean fechasValidas() {
        return (prescripcionActual.getFechaInicio().before(prescripcionActual.getFechaFin()));
    }
    
    /**
     * Devuelce la lista de Medicamentos filtrados para una prescripción
     * @return Lista de medicamentos como List(Medicamento)
     */
    public List<Medicamento> getMedicamentos(){
        return this.medicamentos;
    }
    
    /**
     * Evento llamado al pulsar el botón de filtrar medicamentos para una nueva 
     * prescripción (en el formulario 'fragmentoNuevo'.
     * Establece una lista de medicamentos filtrados en el campo Nombre
     * por el patrón dado 
     */
    public void onFiltrarMedicamentos(){
        medicamentos = medicamentoDAO.buscarPorFiltro(filtroMedicamentos);
    }
    
    /**
     * Evento llamado al seleccionar un medicamento en la tabla de medicamentos
     * filtrados en el formulario 'fragmentoNuevo'
     * Establece el medicamento para una prescripcion
     * @param medicamento Medicamento como Medicamento
     */
    public void onSelect(Medicamento medicamento){
        prescripcionActual.setMedicamento(medicamento);
    }

    /**
     * Establece la prescripción seleccionada como Actual para 
     * mostrar un formulario modal con el detalle de la prescripción seleccionada
     * @param prescripcion Prescripcion como Prescripcion
     */
    public void doVer(Prescripcion prescripcion) {
        this.prescripcionActual = prescripcion;   // Otra alternativa: volver a refrescarlos desde el DAO
    }
    
    /**
     * Establece los valores principales de la prescripción y resetea otros
     * valores/variables para mostrar un formulario modal vacío 
     * para crear una nueva prescripción
     */
    public void doNuevo() {
        prescripcionActual = new Prescripcion(); // Prescripcion
        prescripcionActual.setMedico(medicoActual);
        prescripcionActual.setPaciente(pacienteActual);
        prescripcionActual.setFechaInicio( Calendar.getInstance().getTime());
        filtroMedicamentos="";
        medicamentos = new ArrayList<Medicamento>();
        
    }
    
    /**
     * Editar la prescripcion seleccionada
     * @param prescripcion Prescripción a editar como Prescripcion
     */
    public void doEditar(Prescripcion prescripcion) {
        prescripcionActual = prescripcion;   // Otra alternativa: volver a refrescarlos desde el DAO
        medicamentos= new ArrayList<Medicamento>();
        medicamentos.add(prescripcionActual.getMedicamento());
        filtroMedicamentos = "";
    }
    
    /**
     * Elimina una prescripción dada y actualiza la lista de prescripciones
     * @param prescipcion Prescipcion a eliminar como Prescripcion
     */
    public void doEliminar(Prescripcion prescipcion) {
        prescripcionDAO.eliminar(prescipcion);
        setPrescripcionesPaciente(medicoControlador.getCitaActual().getPaciente()); // Actualizar lista de prescripcioness
    }
        
    /**
     * Valida los datos insertados y Añade una prescripcion nueva
     */
    public void doGuardarNuevo() {
        if (prescripcionActual.getMedicamento()!=null){
            if (fechasValidas()) {
                // Crea  nuevo
                prescripcionActual = prescripcionDAO.crear(prescripcionActual);
                
                // Actualiza lista
                this.prescripciones = prescripcionDAO.buscarPorPaciente(prescripcionActual.getPaciente());
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "La fecha de finalización de la prescripción debe ser posterior a la fecha de inicio", ""));
            }
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe seleccionar un medicamento para la nueva prescripción", ""));
        }
    }
    
    public void doGuardarEditado() {
       /* if (prescripcionActual.getMedicamento()!=null){
            if (fechasValidas()) {
                // Crea  nuevo
                prescripcionActual = prescripcionDAO.actualizar(prescripcionActual);
                // Actualiza lista
                this.prescripciones = prescripcionDAO.buscarPorPaciente(prescripcionActual.getPaciente());
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "La fecha de finalización de la prescripción debe ser posterior a la fecha de inicio", ""));
            }
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Debe seleccionar un medicamento para la nueva prescripción", ""));
        }*/
/*        if (passwordsVacios()) { // No modifica password
            // Actualiza 
            medicoActual = medicoDAO.actualizar(medicoActual);

            // Actualiza lista 
            medicos = medicoDAO.buscarTodos();
        } else if (password1.equals(password2)) {
            // Actualiza
            medicoActual = medicoDAO.actualizar(medicoActual);

            // Actualiza lista a mostrar
            medicos = medicoDAO.buscarTodos();

            // Ajustar su password 
            usuarioDAO.actualizarPassword(medicoActual.getId(), password1);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Passwords incorrectos (no coincidencia)", ""));
        }*/
    }
}
