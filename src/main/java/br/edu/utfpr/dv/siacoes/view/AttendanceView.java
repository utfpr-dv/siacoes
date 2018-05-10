package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.EventRouter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.window.EditAttendanceWindow;

public class AttendanceView extends ListView {
	
	public static final String NAME = "attendance";
	
	private final StudentComboBox comboStudent;
	private final NativeSelect comboProposal;
	private final NativeSelect comboSupervisor;
	private final StageComboBox comboStage;
	private final Button buttonPrint;
	
	public AttendanceView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Registro de Reuniões");
		
		this.comboSupervisor = new NativeSelect("Orientador");
		this.comboSupervisor.setWidth("400px");
		this.comboSupervisor.setNullSelectionAllowed(false);
		
		this.comboProposal = new NativeSelect("Proposta");
		this.comboProposal.setWidth("400px");
		this.comboProposal.setNullSelectionAllowed(false);
		
		if(Session.isUserProfessor()){
			this.comboStudent = new StudentComboBox("Acadêmico", Session.getUser().getIdUser());
			this.comboStudent.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(comboStudent.getStudent() != null){
						loadProposals();
					}
				}
			});
			
			this.comboSupervisor.addItem(Session.getUser());
			this.comboSupervisor.select(Session.getUser());
			this.comboSupervisor.setVisible(false);
		}else{
			this.comboStudent = new StudentComboBox("Acadêmico");
			this.comboStudent.addItem(Session.getUser());
			this.comboStudent.setStudent(Session.getUser());
			this.loadProposals();
			this.loadSupervisors();
			
			this.comboStudent.setVisible(false);
		}
		
		this.comboStage = new StageComboBox();
		
		this.addFilterField(this.comboStudent);
		this.addFilterField(this.comboProposal);
		this.addFilterField(this.comboSupervisor);
		this.addFilterField(this.comboStage);
		
		this.comboProposal.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(comboSupervisor.isVisible() && (comboProposal.getValue() != null)) {
					loadSupervisors();
				}
			}
		});
		
		this.buttonPrint = new Button("Imprimir");
		this.buttonPrint.setWidth("150px");
		this.buttonPrint.setIcon(FontAwesome.PRINT);
		
		this.addActionButton(this.buttonPrint);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Data", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Horário Inicial", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("HH:mm")));
		this.getGrid().addColumn("Horário Final", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("HH:mm")));
		this.getGrid().addColumn("Observaçoes", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(125);
		this.getGrid().getColumns().get(1).setWidth(125);
		this.getGrid().getColumns().get(2).setWidth(125);
		
		this.prepareDownload();
		
		try {			
			AttendanceBO bo = new AttendanceBO();
	    	List<Attendance> list = new ArrayList<Attendance>();
	    	
	    	if((this.comboStudent.getStudent() != null) && (this.comboProposal.getValue() != null) && (this.comboSupervisor.getValue() != null)) {
	    		list = bo.listByStudent(this.comboStudent.getStudent().getIdUser(), ((User)this.comboSupervisor.getValue()).getIdUser(), ((Proposal)this.comboProposal.getValue()).getIdProposal(), this.comboStage.getStage());
	    	}
	    	
	    	for(Attendance a : list){
				Object itemId = this.getGrid().addRow(a.getDate(), a.getStartTime(), a.getEndTime(), (a.getComments().length() > 100 ? a.getComments().substring(0, 99) + "..." : a.getComments()));
				this.addRowId(itemId, a.getIdAttendance());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Acompanhamentos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void loadProposals(){
		try {
			ProposalBO bo = new ProposalBO();
			List<Proposal> list = bo.listByStudent(this.comboStudent.getStudent().getIdUser());
			
			this.comboProposal.clear();
			this.comboProposal.addItems(list);
			if(list.size() > 0){
				this.comboProposal.select(list.get(0));	
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Acompanhamentos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void loadSupervisors(){
		try {
			this.comboSupervisor.clear();
			
			if(this.comboProposal.getValue() != null){
				ProposalBO bo = new ProposalBO();
				List<User> list = bo.listSupervisors(((Proposal)this.comboProposal.getValue()).getIdProposal());
				
				this.comboSupervisor.addItems(list);
				if(list.size() > 0){
					this.comboSupervisor.select(list.get(0));	
				}
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Acompanhamentos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void addClick() {
		if(this.comboStudent.getStudent() == null){
			Notification.show("Incluir Reunião", "Selecione o acadêmico.", Notification.Type.ERROR_MESSAGE);
		}else if(this.comboProposal.getValue() == null){
			Notification.show("Incluir Reunião", "Selecione o projeto.", Notification.Type.ERROR_MESSAGE);
		}else if(this.comboSupervisor.getValue() == null){
			Notification.show("Incluir Reunião", "Selecione o orientador.", Notification.Type.ERROR_MESSAGE);
		}else{
			Attendance attendance = new Attendance();
	    	
	    	attendance.setProposal((Proposal)this.comboProposal.getValue());
	    	attendance.setStudent(((Proposal)this.comboProposal.getValue()).getStudent());
	    	attendance.setSupervisor((User)this.comboSupervisor.getValue());
	    	attendance.setStage(this.comboStage.getStage());
	    	
	    	UI.getCurrent().addWindow(new EditAttendanceWindow(attendance, this));	
		}
	}

	@Override
	public void editClick(Object id) {
		try {
			AttendanceBO bo = new AttendanceBO();
			Attendance attendance = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditAttendanceWindow(attendance, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Editar Reunião", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void deleteClick(Object id) {
		try {
			AttendanceBO bo = new AttendanceBO();
			
			bo.delete((int)id);
			
			this.refreshGrid();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Excluir Reunião", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void filterClick() throws Exception {
		if(this.comboStudent.getStudent() == null){
			throw new Exception("Selecione o acadêmico.");
		}
		
		if(this.comboProposal.getValue() == null){
			throw new Exception("Selecione a proposta.");
		}
	}

	private void prepareDownload(){
		new EventRouter().removeListener(Button.ClickListener.class, this.buttonPrint);
		
		if((this.comboStudent.getStudent() != null) && (this.comboProposal.getValue() != null)){
			try {
				AttendanceReport attendance = new AttendanceBO().getReport(this.comboStudent.getStudent().getIdUser(), ((Proposal)this.comboProposal.getValue()).getIdProposal(), ((User)this.comboSupervisor.getValue()).getIdUser(), this.comboStage.getStage());
				
				Proposal proposal = new ProposalBO().findById(((Proposal)this.comboProposal.getValue()).getIdProposal());
				
				List<AttendanceReport> list = new ArrayList<AttendanceReport>();
				list.add(attendance);
				
				new ReportUtils().prepareForPdfReport("Attendances", "Acompanhamento", list, proposal.getDepartment().getIdDepartment(), this.buttonPrint);
			} catch (Exception e) {
				this.buttonPrint.addClickListener(new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Imprimir Acompanhamentos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        });
			}
		}else{
			this.buttonPrint.addClickListener(new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	if(Session.isUserStudent()) {
	            		Notification.show("Imprimir Acompanhamentos", "É necessário selecionar o orientador e a proposta para imprimir os acompanhamentos.", Notification.Type.WARNING_MESSAGE);
	            	} else {
	            		Notification.show("Imprimir Acompanhamentos", "É necessário selecionar o acadêmico e a proposta para imprimir os acompanhamentos.", Notification.Type.WARNING_MESSAGE);	
	            	}
	            }
	        });
		}
	}

}
