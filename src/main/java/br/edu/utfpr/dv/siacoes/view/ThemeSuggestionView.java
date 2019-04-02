package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ThemeSuggestionBO;
import br.edu.utfpr.dv.siacoes.model.ThemeSuggestion;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.window.EditThemeSuggestionWindow;

public class ThemeSuggestionView extends ListView {

	public static final String NAME = "themesuggestion";
	
	private final CheckBox checkActive;
	
	public ThemeSuggestionView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Sugestões de Projeto de TCC");
		
		this.checkActive = new CheckBox("Apenas Ativos");
		this.checkActive.setValue(true);
		
		this.addFilterField(this.checkActive);
		
		if(!Session.isUserProfessor()){
			this.setFiltersVisible(false);
			this.setAddVisible(false);
			this.setDeleteVisible(false);
			this.setEditCaption("Visualizar");
		}
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Proponente", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(125);
		
		try {
			ThemeSuggestionBO bo = new ThemeSuggestionBO();
	    	List<ThemeSuggestion> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.checkActive.getValue());
	    	
	    	for(ThemeSuggestion p : list){
				Object itemId = this.getGrid().addRow(p.getSubmissionDate(), p.getTitle(), p.getProponent());
				this.addRowId(itemId, p.getIdThemeSuggestion());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Sugestões", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditThemeSuggestionWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try {
			ThemeSuggestionBO bo = new ThemeSuggestionBO();
			ThemeSuggestion p = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditThemeSuggestionWindow(p, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Sugestão", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		try {
			ThemeSuggestionBO bo = new ThemeSuggestionBO();
			
			if(!Session.isUserManager(SystemModule.SIGET)){
				ThemeSuggestion theme = bo.findById((int)id);
				
				if(theme.getUser().getIdUser() != Session.getUser().getIdUser()){
					throw new Exception("Não é possível excluir sugestões de outros usuários.");
				}
			}

			bo.delete(Session.getIdUserLog(), (int)id);
			
			this.refreshGrid();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Sugestão", e.getMessage());
		}
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
