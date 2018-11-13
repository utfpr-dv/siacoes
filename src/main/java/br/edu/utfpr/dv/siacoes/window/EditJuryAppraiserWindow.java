package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.model.User;

public class EditJuryAppraiserWindow extends EditWindow {

	private final EditWindow parentWindow;
	
	private final SupervisorComboBox comboProfessor;
	private final OptionGroup optionAppraiserType;
	private final CheckBox checkChair;
	private final Button buttonSchedule;
	private final Button buttonAddExternalSupervisor;
	private final boolean edit;
	
	public EditJuryAppraiserWindow(EditJuryWindow parentWindow) {
		super("Adicionar Membro", null);
		
		this.parentWindow = parentWindow;
		this.edit = false;
		
		this.buttonSchedule = new Button();
		this.buttonAddExternalSupervisor = new Button();
		
		this.comboProfessor = new SupervisorComboBox("Membro", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboProfessor.setRequired(true);
		this.comboProfessor.setWidth("570px");
		
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
		this.edit = false;
		
		this.buttonSchedule = new Button("Agenda do Professor", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	professorSchecule();
            }
        });
		this.buttonSchedule.setIcon(FontAwesome.CALENDAR_O);
		this.addButton(this.buttonSchedule);
		this.buttonSchedule.setWidth("200px");
		
		this.buttonAddExternalSupervisor = new Button("Adicionar Membro Externo", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addExternalSupervisor();
            }
        });
		this.buttonAddExternalSupervisor.setIcon(FontAwesome.PLUS);
		this.buttonAddExternalSupervisor.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.addButton(this.buttonAddExternalSupervisor);
		this.buttonAddExternalSupervisor.setWidth("200px");
		
		this.comboProfessor = new SupervisorComboBox((substitute ? "Suplente" : "Membro"), Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboProfessor.setRequired(true);
		this.comboProfessor.setWidth("570px");
		
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
			
			JuryAppraiser appraiser = new JuryAppraiser();
			
			appraiser.setAppraiser(this.comboProfessor.getProfessor());
			appraiser.setChair(this.checkChair.getValue());
			if(this.optionAppraiserType.getValue() != this.optionAppraiserType.getItemIds().iterator().next()) {
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Adicionar Membro", e.getMessage());
		}
	}
	
	private void professorSchecule() {
		User professor = this.comboProfessor.getProfessor();
		
		if((professor == null) || (professor.getIdUser() == 0)) {
			this.showWarningNotification("Agenda do Professor", "Selecione o professor para visualizar a agenda.");
		} else {
			UI.getCurrent().addWindow(new ProfessorScheculeWindow(professor));
		}
	}
	
	private void addExternalSupervisor() {
		UI.getCurrent().addWindow(new EditExternalSupervisorWindow(this));
	}
	
	public void refreshComboProfessor() {
		this.comboProfessor.setFilter(SupervisorFilter.EVERYONE);
	}

}
