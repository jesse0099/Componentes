/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.r6.mensajeria.Bitacora;
import com.r6.service.BitacoraDao;
import com.r6.service.Servicio;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Nvidi
 */
@ManagedBean(name = "bitacoraController")
@ViewScoped
public class BitacoraController {

    private Bitacora selectedBitacora;
    private List<Bitacora> bitacoras;

    @PostConstruct
    public void init() {
        onLoad();
    }

    public void onLoad() {
        this.bitacoras = new ArrayList<>();
        BitacoraDao bDao = new BitacoraDao();
        bDao.setEm(Servicio.getEm());

        for (Bitacora b : bDao.getByUserAndSys(String.valueOf(DatosUsuario.user.getIdUsuario()), String.valueOf(DatosUsuario.sis.getIdSistema()))) {

            String paraFormateado = "";
            String copiadosFormateado = "";
            b.setDe(b.getDe().split("-")[0]);

            for (String par : b.getPara().split(";")) {

                paraFormateado += par + "\n" + "  ";
            }

            for (String cop : b.getCopiados().split(";")) {
                copiadosFormateado += cop + "\n" + "  ";
            }

            b.setPara(paraFormateado);
            b.setCopiados(copiadosFormateado);

            this.bitacoras.add(b);
        }
    }

    public Bitacora getSelectedBitacora() {
        return selectedBitacora;
    }

    public void setSelectedBitacora(Bitacora selectedBitacora) {
        this.selectedBitacora = selectedBitacora;
    }

    public List<Bitacora> getBitacoras() {
        return bitacoras;
    }

    public void setBitacoras(List<Bitacora> bitacoras) {
        this.bitacoras = bitacoras;
    }

}
