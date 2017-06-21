package br.edu.utfpr.dv.siacoes.model;

public class SigacConfig {
	
	public Department department;
	public double minimumScore;
	
	public SigacConfig(){
		this.setDepartment(new Department());
		this.setMinimumScore(70);
	}
	
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public double getMinimumScore() {
		return minimumScore;
	}
	public void setMinimumScore(double minimumScore) {
		this.minimumScore = minimumScore;
	}

}
