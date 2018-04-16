package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditProposalAppraiserWindow extends EditWindow {

	private final ProposalAppraiser appraiser;
	private final EditProposalWindow editProposalWindow;
	
	private final SupervisorComboBox comboAppraiser;
	private final NativeSelect comboFeedback;
	private final TextArea textComments;
	private final CheckBox checkAllowEditing;
	
	public EditProposalAppraiserWindow(ProposalAppraiser appraiser, EditProposalWindow editProposalWindow){
		super("Editar Avaliador", null);
		
		if(appraiser == null){
			this.appraiser = new ProposalAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		this.editProposalWindow = editProposalWindow;
		
		this.comboAppraiser = new SupervisorComboBox("Avaliador", Session.getUser().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboFeedback = new NativeSelect("Parecer");
		this.textComments = new TextArea("Observações");
		this.checkAllowEditing = new CheckBox("Permite edição");
		
		this.buildWindow();
	}
	
	public EditProposalAppraiserWindow(ProposalAppraiser appraiser, ListView parentView) {
		super("Emitir Parecer", parentView);
		
		if(appraiser == null){
			this.appraiser = new ProposalAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		this.editProposalWindow = null;
		
		this.comboAppraiser = new SupervisorComboBox("Avaliador", Session.getUser().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboFeedback = new NativeSelect("Parecer");
		this.textComments = new TextArea("Observações");
		this.checkAllowEditing = new CheckBox("Permite edição");
		
		this.buildWindow();
	}
	
	private void buildWindow(){
		this.comboAppraiser.setWidth("800px");
		
		this.comboFeedback.setNullSelectionAllowed(false);
		this.comboFeedback.setWidth("800px");
		this.comboFeedback.addItem(ProposalFeedback.NONE);
		this.comboFeedback.addItem(ProposalFeedback.APPROVED);
		this.comboFeedback.addItem(ProposalFeedback.APPROVEDWITHRESERVATIONS);
		this.comboFeedback.addItem(ProposalFeedback.DISAPPROVED);
		
		this.textComments.setWidth("800px");
		this.textComments.setHeight("200px");
		this.textComments.addStyleName("textscroll");
		
		this.addField(this.comboAppraiser);
		this.addField(this.comboFeedback);
		this.addField(this.textComments);
		
		if(Session.isUserManager(SystemModule.SIGET)){
			this.addField(this.checkAllowEditing);
		}else if(!this.appraiser.isAllowEditing()){
			this.setSaveButtonEnabled(false);
		}
		
		this.loadAppraiser();
	}
	
	private void loadAppraiser(){
		this.comboAppraiser.setProfessor(this.appraiser.getAppraiser());
		this.comboFeedback.setValue(this.appraiser.getFeedback());
		this.textComments.setValue(this.appraiser.getComments());
		this.checkAllowEditing.setValue(this.appraiser.isAllowEditing());
		
		if(!this.appraiser.isAllowEditing() || (this.appraiser.getAppraiser().getIdUser() != Session.getUser().getIdUser())){
			this.comboFeedback.setEnabled(false);
			this.textComments.setEnabled(false);
		}
		
		if((this.appraiser.getIdProposalAppraiser() != 0) || (this.appraiser.getAppraiser().getIdUser() != 0)){
			this.comboAppraiser.setEnabled(false);
		}
	}
	
	@Override
	public void save() {
		try{
			if(Session.isUserManager(SystemModule.SIGET)){
				this.appraiser.setAllowEditing(this.checkAllowEditing.getValue());
				
				if(this.appraiser.getAppraiser().getIdUser() == 0){
					this.appraiser.setAppraiser(this.comboAppraiser.getProfessor());
				}
			}
			
			if(this.appraiser.isAllowEditing() && (this.appraiser.getAppraiser().getIdUser() == Session.getUser().getIdUser())){
				this.appraiser.setFeedback((ProposalFeedback)this.comboFeedback.getValue());
				this.appraiser.setComments(this.textComments.getValue());
			}
			
			if(this.editProposalWindow == null){
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				
				bo.save(this.appraiser);	
			}else{
				this.editProposalWindow.setAppraiser(this.appraiser);
			}
			
			Notification.show("Salvar Avaliador", "Avaliador salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Avaliador", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
