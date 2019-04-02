package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class Activity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int idActivity;
	private ActivityGroup group;
	private ActivityUnit unit;
	private Department department;
	private String description;
	private double score;
	private double maximumInSemester;
	private boolean active;
	private int sequence;
	
	public Activity(){
		this.setIdActivity(0);
		this.setGroup(new ActivityGroup());
		this.setUnit(new ActivityUnit());
		this.setDepartment(new Department());
		this.setDescription("");
		this.setScore(0);
		this.setMaximumInSemester(0);
		this.setActive(true);
	}
	
	public int getIdActivity() {
		return idActivity;
	}
	public void setIdActivity(int idActivity) {
		this.idActivity = idActivity;
	}
	public ActivityGroup getGroup() {
		return group;
	}
	public void setGroup(ActivityGroup group) {
		this.group = group;
	}
	public ActivityUnit getUnit() {
		return unit;
	}
	public void setUnit(ActivityUnit unit) {
		this.unit = unit;
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
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getMaximumInSemester() {
		return maximumInSemester;
	}
	public void setMaximumInSemester(double maximumInSemester) {
		this.maximumInSemester = maximumInSemester;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public void setSequence(int sequence){
		this.sequence = sequence;
	}
	public int getSequence(){
		return sequence;
	}
	
	public String toString(){
		return this.getDescription();
	}
	
	@Override
    public int hashCode() {
        return this.getIdActivity();
    }
	
	@Override
    public boolean equals(final Object object) {
        if (!(object instanceof Activity)) {
            return false;
        }else if(this.getIdActivity() == ((Activity)object).getIdActivity()){
        	return true;
        }else{
        	return false;
        }
    }
	
}
