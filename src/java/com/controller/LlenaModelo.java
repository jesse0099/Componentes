/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.r6.funciones.CorreoFunc;
import com.r6.funciones.RecordatorioFunc;
import com.r6.mensajeria.Adjunto;
import com.r6.mensajeria.Contacto;
import com.r6.mensajeria.Correo;
import com.r6.mensajeria.Sistema;
import com.r6.mensajeria.Usuario;
import com.r6.service.AdjuntoDao;
import com.r6.service.ContactoDao;
import com.r6.service.IntervalosTiempo;
import com.r6.service.Servicio;
import com.r6.service.SistemaDao;
import com.r6.service.UsuarioDao;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Lenovo
 */
@ManagedBean(name = "llenaModelo", eager = true)
@ApplicationScoped
public class LlenaModelo {

    @PostConstruct
    public void init() {

        Servicio.setServerURL("jdbc:mysql://localhost:3306/mensajeria?serverTimezone=UTC");
        Servicio.setUsername("root");
        Servicio.setPassword("AsdF2013");
        Servicio.setDriver("com.mysql.jdbc.Driver");
        Servicio.setHbm2DDLprotocol("update");
        Servicio.setDialect("org.hibernate.dialect.MySQLDialect");

        try {
            Servicio.startEntityManagerFactory();

            SistemaDao sis = new SistemaDao(Servicio.getEm());
            UsuarioDao uDao = new UsuarioDao(Servicio.getEm());

            //Esto es para hacer que solo intente ingresar los datos iniciales una vez
            try {

                Optional<Sistema> s;

                //Intenta hacer un select del sistema 2, pero si no ha sido insertado
                //aún, la consulta va a fallar, y se va a ir al catch, donde 
                //se va a ejecutar el método para poblar el modelo
                s = sis.get(2);

            } catch (Exception e) {
                poblarModelo();
            }

            //Esto es para asignar el sistema y usuario que se va a usar
            Optional<Usuario> opUser = uDao.get(2);
            DatosUsuario.user = opUser.get();
            System.out.println("Asignó user");
            //Sistema de contabilidad
            Optional<Sistema> opSis = sis.get(2);
            DatosUsuario.sis = opSis.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void poblarModelo() {

        //Colocar aquí el directorio en que está el archivo adjunto
        String directorioAdjunto = "C:/Users/Daniel/Documents/Setup/setupMensajeria.txt";
        try {

            SistemaDao sis = new SistemaDao(Servicio.getEm());

            Sistema sist = new Sistema();
            sist.setNombre("Contabilidad");
            sist.setActivo(true);

            sis.save(sist);

            UsuarioDao uDao = new UsuarioDao(Servicio.getEm());

            Usuario usuario = new Usuario(2, "teamr6cr@gmail.com", "bada907817", false, false, false);
            usuario.setSistema(sist);
            uDao.save(usuario);
            Usuario usuario2 = new Usuario(2, "ventasestructurasulatina@gmail.com", "wvjjk611", false, false, false);
            usuario2.setSistema(sist);
            uDao.save(usuario2);

            ContactoDao conc = new ContactoDao();
            conc.setEm(Servicio.getEm());

            Contacto contacto1 = new Contacto();
            contacto1.setNombre("Wes");
            contacto1.setApellido("Mena");
            contacto1.setCorreo("weslinmena@gmail.com");
            contacto1.setSuscriptor(true);
            contacto1.setUsuario(usuario);
            conc.save(contacto1);

            Contacto cont = new Contacto();
            cont.setNombre("Jese");
            cont.setApellido("Chavez");
            cont.setCorreo("jese.chavez@ulatina.net");
            cont.setSuscriptor(false);
            cont.setUsuario(usuario);
            conc.save(cont);

            Contacto contacto3 = new Contacto();
            contacto3.setNombre("Jose");
            contacto3.setApellido("Chaves");
            contacto3.setCorreo("josemcm85@gmail.com");
            contacto3.setSuscriptor(true);
            contacto3.setUsuario(usuario2);
            conc.save(contacto3);

            Contacto contacto4 = new Contacto();
            contacto4.setNombre("Daniel");
            contacto4.setApellido("Hernández");
            contacto4.setCorreo("daniel.hernandez20@ulatina.net");
            contacto4.setSuscriptor(false);
            contacto4.setUsuario(usuario2);
            conc.save(contacto4);

            CorreoFunc funtion = new CorreoFunc();
            RecordatorioFunc recFuntion = new RecordatorioFunc();
            funtion.setEm(Servicio.getEm());
            recFuntion.setEm(Servicio.getEm());

            Date date = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            cal.add(Calendar.MONTH, 3);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Correo c = new Correo();
            c.setAsunto(" Reunión con accionistas");
            c.setCuerpo(" Es necesario llevar gráficas de los beneficios de la compañía");
            c.setFechaEnvio(cal.getTime());
            c.setTipo("html/text");
            c.setInifinito(Boolean.FALSE);

            //Remitente
            Set<Usuario> usuarioList1 = new HashSet<>();
            usuarioList1.add(usuario);
            c.setUsuarios(usuarioList1);

            //Destinatario
            Set<Contacto> contactList1 = new HashSet<>();
            contactList1.add(contacto1);
            contactList1.add(cont);
            c.setDestinatarios(contactList1);

            //Copiados
            Set<Usuario> usuarioCopyList = new HashSet<>();
            usuarioCopyList.add(usuario2);
            c.setUsuarioscopiados(usuarioCopyList);

            //Adjuntos
            AdjuntoDao adDao = new AdjuntoDao();
            adDao.setEm(Servicio.getEm());

            File file = new File(directorioAdjunto);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            //Adjunto   \

            List<File> files = new ArrayList<>();
            Adjunto adj = new Adjunto();
            adj.setArchivo(fileContent);

            adDao.save(adj);

            Set<Adjunto> adjuntoList = new HashSet<>();
            adjuntoList.add(adj);

            c.setAdjuntos(adjuntoList);

            funtion.crearRecxMes(c, 2);

            //Correo 2 
            Date date2 = new Date();
            Calendar cal2 = new GregorianCalendar();
            cal2.setTime(date2);
            Correo correo2 = new Correo();
            correo2.setAsunto("Cumpleaños de empleado 1");
            correo2.setCuerpo(" Comprar una tarjeta");
            correo2.setFechaEnvio(cal2.getTime());
            correo2.setTipo("html/text");
            correo2.setInifinito(Boolean.TRUE);
            //Ajustar para que sea cada año
            correo2.setIntervalo(IntervalosTiempo.ANIO);
            //Remitente
            Set<Usuario> usuarioList2 = new HashSet<>();
            usuarioList2.add(usuario);
            correo2.setUsuarios(usuarioList2);

            //Destinatario
            Set<Contacto> contactList2 = new HashSet<>();
            contactList2.add(cont);

            correo2.setDestinatarios(contactList2);

            funtion.crearRecxMes(correo2, 0);

            //Correo3
            Date date3 = new Date();
            Calendar cal3 = new GregorianCalendar();
            cal3.setTime(date3);
            Correo correo3 = new Correo();
            correo3.setAsunto(" Reunión con recursos humanos");
            correo3.setCuerpo(" llevar una lista de los salarios de empleados");
            correo3.setFechaEnvio(cal3.getTime());
            correo3.setTipo("html/text");
            correo3.setInifinito(Boolean.FALSE);

            //Remitente
            Set<Usuario> usuarioList3 = new HashSet<>();
            usuarioList3.add(usuario2);
            correo3.setUsuarios(usuarioList3);

            //Destinatario
            Set<Contacto> contactList3 = new HashSet<>();
            contactList3.add(contacto3);

            correo3.setDestinatarios(contactList3);

            funtion.crearRecxMes(correo3, 0);

            //Correo4
            Date date4 = new Date();
            Calendar cal4 = new GregorianCalendar();
            cal4.setTime(date4);
            Correo correo4 = new Correo();
            correo4.setAsunto(" Charla SAP");
            correo4.setCuerpo(" Un técnico de SAP vendrá a mostrar como funcionan las herramientas de la compañía");
            correo4.setFechaEnvio(cal4.getTime());
            correo4.setTipo("html/text");
            correo4.setInifinito(Boolean.FALSE);

            //Remitente
            Set<Usuario> usuarioList4 = new HashSet<>();
            usuarioList4.add(usuario2);
            correo4.setUsuarios(usuarioList4);

            //Destinatario
            Set<Contacto> contactList4 = new HashSet<>();
            contactList4.add(contacto4);

            correo4.setDestinatarios(contactList4);

            funtion.crearRecxMes(correo4, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
