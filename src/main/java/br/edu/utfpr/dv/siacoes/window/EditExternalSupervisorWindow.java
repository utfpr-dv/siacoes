package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.StringUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditExternalSupervisorWindow extends EditWindow {
	
	private final User user;
	private final EditJuryAppraiserWindow juryAppraiserWindow;
	
	private final TextField textName;
	private final TextField textEmail;
	private final TextField textInstitution;
	private final TextField textArea;
	private final TextArea textResearch;
	private final TextField textLattes;
	
	public EditExternalSupervisorWindow(User user, ListView parentView, EditJuryAppraiserWindow juryAppraiserWindow) {
		super("Editar Orientador", parentView);
		
		this.juryAppraiserWindow = juryAppraiserWindow;
		
		if(user == null) {
			this.user = new User();
		} else {
			this.user = user;
		}
		
		this.textName = new TextField("Nome");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		this.textName.setRequired(true);
		
		this.textEmail = new TextField("E-mail");
		this.textEmail.setWidth("400px");
		this.textEmail.setMaxLength(100);
		this.textEmail.setRequired(true);
		
		this.textInstitution = new TextField("Instituição");
		this.textInstitution.setWidth("400px");
		this.textInstitution.setMaxLength(100);
		
		this.textArea = new TextField("Área/Subárea");
		this.textArea.setWidth("800px");
		this.textArea.setMaxLength(100);
		
		this.textResearch = new TextArea("Áreas de Pesquisa");
		this.textResearch.setWidth("800px");
		
		this.textLattes = new TextField("Link do Lattes");
		this.textLattes.setWidth("400px");
		this.textLattes.setMaxLength(100);
		
		this.addField(new HorizontalLayout(this.textName, this.textEmail));
		this.addField(new HorizontalLayout(this.textInstitution, this.textLattes));
		this.addField(this.textArea);
		this.addField(this.textResearch);
		
		this.loadSupervisor();
		this.textName.focus();
	}
	
	public EditExternalSupervisorWindow(User user, ListView parentView) {
		this(user, parentView, null);
	}
	
	public EditExternalSupervisorWindow(EditJuryAppraiserWindow juryAppraiserWindow) {
		this(null, null, juryAppraiserWindow);
	}

	private void loadSupervisor() {
		this.textName.setValue(this.user.getName());
		this.textEmail.setValue(this.user.getEmail());
		this.textInstitution.setValue(this.user.getInstitution());
		this.textLattes.setValue(this.user.getLattes());
		this.textArea.setValue(this.user.getArea());
		this.textResearch.setValue(this.user.getResearch());
	}
	
	@Override
	public void save() {
		try {
			this.user.setName(this.textName.getValue());
			this.user.setEmail(this.textEmail.getValue());
			this.user.setInstitution(this.textInstitution.getValue());
			this.user.setLattes(this.textLattes.getValue());
			this.user.setArea(this.textArea.getValue());
			this.user.setResearch(this.textResearch.getValue());
			
			if(this.user.getIdUser() == 0) {
				this.user.setLogin(this.user.getEmail());
				this.user.setSalt(StringUtils.generateSalt());
				this.user.setPassword(this.user.getEmail() + this.user.getSalt());
			}
			
			if((this.user.getProfiles() != null) && (this.user.getProfiles().size() == 0)) {
				this.user.getProfiles().add(UserProfile.SUPERVISOR);
			}
			
			new UserBO().save(Session.getIdUserLog(), this.user);
			
			this.showSuccessNotification("Salvar Informações", "Informações salvas com sucesso.");
			
			if(this.juryAppraiserWindow != null) {
				this.juryAppraiserWindow.refreshComboProfessor();
			}
			
			this.parentViewRefreshGrid();
			this.close();
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Informações", e.getMessage());
		}
	}

}
