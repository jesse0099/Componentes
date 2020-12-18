/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.r6.mensajeria.Adjunto;
import com.r6.mensajeria.Bitacora;
import com.r6.mensajeria.Usuario;
import com.r6.service.AdjuntoDao;
import com.r6.service.BitacoraDao;
import com.r6.service.Servicio;
import com.r6.service.UsuarioDao;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Nvidi
 */
@ManagedBean(name = "bitacoraController")
@SessionScoped
public class BitacoraController {

    private Bitacora selectedBitacora;
    private List<Bitacora> bitacoras;

    @PostConstruct
    public void init() {
        onLoad();
    }
    
    //Sí, esto bien se podía haber hecho con un array de Strings, pero queda más limpio así
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
        
        this.selectedBitacora = new  Bitacora();
        
        SimpleDateFormat format = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy",  Locale.forLanguageTag("es-ES"));
        //SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
        this.bitacoras = new ArrayList<>();
        BitacoraDao bDao = new BitacoraDao();
        bDao.setEm(Servicio.getEm());
        
        String userYSis;
        String userStr;
        String sistemaStr;
        int indexSemicolon;
        
        for(Bitacora b:bDao.getAll()){
            userYSis=b.getDe().substring(b.getDe().lastIndexOf("-")+1);
            System.out.println("Correo:"+userYSis);
            indexSemicolon=userYSis.indexOf(";");
            System.out.println(""+indexSemicolon);
            
            userStr=userYSis.substring(0,indexSemicolon);
            int userId=Integer.parseInt(userStr);
           sistemaStr=userYSis.substring(indexSemicolon+1);
            
            System.out.println(userStr);
            System.out.println(sistemaStr);
            
            
            
            if(DatosUsuario.user.getIdUsuario()==userId){
               
                String paraFormateado = "";
            String copiadosFormateado = "";
            b.setDe(b.getDe().split("-")[0]);

            for (String par : b.getPara().split(";")) {

                paraFormateado += par + "\n" + "  ";
            }
            
            if(b.getCopiados()!=null){
                for (String cop : b.getCopiados().split(";")) {
                copiadosFormateado += cop + "\n" + "  ";
            } 
                
            }
           
          
                     
            b.setPara(paraFormateado);
            b.setCopiados(copiadosFormateado);
            bDao.getEm().detach(b);
            this.bitacoras.add(b);
            }
        }
        /*
        for (Bitacora b : bDao.getByUserAndSys(String.valueOf(DatosUsuario.user.getIdUsuario()), String.valueOf(DatosUsuario.sis.getIdSistema()))) {

            
            
        }
*/
    }
    
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
    
    public List<Adjunto> getAdjuntoByBit(Bitacora bit){
        AdjuntoDao aDao = new AdjuntoDao();
        AdjuntoDao.setEm(Servicio.getEm());
        return aDao.getByBit(bit);
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
