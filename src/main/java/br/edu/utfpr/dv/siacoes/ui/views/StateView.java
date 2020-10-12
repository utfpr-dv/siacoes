package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.StateBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.State;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.StateDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditStateWindow;

@PageTitle("Estados")
@Route(value = "state", layout = MainLayout.class)
public class StateView extends ListView<StateDataSource> {
	
	public StateView(){
		super(SystemModule.SIGES);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(StateDataSource::getName, "Name").setHeader("Nome");
		this.getGrid().addColumn(StateDataSource::getCountry, "Country").setHeader("País");
		this.getGrid().addColumn(StateDataSource::getInitials, "Initials").setHeader("Sigla").setFlexGrow(0).setWidth("100px");
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try{
			StateBO bo = new StateBO();
			List<State> list = bo.listAll();
			
			this.getGrid().setItems(StateDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Estados", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditStateWindow window = new EditStateWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			StateBO bo = new StateBO();
			State state = bo.findById((int)id);
			
			EditStateWindow window = new EditStateWindow(state, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Estado", e.getMessage());
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
