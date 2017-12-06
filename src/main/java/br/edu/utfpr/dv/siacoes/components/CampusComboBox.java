package br.edu.utfpr.dv.siacoes.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.NativeSelect;

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.model.Campus;

public class CampusComboBox extends NativeSelect {

	private List<Campus> list;
	private boolean filterOnlyActives;
	
	public CampusComboBox(){
		super("Câmpus");
		this.setNullSelectionAllowed(false);
		this.setWidth("400px");
		this.setFilterOnlyActives(true);
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
		return (Campus)this.getValue();
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
				
				this.addItem(campus);
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
			
			this.removeAllItems();
			this.addItems(this.list);
			
			if(this.list.size() > 0){
				this.setCampus(this.list.get(0));
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
