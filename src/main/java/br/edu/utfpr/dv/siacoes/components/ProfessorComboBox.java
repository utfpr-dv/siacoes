package br.edu.utfpr.dv.siacoes.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.ComboBox;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.User;

public class ProfessorComboBox extends ComboBox {
	
	private List<User> list;
	private int idCampus;
	
	public ProfessorComboBox(String caption){
		super(caption);
		this.setIdCampus(0);
		this.setNullSelectionAllowed(false);
		this.setWidth("400px");
		this.loadComboProfessor();
	}
	
	public void setIdCampus(int idCampus){
		this.idCampus = idCampus;
		this.loadComboProfessor();
	}
	
	public int getIdCampus(){
		return this.idCampus;
	}
	
	public User getProfessor(){
		return (User)this.getValue();
	}
	
	public void setProfessor(User c){
		if(c == null){
			this.setValue(null);
			return;
		}
		
		boolean find = false;
		
		for(User user : this.list){
			if(c.getIdUser() == user.getIdUser()){
				this.setValue(user);
				find = true;
				break;
			}
		}
		
		if(!find){
			try {
				UserBO bo = new UserBO();
				User user = bo.findById(c.getIdUser());
				
				this.addItem(user);
				this.setValue(user);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void loadComboProfessor(){
		try {
			UserBO bo = new UserBO();
			
			if(this.getIdCampus() == 0){
				this.list = bo.listAllProfessors(true);
			}else{
				this.list = bo.listProfessorsByCampus(this.getIdCampus(), true);
			}
			
			this.removeAllItems();
			this.addItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
