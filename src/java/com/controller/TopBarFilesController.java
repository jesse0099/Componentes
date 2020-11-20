/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.r6.mensajeria.Sistema;
import com.r6.service.Dao;
import com.r6.service.SistemaDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Nvidi
 */
@ManagedBean(name="topBarFilesController")
@SessionScoped
public class TopBarFilesController implements Serializable {
    
    private List<Sistema> sistemas;
    
    private Sistema sisSelected;

    @PostConstruct
    public void init(){
        
        Dao mDao = new SistemaDao();
        
        this.sistemas = new ArrayList<>();
        
        this.sistemas = ((SistemaDao)mDao).getByUser(DatosUsuario.user);
        
        Sistema fake = new Sistema();
        fake.setId(new Integer(0));
        fake.setNombre("None");
        
        sisSelected = fake;
        
    }
    
    
    
    public Sistema getSisSelected() {
        return sisSelected;
    }

    public void setSisSelected(Sistema sisSelected) {
        this.sisSelected = sisSelected;
    }
    
    
    public List<Sistema> getSistemas() {
        return sistemas;
    }

    public void setSistemas(List<Sistema> sistemas) {
        this.sistemas = sistemas;
    }
    
    public void onItemSelectedListener(){
    
        System.out.println("Seleccionado : "+this.sisSelected.getNombre());
    }
    
    
    
}
