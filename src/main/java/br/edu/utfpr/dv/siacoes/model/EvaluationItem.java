package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class EvaluationItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum EvaluationItemType{
		WRITING(0), ORAL(1), ARGUMENTATION(2);
		
		private final int value; 
		EvaluationItemType(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public String toString(){
			switch(this){
			case WRITING:
				return "Escrita";
			case ORAL:
				return "Apresentação";
			case ARGUMENTATION:
				return "Arguição";
			default:
				return "";
			}
		}
		
		public static EvaluationItemType valueOf(int value){
			for(EvaluationItemType d : EvaluationItemType.values()){
				if(d.getValue() == value){
					return d;
				}
			}
			
			return null;
		}
		
		public static EvaluationItemType fromString(String value){
			for(EvaluationItemType d : EvaluationItemType.values()){
				if(d.toString().equals(value)){
					return d;
				}
			}
			
			return null;
		}
	}

	private int idEvaluationItem;
	private String description;
	private double ponderosity;
	private int stage;
	private boolean active;
	private int sequence;
	private EvaluationItemType type;
	private Department department;
	
	public EvaluationItem(){
		this.setIdEvaluationItem(0);
		this.setDescription("");
		this.setPonderosity(0);
		this.setStage(1);
		this.setActive(true);
		this.setSequence(0);
		this.setType(EvaluationItemType.WRITING);
		this.setDepartment(new Department());
	}
	
	public int getIdEvaluationItem() {
		return idEvaluationItem;
	}
	public void setIdEvaluationItem(int idEvaluationItem) {
		this.idEvaluationItem = idEvaluationItem;
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
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
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
