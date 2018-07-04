package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;

public class EditInternshipJuryAppraiserWindow extends EditWindow {
	
	private final EditInternshipJuryWindow parentWindow;
	
	private final SupervisorComboBox comboProfessor;
	private final OptionGroup optionAppraiserType;
	private final CheckBox checkChair;
	private final boolean edit;
	
	public EditInternshipJuryAppraiserWindow(EditInternshipJuryWindow parentWindow){
		super("Adicionar Membro", null);
		
		this.parentWindow = parentWindow;
		this.edit = false;
		
		this.comboProfessor = new SupervisorComboBox("Membro", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		
		this.optionAppraiserType = new OptionGroup();
		this.optionAppraiserType.addItem("Titular");
		this.optionAppraiserType.addItem("Suplente");
		this.optionAppraiserType.select(this.optionAppraiserType.getItemIds().iterator().next());
		
		this.checkChair = new CheckBox("Presidente da Banca");
		
		this.addField(this.comboProfessor);
		this.addField(this.optionAppraiserType);
		this.addField(this.checkChair);
	}
	
	public EditInternshipJuryAppraiserWindow(InternshipJuryAppraiser appraiser, EditInternshipJuryWindow parentWindow) {
		super("Editar Membro", null);
		
		this.parentWindow = parentWindow;
		this.edit = true;
		
		this.comboProfessor = new SupervisorComboBox("Membro", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboProfessor.setProfessor(appraiser.getAppraiser());
		this.comboProfessor.setEnabled(false);
		
		this.optionAppraiserType = new OptionGroup();
		this.optionAppraiserType.addItem("Titular");
		this.optionAppraiserType.addItem("Suplente");
		this.optionAppraiserType.select(appraiser.isSubstitute() ? "Suplente" : "Titular");
		
		this.checkChair = new CheckBox("Presidente da Banca");
		this.checkChair.setValue(appraiser.isChair());
		
		this.addField(this.comboProfessor);
		this.addField(this.optionAppraiserType);
		this.addField(this.checkChair);
	}
	
	@Override
	public void save() {
		try{
			if((this.comboProfessor.getProfessor() == null) || (this.comboProfessor.getProfessor().getIdUser() == 0)){
				throw new Exception("Selecione o membro.");
			}
			
			InternshipJuryAppraiser appraiser = new InternshipJuryAppraiser();
			
			appraiser.setAppraiser(this.comboProfessor.getProfessor());
			appraiser.setChair(this.checkChair.getValue());
			if(this.optionAppraiserType.getValue() != this.optionAppraiserType.getItemIds().iterator().next()) {
				appraiser.setSubstitute(true);
			}
			
			if(this.edit) {
				this.parentWindow.editAppraiser(appraiser);
			} else {
				this.parentWindow.addAppraiser(appraiser);
			}
			
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Adicionar Membro", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
