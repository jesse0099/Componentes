/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.r6.funciones.CorreoFunc;
import com.r6.funciones.RecordatorioFunc;
import com.r6.mensajeria.Adjunto;
import com.r6.mensajeria.Cliente;
import com.r6.mensajeria.Compannia;
import com.r6.mensajeria.Contacto;
import com.r6.mensajeria.Correo;
import com.r6.mensajeria.Individuo;
import com.r6.mensajeria.Producto;
import com.r6.mensajeria.Sistema;
import com.r6.mensajeria.Usuario;
import com.r6.service.AdjuntoDao;
import com.r6.service.CompanniaDao;
import com.r6.service.ContactoDao;
import com.r6.service.Dao;
import com.r6.service.IndividuoDao;
import com.r6.service.IntervalosTiempo;
import com.r6.service.ProductoDao;
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
import javax.faces.bean.ManagedProperty;
import javax.faces.component.behavior.FacesBehavior;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Lenovo
 */
@ManagedBean(name = "llenaModelo", eager = true)
@ApplicationScoped
public class LlenaModelo {
   
@PostConstruct
public void init(){
    
 
   Servicio.setServerURL("jdbc:mysql://localhost:3306/mensajeria?serverTimezone=UTC");
   Servicio.setUsername("root");
   Servicio.setPassword("Leiasuri85");
   Servicio.setDriver("com.mysql.jdbc.Driver");
   Servicio.setHbm2DDLprotocol("update");
   Servicio.setDialect("org.hibernate.dialect.MySQLDialect");
   
   
   
   try{
    Servicio.startEntityManagerFactory();
    
    
    SistemaDao sis=new SistemaDao(Servicio.getEm());
    UsuarioDao uDao=new UsuarioDao(Servicio.getEm());
    

    
    
    //Esto es para hacer que solo intente ingresar los datos iniciales una vez
    
    try{
    
       Optional<Sistema> s;
    
       //Intenta hacer un select del sistema 2, pero si no ha sido insertado
       //aún, la consulta va a fallar, y se va a ir al catch, donde 
       //se va a ejecutar el método para poblar el modelo
       s=sis.get(2);
       
      
       
    }catch(Exception e){
      poblarModelo();
    } 
        
    //Esto es para asignar el sistema y usuario que se va a usar
    
    Optional<Usuario> opUser=uDao.get(2);
    DatosUsuario.user=opUser.get();
    System.out.println("Asignó user");
    //Sistema de contabilidad
    Optional<Sistema> opSis=sis.get(2);
    DatosUsuario.sis=opSis.get();
   }catch(Exception e){
     e.printStackTrace();
   }
  
 
}
  

public static void poblarModelo() {
    	
    	//Colocar aquí el directorio en que está el archivo adjunto
    	String directorioAdjunto = "C:/Users/josem/Desktop/prueba.txt";
        String directorioAdjunto2 = "C:/Users/josem/Desktop/prueba2.txt";
    	 try {
             

      
          SistemaDao sis=new SistemaDao(Servicio.getEm());
          
         Sistema sist=new Sistema();
         sist.setNombre("Contabilidad");
          sist.setActivo(true);
          
          sis.save(sist);
            
          Sistema sist2=new Sistema();
          sist2.setNombre("Recursos humanos");
          sist2.setActivo(true);
          
          sis.save(sist2);
          
          UsuarioDao uDao=new UsuarioDao(Servicio.getEm());
          
          
          
          Usuario usuarioA = new Usuario(3,"teamr6cr@gmail.com","bada907817",false,false,false);
          usuarioA.setSistema(sist2);
            usuarioA.setActivo(Boolean.TRUE);
          uDao.save(usuarioA);
          
          Usuario usuario = new Usuario(2,"ventasestructurasulatina@gmail.com","wvjjk611",false,false,false);
          usuario.setSistema(sist);
          usuario.setActivo(Boolean.TRUE);
          uDao.save(usuario);
          
          Usuario usuario2 = new Usuario(3,"ventasestructurasulatina@gmail.com","wvjjk611",false,false,false);
          usuario2.setSistema(sist2);
          usuario2.setActivo(Boolean.TRUE);
           
          uDao.save(usuario2);
          
          
          ContactoDao conc=new ContactoDao();
          conc.setEm(Servicio.getEm());
          
          IndividuoDao inDao=new IndividuoDao();
          inDao.setEm(Servicio.getEm());
          
          Cliente cli=new Cliente();
          cli.setNombre("Andrés Mora");
          cli.setDireccion("Cartago");
          cli.setTelefono("2754-3690");
          cli.setTipo(1);
          
          Individuo indi=new Individuo();
          indi.setCliente(cli);
          indi.setNumeroLic("123456");
          
          cli.setIndividuo(indi);
          
          inDao.save(indi);
          
          
          
          
          Contacto contacto1=new Contacto();
          contacto1.setNombre("Wes");
          contacto1.setApellido("Mena");
          contacto1.setCorreo("weslinmena@gmail.com");
          contacto1.setSuscriptor(true);
          contacto1.setUsuario(usuario);
          contacto1.setCliente(cli);
          conc.save(contacto1);
          
         
          
          
            
          Cliente cli2=new Cliente();
          cli2.setNombre("Luis Monge");
          cli2.setDireccion("Puntarenas");
          cli2.setTelefono("8850-6920");
          cli2.setTipo(1);
          
          Individuo indi2=new Individuo();
          indi2.setCliente(cli2);
          indi2.setNumeroLic("98420");
          
          cli2.setIndividuo(indi2);
          
          inDao.save(indi2);
          
          
          
         
          
          Contacto cont=new Contacto();
          cont.setNombre("Jese");
          cont.setApellido("Chavez");
          cont.setCorreo("jese.chavez@ulatina.net");
          cont.setSuscriptor(false);
          cont.setUsuario(usuario);
          cont.setCliente(cli2);
          conc.save(cont);
          
          CompanniaDao comDao=new CompanniaDao();
          comDao.setEm(Servicio.getEm());
          
          
          Cliente cli3=new Cliente();
          cli3.setNombre("Dos Cedros S.A");
          cli3.setDireccion("San José");
          //Llame al 1-800-POG
          cli3.setTelefono("1-800-76664");
          cli3.setTipo(2);
          
          Compannia com=new Compannia();
          com.setCliente(cli3);
          
          com.setDescuento(10);
          
          
       
          cli3.setCompannia(com);
          
          comDao.save(com);
          
          
          
          Contacto contacto3=new Contacto();
          contacto3.setNombre("Jose");
          contacto3.setApellido("Chaves");
          contacto3.setCorreo("josemcm85@gmail.com");
          contacto3.setSuscriptor(true);
          contacto3.setUsuario(usuario2);
          contacto3.setCliente(cli3);
          conc.save(contacto3);
          
          
          Cliente cli4=new Cliente();
          cli4.setNombre("DEMADERA  S.A");
          cli4.setDireccion("Heredia");
       
          cli4.setTelefono("2775-2464");
          cli4.setTipo(2);
          
          Compannia com2=new Compannia();
          com2.setCliente(cli4);
          
          com2.setDescuento(5);
          
          
       
          cli4.setCompannia(com2);
          
          comDao.save(com2);
          
          
          
          
          Contacto contacto4=new Contacto();
          contacto4.setNombre("Daniel");
          contacto4.setApellido("Hernández");
          contacto4.setCorreo("daniel.hernandez20@ulatina.net");
          contacto4.setSuscriptor(false);
          contacto4.setUsuario(usuario2);
          contacto4.setCliente(cli4);
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
            c.setTipoCorreo(1);
            
            Correo c2 = new Correo();
            c2.setAsunto(" Reunión con accionistas - COPIA");
            c2.setCuerpo(" Es necesario llevar gráficas de los beneficios de la compañía - COPIA");
            c2.setFechaEnvio(cal.getTime());
            c2.setTipo("html/text");
            c2.setInifinito(Boolean.FALSE);
            c2.setTipoCorreo(1);
            //Remitente
            Set<Usuario> usuarioList1 = new HashSet<>();
            usuarioList1.add(usuario);
            c.setUsuarios(usuarioList1);
            c2.setUsuarios(usuarioList1);

            //Destinatario
            Set<Contacto> contactList1 = new HashSet<>();
            contactList1.add(contacto1);
            contactList1.add(cont);
            c.setDestinatarios(contactList1);
            c2.setDestinatarios(contactList1);

            //Copiados
            Set<Usuario> usuarioCopyList = new HashSet<>();
            usuarioCopyList.add(usuario2);
            c.setUsuarioscopiados(usuarioCopyList);
            c2.setUsuarioscopiados(usuarioCopyList);

            //Adjuntos
            AdjuntoDao adDao = new AdjuntoDao();
            adDao.setEm(Servicio.getEm());

            File file = new File(directorioAdjunto);
            File file2 = new File(directorioAdjunto2);

            byte[] fileContent = Files.readAllBytes(file.toPath());
            byte[] fileContent2 = Files.readAllBytes(file2.toPath());

            //Adjunto   \
            //List<File> files  = new ArrayList<>();
            Adjunto adj = new Adjunto();
            adj.setArchivo(fileContent);

            Adjunto adj2 = new Adjunto();
            adj2.setArchivo(fileContent2);

            adDao.save(adj);
            adDao.save(adj2);

            Set<Adjunto> adjuntoList = new HashSet<>();

            adjuntoList.add(adj);
            adjuntoList.add(adj2);
            c.setAdjuntos(adjuntoList);

            Set<Adjunto> adjuntoList2 = new HashSet<>();
            adjuntoList2.add(adDao.get(adj.getId()).get());

            c2.setAdjuntos(adjuntoList2);

            funtion.crearRecxMes(c, 2);
            funtion.crearRecxMes(c2, 3);
          
         
         
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
         Set<Usuario> usuarioList2=new HashSet<>();
         usuarioList2.add(usuario);
         correo2.setUsuarios(usuarioList2);
         correo2.setTipoCorreo(1);
         //Destinatario
         Set<Contacto>contactList2=new HashSet<>();
         contactList2.add(cont);
     
         correo2.setDestinatarios(contactList2);
         
         funtion.crearRecxMes(correo2,0);
        
         
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
         correo3.setTipoCorreo(1);
       //Remitente
         Set<Usuario> usuarioList3=new HashSet<>();
         usuarioList3.add(usuario2);
         correo3.setUsuarios(usuarioList3);
         
         //Destinatario
         Set<Contacto>contactList3=new HashSet<>();
         contactList3.add(contacto3);
     
         correo3.setDestinatarios(contactList3);
         
         funtion.crearRecxMes(correo3,0);
         
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
         correo4.setTipoCorreo(1);
       //Remitente
         Set<Usuario> usuarioList4=new HashSet<>();
         usuarioList4.add(usuario2);
         correo4.setUsuarios(usuarioList4);
         
         //Destinatario
         Set<Contacto>contactList4=new HashSet<>();
         contactList4.add(contacto4);
     
         correo4.setDestinatarios(contactList4);
         
         funtion.crearRecxMes(correo4,0);
         
         /* Productos */
         
        ProductoDao productoDao = new ProductoDao();
        productoDao.setEm(Servicio.getEm());
        
        Producto prod1 = new Producto();
        Producto prod2 = new Producto();
        Producto prod3 = new Producto();
        
        prod1.setDescripcion("Descripcion");
        prod2.setDescripcion("Descripcion");
        prod3.setDescripcion("Descripcion");
        
        prod1.setNombre("Producto 1");
        prod2.setNombre("Producto 2");
        prod3.setNombre("Producto 3");
        
        prod1.setPrecio(new Double(1500));
        prod2.setPrecio(new Double(8500));
        prod3.setPrecio(new Double(500));
        
        prod1.setTipo(1);
        prod2.setTipo(1);
        prod3.setTipo(1);
        
        
        productoDao.save(prod1);
        productoDao.save(prod2);
        productoDao.save(prod3);
        /*
        productoDao.save(new Producto(1,"Producto 1",new Double(1500.00),"Descripcion",1));
        productoDao.save(new Producto(2,"Producto 2",new Double(8500.00),"Descripcion",1));
        productoDao.save(new Producto(3,"Producto 3",new Double(500.00),"Descripcion",1));
        productoDao.save(new Producto(4,"Producto 4",new Double(2500.00),"Descripcion",1));
        productoDao.save(new Producto(5,"Producto 5",new Double(1550.00),"Descripcion",1));
        productoDao.save(new Producto(6,"Producto 6",new Double(3300.00),"Descripcion",1));
        */     
         } catch (Exception e) {
             System.out.println("XDXD");
             e.printStackTrace();
         }
    }
}
