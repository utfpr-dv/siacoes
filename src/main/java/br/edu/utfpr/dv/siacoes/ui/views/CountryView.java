package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.CountryBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Country;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.CountryDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditCountryWindow;

@PageTitle("Países")
@Route(value = "country", layout = MainLayout.class)
public class CountryView extends ListView<CountryDataSource> {
	
	public CountryView(){
		super(SystemModule.SIGES);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(CountryDataSource::getName, "Name").setHeader("Nome");
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try{
			CountryBO bo = new CountryBO();
			List<Country> list = bo.listAll();
			
			this.getGrid().setItems(CountryDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Países", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditCountryWindow window = new EditCountryWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			CountryBO bo = new CountryBO();
			Country country = bo.findById((int)id);
			
			EditCountryWindow window = new EditCountryWindow(country, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar País", e.getMessage());
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
