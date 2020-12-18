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
import com.r6.mensajeria.Adjunto;
import com.r6.mensajeria.Contacto;
import com.r6.mensajeria.Correo;
import com.r6.mensajeria.Usuario;
import com.r6.service.CorreoDao;
import com.r6.service.Servicio;
import com.r6.service.UsuarioDao;
import com.r6.service.AdjuntoDao;
import com.r6.service.ClienteDao;
import com.r6.service.OrdenDao;
import com.r6.service.ProductoDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;

//</editor-fold>
@ManagedBean(name = "noEnviadosController")
@SessionScoped
public class NoEviadosController implements Serializable {

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

    /*Usuario Orden*/
    UsuarioDao usuarioDao = new UsuarioDao();

    /* Cliente*/
    ClienteDao clienteDao = new ClienteDao();

    /*Adjuntos*/
    AdjuntoDao adjuntosDao = new AdjuntoDao();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Listas">
    List<Correo> correosUsuario;
    List<Correo> listaLimpia;
    List<Adjunto> adjuntos;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Objetos">
    Correo target;

    //</editor-fold>
    //</editor-fold>
    @PostConstruct
    public void initBean() {
        this.onLoad();

    }
    
    enum Meses{
        Enero(1), Febrero(2), Marzo(3), Abril(4), Mayo(5), Junio(6),Julio(7),Agosto(8),Septiembre(9),Octubre(10),Noviembre(11),Diciembre(12);
        
        private int numMes;
        
        Meses(int num){
            this.numMes=num;
        }
        
        public int getMes(){return numMes;}
        
        public static Meses getFromId(int mes){
            for(Meses m:Meses.values()){
                if(m.getMes()==mes){
                    return m;
                }
                
            }
            return null;
        }
        
    }

    public void onLoad() {
        boolean allSet = this.setAllEms();

        if (allSet) {
            this.correosUsuario = new ArrayList<>();
            this.listaLimpia = new ArrayList<>();
            this.adjuntos = new ArrayList<>();
            this.correosUsuario.clear();
            this.listaLimpia.clear();

            System.out.println("noEviadosCOntroller: All set!");
           
            for(Correo c:correoDao.getAll()){
                for(Usuario u:c.getUsuarios()){
                    if(u.getIdUsuario()==DatosUsuario.user.getIdUsuario() && u.getSistema().getId()==DatosUsuario.sis.getId()){
                        correosUsuario.add(c);
                    }
                }
            }
            
            //correosUsuario = correoDao.getByUserAndSys(DatosUsuario.user, DatosUsuario.sis);
            Date today = new Date();
            System.out.println("Today is: " + today);
            System.out.println("User: " + DatosUsuario.user.getIdUsuario());
            System.out.println("System: " + DatosUsuario.sis.getNombreSistema());
            System.out.println("Size: " + this.correosUsuario.size());
            //this.cleanList();
            this.listaLimpia=this.correosUsuario;
        } else {
            System.out.println("noEviadosCOntroller: Error!");
        }
    }

    //Método sobrecargado,porque noEnviados.xhtml a veces envía date y a veces envía timestamps
    public String darFormatoFecha(java.util.Date fechaParam){
        java.sql.Timestamp f = new java.sql.Timestamp(fechaParam.getTime());
        String fecha=darFormatoFecha(f);
        
        return fecha;
    }
     public String darFormatoFecha(java.sql.Timestamp f){
       
         String fecha=f.toString();
         
           fecha=fecha.substring(0,fecha.indexOf(" "));
         
           String[] fechaArray=fecha.split("-");
           
           int anno=Integer.parseInt(fechaArray[0]);
           int mes=Integer.parseInt(fechaArray[1]);; 
           int dia=Integer.parseInt(fechaArray[2]);; 
         
         
          String fechaAjustada=dia+" de " + Meses.getFromId(mes) +" del "+ anno;
          
          return fechaAjustada;
    }
    //<editor-fold defaultstate="collapsed" desc="Funciones Extra">
    /* Settea todos los EM's*/
    public boolean setAllEms() {
        boolean allOK = true;
        try {
            EntityManager em = servicio.getEm();
            this.correoDao.setEm(em);
            this.ordenDao.setEm(em);
            this.productoDao.setEm(em);
            this.usuarioDao.setEm(em);
            this.clienteDao.setEm(em);
            this.adjuntosDao.setEm(em);
        } catch (Exception e) {
            allOK = false;
            e.printStackTrace();
        }
        return allOK;
    }

    /* Limpia la Lista*/
    public void cleanList() {
        System.out.println("Limpiando lista...");

        if (this.correosUsuario.size() > 0) {
            Date today = new Date();
            for (Correo c : this.correosUsuario) {
                if (c.getFechaEnvio().compareTo(today) > 0) {
                    System.out.println("Correo agregado: " + c.getId() + ", Fecha: " + c.getFechaEnvio().toLocaleString());
                    this.listaLimpia.add(c);
                }
            }
        } else {
            System.out.println("Error: La lista de Correos esta Vacia");

        }

    }

    public void getAdjuntosCoreo(Correo c) {
        if (c != null) {
            this.adjuntos.clear();
            this.setAdjuntos(this.adjuntosDao.getByMail(c));
            System.out.println("Size Adj: "+this.adjuntos.size());
        }else{
            System.out.println(" Correo Nulo");
        }

    }

    public String getDestinatarios(Correo c) {
        String out = "";

        for (Contacto destinatario : c.getDestinatarios()) {
            out += destinatario.getCorreo() + ", ";
        }

        return out;
    }

    public String getCopiados(Correo c) {
        String out = "";
        if(c.getUsuarioscopiados()!=null){
         for (Usuario copiado : c.getUsuarioscopiados()) {
            out += copiado.getCorreo() + ", ";
        }

        }
       
        return out;
    }

    public String getRemitente(Correo c) {
        String out = "";
        if(c.getRemitentes()!=null){
            for(Usuario u:c.getUsuarios()){
                out+=u.getCorreo()+ " , ";
            }
           
            
        }
       

        return out;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Gets y Sets">
    public AdjuntoDao getAdjuntosDao() {
        return adjuntosDao;
    }

    public void setAdjuntosDao(AdjuntoDao adjuntosDao) {
        this.adjuntosDao = adjuntosDao;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public CorreoDao getCorreoDao() {
        return correoDao;
    }

    public void setCorreoDao(CorreoDao correoDao) {
        this.correoDao = correoDao;
    }

    public ProductoDao getProductoDao() {
        return productoDao;
    }

    public void setProductoDao(ProductoDao productoDao) {
        this.productoDao = productoDao;
    }

    public OrdenDao getOrdenDao() {
        return ordenDao;
    }

    public void setOrdenDao(OrdenDao ordenDao) {
        this.ordenDao = ordenDao;
    }

    public UsuarioDao getUsuarioDao() {
        return usuarioDao;
    }

    public void setUsuarioDao(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public ClienteDao getClienteDao() {
        return clienteDao;
    }

    public void setClienteDao(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    public List<Correo> getCorreosUsuario() {
        return correosUsuario;
    }

    public void setCorreosUsuario(List<Correo> correosUsuario) {
        this.correosUsuario = correosUsuario;
    }

    public List<Correo> getListaLimpia() {
        return listaLimpia;
    }

    public void setListaLimpia(List<Correo> listaLimpia) {
        this.listaLimpia = listaLimpia;
    }

    public Correo getTarget() {
        return target;
    }

    public void setTarget(Correo target) {
        this.target = target;
    }

    public List<Adjunto> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<Adjunto> adjuntos) {
        this.adjuntos = adjuntos;
    }

    //</editor-fold>
}
