package br.edu.utfpr.dv.siacoes.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.window.EditEmailConfigWindow;
import br.edu.utfpr.dv.siacoes.window.EditUserWindow;
import br.edu.utfpr.dv.siacoes.window.SupervisorAreaWindow;

public class SupervisorView extends ListView {
	
	public static final String NAME = "supervisor";
	
	public final Button buttonPrint;
	public final Button buttonLattes;
	
	public SupervisorView() {
		super(SystemModule.SIGET);
		
		this.setCaption("Orientadores");
		
		this.setFiltersVisible(false);
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		this.setEditCaption("Áreas");
		
		this.buttonPrint = new Button("Imprimir");
		this.buttonPrint.setWidth("150px");
		this.addActionButton(this.buttonPrint);
		
		this.buttonLattes = new Button("Currículo", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	openLattes();
            }
        });
		this.buttonLattes.setWidth("150px");
		this.addActionButton(this.buttonLattes);
		
		try {
			UserBO bo = new UserBO();
	    	List<User> list = bo.listAllProfessors(true);
	    	
			new ReportUtils().prepareForPdfReport("Supervisors", "Orientadores", list, this.buttonPrint);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Impressão", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("E-mail", String.class);
		this.getGrid().addColumn("Instituição", String.class);
		this.getGrid().addColumn("Áreas de Pesquisa", String.class);
		
		try {
			UserBO bo = new UserBO();
	    	List<User> list = bo.listAllProfessors(true);
	    	
	    	for(User u : list){
				Object itemId = this.getGrid().addRow(u.getName(), u.getEmail(), u.getInstitution(), u.getResearch());
				this.addRowId(itemId, u.getIdUser());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Orientadores", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void openLattes(){
		Object id = this.getIdSelected();
		
		if(id != null){
			try {
				UserBO bo = new UserBO();
				User user = bo.findById((int)id);
				
				if(user.getLattes().isEmpty()){
					Notification.show("Abrir Currículo", "O professor selecionado não cadastrou o currículo.", Notification.Type.WARNING_MESSAGE);
				}else{
					getUI().getPage().open(user.getLattes(), "_blank");
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show("Abrir Currículo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}else{
			Notification.show("Abrir Currículo", "Selecione o professor para abrir o currículo.", Notification.Type.WARNING_MESSAGE);
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			UserBO bo = new UserBO();
			User user = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new SupervisorAreaWindow(user));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Áreas de Pesquisa", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
