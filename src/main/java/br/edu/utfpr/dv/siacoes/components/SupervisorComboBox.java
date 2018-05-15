package br.edu.utfpr.dv.siacoes.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.ComboBox;

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.model.User;

public class SupervisorComboBox extends ComboBox {
	
	private List<User> list;
	private int idCampus;
	private int idDepartment;
	private SupervisorFilter filter;
	
	public SupervisorComboBox(String caption, int idDepartment, SupervisorFilter filter){
		super(caption);
		this.setIdDepartment(idDepartment);
		this.setFilter(filter);
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
	
	public int getIdDepartment() {
		return idDepartment;
	}

	public void setIdDepartment(int idDepartment) {
		this.idDepartment = idDepartment;
		if(idDepartment > 0) {
			try {
				this.setIdCampus(new CampusBO().findByDepartment(idDepartment).getIdCampus());
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		} else {
			this.setIdCampus(0);
		}
	}

	public SupervisorFilter getFilter() {
		return filter;
	}

	public void setFilter(SupervisorFilter filter) {
		this.filter = filter;
		this.loadComboProfessor();
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
			
			if(this.getFilter() == SupervisorFilter.DEPARTMENT) {
				this.list = bo.listSupervisorsByDepartment(this.getIdDepartment(), true);
			} else if(this.getFilter() == SupervisorFilter.CAMPUS) {
				this.list = bo.listSupervisorsByCampus(this.getIdCampus(), true);
			} else if(this.getFilter() == SupervisorFilter.INSTITUTION) {
				this.list = bo.listInstitutionalSupervisors(true);
			} else {
				this.list = bo.listAllSupervisors(true, false);
			}
			
			this.removeAllItems();
			this.addItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
