package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentDataSource extends BasicDataSource {

	private String name;
	private String active;
	
	public DepartmentDataSource(Department department) {
		this.setId(department.getIdDepartment());
		this.setName(department.getName());
		this.setActive(department.isActive() ? "Sim" : "Não");
	}
	
	public static List<DepartmentDataSource> load(List<Department> list) {
		List<DepartmentDataSource> ret = new ArrayList<DepartmentDataSource>();
		
		for(Department department : list) {
			ret.add(new DepartmentDataSource(department));
		}
		
		return ret;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	
}
