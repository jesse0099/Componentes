/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.model.Document;
import com.r6.mensajeria.Adjunto;
import com.r6.service.AdjuntoDao;
import com.r6.service.Servicio;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Nvidi
 */
@ManagedBean(name="fileController")
@ViewScoped 
public class FilesController {
    
    private List<Adjunto> adjuntos ;
    
    private AdjuntoDao adjDao;
    
    @PostConstruct
    public void init(){
        adjDao  = new AdjuntoDao();
        adjDao.setEm(Servicio.getEm());
        adjuntos = adjDao.getAll();
        
    }

    public List<Adjunto> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<Adjunto> adjuntos) {
        this.adjuntos = adjuntos;
    }
    
    
    
}
