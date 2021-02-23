package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.ui.components.SupervisorComboBox;

public class EditJuryAppraiserWindow extends EditWindow {

	private final EditWindow parentWindow;
	
	private final SupervisorComboBox comboProfessor;
	private final RadioButtonGroup<String> optionAppraiserType;
	private final Checkbox checkChair;
	private final Button buttonSchedule;
	private final Button buttonAddExternalSupervisor;
	private final boolean edit;
	
	private static final String MEMBER = "Titular";
	private static final String SUBSTITUTE = "Suplente";
	
	public EditJuryAppraiserWindow(EditJuryWindow parentWindow) {
		super("Adicionar Membro", null);
		
		this.parentWindow = parentWindow;
		this.edit = false;
		
		this.buttonSchedule = new Button();
		this.buttonAddExternalSupervisor = new Button();
		
		this.comboProfessor = new SupervisorComboBox("Membro", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboProfessor.setRequired(true);
		this.comboProfessor.setWidth("570px");
		
		this.optionAppraiserType = new RadioButtonGroup<String>();
		this.optionAppraiserType.setItems(MEMBER, SUBSTITUTE);
		this.optionAppraiserType.setValue(MEMBER);
		
		this.checkChair = new Checkbox("Presidente da Banca");
		
		this.addField(this.comboProfessor);
		this.addField(this.optionAppraiserType);
		this.addField(this.checkChair);
	}
	
	public EditJuryAppraiserWindow(EditJuryRequestWindow parentWindow, boolean substitute) {
		super("Adicionar Membro", null);
		
		this.parentWindow = parentWindow;
		this.edit = false;
		
		this.buttonSchedule = new Button("Agenda do Professor", new Icon(VaadinIcon.CALENDAR_O), event -> {
            professorSchecule();
        });
		this.addButton(this.buttonSchedule);
		this.buttonSchedule.setWidth("200px");
		
		this.buttonAddExternalSupervisor = new Button("Adicionar Membro Externo", new Icon(VaadinIcon.PLUS), event -> {
            addExternalSupervisor();
        });
		this.buttonAddExternalSupervisor.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.addButton(this.buttonAddExternalSupervisor);
		this.buttonAddExternalSupervisor.setWidth("200px");
		
		this.comboProfessor = new SupervisorComboBox((substitute ? "Suplente" : "Membro"), Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboProfessor.setRequired(true);
		this.comboProfessor.setWidth("570px");
		
		this.optionAppraiserType = new RadioButtonGroup<String>();
		this.optionAppraiserType.setItems(MEMBER, SUBSTITUTE);
		
		if(substitute) {
			this.optionAppraiserType.setValue(SUBSTITUTE);
		} else {
			this.optionAppraiserType.setValue(MEMBER);
		}
		
		this.checkChair = new Checkbox("Presidente da Banca");
		this.checkChair.setValue(false);
		
		this.addField(this.comboProfessor);
	}
	
	public EditJuryAppraiserWindow(JuryAppraiser appraiser, EditJuryWindow parentWindow) {
		super("Editar Membro", null);
		
		this.parentWindow = parentWindow;
		this.edit = true;
		
		this.buttonSchedule = new Button();
		this.buttonAddExternalSupervisor = new Button();
		
		this.comboProfessor = new SupervisorComboBox("Membro", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboProfessor.setProfessor(appraiser.getAppraiser());
		this.comboProfessor.setEnabled(false);
		this.comboProfessor.setRequired(true);
		this.comboProfessor.setWidth("570px");
		
		this.optionAppraiserType = new RadioButtonGroup<String>();
		this.optionAppraiserType.setItems(MEMBER, SUBSTITUTE);
		this.optionAppraiserType.setValue(appraiser.isSubstitute() ? SUBSTITUTE : MEMBER);
		
		this.checkChair = new Checkbox("Presidente da Banca");
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
			
			JuryAppraiser appraiser = new JuryAppraiser();
			
			appraiser.setAppraiser(this.comboProfessor.getProfessor());
			appraiser.setChair(this.checkChair.getValue());
			if(this.optionAppraiserType.getValue().equals(SUBSTITUTE)) {
				appraiser.setSubstitute(true);
			}
			
			if(this.parentWindow instanceof EditJuryWindow) {
				if(this.edit) {
					((EditJuryWindow)this.parentWindow).editAppraiser(appraiser);
				} else {
					((EditJuryWindow)this.parentWindow).addAppraiser(appraiser);
				}
			} else if(this.parentWindow instanceof EditJuryRequestWindow) {
				JuryAppraiserRequest request = new JuryAppraiserRequest();
				
				request.setAppraiser(appraiser.getAppraiser());
				request.setChair(appraiser.isChair());
				request.setSubstitute(appraiser.isSubstitute());
				
				((EditJuryRequestWindow)this.parentWindow).addAppraiser(request);
			}
			
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Adicionar Membro", e.getMessage());
		}
	}
	
	private void professorSchecule() {
		User professor = this.comboProfessor.getProfessor();
		
		if((professor == null) || (professor.getIdUser() == 0)) {
			this.showWarningNotification("Agenda do Professor", "Selecione o professor para visualizar a agenda.");
		} else {
			ProfessorScheculeWindow window = new ProfessorScheculeWindow(professor);
			window.open();
		}
	}
	
	private void addExternalSupervisor() {
		EditExternalSupervisorWindow window = new EditExternalSupervisorWindow(this);
		window.open();
	}
	
	public void refreshComboProfessor() {
		this.comboProfessor.setFilter(SupervisorFilter.EVERYONE);
	}

}
