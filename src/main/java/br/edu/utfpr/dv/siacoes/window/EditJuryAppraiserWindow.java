package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;

public class EditJuryAppraiserWindow extends EditWindow {

	private final EditWindow parentWindow;
	
	private final SupervisorComboBox comboProfessor;
	private final OptionGroup optionAppraiserType;
	private final CheckBox checkChair;
	
	public EditJuryAppraiserWindow(EditJuryWindow parentWindow) {
		super("Adicionar Membro", null);
		
		this.parentWindow = parentWindow;
		
		this.comboProfessor = new SupervisorComboBox("Membro", Session.getUser().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		
		this.optionAppraiserType = new OptionGroup();
		this.optionAppraiserType.addItem("Titular");
		this.optionAppraiserType.addItem("Suplente");
		this.optionAppraiserType.select(this.optionAppraiserType.getItemIds().iterator().next());
		
		this.checkChair = new CheckBox("Presidente da Banca");
		
		this.addField(this.comboProfessor);
		this.addField(this.optionAppraiserType);
		this.addField(this.checkChair);
	}
	
	public EditJuryAppraiserWindow(EditJuryRequestWindow parentWindow, boolean substitute) {
		super("Adicionar Membro", null);
		
		this.parentWindow = parentWindow;
		
		this.comboProfessor = new SupervisorComboBox((substitute ? "Suplente" : "Membro"), Session.getUser().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		
		this.optionAppraiserType = new OptionGroup();
		this.optionAppraiserType.addItem("Titular");
		this.optionAppraiserType.addItem("Suplente");
		
		if(substitute) {
			this.optionAppraiserType.select("Suplente");
		} else {
			this.optionAppraiserType.select("Titular");	
		}
		
		this.checkChair = new CheckBox("Presidente da Banca");
		this.checkChair.setValue(false);
		
		this.addField(this.comboProfessor);
	}
	
	@Override
	public void save() {
		try{
			if((this.comboProfessor.getProfessor() == null) || (this.comboProfessor.getProfessor().getIdUser() == 0)){
				throw new Exception("Selecione o membro.");
			}
			
			JuryAppraiser appraiser = new JuryAppraiser();
			
			appraiser.setAppraiser(this.comboProfessor.getProfessor());
			appraiser.setChair(this.checkChair.getValue());
			if(this.optionAppraiserType.getValue() != this.optionAppraiserType.getItemIds().iterator().next()) {
				appraiser.setSubstitute(true);
			}
			
			if(this.parentWindow instanceof EditJuryWindow) {
				((EditJuryWindow)this.parentWindow).addAppraiser(appraiser);
			} else if(this.parentWindow instanceof EditJuryRequestWindow) {
				JuryAppraiserRequest request = new JuryAppraiserRequest();
				
				request.setAppraiser(appraiser.getAppraiser());
				request.setChair(appraiser.isChair());
				request.setSubstitute(appraiser.isSubstitute());
				
				((EditJuryRequestWindow)this.parentWindow).addAppraiser(request);
			}
			
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Adicionar Membro", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
