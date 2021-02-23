package br.edu.utfpr.dv.siacoes.ui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.select.Select;

import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentComboBox extends Select<Department> {

	private List<Department> list;
	private int idCampus;
	
	public DepartmentComboBox(int idCampus){
		this.setLabel("Departamento/Coordenação");
		this.setWidth("400px");
		this.setIdCampus(idCampus);
		this.setItemLabelGenerator(Department::getName);
		this.loadComboDepartment();
	}
	
	public int getIdCampus(){
		return this.idCampus;
	}
	
	public void setIdCampus(int idCampus){
		this.idCampus = idCampus;
		this.loadComboDepartment();
	}
	
	public Department getDepartment(){
		return this.getValue();
	}
	
	public void setDepartment(Department c){
		if(c == null){
			this.setValue(null);
			return;
		}
		
		boolean find = false;
		
		for(Department department : this.list){
			if(c.getIdDepartment() == department.getIdDepartment()){
				this.setValue(department);
				find = true;
				break;
			}
		}
		
		if(!find){
			try {
				DepartmentBO bo = new DepartmentBO();
				Department department = bo.findById(c.getIdDepartment());
				
				if(department.getCampus().getIdCampus() == this.getIdCampus()){
					//this.addItem(department);
					this.setValue(department);
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void loadComboDepartment(){
		try {
			this.setItems(new ArrayList<Department>());
			
			if(this.getIdCampus() != 0){
				DepartmentBO bo = new DepartmentBO();
				this.list = bo.listByCampus(this.getIdCampus(), true);
				
				this.setItems(this.list);
				
				if(this.list.size() > 0){
					this.setDepartment(this.list.get(0));
				}
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
