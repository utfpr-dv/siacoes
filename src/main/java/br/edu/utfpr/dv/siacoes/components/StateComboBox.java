package br.edu.utfpr.dv.siacoes.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.ComboBox;

import br.edu.utfpr.dv.siacoes.bo.StateBO;
import br.edu.utfpr.dv.siacoes.model.State;

public class StateComboBox extends ComboBox {

	private List<State> list;
	private int idCountry;
	
	public StateComboBox(){
		super("Estado");
		this.setIdCountry(0);
		this.setNullSelectionAllowed(false);
		this.setWidth("400px");
		this.loadComboState();
	}
	
	public void setIdCountry(int idCountry){
		this.idCountry = idCountry;
		this.loadComboState();
	}
	
	public int getIdCountry(){
		return this.idCountry;
	}
	
	public State getStateValue(){
		return (State)this.getValue();
	}
	
	public void setState(State c){
		if(c == null){
			this.setValue(null);
			return;
		}
		
		boolean find = false;
		
		for(State state : this.list){
			if(c.getIdState() == state.getIdState()){
				this.setValue(state);
				find = true;
				break;
			}
		}
		
		if(!find){
			try {
				StateBO bo = new StateBO();
				State state = bo.findById(c.getIdState());
				
				this.addItem(state);
				this.setValue(state);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void loadComboState(){
		try {
			StateBO bo = new StateBO();
			
			if(this.getIdCountry() == 0){
				this.list = bo.listAll();
			}else{
				this.list = bo.listByCountry(this.getIdCountry());
			}
			
			this.removeAllItems();
			this.addItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
