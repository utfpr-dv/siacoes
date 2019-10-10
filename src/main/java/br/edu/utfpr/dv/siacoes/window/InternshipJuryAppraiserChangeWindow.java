package br.edu.utfpr.dv.siacoes.window;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.ComboBox;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.User;

public class InternshipJuryAppraiserChangeWindow extends EditWindow {
	
	private final InternshipJury jury;
	
	private final ComboBox comboMember;
	private final ComboBox comboSubstitute;
	
	public InternshipJuryAppraiserChangeWindow(InternshipJury jury) {
		super("Substituir Membro da Banca", null);
		
		this.jury = jury;
		
		this.comboMember = new ComboBox("Selecione o membro titutar que será removido da banca");
		this.comboMember.setNullSelectionAllowed(false);
		this.comboMember.setWidth("400px");
		
		this.comboSubstitute = new ComboBox("Selecione o suplente que será escalado na banca");
		this.comboSubstitute.setNullSelectionAllowed(false);
		this.comboSubstitute.setWidth("400px");
		
		this.addField(this.comboMember);
		this.addField(this.comboSubstitute);
		
		this.loadCombos();
	}
	
	private void loadCombos() {
		try {
			List<InternshipJuryAppraiser> list = new InternshipJuryAppraiserBO().listAppraisers(jury.getIdInternshipJury());
			
			for(InternshipJuryAppraiser appraiser : list) {
				if(appraiser.isSubstitute()) {
					this.comboSubstitute.addItem(appraiser.getAppraiser());
				} else if(!appraiser.isChair()) {
					this.comboMember.addItem(appraiser.getAppraiser());
				}
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Membros", e.getMessage());
		}
	}

	@Override
	public void save() {
		try {
			List<InternshipJuryAppraiser> list = new InternshipJuryAppraiserBO().listAppraisers(jury.getIdInternshipJury());
			InternshipJuryAppraiser member = null, substitute = null;
			
			for(InternshipJuryAppraiser appraiser : list) {
				if((this.comboMember.getValue() != null) && (appraiser.getAppraiser().getIdUser() == ((User)this.comboMember.getValue()).getIdUser())) {
					member = appraiser;
				}
				
				if((this.comboSubstitute.getValue() != null) && (appraiser.getAppraiser().getIdUser() == ((User)this.comboSubstitute.getValue()).getIdUser())) {
					substitute = appraiser;
				}
			}
			
			new InternshipJuryAppraiserBO().changeAppraiser(Session.getIdUserLog(), member, substitute);
			
			this.showSuccessNotification("Substituir Membros", "Membro da banca substituído com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Substituir Membros", e.getMessage());
		}
	}

}
