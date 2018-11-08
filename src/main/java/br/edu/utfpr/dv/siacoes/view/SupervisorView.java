package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
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
	    	
			new ReportUtils().prepareForPdfReport("Supervisors", "Orientadores", list, Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.buttonPrint);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Impressão", e.getMessage());
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
			
			this.showErrorNotification("Listar Orientadores", e.getMessage());
		}
	}
	
	private void openLattes(){
		Object id = this.getIdSelected();
		
		if(id != null){
			try {
				UserBO bo = new UserBO();
				User user = bo.findById((int)id);
				
				if(user.getLattes().isEmpty()){
					this.showWarningNotification("Abrir Currículo", "O professor selecionado não cadastrou o currículo.");
				}else{
					getUI().getPage().open(user.getLattes(), "_blank");
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Abrir Currículo", e.getMessage());
			}
		}else{
			this.showWarningNotification("Abrir Currículo", "Selecione o professor para abrir o currículo.");
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
			
			this.showErrorNotification("Áreas de Pesquisa", e.getMessage());
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
