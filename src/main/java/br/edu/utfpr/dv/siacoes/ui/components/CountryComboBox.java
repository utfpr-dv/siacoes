package br.edu.utfpr.dv.siacoes.ui.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.combobox.ComboBox;

import br.edu.utfpr.dv.siacoes.bo.CountryBO;
import br.edu.utfpr.dv.siacoes.model.Country;

public class CountryComboBox extends ComboBox<Country> {

	private List<Country> list;
	
	public CountryComboBox(){
		this.setLabel("País");
		this.setWidth("400px");
		this.setItemLabelGenerator(Country::getName);
		this.loadComboCountry();
	}
	
	public Country getCountry(){
		return this.getValue();
	}
	
	public void setCountry(Country c){
		if(c == null){
			this.setValue(null);
			return;
		}
		
		boolean find = false;
		
		for(Country country : this.list){
			if(c.getIdCountry() == country.getIdCountry()){
				this.setValue(country);
				find = true;
				break;
			}
		}
		
		if(!find){
			try {
				CountryBO bo = new CountryBO();
				Country country = bo.findById(c.getIdCountry());
				
				//this.addItem(country);
				this.setValue(country);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void loadComboCountry(){
		try {
			CountryBO bo = new CountryBO();
			
			this.list = bo.listAll();
			
			this.setItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
