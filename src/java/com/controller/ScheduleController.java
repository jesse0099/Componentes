/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.controller.DatosUsuario;
import com.r6.funciones.CorreoFunc;
import com.r6.funciones.RecordatorioFunc;
import com.r6.mensajeria.Contacto;
import com.r6.mensajeria.Correo;
import com.r6.mensajeria.Usuario;
import com.r6.service.ContactoDao;
import com.r6.service.CorreoDao;
import com.r6.service.IntervalosTiempo;
import com.r6.service.Servicio;
import com.r6.service.UsuarioDao;
import com.r6.service.RecordatorioDao;
import com.r6.mensajeria.Recordatorio;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author Nvidi
 */
@ManagedBean(name = "scheduleController")
@ViewScoped
public class ScheduleController implements Serializable {

    private ScheduleModel eventModel;

    private ScheduleModel lazyEventModel;

    private ScheduleEvent event = new DefaultScheduleEvent();

    private boolean showWeekends = true;
    private boolean tooltip = true;
    private boolean allDaySlot = true;

    private String timeFormat;
    private String slotDuration = "00:30:00";
    private String slotLabelInterval;
    private String scrollTime = "06:00:00";
    private String minTime = "04:00:00";
    private String maxTime = "20:00:00";
    private String locale = "en";
    private String timeZone = "";
    private String clientTimeZone = "local";
    private String columnHeaderFormat = "";

    private String titulo = "";

    private Date fecha;

    private String descripcion = "";

    private List<String> participantes;
    private String[] participantesSeleccionados;

    private Map<String, String> repetirCada;
    private String repetirSeleccionado;

    private int cantidadRecordatorios;

    private Map<String, String> tipo;
    private String tipoSeleccionado;

    private List<String> participantesOcultos;
    private String[] pOcultosSeleccionados;

    private int idCorreo;
    private boolean mostrarBtn;

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();

        CorreoDao cDao = new CorreoDao();
        Correo correo = new Correo();
        cDao.setEm(Servicio.getEm());

        int idUser = 0;
        for (Correo cList : cDao.getAll()) {

            for (Usuario u : cList.getUsuarios()) {
                idUser = u.getIdUsuario();
            }

            if (idUser == DatosUsuario.user.getIdUsuario()) {

                DefaultScheduleEvent event = new DefaultScheduleEvent(
                        cList.getAsunto(),
                        cList.getFechaEnvio(),
                        cList.getFechaEnvio(),
                        cList.getCuerpo());

                eventModel.addEvent(event);

            }

        }

        //private List<String> participantes;
        participantes = new ArrayList<String>();

        ContactoDao contDao = new ContactoDao();

        contDao.setEm(Servicio.getEm());

        contDao.setUsuario(DatosUsuario.user);

        for (Contacto c : contDao.getAll()) {

            participantes.add(c.getCorreo());
            System.out.println(c.getCorreo());

        }

        participantesOcultos = new ArrayList<String>();

        UsuarioDao userDao = new UsuarioDao();

        for (Usuario us : userDao.getAll()) {
            if (us.getIdUsuario() != DatosUsuario.user.getIdUsuario()) {
                participantesOcultos.add(us.getCorreo());
            }

        }

        repetirCada = new HashMap<String, String>();
        repetirCada.put("Nunca", "Nunca");
        repetirCada.put("Cada día", "Cada día");
        repetirCada.put("Cada semana", "Cada semana");
        repetirCada.put("Cada mes", "Cada mes");
        repetirCada.put("Cada año", "Cada año");

        tipo = new HashMap<String, String>();
        tipo.put("HTML", "HTML");
        tipo.put("Texto plano", "Texto plano");
    }

    public void guardar() {
        RecordatorioDao rDao = new RecordatorioDao();
        rDao.setEm(Servicio.getEm());

        CorreoDao correoDao = new CorreoDao();
        correoDao.setEm(Servicio.getEm());
        Correo c = new Correo();
        c.setAsunto(titulo);
        c.setCuerpo(descripcion);
        c.setFechaEnvio(fecha);

        if (tipoSeleccionado.equals("HTML")) {
            c.setTipo("html/text");
        } else {
            c.setTipo("text");
        }

        //Remitente
        Set<Usuario> usuarioList1 = new HashSet<>();
        usuarioList1.add(DatosUsuario.user);
        c.setUsuarios(usuarioList1);

        Set<Usuario> usuariosOcultos = new HashSet<>();
        UsuarioDao uDao = new UsuarioDao(Servicio.getEm());

        //Compara los usuarios con el array pOcultosSeleccionados
        //Y agrega las coincidencias a nivel de correo a usuariosOcultos
        for (Usuario u : uDao.getAll()) {
            for (int i = 0; i < pOcultosSeleccionados.length; i++) {
                if (pOcultosSeleccionados[i].equals(u.getCorreo())) {
                    usuariosOcultos.add(u);
                }
            }
        }

        c.setUsuarioscopiados(usuariosOcultos);

        Set<Contacto> destinatarios = new HashSet<>();
        ContactoDao conDao = new ContactoDao();
        conDao.setEm(Servicio.getEm());
        conDao.setUsuario(DatosUsuario.user);

        for (Contacto con : conDao.getAll()) {
            for (int i = 0; i < participantesSeleccionados.length; i++) {

                if (participantesSeleccionados[i].equals(con.getCorreo())) {
                    destinatarios.add(con);
                }
            }
        }

        c.setDestinatarios(destinatarios);

        switch (repetirSeleccionado) {

            case "Nunca":
                c.setInifinito(false);
                break;

            case "Cada día":
                c.setInifinito(true);
                c.setIntervalo(IntervalosTiempo.DIA);
                break;

            case "Cada semana":
                c.setInifinito(true);
                c.setIntervalo(IntervalosTiempo.SEMANA);
                break;

            case "Cada mes":
                c.setInifinito(true);
                c.setIntervalo(IntervalosTiempo.MES);
                break;

            case "Cada año":
                c.setInifinito(true);
                c.setIntervalo(IntervalosTiempo.ANIO);
                break;
        }

        //Recordatorios /guardar correo
        CorreoFunc function = new CorreoFunc();
        RecordatorioFunc recFunction = new RecordatorioFunc();
        function.setEm(Servicio.getEm());
        recFunction.setEm(Servicio.getEm());

        if (this.idCorreo > 0) {

            Correo correo = correoDao.getById(idCorreo);
            //Si un correo se edita el numero de recordatorios,mi idea era borrar los recordatorios que pertenecen a ese correo para despues volverlos a crear con el numero nuevo.
            // Aqui abajo esta el metodo borrar que habia hecho pero no esta funcionando bien.
            //Ademas del for de abajo, hay que agregar el metodo que le crearia los nuevos recordatorios a este evento.
            /*
            for (Recordatorio r : rDao.getByMail(correo)) {
                    rDao.delete(r);
            }
            */
            correo.setAsunto(c.getAsunto());
            correo.setCuerpo(c.getCuerpo());
            correo.setFechaEnvio(c.getFechaEnvio());
            correo.setTipo(c.getTipo());
            correo.setInifinito(c.getInifinito());
            //correo.setAdjuntos(c.getAdjuntos()); --No esta contemplado para este Sprint, lo dejo por aqui para no perderlo de vista
            correo.setDestinatarios(c.getDestinatarios());
            correo.setUsuarioscopiados(c.getUsuarioscopiados());
            correo.setIntervalo(c.getIntervalo());
            correoDao.update(correo);

            

        } else {

            function.crearRecxMes(c, cantidadRecordatorios);

        }

        eventModel.clear();
        init();

//        DefaultScheduleEvent event = new DefaultScheduleEvent(
//                titulo,
//                fecha,
//                fecha,
//                descripcion);
//
//        eventModel.addEvent(event);
//
//        //Para limpiar los campos del dialog
//        titulo = "";
//        descripcion = "";
//
//        participantesSeleccionados = new String[1];
//
//        repetirSeleccionado = "";
//
//        cantidadRecordatorios = 0;
//
//        tipoSeleccionado = "";
//
//        pOcultosSeleccionados = new String[1];
    }

    public LocalDateTime getRandomDateTime(LocalDateTime base) {
        LocalDateTime dateTime = base.withMinute(0).withSecond(0).withNano(0);
        return dateTime.plusDays(((int) (Math.random() * 30)));
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }

    private LocalDateTime previousDay8Pm() {
        return LocalDateTime.now().minusDays(1).withHour(20).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime previousDay11Pm() {
        return LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime today1Pm() {
        return LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime theDayAfter3Pm() {
        return LocalDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime today6Pm() {
        return LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime nextDay9Am() {
        return LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime nextDay11Am() {
        return LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime fourDaysLater3pm() {
        return LocalDateTime.now().plusDays(4).withHour(15).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime sevenDaysLater0am() {
        return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime eightDaysLater0am() {
        return LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public LocalDate getInitialDate() {
        return LocalDate.now().plusDays(1);
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    /*
	public void addEvent() {
        
    	if (event.isAllDay()) {
  		//see https://github.com/primefaces/primefaces/issues/1164
   		if (event.getStartDate().toLocalDate().equals(event.getEndDate().toLocalDate())) {
				event.setEndDate(event.getEndDate().plusDays(1));
			}
		}

		if(event.getId() == null)
			eventModel.addEvent(event);
		else
			eventModel.updateEvent(event);
		
		event = new DefaultScheduleEvent();
	}
     */
//	public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
//		event = selectEvent.getObject();
//	}
    public void onDateSelect(SelectEvent selectEvent) {
        this.idCorreo = 0;
        titulo = "";
        descripcion = "";

        participantesSeleccionados = new String[1];

        repetirSeleccionado = "";

        cantidadRecordatorios = 0;

        tipoSeleccionado = "";

        pOcultosSeleccionados = new String[1];
        
        mostrarBtn = false;
        
        Date d = (Date) selectEvent.getObject();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.HOUR, 6);
        d = cal.getTime();

        fecha = d;
        event = new DefaultScheduleEvent("", d, d);

    }

    public void onEventSelect(SelectEvent selectEvent) {

        this.idCorreo = 0;

        titulo = "";
        descripcion = "";

        participantesSeleccionados = new String[1];

        repetirSeleccionado = "";

        cantidadRecordatorios = 0;

        tipoSeleccionado = "";

        pOcultosSeleccionados = new String[1];

        mostrarBtn = true;
        
        CorreoDao correoDao = new CorreoDao();
        correoDao.setEm(Servicio.getEm());

        ScheduleEvent event = (ScheduleEvent) selectEvent.getObject();

        String asunto = event.getTitle();
        Date fecha = event.getStartDate();
        String fechaEvento = new SimpleDateFormat("dd/MM/yyyy").format(fecha);

        for (Correo c : correoDao.getAll()) {
            Date currentMail = c.getFechaEnvio();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentMail);
            cal.add(Calendar.HOUR, 6);
            currentMail = cal.getTime();
            SimpleDateFormat DateCurrent = new SimpleDateFormat("dd/MM/yyyy");
            String CurrentMailStr = DateCurrent.format(currentMail);

            if (c.getAsunto().equals(asunto) && fechaEvento.equals(CurrentMailStr)) {
                this.idCorreo = c.getId();
                this.titulo = c.getAsunto();
                this.descripcion = c.getCuerpo();
                try {
                    Date currentMailDate = new SimpleDateFormat("dd/MM/yyyy").parse(CurrentMailStr);
                    this.fecha = c.getFechaEnvio();
                } catch (ParseException ex) {
                    Logger.getLogger(ScheduleController.class.getName()).log(Level.SEVERE, null, ex);
                }

                int arraySize = c.getDestinatarios().size();
                participantesSeleccionados = new String[arraySize];
                int agregarParticipante = 0;

                for (Contacto dest : c.getDestinatarios()) {

                    participantesSeleccionados[agregarParticipante] = dest.getCorreo();

                    agregarParticipante++;

                }

                int casoIntervalo = c.getIntervalo();

                switch (casoIntervalo) {
                    case 0:
                        this.repetirSeleccionado = "Nunca";
                        break;
                    case 1:
                        this.repetirSeleccionado = "Cada día";
                        break;
                    case 2:
                        this.repetirSeleccionado = "Cada semana";
                        break;
                    case 3:
                        this.repetirSeleccionado = "Cada mes";
                        break;
                    case 4:
                        this.repetirSeleccionado = "Cada año";
                        break;

                }

                this.cantidadRecordatorios = c.getRecordatorios().size();

                if (c.getTipo().equals("html/text")) {
                    this.tipoSeleccionado = "HTML";
                } else {
                    this.tipoSeleccionado = "Texto plano";
                }

                int arraySizePOcultos = c.getUsuarioscopiados().size();
                pOcultosSeleccionados = new String[arraySize];
                int agregarPOculto = 0;

                for (Usuario UsuarioOculto : c.getUsuarioscopiados()) {

                    pOcultosSeleccionados[agregarPOculto] = UsuarioOculto.getCorreo();

                    agregarPOculto++;

                }

            }
        }

        System.out.println(asunto);
        System.out.println(fechaEvento);

    }

    public ScheduleController() {
    }
    
    public void borrar(){
    
        CorreoDao correoDao = new CorreoDao();
        correoDao.setEm(Servicio.getEm());
        
        Correo correo = correoDao.getById(idCorreo);
        correoDao.delete(correo);
        
        eventModel.clear();
        init();
    
    }

//	public void onEventMove(ScheduleEntryMoveEvent event) {
//		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Delta:" + event.getDeltaAsDuration());
//		
//		addMessage(message);
//	}
//	
//	public void onEventResize(ScheduleEntryResizeEvent event) {
//		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Start-Delta:" + event.getDeltaStartAsDuration() + ", End-Delta: " + event.getDeltaEndAsDuration());
//		
//		addMessage(message);
//	}
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public boolean isShowWeekends() {
        return showWeekends;
    }

    public void setShowWeekends(boolean showWeekends) {
        this.showWeekends = showWeekends;
    }

    public boolean isTooltip() {
        return tooltip;
    }

    public void setTooltip(boolean tooltip) {
        this.tooltip = tooltip;
    }

    public boolean isAllDaySlot() {
        return allDaySlot;
    }

    public void setAllDaySlot(boolean allDaySlot) {
        this.allDaySlot = allDaySlot;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getSlotDuration() {
        return slotDuration;
    }

    public void setSlotDuration(String slotDuration) {
        this.slotDuration = slotDuration;
    }

    public String getSlotLabelInterval() {
        return slotLabelInterval;
    }

    public void setSlotLabelInterval(String slotLabelInterval) {
        this.slotLabelInterval = slotLabelInterval;
    }

    public String getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(String scrollTime) {
        this.scrollTime = scrollTime;
    }

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getClientTimeZone() {
        return clientTimeZone;
    }

    public void setClientTimeZone(String clientTimeZone) {
        this.clientTimeZone = clientTimeZone;
    }

    public String getColumnHeaderFormat() {
        return columnHeaderFormat;
    }

    public void setColumnHeaderFormat(String columnHeaderFormat) {
        this.columnHeaderFormat = columnHeaderFormat;
    }

    public ScheduleController(ScheduleModel eventModel, ScheduleModel lazyEventModel, String timeFormat, String slotLabelInterval, Date fecha, List<String> participantes, String[] participantesSeleccionados, Map<String, String> repetirCada, String repetirSeleccionado, int cantidadRecordatorios, Map<String, String> tipo, String tipoSeleccionado, List<String> participantesOcultos, String[] pOcultosSeleccionados) {
        this.eventModel = eventModel;
        this.lazyEventModel = lazyEventModel;
        this.timeFormat = timeFormat;
        this.slotLabelInterval = slotLabelInterval;
        this.fecha = fecha;
        this.participantes = participantes;
        this.participantesSeleccionados = participantesSeleccionados;
        this.repetirCada = repetirCada;
        this.repetirSeleccionado = repetirSeleccionado;
        this.cantidadRecordatorios = cantidadRecordatorios;
        this.tipo = tipo;
        this.tipoSeleccionado = tipoSeleccionado;
        this.participantesOcultos = participantesOcultos;
        this.pOcultosSeleccionados = pOcultosSeleccionados;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<String> participantes) {
        this.participantes = participantes;
    }

    public String[] getParticipantesSeleccionados() {
        return participantesSeleccionados;
    }

    public void setParticipantesSeleccionados(String[] participantesSeleccionados) {
        this.participantesSeleccionados = participantesSeleccionados;
    }

    public Map<String, String> getRepetirCada() {
        return repetirCada;
    }

    public void setRepetirCada(Map<String, String> repetirCada) {
        this.repetirCada = repetirCada;
    }

    public String getRepetirSeleccionado() {
        return repetirSeleccionado;
    }

    public void setRepetirSeleccionado(String repetirSeleccionado) {
        this.repetirSeleccionado = repetirSeleccionado;
    }

    public int getCantidadRecordatorios() {
        return cantidadRecordatorios;
    }

    public void setCantidadRecordatorios(int cantidadRecordatorios) {
        this.cantidadRecordatorios = cantidadRecordatorios;
    }

    public Map<String, String> getTipo() {
        return tipo;
    }

    public void setTipo(Map<String, String> tipo) {
        this.tipo = tipo;
    }

    public String getTipoSeleccionado() {
        return tipoSeleccionado;
    }

    public void setTipoSeleccionado(String tipoSeleccionado) {
        this.tipoSeleccionado = tipoSeleccionado;
    }

    public List<String> getParticipantesOcultos() {
        return participantesOcultos;
    }

    public void setParticipantesOcultos(List<String> participantesOcultos) {
        this.participantesOcultos = participantesOcultos;
    }

    public String[] getpOcultosSeleccionados() {
        return pOcultosSeleccionados;
    }

    public void setpOcultosSeleccionados(String[] pOcultosSeleccionados) {
        this.pOcultosSeleccionados = pOcultosSeleccionados;
    }

    public int getIdCorreo() {
        return idCorreo;
    }

    public void setIdCorreo(int idCorreo) {
        this.idCorreo = idCorreo;
    }

    public boolean isMostrarBtn() {
        return mostrarBtn;
    }

    public void setMostrarBtn(boolean mostrarBtn) {
        this.mostrarBtn = mostrarBtn;
    }



    
    
}