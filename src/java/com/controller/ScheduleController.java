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
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.primefaces.PrimeFaces;
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

    /* Nuevo de Recordatorio*/
    private Map<String, String> opcionesRecordatorio;
    private String recordatorioSelec;
    private int meses;
    private int veces;
    private int frecunecia;

    private List<Recordatorio> recordatorios;
    List<Date> fechas = new ArrayList<>();
    private String errorText;
    
    
    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        this.recordatorioSelec = "";
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

        opcionesRecordatorio = new HashMap<String, String>();
        opcionesRecordatorio.put("Por Mes", "Por Mes");
        opcionesRecordatorio.put("Veces por Mes", "Veces por Mes");
        opcionesRecordatorio.put("Veces por Mes con frecuencia", "Veces por Mes con frecuencia");
        opcionesRecordatorio.put("Ninguno", "Ninguno");

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

//        int cantidadNueva = this.cantidadRecordatorios;
//        List<Recordatorio> recordatorios = rDao.getByMail(c);
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

        ///actualizar 
        if (this.idCorreo > 0) {

            Correo correo = correoDao.getById(idCorreo);

            if (rDao.getByMail(correo).size() > 0) {

                if (this.recordatorioSelec == "Por Mes") {
                    if(this.checkDatosxMeses(correo, meses)){
                    List<Recordatorio> recordatorios = rDao.getByMail(correo);

                    meses = this.getMeses();

                    for (Recordatorio rec : recordatorios) {
                        rDao.delete(rec);
                    }
                    correo.setMeses(meses);
                    correo.setVeces(0);
                    correo.setFrecuencia(0);
                    function.crearRecxMes(correo, this.getMeses());
                    }else{
                       PrimeFaces.current().ajax().update("errorText");
                    }
                    
                } else if (this.recordatorioSelec == "Veces por Mes") {
                    List<Recordatorio> recordatorios = rDao.getByMail(correo);
                    meses = this.getMeses();
                    veces = this.getVeces();

                    for (Recordatorio rec : recordatorios) {
                        rDao.delete(rec);
                    }
                    correo.setMeses(meses);
                    correo.setVeces(veces);
                    correo.setFrecuencia(0);
                    function.crearRecxVez(correo, this.getVeces(), this.getMeses());

                } else if (this.recordatorioSelec == "Veces por Mes con frecuencia") {

                    meses = this.getMeses();
                    veces = this.getVeces();
                    this.frecunecia = this.getFrecunecia();

                    List<Recordatorio> recordatorios = rDao.getByMail(correo);

                    for (Recordatorio rec : recordatorios) {
                        rDao.delete(rec);
                    }
                    correo.setMeses(meses);
                    correo.setVeces(veces);
                    correo.setFrecuencia(frecunecia);
                    function.crearRecxFrecuencia(correo, this.getVeces(), this.getMeses(), this.frecunecia);

                } else if (this.recordatorioSelec == "Ninguno" || this.recordatorioSelec == "" || this.recordatorioSelec == null) {

                    List<Recordatorio> recordatorios = rDao.getByMail(correo);
                    for (Recordatorio rec : recordatorios) {
                        rDao.delete(rec);
                    }

                    correo.setVeces(0);
                    correo.setMeses(0);
                    correo.setFrecuencia(0);

                    function.saveCorreo(correo);
                }
            } else {
                if (this.recordatorioSelec == "Por Mes") {

                    meses = this.getMeses();
                    correo.setMeses(meses);
                    correo.setVeces(0);
                    correo.setFrecuencia(0);
                    function.crearRecxMes(correo, this.getMeses());

                } else if (this.recordatorioSelec == "Veces por Mes") {

                    meses = this.getMeses();
                    veces = this.getVeces();
                    correo.setMeses(meses);
                    correo.setVeces(veces);
                    correo.setFrecuencia(0);
                    function.crearRecxVez(correo, this.getVeces(), this.getMeses());

                } else if (this.recordatorioSelec == "Veces por Mes con frecuencia") {

                    meses = this.getMeses();
                    veces = this.getVeces();
                    this.frecunecia = this.getFrecunecia();
                    correo.setMeses(meses);
                    correo.setVeces(veces);
                    correo.setFrecuencia(frecunecia);
                    function.crearRecxFrecuencia(correo, this.getVeces(), this.getMeses(), this.frecunecia);

                } else if (this.recordatorioSelec == "Ninguno") {
                    correo.setVeces(0);
                    correo.setMeses(0);
                    correo.setFrecuencia(0);
                    function.saveCorreo(correo);
                }
            }

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

        } else if (this.recordatorioSelec != "Ninguno" || this.recordatorioSelec != "" || this.recordatorioSelec != null) {

            if (this.recordatorioSelec == "Por Mes") {

                meses = this.getMeses();
                c.setMeses(meses);
                c.setVeces(0);
                c.setFrecuencia(0);
                function.crearRecxMes(c, this.getMeses());

            } else if (this.recordatorioSelec == "Veces por Mes") {

                meses = this.getMeses();
                veces = this.getVeces();

                c.setMeses(meses);
                c.setVeces(veces);
                c.setFrecuencia(0);
                function.crearRecxVez(c, this.getVeces(), this.getMeses());

            } else if (this.recordatorioSelec == "Veces por Mes con frecuencia") {

                meses = this.getMeses();
                veces = this.getVeces();
                this.frecunecia = this.getFrecunecia();

                c.setMeses(meses);
                c.setVeces(veces);
                c.setFrecuencia(frecunecia);
                function.crearRecxFrecuencia(c, this.getVeces(), this.getMeses(), this.frecunecia);

            } else if (this.recordatorioSelec == "Ninguno" || this.recordatorioSelec == "" || this.recordatorioSelec == null) {
                c.setMeses(0);
                c.setVeces(0);
                c.setFrecuencia(0);
                correoDao.save(c);
            }

        } else {
            c.setMeses(0);
            c.setVeces(0);
            c.setFrecuencia(0);
            correoDao.save(c);
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
        this.setRecordatorioSelec("");
        this.setMeses(0);
        this.setVeces(0);
        this.setFrecunecia(0);

        Date d = (Date) selectEvent.getObject();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.HOUR, 6);
        d = cal.getTime();

        fecha = d;
        event = new DefaultScheduleEvent("", d, d);

    }

    public void onEventSelect(SelectEvent selectEvent) {
        RecordatorioDao rDao = new RecordatorioDao();
        rDao.setEm(Servicio.getEm());
        this.idCorreo = 0;

        titulo = "";
        descripcion = "";

        participantesSeleccionados = new String[1];

        repetirSeleccionado = "";

        cantidadRecordatorios = 0;

        tipoSeleccionado = "";

        pOcultosSeleccionados = new String[1];

        mostrarBtn = true;
        this.setRecordatorioSelec("");
        this.setMeses(0);
        this.setVeces(0);
        this.setFrecunecia(0);
        this.setMesesbol(true);
        this.setVecesbol(true);
        this.setFrecuenciabol(true);

        Correo target = new Correo();
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
                target = c;

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

        recordatorios = rDao.getByMail(target);

        if (recordatorios.size() > 0) {
            this.meses = target.getMeses();
            this.veces = target.getVeces();
            this.frecunecia = target.getFrecuencia();

        } else {
            this.setVeces(0);
            this.setMeses(0);
            this.setFrecunecia(0);
        }

        System.out.println(asunto);
        System.out.println(fechaEvento);

    }

    public ScheduleController() {
    }

    public void borrar() {

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

    public Map<String, String> getOpcionesRecordatorio() {
        return opcionesRecordatorio;
    }

    public void setOpcionesRecordatorio(Map<String, String> opcionesRecordatorio) {
        this.opcionesRecordatorio = opcionesRecordatorio;
    }

    public String getRecordatorioSelec() {
        return recordatorioSelec;
    }

    public void setRecordatorioSelec(String recordatorioSelec) {
        this.recordatorioSelec = recordatorioSelec;
    }

    public int getMeses() {
        return meses;
    }

    public void setMeses(int meses) {
        this.meses = meses;
    }

    public int getVeces() {
        return veces;
    }

    public void setVeces(int veces) {
        this.veces = veces;
    }

    public int getFrecunecia() {
        return frecunecia;
    }

    public void setFrecunecia(int frecunecia) {
        this.frecunecia = frecunecia;
    }

    public String getValueofPick() {
        return valueofPick;
    }

    public void setValueofPick(String valueofPick) {
        this.valueofPick = valueofPick;
    }

    public boolean isMesesbol() {
        return mesesbol;
    }

    public void setMesesbol(boolean mesesbol) {
        this.mesesbol = mesesbol;
    }

    public boolean isVecesbol() {
        return vecesbol;
    }

    public void setVecesbol(boolean vecesbol) {
        this.vecesbol = vecesbol;
    }

    public boolean isFrecuenciabol() {
        return frecuenciabol;
    }

    public void setFrecuenciabol(boolean frecuenciabol) {
        this.frecuenciabol = frecuenciabol;
    }

    /*recordatorios selec*/
    private String valueofPick;

    private boolean mesesbol = true;
    private boolean vecesbol = true;
    private boolean frecuenciabol = true;

    public void visibility() {
        System.out.println("Entro con Item: " + this.recordatorioSelec);
        if (this.recordatorioSelec == "Por Mes") {
            this.setMesesbol(false);
            this.setVecesbol(true);
            this.setFrecuenciabol(true);

        } else if (this.recordatorioSelec == "Veces por Mes") {
            this.setMesesbol(false);
            this.setVecesbol(false);
            this.setFrecuenciabol(true);
        } else if (this.recordatorioSelec == "Veces por Mes con frecuencia") {
            this.setMesesbol(false);
            this.setVecesbol(false);
            this.setFrecuenciabol(false);
        } else if (this.recordatorioSelec == "Ninguno") {
            this.setMesesbol(true);
            this.setVecesbol(true);
            this.setFrecuenciabol(true);
        }

        PrimeFaces.current().ajax().update("meses");
        PrimeFaces.current().ajax().update("veces");
        PrimeFaces.current().ajax().update("frecuencia");
    }

    public List<Recordatorio> getRecordatorios() {
        return recordatorios;
    }

    public void setRecordatorios(List<Recordatorio> recordatorios) {
        this.recordatorios = recordatorios;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    
    
    
    
    //<editor-fold defaultstate="collapsed" desc="Validaciones">
    Date today = new Date();

    //<editor-fold defaultstate="collapsed" desc="Meses">
    public boolean checkDatosxMeses(Correo correo, int meses) {
        boolean allOk = false;
        int mesesRestantes = (int) ChronoUnit.MONTHS.between(convertToLocal(today), convertToLocal(correo.getFechaEnvio()));
        System.out.println(" ! Meses entre dia actual y la fecha meta " + mesesRestantes);

        if (meses <= mesesRestantes && meses > 0) {
            allOk = true;
        } else {
            this.setErrorText(" Error: Datos proporcionados son erroneos. \n"
                    + "* La cantidad de meses proporcionada sobrepasa los meses actuales\n");
        }

        return allOk;
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Veces x Mes">
    public boolean checkDatosVecesxMeses(Correo correo, int veces, int meses) {

        boolean allOK = false;

        /*Anno en que se envia el correo meta*/
        int yearSend = getYear(correo.getFechaEnvio());

        /*Anno actual*/
        int yearActual = getYear(today);

        System.out.println(" ! Anno meta: " + yearSend + " Anno Actual: " + yearActual);

        /*Dias entre dia actual y la fecha meta*/
        int diasRestantes = (int) daysBetween(convertToLocal(today), convertToLocal(correo.getFechaEnvio()));
        System.out.println(" ! Dias entre dia actual y la fecha meta " + diasRestantes);

        /* Meses entre el dia actual y la fecha meta*/
        int mesesRestantes = (int) monthsBetween(convertToLocal(today), convertToLocal(correo.getFechaEnvio()));
        System.out.println(" ! Meses entre dia actual y la fecha meta " + mesesRestantes);

        /* Si el anno meta es mayor o igual que el anno actual*/
 /* Si la cantidad de veces propuestas es mayor a cero y si la cantidad de meses es mayor a 0*/
        if (yearActual <= yearSend && veces > 0 && meses > 0) {

            /* Si la cantidad de meses es valida*/
            if (meses <= mesesRestantes) {

                /* Si la cantidad de veces que se puede avisar es factble (Por dias restantes)*/
                if (this.checkMonthAv(today, correo.getFechaEnvio(), veces, meses)) {

                    /* Si las veces no exceden el limite de dias estandar por mes*/
                    if (veces <= 31) {
                        System.out.println(" All set! ( Veces x Mes )");
                        allOK = true;

                    } else {
                        System.out.println(" Error: Datos proporcionados son erroneos. \n"
                                + "* La cantidad de veces proporcionada sobrepasa los dias en un mes\n");
                    }

                } else {
                    System.out.println(" Error: Datos proporcionados son erroneos. \n"
                            + "* La cantidad de veces proporcionada sobrepasa los dias actuales\n");
                }

            } else {
                System.out.println(" Error: Datos proporcionados son erroneos. \n"
                        + "* La cantidad de meses proporcionada sobrepasa los meses actuales\n");
            }

        } else {
            System.out.println(" Error: Datos proporcionados son erroneos. +\n"
                    + "* El anno porporcionado es menor al actual \n"
                    + "* La cantidad de veces es igual o menor a 0 \n"
                    + "* La cantidad de meses es menor a 0 \n");
        }

        return allOK;

    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Veces x Mes con Frecuencia">
    public boolean checkDatosMesesXFrecuencia(Correo correo, int veces, int meses, int frecuencia) {

        boolean allOk = true;
        Calendar cal = new GregorianCalendar();
        cal.setTime(correo.getFechaEnvio());
        List<Date> fechas = new ArrayList<>();
        int maxDay = 0;
        int capxMes = 0;

        for (int i = 1; i <= meses; i++) {
            Date fecha = this.genFechasxMes(cal);
            fechas.add(fecha);
        }
        Collections.sort(fechas);

        for (Date fecha : fechas) {
            Calendar calendario = new GregorianCalendar();
            calendario.setTime(fecha);
            maxDay = calendario.getActualMaximum(Calendar.DAY_OF_MONTH);
            capxMes = Math.round(maxDay / veces);

            if (capxMes < frecuencia) {
                allOk = false;
                System.out.println(" Error: Frecuencia digitada no se soporta en el mes " + calendario.getTime().getMonth() + " del año " + calendario.getTime().getYear());
            }

        }

        if (allOk) {
            System.out.println(" All set! (Meses x Frecuencia)");
        }
        return allOk;
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Funciones y utilidades">
    /*   -convertToLocal-   
	 * Convierte los datos tipo Date a Local date 
	 * para calcular los dias restantes   */
    public LocalDate convertToLocal(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /*   -daysBetween-   
	 * Calcula los dias restantes entre dos local dates*/
    public long daysBetween(LocalDate d1, LocalDate d2) {
        long restantes = ChronoUnit.DAYS.between(d1, d2);
        return restantes;
    }

    /*  -monthsBetween- 
	 * Calcula los meses entre las dos fechas tipo LocalDate*/
    public int monthsBetween(LocalDate d1, LocalDate d2) {
        int restantes = 0;
        
        if (d1.getYear() <= d2.getYear()) {
            restantes = d1.getMonthValue() - d2.getMonthValue();
        } else {
            restantes = d2.getMonthValue() - d1.getMonthValue();
        }

        return restantes;
    }

    /* - getYear - 
	 * Devuelve el anno en base a la fecha*/
    public int getYear(Date fecha) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(fecha);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    /*   -- genCalendar-- */
 /*Pasa un objeto tipo date a calendario */
    public Calendar genCalendar(Date fecha) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(fecha);
        return cal;
    }

    /* genDate */
 /* Genera un valor tipo date con los valores definidos de YYYY/MM/DD*/
    public Date dateGen(int y, int m, int d) {
        Date fecha = new Date();
        fecha.setYear(y);
        fecha.setMonth(m);
        fecha.setDate(d);
        return fecha;
    }

    /* -- calendarToDate -- 
     Pasa calores calendar a Date*/
    public Date calendarToDate(Calendar cal) {
        return cal.getTime();
    }

    public Calendar dateToCalendar(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
    }

    public boolean isLate(Date today, Date due) {
        boolean late = false;
        if (due.before(today)) {
            late = true;
        }
        return late;
    }

    /* -- compareDates-- */
 /* Compara dos fechas si son iguales*/
    public boolean compareDates(Date cal1, Date cal2) {
        boolean chk = false;

        if (cal1.equals(cal2)) {
            chk = true;
        }
        return chk;
    }

    public boolean checkMonthAv(Date fechaActual, Date fechaMeta, int veces, int meses) {
        boolean allOk = true;
        YearMonth yearMonthObject = null;
        int daysInMonth = 0;

        yearMonthObject = YearMonth.of(fechaActual.getYear(), fechaActual.getMonth());
        daysInMonth = yearMonthObject.lengthOfMonth();

        /* Si las veces caben en el primer mes se ejecuta el resto de checks*/
        if (veces <= daysInMonth) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(fechaActual);

            for (int i = 1; i < meses; i++) {
                cal.add(Calendar.MONTH, cal.getTime().getMonth() + i);
                yearMonthObject = YearMonth.of(cal.getTime().getYear(), cal.getTime().getMonth());
                daysInMonth = yearMonthObject.lengthOfMonth();

                if (veces > daysInMonth) {
                    allOk = false;
                }
            }

        } else {
            allOk = false;
        }

        return allOk;
    }

    public Date genFechasxMes(Calendar calendario) {
        int orgMonth = calendario.getTime().getMonth();

        calendario.set(Calendar.MONTH, orgMonth - 1);
        Date fecha = calendario.getTime();

        return fecha;
    }
    //</editor-fold>
    
    
    //</editor-fold>
}
