/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

/**
 *
 * @author Daniel
 */
//<editor-fold defaultstate="collapsed" desc="Imports">
import com.controller.DatosUsuario;
import com.r6.funciones.CorreoFunc;
import com.r6.funciones.RecordatorioFunc;
import com.r6.mensajeria.Adjunto;
import com.r6.mensajeria.Cliente;
import com.r6.mensajeria.Contacto;
import com.r6.mensajeria.Correo;
import com.r6.mensajeria.Itemorden;
import com.r6.mensajeria.Orden;
import com.r6.mensajeria.Producto;
import com.r6.mensajeria.Usuario;
import com.r6.service.ContactoDao;
import com.r6.service.CorreoDao;
import com.r6.service.IntervalosTiempo;
import com.r6.service.Servicio;
import com.r6.service.UsuarioDao;
import com.r6.service.RecordatorioDao;
import com.r6.mensajeria.Recordatorio;
import com.r6.mensajeria.Sistema;
import com.r6.service.AdjuntoDao;
import com.r6.service.BitacoraDao;
import com.r6.service.ClienteDao;
import com.r6.service.Control;
import com.r6.service.Dao;
import com.r6.service.ItemOrdenDao;
import com.r6.service.OrdenDao;
import com.r6.service.ProductoDao;
import java.io.IOException;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

//</editor-fold>
@ManagedBean(name = "orderController")
@ViewScoped
public class OrderController implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Variables">
    //<editor-fold defaultstate="collapsed" desc="Daos y Componente">
    /* Servicio*/
    Servicio servicio = new Servicio();

    /*Correo*/
    CorreoDao correoDao = new CorreoDao();

    /*Productos*/
    ProductoDao productoDao = new ProductoDao();

    /*Ordenes*/
    OrdenDao ordenDao = new OrdenDao();

    /*Item Orden*/
    ItemOrdenDao itemOrdenDao = new ItemOrdenDao();

    /*Usuario Orden*/
    UsuarioDao usuarioDao = new UsuarioDao();

    /* Cliente*/
    ClienteDao clienteDao = new ClienteDao();

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Productos">
    private List<Producto> productList;
    private List<Itemorden> ordenList;
    private DualListModel<Producto> productosSeleccionados;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Orden">
    private int idProducto;
    private String nombreProducto;
    private double precioProducto;
    private int cantidadProducto;
    private Date fechaOrden;
    private List<UploadedFile> filesUpoloaded;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Cliente">
    private List<Cliente> clientes;
    private Cliente selectedClient;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Modal">
    private String mensajeModal;

    //</editor-fold>
    //</editor-fold>
    @PostConstruct
    public void initBean() {

        this.fechaOrden = null;
        this.filesUpoloaded = new ArrayList<>();
        this.ordenList = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.setMensajeModal("");
        /* Inicializar y settear los EM
         Su hubo un error no realiza las operaciones*/
        if (this.setAllEms()) {
            this.refeshProductList();
        }

        //Clientes de BD
        ClienteDao dao = new ClienteDao();
        dao.setEm(Servicio.getEm());
        this.clientes = dao.getAll();

        this.selectedClient = new Cliente();
        selectedClient.setIdCliente(-1);
        selectedClient.setNombre("Selecciona un cliente");

    }

    public void addToCart(Producto p) {
        Double total = 0.00;

        Producto productoNuevo = new Producto(p.getIdProducto(), p.getNombre(), p.getPrecio());

        if (this.ordenList.isEmpty()) {
            total = (productoNuevo.getPrecio() + (productoNuevo.getPrecio() * 0.13)) * this.cantidadProducto;
            Itemorden item = new Itemorden(1, this.cantidadProducto, 0.13, productoNuevo.getPrecio(), total, productoNuevo);
            this.ordenList.add(item);

        } else {

            Boolean exists = false;
            for (Itemorden x : this.ordenList) {
                if (Objects.equals(x.getProducto().getIdProducto(), p.getIdProducto())) {
                    x.setTotalItem((productoNuevo.getPrecio() + (productoNuevo.getPrecio() * 0.13)) * this.cantidadProducto);
                    x.setCantidad(x.getCantidad() + this.cantidadProducto);
                    exists = true;
                }
            }

            if (!exists) {
                total = (productoNuevo.getPrecio() + (productoNuevo.getPrecio() * 0.13)) * this.cantidadProducto;
                Itemorden item = new Itemorden(1, this.cantidadProducto, 0.13, productoNuevo.getPrecio(), total, productoNuevo);
                this.ordenList.add(item);
            }

        }

        this.cantidadProducto = 1;
        PrimeFaces.current().ajax().update("panelProductos");
        PrimeFaces.current().ajax().update("panelOrden");

    }

    //<editor-fold defaultstate="collapsed" desc="Metodos">
    //Evento disparado cuando un archivo termina de cargar
    public void handleFileUpload(FileUploadEvent event) {
        this.filesUpoloaded.add(event.getFile());
        System.out.println("Entramos");
        PrimeFaces.current().ajax().update("files");
    }

    public void deleteAddedFile(UploadedFile up) {
        int index = 0;
        int deleteIndex = 0;
        for (UploadedFile p : this.filesUpoloaded) {
            if (p.getFileName().equals(up.getFileName())) {
                deleteIndex = index;
            }
            index++;
        }
        this.filesUpoloaded.remove(deleteIndex);
        PrimeFaces.current().ajax().update("files");

    }

    public StreamedContent setStreamedContent(UploadedFile file) {
        DefaultStreamedContent returned = null;
        try {
            returned = new DefaultStreamedContent(file.getInputstream(), file.getContentType(), file.getFileName());
        } catch (IOException ex) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returned;
    }

    public void deleteFromCart(Itemorden p) {
        int index = 0;
        int deleteindex = 0;
        for (Itemorden x : this.ordenList) {
            if (Objects.equals(x.getProducto().getNombre(), p.getProducto().getNombre())) {
                deleteindex = index;
            }
            index++;
        }
        this.ordenList.remove(deleteindex);
        PrimeFaces.current().ajax().update("panelOrden");
    }

    public void addToCartSpin(int p) {
        System.out.println("C1 : " + p);
    }

    public void addOrder() {
        if (this.ordenList.size() > 0 && (this.selectedClient.getIdCliente() != -1)
                && (null != this.fechaOrden)) {
            try {

                String cuerpoCorreo = "";

                //Generar Orden
                Orden newOrden = new Orden();
                Date fecha = new Date();
                newOrden.setCliente(clienteDao.get(1).get());
                newOrden.setFechaOrden(fechaOrden);
                newOrden.setTotalOrden(getTotal());

                //Insertarla con el cliente seleccionado
                ClienteDao cliD = new ClienteDao();
                cliD.setEm(Servicio.getEm());
                Cliente cli = cliD.get(this.selectedClient.getIdCliente()).get();

                newOrden.setCliente(cli);
                ordenDao.save(newOrden);
                ordenDao.getEm().refresh(newOrden);

                for (Itemorden item : this.ordenList) {
                    item.setOrden(newOrden);
                    cuerpoCorreo += item.getNumLinea() + " - " + item.getProducto().getNombre() + " - " + item.getCantidad() + " - " + item.getPrecioUnitario() + " - " + item.getTotalItem() + "\n";
                    itemOrdenDao.save(item);

                }

                //Adjuntos y correos
                Dao masterDao = new AdjuntoDao();
                ((AdjuntoDao) masterDao).setEm(Servicio.getEm());

                Set<Adjunto> adjs = new HashSet<>();

                if (!this.filesUpoloaded.isEmpty()) {
                    for (UploadedFile upf : this.filesUpoloaded) {
                        Adjunto adj = new Adjunto();
                        adj.setArchivo(upf.getContents());
                        adjs.add(adj);
                        ((AdjuntoDao) masterDao).save(adj);
                    }
                }

                masterDao = new CorreoDao();
                ((CorreoDao) masterDao).setEm(Servicio.getEm());

                //Correo
                Correo corr = new Correo();
                corr.setAdjuntos(adjs);
                corr.setCuerpo(cuerpoCorreo);
                corr.setAsunto(String.valueOf(newOrden.getIdOrden()));
                corr.setTipoCorreo(2);
                corr.setTipo("html/text");
                corr.setFechaEnvio(this.fechaOrden);
                corr.setOrden(newOrden);
                corr.setInifinito(false);
                //Remitente del correo
                Set<Usuario> usuarioList1 = new HashSet<>();
                usuarioList1.add(DatosUsuario.user);
                corr.setUsuarios(usuarioList1);

                //Destinatarios del corrreo
                masterDao = new ContactoDao();
                ((ContactoDao) masterDao).setEm(Servicio.getEm());

                Set<Contacto> contactos = new HashSet<>();
                for (Contacto cont : (((ContactoDao) masterDao).getByClientId(cli.getIdCliente()))) {
                    contactos.add(cont);
                }

                corr.setDestinatarios(contactos);

                masterDao = new CorreoDao();
                ((CorreoDao) masterDao).save(corr);
                

                
                //Limpiando la vista
                initBean();
                //PrimeFaces.current().executeScript("$('#myModalReject').modal();") ;
                this.setMensajeModal("Orden Agregada!");
                PrimeFaces.current().ajax().update("files");
                PrimeFaces.current().ajax().update("panelProductos");
                PrimeFaces.current().ajax().update("panelOrden");
               // PrimeFaces.current().ajax().update("dialogo");
                PrimeFaces.current().ajax().update("errorDialogPanel");
                PrimeFaces.current().executeScript("PF('errorDialog').show()");
                
                Servicio.setEm(Servicio.getEntityManagerFactory().createEntityManager());
                Control prueba = new Control();
                prueba.setEm(Servicio.getEm());
                prueba.controlCorreos();
                
                

            } catch (Exception e) {
                e.printStackTrace();
                
            }

        } else {
            this.setMensajeModal(" Error: Revise que todos los campos esten llenos");
           // PrimeFaces.current().ajax().update("dialogo");
            PrimeFaces.current().ajax().update("errorDialogPanel");
            PrimeFaces.current().executeScript("PF('errorDialog').show()");
        }
    }

    public void onItemSelectedListener() {

        if (selectedClient.getIdCliente() != -1) {
            System.out.println("ID : " + this.selectedClient.getIdCliente());

        }

    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Funcionalidades Extra">
    /*Settea todos los em de uan vez*/
    public Boolean setAllEms() {

        boolean allOK = true;
        try {
            EntityManager em = servicio.getEm();
            this.correoDao.setEm(em);
            this.itemOrdenDao.setEm(em);
            this.ordenDao.setEm(em);
            this.productoDao.setEm(em);
            this.usuarioDao.setEm(em);
            this.clienteDao.setEm(em);
        } catch (Exception e) {
            allOK = false;
            e.printStackTrace();
        }
        return allOK;

    }

    /* Settea la lista general de Productos con respecto a lo reflejado en BD */
    public void refeshProductList() {
        try {

            this.setProductList(productoDao.getAll());
            List<Producto> dummy = new ArrayList<>();

            this.productosSeleccionados = new DualListModel<>(this.productList, dummy);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onTransfer(TransferEvent event) {
        StringBuilder builder = new StringBuilder();
        for (Object item : event.getItems()) {
            builder.append(((Producto) item).getNombre()).append("<br />");
        }

    }

    public void onSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        System.out.println("Objeto : " + ((Producto) event.getObject()).getNombre());
    }

    /* Devuelve el total de la lista*/
    public double getTotal() {

        double total = 0.00;

        for (Itemorden item : this.ordenList) {

            total += item.getTotalItem();
        }

        return total;

    }

    /* Devuelve la lista de Orden en tipo set*/
    public Set<Itemorden> listToSet() {
        Set<Itemorden> set = new HashSet();

        for (Itemorden item : this.ordenList) {
            set.add(item);
        }

        return set;

    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Gets y sets">
    public List<Producto> getProductList() {
        return productList;
    }

    public Date getFechaOrden() {
        return fechaOrden;
    }

    public void setFechaOrden(Date fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public Cliente getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(Cliente selectedClient) {
        this.selectedClient = selectedClient;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public List<UploadedFile> getFilesUpoloaded() {
        return filesUpoloaded;
    }

    public void setFilesUpoloaded(List<UploadedFile> filesUpoloaded) {
        this.filesUpoloaded = filesUpoloaded;
    }

    public DualListModel<Producto> getProductosSeleccionados() {
        return productosSeleccionados;
    }

    public void setProductosSeleccionados(DualListModel<Producto> productosSeleccionados) {
        this.productosSeleccionados = productosSeleccionados;
    }

    public void setProductList(List<Producto> productList) {
        this.productList = productList;
    }

    public List<Itemorden> getOrdenList() {
        return ordenList;
    }

    public void setOrdenList(List<Itemorden> ordenList) {
        this.ordenList = ordenList;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public String getMensajeModal() {
        return mensajeModal;
    }

    public void setMensajeModal(String mensajeModal) {
        this.mensajeModal = mensajeModal;
    }

    //</editor-fold>
}
