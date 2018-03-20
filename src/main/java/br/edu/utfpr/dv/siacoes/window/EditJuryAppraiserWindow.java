package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;

public class EditJuryAppraiserWindow extends EditWindow {

	private final EditJuryWindow parentWindow;
	
	private final SupervisorComboBox comboProfessor;
	
	public EditJuryAppraiserWindow(EditJuryWindow parentWindow) {
		super("Adicionar Membro", null);
		
		this.parentWindow = parentWindow;
		
		this.comboProfessor = new SupervisorComboBox("Membro", Session.getUser().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		
		this.addField(this.comboProfessor);
	}
	
	@Override
	public void save() {
		try{
			if((this.comboProfessor.getProfessor() == null) || (this.comboProfessor.getProfessor().getIdUser() == 0)){
				throw new Exception("Selecione o membro.");
			}
			
			this.parentWindow.addAppraiser(this.comboProfessor.getProfessor());
			
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Adicionar Membro", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
