package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
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
		this.comboProposal.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(comboProposal.getValue() != null){
					selectStage();
				}
			}
		});
		
		this.comboStage = new StageComboBox();
		this.comboStage.setShowBoth(true);
		
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
		
		this.buttonPrint = new Button("Imprimir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	printReport();
            }
        });
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
		
		try {			
			AttendanceBO bo = new AttendanceBO();
	    	List<Attendance> list = new ArrayList<Attendance>();
	    	
	    	if((this.comboStudent.getStudent() != null) && (this.comboProposal.getValue() != null) && (this.comboSupervisor.getValue() != null)) {
	    		list = bo.listByStudent(this.comboStudent.getStudent().getIdUser(), ((User)this.comboSupervisor.getValue()).getIdUser(), ((Proposal)this.comboProposal.getValue()).getIdProposal(), (this.comboStage.isBothSelected() ? 0 : this.comboStage.getStage()));
	    	}
	    	
	    	for(Attendance a : list){
				Object itemId = this.getGrid().addRow(a.getDate(), a.getStartTime(), a.getEndTime(), (a.getComments().length() > 100 ? a.getComments().substring(0, 99) + "..." : a.getComments()));
				this.addRowId(itemId, a.getIdAttendance());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acompanhamentos", e.getMessage());
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
			
			selectStage();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acompanhamentos", e.getMessage());
		}
	}
	
	private void loadSupervisors(){
		try {
			this.comboSupervisor.clear();
			
			if(this.comboProposal.getValue() != null){
				List<User> list = new ProposalBO().listSupervisors(((Proposal)this.comboProposal.getValue()).getIdProposal(), true);
				
				this.comboSupervisor.addItems(list);
				if(list.size() > 0){
					this.comboSupervisor.select(list.get(0));	
				}
				
				User supervisor = new SupervisorChangeBO().findCurrentSupervisor(((Proposal)this.comboProposal.getValue()).getIdProposal());
				for(User u : list) {
					if(u.getIdUser() == supervisor.getIdUser()) {
						this.comboSupervisor.select(u);
						break;
					}
				}
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acompanhamentos", e.getMessage());
		}
	}
	
	private void selectStage() {
		try {
			if(this.comboProposal.getValue() != null) {
				int stage = new ProposalBO().getProposalStage(((Proposal)this.comboProposal.getValue()).getIdProposal());
				
				this.comboStage.setStage(stage);
			} else {
				this.comboStage.setStage(1);
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acompanhamentos", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		if(this.comboStudent.getStudent() == null){
			this.showErrorNotification("Incluir Reunião", "Selecione o acadêmico.");
		}else if(this.comboProposal.getValue() == null){
			this.showErrorNotification("Incluir Reunião", "Selecione o projeto.");
		}else if(this.comboSupervisor.getValue() == null){
			this.showErrorNotification("Incluir Reunião", "Selecione o orientador.");
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
			
			this.showErrorNotification("Editar Reunião", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		try {
			AttendanceBO bo = new AttendanceBO();
			
			bo.delete(Session.getIdUserLog(), (int)id);
			
			this.refreshGrid();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Reunião", e.getMessage());
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
	
	private void printReport() {
		if((this.comboStudent.getStudent() != null) && (this.comboProposal.getValue() != null)){
			try {
				this.showReport(new AttendanceBO().getReport(this.comboStudent.getStudent().getIdUser(), ((Proposal)this.comboProposal.getValue()).getIdProposal(), ((User)this.comboSupervisor.getValue()).getIdUser(), this.comboStage.getStage()));
			} catch (Exception e) {
				showErrorNotification("Imprimir Acompanhamentos", e.getMessage());
			}
		}else{
        	if(Session.isUserStudent()) {
        		showWarningNotification("Imprimir Acompanhamentos", "É necessário selecionar o orientador e a proposta para imprimir os acompanhamentos.");
        	} else {
        		showWarningNotification("Imprimir Acompanhamentos", "É necessário selecionar o acadêmico e a proposta para imprimir os acompanhamentos.");
        	}
		}
	}

}
