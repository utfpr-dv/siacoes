package br.edu.utfpr.dv.siacoes.ui.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.combobox.ComboBox;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.User;

public class StudentComboBox extends ComboBox<User> {
	
	private List<User> list;
	
	public StudentComboBox(String caption){
		this.setLabel(caption);
		this.setWidth("400px");
		this.setItemLabelGenerator(User::getName);
		this.loadComboStudent();
	}
	
	public StudentComboBox(String caption, int idSupervisor){
		this.setLabel(caption);
		this.setWidth("400px");
		this.setItemLabelGenerator(User::getName);
		this.loadComboStudent(idSupervisor);
	}
	
	public User getStudent(){
		return this.getValue();
	}
	
	public void setStudent(User c){
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
				
				//this.addItem(user);
				this.setValue(user);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void loadComboStudent(){
		try {
			UserBO bo = new UserBO();
			this.list = bo.listAllStudents(true);
			
			this.setItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private void loadComboStudent(int idSupervisor){
		try {
			UserBO bo = new UserBO();
			this.list = bo.listStudentBySupervisor(idSupervisor);
			
			this.setItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
