package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;

public class InternshipEvaluationItem implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int idInternshipEvaluationItem;
	private String description;
	private double ponderosity;
	private boolean active;
	private int sequence;
	private EvaluationItemType type;
	private Department department;
	
	public InternshipEvaluationItem(){
		this.setIdInternshipEvaluationItem(0);
		this.setDescription("");
		this.setPonderosity(0);
		this.setActive(true);
		this.setSequence(0);
		this.setType(EvaluationItemType.WRITING);
		this.setDepartment(new Department());
	}
	
	public int getIdInternshipEvaluationItem() {
		return idInternshipEvaluationItem;
	}
	public void setIdInternshipEvaluationItem(int idInternshipEvaluationItem) {
		this.idInternshipEvaluationItem = idInternshipEvaluationItem;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getPonderosity() {
		return ponderosity;
	}
	public void setPonderosity(double ponderosity) {
		this.ponderosity = ponderosity;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getSequence(){
		return sequence;
	}
	public void setSequence(int sequence){
		this.sequence = sequence;
	}
	public EvaluationItemType getType(){
		return type;
	}
	public void setType(EvaluationItemType type){
		this.type = type;
	}
	public void setDepartment(Department department){
		this.department = department;
	}
	public Department getDepartment(){
		return department;
	}
	
}
