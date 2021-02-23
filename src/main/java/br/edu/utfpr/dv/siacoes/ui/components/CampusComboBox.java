package br.edu.utfpr.dv.siacoes.ui.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.select.Select;

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.model.Campus;

public class CampusComboBox extends Select<Campus> {

	private List<Campus> list;
	private boolean filterOnlyActives;
	
	public CampusComboBox(){
		this.setLabel("Câmpus");
		this.setWidth("400px");
		this.setFilterOnlyActives(true);
		this.setItemLabelGenerator(Campus::getName);
		this.loadComboCampus();
	}
	
	public boolean isFilterOnlyActives(){
		return filterOnlyActives;
	}
	
	public void setFilterOnlyActives(boolean filterOnlyActives){
		this.filterOnlyActives = filterOnlyActives;
		this.loadComboCampus();
	}
	
	public Campus getCampus(){
		return this.getValue();
	}
	
	public void setCampus(Campus c){
		if(c == null){
			this.setValue(null);
			return;
		}
		
		boolean find = false;
		
		for(Campus campus : this.list){
			if(c.getIdCampus() == campus.getIdCampus()){
				this.setValue(campus);
				find = true;
				break;
			}
		}
		
		if(!find){
			try {
				CampusBO bo = new CampusBO();
				Campus campus = bo.findById(c.getIdCampus());
				
				//this.addItem(campus);
				this.setValue(campus);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void loadComboCampus(){
		try {
			CampusBO bo = new CampusBO();
			this.list = bo.listAll(this.isFilterOnlyActives());
			
			this.setItems(this.list);
			
			if(this.list.size() > 0){
				this.setCampus(this.list.get(0));
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
