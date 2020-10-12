package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.CityBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.City;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.CityDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditCityWindow;

@PageTitle("Cidades")
@Route(value = "city", layout = MainLayout.class)
public class CityView extends ListView<CityDataSource> {
	
	public CityView(){
		super(SystemModule.SIGES);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(CityDataSource::getName, "Name").setHeader("Nome");
		this.getGrid().addColumn(CityDataSource::getState, "State").setHeader("Estado").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(CityDataSource::getCountry, "Country").setHeader("País");
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try{
			CityBO bo = new CityBO();
			List<City> list = bo.listAll();
			
			this.getGrid().setItems(CityDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Cidades", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditCityWindow window = new EditCityWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			CityBO bo = new CityBO();
			City city = bo.findById((int)id);
			
			EditCityWindow window = new EditCityWindow(city, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Cidade", e.getMessage());
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
