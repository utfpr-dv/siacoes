package br.edu.utfpr.dv.siacoes.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.ComboBox;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.User;

public class CompanySupervisorComboBox extends ComboBox {
	
	private List<User> list;
	private int idCompany;
	
	public CompanySupervisorComboBox(){
		super("Supervisor");
		this.setIdCompany(0);
		this.setNullSelectionAllowed(false);
		this.setWidth("400px");
		this.loadComboSupervisor();
	}
	
	public void setIdCompany(int idCompany){
		this.idCompany = idCompany;
		this.loadComboSupervisor();
	}
	
	public int getIdCompany(){
		return this.idCompany;
	}
	
	public User getSupervisor(){
		return (User)this.getValue();
	}
	
	public void setSupervisor(User c){
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
	
	private void loadComboSupervisor(){
		try {
			UserBO bo = new UserBO();
			
			if(this.getIdCompany() == 0){
				this.list = bo.listAllCompanySupervisors(true);
			}else{
				this.list = bo.listSupervisorsByCompany(this.getIdCompany(), true);
			}
			
			this.removeAllItems();
			this.addItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
