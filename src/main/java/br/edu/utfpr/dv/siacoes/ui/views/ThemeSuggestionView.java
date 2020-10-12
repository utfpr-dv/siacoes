package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ThemeSuggestionBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.ThemeSuggestion;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.ThemeSuggestionDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditThemeSuggestionWindow;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

@PageTitle("Sugestões de Projeto de TCC")
@Route(value = "themesuggestion", layout = MainLayout.class)
public class ThemeSuggestionView extends ListView<ThemeSuggestionDataSource> {
	
	private final Checkbox checkActive;
	
	public ThemeSuggestionView(){
		super(SystemModule.SIGET);
		
		this.getGrid().addColumn(new LocalDateRenderer<>(ThemeSuggestionDataSource::getSubmission, "dd/MM/yyyy"), "Submission").setHeader("Submissão").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(ThemeSuggestionDataSource::getTitle, "Title").setHeader("Título");
		this.getGrid().addColumn(ThemeSuggestionDataSource::getUser, "User").setHeader("Proponente");
		
		this.checkActive = new Checkbox("Apenas Ativos");
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
		try {
			ThemeSuggestionBO bo = new ThemeSuggestionBO();
	    	List<ThemeSuggestion> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.checkActive.getValue());
	    	
	    	this.getGrid().setItems(ThemeSuggestionDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Sugestões", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditThemeSuggestionWindow window = new EditThemeSuggestionWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try {
			ThemeSuggestionBO bo = new ThemeSuggestionBO();
			ThemeSuggestion p = bo.findById((int)id);
			
			EditThemeSuggestionWindow window = new EditThemeSuggestionWindow(p, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Sugestão", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Sugestão", e.getMessage());
		}
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
