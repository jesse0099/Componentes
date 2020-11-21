/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.model.Document;
import com.r6.mensajeria.Adjunto;
import com.r6.mensajeria.Contacto;
import com.r6.mensajeria.Correo;
import com.r6.mensajeria.Sistema;
import com.r6.mensajeria.Usuario;
import com.r6.service.AdjuntoDao;
import com.r6.service.CorreoDao;
import com.r6.service.Dao;
import com.r6.service.Servicio;
import com.r6.service.SistemaDao;
import com.r6.service.UsuarioDao;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Nvidi
 */
@ManagedBean(name = "fileController")
@SessionScoped
public class FilesController {

    private boolean itemSelected;

    private List<Correo> correos;

    private List<Correo> correosBackUp;

    private List<Adjunto> adjuntos;

    private List<Sistema> sistemas;

    private String filterDests;

    private Sistema sisSelected;

    @PostConstruct
    public void init() {

        //Carga de destinatarios
        Dao mDao = new SistemaDao();

        this.sistemas = new ArrayList<>();

        this.sistemas = ((SistemaDao) mDao).getByUser(DatosUsuario.user);

        Sistema fake = new Sistema();
        fake.setId(new Integer(0));
        fake.setNombre("None");

        sisSelected = fake;

        correos = new ArrayList<>();

        CorreoDao cDao = new CorreoDao();
        cDao.setEm(Servicio.getEm());

        for (Correo c : cDao.getByUserAndSys(DatosUsuario.user, DatosUsuario.sis)) {
            System.out.println("Correo : " + c.getId());
            //Listado final de correos
            this.correos.add(c);
        }

        this.correosBackUp = new ArrayList<>();
        for (Correo c : this.correos) {
            this.correosBackUp.add(c);
        }

    }

    public void onItemSelectedListener() {

        if (!sisSelected.getNombre().equals("None")) {

            this.itemSelected = true;

            for (Sistema s : sistemas) {
                if (s.getNombre().equals(sisSelected.getNombre())) {
                    sisSelected.setId(s.getId());
                }
            }

            System.out.println("Seleccionado : " + this.sisSelected.getNombre());
            System.out.println("ID : " + this.sisSelected.getId());
            //Recargar lista de correos
            Dao daoM = new CorreoDao();
            this.correos = ((CorreoDao) daoM).getByUserAndSys(DatosUsuario.user, sisSelected);
            System.out.println("Size" + this.correos.size());

            updateView();
        }

    }

    public List<String> getDestinatariosByMail(Correo c) {
        List<String> returned = new ArrayList<>();
        for (Contacto us : c.getDestinatarios()) {
            returned.add(us.getCorreo());
        }
        return returned;
    }

    public static List<Adjunto> getFilesByMail(Correo c) {
        List<Adjunto> returned = new ArrayList<>();
        Dao daoM = new AdjuntoDao();
        ((AdjuntoDao) daoM).setEm(Servicio.getEm());
        for (Adjunto ad : (List<Adjunto>) ((AdjuntoDao) daoM).getByMail(c)) {

            System.out.println("Adjunto : " + ad.getId() + " * Archivo * " + ad.getArchivo().toString());

            returned.add(ad);

        }
        return returned;
    }

    public static StreamedContent byteArrayToStreamedContent(byte[] adj, int name) {
        StreamedContent returned = null;
        InputStream inputStr = null;

        inputStr = new ByteArrayInputStream(adj);

        returned = new DefaultStreamedContent(inputStr, getMIMETypeFromByteArray(adj), String.valueOf(name) + "." + getMIMETypeFromByteArray(adj).split("/")[1]);

        return returned;
    }

    public static String getMIMETypeFromByteArray(byte[] input) {
        byte[] topOfStream = new byte[32];
        System.arraycopy(input, 0, topOfStream, 0, topOfStream.length
        );
        return guessMimeType(topOfStream);
    }

    public static StreamedContent byteArrayToStreamedContent(byte[] adj, int name,String contentType) {
        StreamedContent returned = null;
        InputStream inputStr = null;

        inputStr = new ByteArrayInputStream(adj);

        returned = new DefaultStreamedContent(inputStr, getMIMETypeFromByteArray(adj), String.valueOf(name) + "." + contentType.split("/")[1]);

        return returned;
    }

    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }

    public static String guessMimeType(byte[] topOfStream) {

        String mimeType = null;
        Properties magicmimes = new Properties();
        FileInputStream in = null;

        try {
            in = new FileInputStream("C:/Users/Nvidi/Documents/magicmimes.properties");
            magicmimes.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Enumeration keys = magicmimes.keys(); keys.hasMoreElements();) {
            String key = (String) keys.nextElement();
            byte[] sample = new byte[key.length()];
            System.arraycopy(topOfStream, 0, sample, 0, sample.length);
            if (key.equals(new String(sample))) {
                mimeType = magicmimes.getProperty(key);
                System.out.println("Mime Found! " + mimeType);
                break;
            } else {
                System.out.println("trying " + key + " == " + new String(sample));
            }
        }

        return mimeType;
    }

    public List<Correo> getCorreos() {
        return correos;
    }

    public void setCorreos(List<Correo> correos) {
        this.correos = correos;
    }

    public List<Adjunto> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<Adjunto> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public String getFilterDests() {
        return filterDests;
    }

    public void setFilterDests(String filterDests) {
        this.filterDests = filterDests;
    }

    public List<Sistema> getSistemas() {
        return sistemas;
    }

    public void setSistemas(List<Sistema> sistemas) {
        this.sistemas = sistemas;
    }

    public int adjListSize(List<Adjunto> adj) {
        return adj.size();
    }

    public Sistema getSisSelected() {
        return sisSelected;
    }

    public void setSisSelected(Sistema sisSelected) {
        this.sisSelected = sisSelected;
    }

    public List<Correo> getCorreosBackUp() {
        return correosBackUp;
    }

    public void setCorreosBackUp(List<Correo> correosBackUp) {
        this.correosBackUp = correosBackUp;
    }

    public void updateView() {
        PrimeFaces.current().ajax().update("pricing");
    }

    public void filterByDests() {
        if (filterDests.isEmpty() || filterDests.equals("")) {
            correos = new ArrayList<>();

            CorreoDao cDao = new CorreoDao();
            cDao.setEm(Servicio.getEm());
            Sistema sisID;

            if (itemSelected) {
                sisID = sisSelected;
            } else {
                sisID = DatosUsuario.sis;
            }

            for (Correo c : cDao.getByUserAndSys(DatosUsuario.user, sisID)) {
                System.out.println("Correo : " + c.getId());
                //Listado final de correos
                this.correos.add(c);
            }
            updateView();
        } else {

            List<Correo> temp = new ArrayList<>();
            System.out.println("Filtro " + this.filterDests);
            this.correos.forEach((c) -> {
                getDestinatariosByMail(c).stream().filter((con) -> (con.contains(filterDests))).forEachOrdered((_item) -> {
                    System.out.println("Correo :" + c.getAsunto());
                    temp.add(c);
                });
            });
            this.correos.clear();
            for (Correo c : temp) {
                this.correos.add(c);
            }
            updateView();
        }
    }

}
