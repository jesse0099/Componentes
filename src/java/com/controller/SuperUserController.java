/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.r6.mensajeria.Contacto;
import com.r6.mensajeria.Sistema;
import com.r6.mensajeria.Usuario;
import com.r6.service.ContactoDao;
import com.r6.service.Servicio;
import com.r6.service.SistemaDao;
import com.r6.service.UsuarioDao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Lenovo
 */
@ManagedBean(name = "superUserController")
@ViewScoped
public class SuperUserController {
    
   private boolean activo;
   private String correo;
   private String contraseña; 
   private String sistemaElegido;
  private String usuarioElegido;
   private Map<String,String> sistemas = new HashMap<String, String>();
      private Map<String,String> usuarios;
    @PostConstruct
    public void init(){
       
        usuarios=new HashMap<String, String>();
       sistemas=new HashMap<String, String>();

       SistemaDao sisDao=new SistemaDao(Servicio.getEm());
       
      UsuarioDao uDao=new UsuarioDao(Servicio.getEm());
      Usuario user=new Usuario();
       
       for(Sistema sis:sisDao.getAll()){
           if(!sis.getNombreSistema().equals("SuperSistema")){
           sistemas.put(sis.getNombreSistema(),sis.getNombreSistema());  
           
           }
           
       }
        Map<String,String> map = new HashMap<String, String>();
       for(Usuario u:uDao.getAll()){
           if(!u.getCorreo().equals("super@mail.com")){
                map.put(u.getCorreo(),u.getCorreo());
           }
 
           
       }
       
    }

    public Map<String, String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Map<String, String> usuarios) {
        this.usuarios = usuarios;
    }

 
    
    
    public void guardarUser(){
    
         SistemaDao sisDao=new SistemaDao(Servicio.getEm());
          
         Sistema sis=new Sistema();
        
         for(Sistema s:sisDao.getAll()){
             if(s.getNombreSistema().equals(sistemaElegido)){
               
                 sis=s;
             }
         }
         
          UsuarioDao uDao=new UsuarioDao(Servicio.getEm());
          
          Usuario user=new Usuario();
          user.setActivo(Boolean.TRUE);
          user.setSistema(sis);
          user.setCorreo(correo);
          user.setContrasenia(contraseña);
          user.setAdmin(Boolean.FALSE);
          user.setSuperUser(Boolean.FALSE);
          
          Boolean existeUsuario=false;
          //Revisa si los campos están vacíos
          if(!correo.equals("") && !contraseña.equals("")){
              //Revisa si el usuario ya existe en ese sistema 
              for(Usuario u:uDao.getAll()){
                  if(u.getCorreo().equals(correo) && u.getSistema().equals(sis)){
                      existeUsuario=true;
                  }
              }
              //Si existe el usuario, muestra error, si no, lo guarda 
              if(existeUsuario){
              PrimeFaces.current().executeScript("PF('dlgError').show();"); 
              }else{
              uDao.save(user);
               PrimeFaces.current().executeScript("PF('dlg1').show();"); 
              }
        
          }else{
             PrimeFaces.current().executeScript("PF('dlgError').show();");  
          }
         
          
          ContactoDao conc=new ContactoDao();
          conc.setEm(Servicio.getEm());
          
     
        
        correo="";
        contraseña="";
        
          
    }
    
    Sistema sis=new Sistema();
    public void elegirSistema(){
        
         usuarios=new HashMap<String, String>();
        
        if(sistemaElegido!=null && !sistemaElegido.equals("") ){
            
        SistemaDao sisDao=new SistemaDao(Servicio.getEm());
          
         sis=new Sistema();
        
         for(Sistema s:sisDao.getAll()){
             if(s.getNombreSistema().equals(sistemaElegido)){
               
                 sis=s;
             }
         }    
        
         UsuarioDao uDao=new UsuarioDao(Servicio.getEm());
          
        for(Usuario u:uDao.getAll()){
            if(u.getSistema().getIdSistema()==sis.getIdSistema()){
                usuarios.put(u.getCorreo(),u.getCorreo());
            }
        }
         
         
        }
        
    }
    
    
    public void editUser(){
        
        
        Usuario userEdit=new Usuario();
        
        UsuarioDao uDao=new UsuarioDao(Servicio.getEm());
        
        if(sistemaElegido.equals("")){
          PrimeFaces.current().executeScript("PF('dlgError').show();"); 
           setActivo(false);
           contraseña="";
          usuarioElegido="";   
        }else{
             for(Usuario u:uDao.getAll()){
              if(u.getCorreo().equals(usuarioElegido)){
                  userEdit=u;
                  
                  if(!contraseña.equals("")){
                     userEdit.setContrasenia(contraseña);   
                     userEdit.setActivo(activo);
                     uDao.update(userEdit);
                     PrimeFaces.current().executeScript("PF('dlg1').show();");
                    }else{
                        PrimeFaces.current().executeScript("PF('dlgPass').show();");
                  }
          
              
              }
          } 
              setActivo(false);
           contraseña="";
          usuarioElegido="";   
        
        }
       
             
    }
    
    public void estadoUsuario(){
        
        Usuario userEdit=new Usuario();
        
        UsuarioDao uDao=new UsuarioDao(Servicio.getEm());
        
         for(Usuario u:uDao.getAll()){
              if(u.getSistema().getIdSistema()==sis.getIdSistema() && u.getCorreo().equals(usuarioElegido)){
                  activo = u.getActivo();
              }
          } 

    }
    
    
    public String getSistemaElegido() {
        return sistemaElegido;
    }

    public void setSistemaElegido(String sistemaElegido) {
        this.sistemaElegido = sistemaElegido;
    }

    public Map<String, String> getSistemas() {
        return sistemas;
    }

    public void setSistemas(Map<String, String> sistemas) {
        this.sistemas = sistemas;
    }

    public String getUsuarioElegido() {
        return usuarioElegido;
    }

    public void setUsuarioElegido(String usuarioElegido) {
        this.usuarioElegido = usuarioElegido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    
    


    
}
