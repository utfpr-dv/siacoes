package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CountryBO;
import br.edu.utfpr.dv.siacoes.model.Country;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditCountryWindow extends EditWindow {
	
	private final Country country;
	
	private final TextField textName;
	
	public EditCountryWindow(Country country, ListView parentView){
		super("Editar País", parentView);
		
		if(country == null){
			this.country = new Country();
		}else{
			this.country = country;
		}
		
		this.textName = new TextField("País");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		this.textName.setRequired(true);
		
		this.addField(this.textName);
		
		this.loadCountry();
		this.textName.focus();
	}
	
	private void loadCountry(){
		this.textName.setValue(this.country.getName());
	}

	@Override
	public void save() {
		try{
			CountryBO bo = new CountryBO();
			
			this.country.setName(this.textName.getValue());
			
			bo.save(Session.getIdUserLog(), this.country);
			
			this.showSuccessNotification("Salvar País", "País salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar País", e.getMessage());
		}
	}

}
