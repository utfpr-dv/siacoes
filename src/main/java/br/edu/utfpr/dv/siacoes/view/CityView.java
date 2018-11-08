package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.CityBO;
import br.edu.utfpr.dv.siacoes.model.City;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditCityWindow;

public class CityView extends ListView {
	
	public static final String NAME = "city";
	
	public CityView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Cidades");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("Estado", String.class);
		this.getGrid().addColumn("País", String.class);
		
		this.getGrid().getColumns().get(1).setWidth(100);
		
		try{
			CityBO bo = new CityBO();
			List<City> list = bo.listAll();
			
			for(City c : list){
				Object itemId = this.getGrid().addRow(c.getName(), c.getState().getInitials(), c.getState().getCountry().getName());
				this.addRowId(itemId, c.getIdCity());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Cidades", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditCityWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			CityBO bo = new CityBO();
			City city = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditCityWindow(city, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Cidade", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
