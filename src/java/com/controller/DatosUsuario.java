/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.r6.mensajeria.Sistema;
import com.r6.mensajeria.Usuario;
import com.r6.service.Servicio;
import com.r6.service.SistemaDao;
import com.r6.service.UsuarioDao;
import java.io.Serializable;

import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Lenovo
 */
@ManagedBean(name = "userController")
@ViewScoped
public class DatosUsuario implements Serializable {
    
  //Estos son el usuario y el sistema seleccionados   
  public static Sistema sis; 
  public static Usuario user;
  
  
  
 private String usuarioStr="";
 private String contraStr="";
  
  public static boolean hayCoincidencias;
  private List<Usuario> usuariosCoincidencia;
  
  public void getCoincidencias(){
      
      hayCoincidencias=false;
    
    SistemaDao sis=new SistemaDao(Servicio.getEm());
    UsuarioDao uDao=new UsuarioDao(Servicio.getEm());
    
    usuariosCoincidencia=new ArrayList<>();
    
   
    for(Usuario u:uDao.getAll()){
     
        
        
        if(u.getCorreo().equals(usuarioStr) && u.getContrasenia().equals(contraStr)){
           
            hayCoincidencias=true;
            usuariosCoincidencia.add(u);
        }
        
    }
     

 
if(hayCoincidencias){
  
     PrimeFaces.current().ajax().update("sisDialog");
     
 PrimeFaces.current().executeScript("PF('sisDialog').show();");
     
}else{
           PrimeFaces.current().executeScript("PF('errorDialog').show();");
       
     
              
     
      
  }

  }
  
  
  public void setUsuarioSelect(Usuario u){
      DatosUsuario.user=u;
      
      usuarioStr=u.getCorreo();
      String destino;
      
      if(u.getIdUsuario()==1){
          destino="AgregarUsuarios.xhtml";
      }else{
          destino="schedule.xhtml";
      }
             
      
      
      try {

            HttpServletRequest request = (HttpServletRequest) FacesContext
                    .getCurrentInstance().getExternalContext().getRequest();

            FacesContext context = FacesContext.getCurrentInstance();

            FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .redirect(
                            request.getContextPath()
                            + String.format("/faces/%s", destino));
         

        } catch (Exception e) {

        }
  } 
  
    public static Sistema getSis() {
        return sis;
    }

    public static void setSis(Sistema sis) {
        DatosUsuario.sis = sis;
    }

    public static Usuario getUser() {
        return user;
    }

    public static void setUser(Usuario user) {
        DatosUsuario.user = user;
    }

    public String getUsuarioStr() {
        return usuarioStr;
    }

    public void setUsuarioStr(String usuarioStr) {
        this.usuarioStr = usuarioStr;
    }

    public String getContraStr() {
        return contraStr;
    }

    public void setContraStr(String contraStr) {
        this.contraStr = contraStr;
    }

    public static boolean isHayCoincidencias() {
        return hayCoincidencias;
    }

    public static void setHayCoincidencias(boolean hayCoincidencias) {
        DatosUsuario.hayCoincidencias = hayCoincidencias;
    }

    public List<Usuario> getUsuariosCoincidencia() {
        return usuariosCoincidencia;
    }

    public void setUsuariosCoincidencia(List<Usuario> usuariosCoincidencia) {
        this.usuariosCoincidencia = usuariosCoincidencia;
    }

  
  
  
}
