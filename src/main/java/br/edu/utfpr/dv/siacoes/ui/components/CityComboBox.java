package br.edu.utfpr.dv.siacoes.ui.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.combobox.ComboBox;

import br.edu.utfpr.dv.siacoes.bo.CityBO;
import br.edu.utfpr.dv.siacoes.model.City;

public class CityComboBox extends ComboBox<City> {

	private List<City> list;
	private int idState;
	
	public CityComboBox(){
		this.setLabel("Cidade");
		this.setIdState(0);
		this.setWidth("400px");
		this.setItemLabelGenerator(City::getName);
		this.loadComboCity();
	}
	
	public void setIdState(int idState){
		this.idState = idState;
		this.loadComboCity();
	}
	
	public int getIdState(){
		return this.idState;
	}
	
	public City getCity(){
		return this.getValue();
	}
	
	public void setCity(City c){
		if(c == null){
			this.setValue(null);
			return;
		}
		
		boolean find = false;
		
		for(City city : this.list){
			if(c.getIdCity() == city.getIdCity()){
				this.setValue(city);
				find = true;
				break;
			}
		}
		
		if(!find){
			try {
				CityBO bo = new CityBO();
				City city = bo.findById(c.getIdCity());
				
				//this.addItem(city);
				this.setValue(city);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void loadComboCity(){
		try {
			CityBO bo = new CityBO();
			
			if(this.getIdState() == 0){
				this.list = bo.listAll();
			}else{
				this.list = bo.listByState(this.getIdState());
			}
			
			this.setItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
