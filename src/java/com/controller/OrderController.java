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
import com.r6.mensajeria.Contacto;
import com.r6.mensajeria.Correo;
import com.r6.mensajeria.Itemorden;
import com.r6.mensajeria.Producto;
import com.r6.mensajeria.Usuario;
import com.r6.service.ContactoDao;
import com.r6.service.CorreoDao;
import com.r6.service.IntervalosTiempo;
import com.r6.service.Servicio;
import com.r6.service.UsuarioDao;
import com.r6.service.RecordatorioDao;
import com.r6.mensajeria.Recordatorio;
import com.r6.service.AdjuntoDao;
import com.r6.service.BitacoraDao;
import com.r6.service.Dao;
import com.r6.service.ItemOrdenDao;
import com.r6.service.OrdenDao;
import com.r6.service.ProductoDao;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import java.io.ByteArrayInputStream;
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

    //</editor-fold>
    //</editor-fold>
    @PostConstruct
    public void initBean() {

        this.ordenList = new ArrayList<>();
        /* Inicializar y settear los EM
         Su hubo un error no realiza las operaciones*/
        if (this.setAllEms()) {
            this.refeshProductList();
        }

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

    public void deleteFromCart(Itemorden p) {
        int index = 0;
        int index2 = 0;
        for (Itemorden x : this.ordenList) {
            if (Objects.equals(x.getIdItem(), p.getIdItem())) {
                index2 = index;
            }
            index++;
        }
        this.ordenList.remove(this.ordenList.get(index2));
        PrimeFaces.current().ajax().update("panelOrden");
    }

    public void addToCartSpin(int p) {
        System.out.println("C1 : " + p);
    }

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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Gets y sets">
    public List<Producto> getProductList() {
        return productList;
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

    //</editor-fold>
}
