package br.edu.utfpr.dv.siacoes.model;

import java.util.ArrayList;
import java.util.List;

public class ThesisFormat {

	private int idThesisFormat;
	private Department department;
	private String description;
	private boolean active;
	private List<EvaluationItem> items;
	
	public ThesisFormat() {
		this.setIdThesisFormat(0);
		this.setDepartment(new Department());
		this.setDescription("");
		this.setActive(true);
		this.setItems(new ArrayList<EvaluationItem>());
	}
	
	public int getIdThesisFormat() {
		return idThesisFormat;
	}
	public void setIdThesisFormat(int idThesisFormat) {
		this.idThesisFormat = idThesisFormat;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public List<EvaluationItem> getItems() {
		return items;
	}
	public void setItems(List<EvaluationItem> items) {
		this.items = items;
	}
	
}
