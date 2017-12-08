/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.farmacia;

import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.dominio.daos.FarmaciaDAO;
import es.uvigo.esei.dagss.dominio.daos.RecetaDAO;
import es.uvigo.esei.dagss.dominio.entidades.EstadoReceta;
import es.uvigo.esei.dagss.dominio.entidades.Farmacia;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import es.uvigo.esei.dagss.dominio.entidades.TipoUsuario;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author ribadas
 */
@Named(value = "farmaciaControlador")
@SessionScoped
public class FarmaciaControlador implements Serializable {

    private Farmacia farmaciaActual;
    private String nif;
    private String password;
    private String numTarjetaPacienteBuscar; // Numero tarjeta sanitaria del paciente cuyas recetas se quieren buscar
    private List<Receta> listRecetasPaciente;
    private List<EstadoReceta> listEstadoReceta;
    
    @Inject
    private AutenticacionControlador autenticacionControlador;

    @EJB
    private FarmaciaDAO farmaciaDAO;
    @EJB
    private RecetaDAO recetaDAO;

    /**
     * Creates a new instance of AdministradorControlador
     */
    public FarmaciaControlador() {
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumTarjetaPacienteBuscar() {
        return numTarjetaPacienteBuscar;
    }

    public void setNumTarjetaPacienteBuscar(String numTarjetaPacienteBuscar) {
        this.numTarjetaPacienteBuscar = numTarjetaPacienteBuscar;
    }

    public Farmacia getFarmaciaActual() {
        return farmaciaActual;
    }

    public void setFarmaciaActual(Farmacia farmaciaActual) {
        this.farmaciaActual = farmaciaActual;
    }

    public List<Receta> getListRecetasPaciente() {
        return listRecetasPaciente;
    }

    public void setListRecetasPaciente(List<Receta> listRecetasPaciente) {
        this.listRecetasPaciente = listRecetasPaciente;
    }

    public List<EstadoReceta> getListEstadoReceta() {
        return listEstadoReceta;
    }

    private boolean parametrosAccesoInvalidos() {
        return ((nif == null) || (password == null));
    }

    public String doLogin() {
        String destino = null;
        if (parametrosAccesoInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado un nif o una contraseña", ""));
        } else {
            Farmacia farmacia = farmaciaDAO.buscarPorNIF(nif);
            if (farmacia == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe una farmacia con el NIF " + nif, ""));
            } else {
                if (autenticacionControlador.autenticarUsuario(farmacia.getId(), password,
                        TipoUsuario.FARMACIA.getEtiqueta().toLowerCase())) {
                    farmaciaActual = farmacia;
                    destino = "privado/index";
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales de acceso incorrectas", ""));
                }

            }
        }
        return destino;
    }
    
    /**
     * Busca las recetas de un usuario a partir de su número de tarjeta
     * @return Lista con las recetas válidas del paciente
     */
    public String doBuscarRecetasUsuario() {
        listRecetasPaciente = new ArrayList<>();
        listRecetasPaciente = 
                farmaciaDAO.buscarRecetasPorPaciente(numTarjetaPacienteBuscar);
        
        // Crear la lista de estados para mostrar el dropdown en la vista
        setListaEstados();
        
        return "index";
    }
    
    /**
     * Comprobar si la receta ha caducado en base a su fecha de validez
     * @param receta Objeto {@link Receta} 
     * @return True si la receta es válida
     */
    public boolean isRecetaValida(Receta receta) {
        return receta.getFinValidez().after(new Date());
    }
    
    /**
     * Construye una lista con los posibles estados de una receta.
     * Estos estados serán mostrados en la vista como un elemento
     * de dropdown para poder cambiar el estado de cada receta.
     */
    public void setListaEstados() {
        listEstadoReceta = new ArrayList<>();
        listEstadoReceta.add(EstadoReceta.GENERADA);
        listEstadoReceta.add(EstadoReceta.SERVIDA);
        listEstadoReceta.add(EstadoReceta.ANULADA);
    }
    
    /**
     * Listener para actualizar el estado de una receta
     * @param recetaConEstado Objeto {@link Receta} con el estado actualizado
     */
    public void onEstadoSeleccionado(Receta recetaConEstado) {
        recetaDAO.actualizar(recetaConEstado);
    }
    
}
