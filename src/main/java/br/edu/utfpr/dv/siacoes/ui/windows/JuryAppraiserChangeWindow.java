package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.combobox.ComboBox;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.User;

public class JuryAppraiserChangeWindow extends EditWindow {
	
	private final Jury jury;

	private final ComboBox<User> comboMember;
	private final ComboBox<User> comboSubstitute;
	
	public JuryAppraiserChangeWindow(Jury jury) {
		super("Substituir Membro da Banca", null);
		
		this.jury = jury;
		
		this.comboMember = new ComboBox<User>("Selecione o membro titutar que será removido da banca");
		this.comboMember.setWidth("400px");
		
		this.comboSubstitute = new ComboBox<User>("Selecione o suplente que será escalado na banca");
		this.comboSubstitute.setWidth("400px");
		
		this.addField(this.comboMember);
		this.addField(this.comboSubstitute);
		
		this.loadCombos();
	}
	
	private void loadCombos() {
		try {
			List<JuryAppraiser> list = new JuryAppraiserBO().listAppraisers(jury.getIdJury());
			List<User> members = new ArrayList<User>();
			List<User> substitutes = new ArrayList<User>();
			
			for(JuryAppraiser appraiser : list) {
				if(appraiser.isSubstitute()) {
					substitutes.add(appraiser.getAppraiser());
				} else if(!appraiser.isChair()) {
					members.add(appraiser.getAppraiser());
				}
			}
			
			this.comboMember.setItems(members);
			this.comboSubstitute.setItems(substitutes);
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Membros", e.getMessage());
		}
	}
	
	@Override
	public void save() {
		try {
			List<JuryAppraiser> list = new JuryAppraiserBO().listAppraisers(jury.getIdJury());
			JuryAppraiser member = null, substitute = null;
			
			for(JuryAppraiser appraiser : list) {
				if((this.comboMember.getValue() != null) && (appraiser.getAppraiser().getIdUser() == ((User)this.comboMember.getValue()).getIdUser())) {
					member = appraiser;
				}
				
				if((this.comboSubstitute.getValue() != null) && (appraiser.getAppraiser().getIdUser() == ((User)this.comboSubstitute.getValue()).getIdUser())) {
					substitute = appraiser;
				}
			}
			
			new JuryAppraiserBO().changeAppraiser(Session.getIdUserLog(), member, substitute);
			
			this.showSuccessNotification("Substituir Membros", "Membro da banca substituído com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Substituir Membros", e.getMessage());
		}
	}

}
