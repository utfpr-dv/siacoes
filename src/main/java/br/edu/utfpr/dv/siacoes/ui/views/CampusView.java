package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.CampusDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditCampusWindow;

@PageTitle("Câmpus")
@Route(value = "campus", layout = MainLayout.class)
public class CampusView extends ListView<CampusDataSource> {
	
	public CampusView(){
		super(SystemModule.GENERAL);
		
		this.setProfilePerimissions(UserProfile.PROFESSOR);
		
		this.getGrid().addColumn(CampusDataSource::getName).setHeader("Nome");
		this.getGrid().addColumn(CampusDataSource::getActive).setHeader("Ativo").setFlexGrow(0).setWidth("100px");
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try{
			CampusBO bo = new CampusBO();
			List<Campus> list = bo.listAll(false);
			
			this.getGrid().setItems(CampusDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Câmpus", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditCampusWindow window = new EditCampusWindow(null, this);
		
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findById(id);
			
			EditCampusWindow window = new EditCampusWindow(campus, this);
			
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Câmpus", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
