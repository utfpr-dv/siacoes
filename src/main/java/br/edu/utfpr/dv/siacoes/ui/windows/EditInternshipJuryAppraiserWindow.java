package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.ui.components.SupervisorComboBox;

public class EditInternshipJuryAppraiserWindow extends EditWindow {
	
	private final EditWindow parentWindow;
	
	private final SupervisorComboBox comboProfessor;
	private final RadioButtonGroup<String> optionAppraiserType;
	private final Checkbox checkChair;
	private final boolean edit;
	
	private static final String MEMBER = "Titular";
	private static final String SUBSTITUTE = "Suplente";
	
	public EditInternshipJuryAppraiserWindow(EditInternshipJuryWindow parentWindow){
		super("Adicionar Membro", null);
		
		this.parentWindow = parentWindow;
		this.edit = false;
		
		this.comboProfessor = new SupervisorComboBox("Membro", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboProfessor.setRequired(true);
		
		this.optionAppraiserType = new RadioButtonGroup<String>();
		this.optionAppraiserType.setItems(MEMBER, SUBSTITUTE);
		this.optionAppraiserType.setValue(MEMBER);
		
		this.checkChair = new Checkbox("Presidente da Banca");
		
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
		
		this.optionAppraiserType = new RadioButtonGroup<String>();
		this.optionAppraiserType.setItems(MEMBER, SUBSTITUTE);
		this.optionAppraiserType.setValue(appraiser.isSubstitute() ? SUBSTITUTE : MEMBER);
		
		this.checkChair = new Checkbox("Presidente da Banca");
		this.checkChair.setValue(appraiser.isChair());
		
		this.addField(this.comboProfessor);
		this.addField(this.optionAppraiserType);
		this.addField(this.checkChair);
	}
	
	public EditInternshipJuryAppraiserWindow(EditInternshipPosterRequestWindow parentWindow, boolean substitute) {
		super("Editar Membro", null);
		
		this.parentWindow = parentWindow;
		this.edit = false;
		
		this.comboProfessor = new SupervisorComboBox("Membro", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboProfessor.setRequired(true);
		
		this.optionAppraiserType = new RadioButtonGroup<String>();
		this.optionAppraiserType.setItems(MEMBER, SUBSTITUTE);
		this.optionAppraiserType.setValue(substitute ? SUBSTITUTE : MEMBER);
		
		this.checkChair = new Checkbox("Presidente da Banca");
		this.checkChair.setValue(false);
		
		this.addField(this.comboProfessor);
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
			if(this.optionAppraiserType.getValue().equals(SUBSTITUTE)) {
				appraiser.setSubstitute(true);
			}
			
			if(this.parentWindow instanceof EditInternshipJuryWindow) {
				if(this.edit) {
					((EditInternshipJuryWindow)this.parentWindow).editAppraiser(appraiser);
				} else {
					((EditInternshipJuryWindow)this.parentWindow).addAppraiser(appraiser);
				}
			} else if(this.parentWindow instanceof EditInternshipPosterRequestWindow) {
				InternshipPosterAppraiserRequest a = new InternshipPosterAppraiserRequest();
				
				a.setAppraiser(appraiser.getAppraiser());
				a.setSubstitute(appraiser.isSubstitute());
				
				((EditInternshipPosterRequestWindow)this.parentWindow).addAppraiser(a);
			}
			
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Adicionar Membro", e.getMessage());
		}
	}

}
