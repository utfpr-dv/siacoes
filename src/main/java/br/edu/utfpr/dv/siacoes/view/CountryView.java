package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.CountryBO;
import br.edu.utfpr.dv.siacoes.model.Country;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditCountryWindow;

public class CountryView extends ListView {
	
	public static final String NAME = "country";
	
	public CountryView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Países");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		
		try{
			CountryBO bo = new CountryBO();
			List<Country> list = bo.listAll();
			
			for(Country c : list){
				Object itemId = this.getGrid().addRow(c.getName());
				this.addRowId(itemId, c.getIdCountry());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Países", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditCountryWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			CountryBO bo = new CountryBO();
			Country country = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditCountryWindow(country, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar País", e.getMessage());
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
