package br.edu.utfpr.dv.siacoes.model;

public class SigetConfig {
	
	public Department department;
	public double minimumScore;
	private boolean registerProposal;
	
	public SigetConfig(){
		this.setDepartment(new Department());
		this.setMinimumScore(6);
		this.setRegisterProposal(false);
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
	public boolean isRegisterProposal() {
		return registerProposal;
	}
	public void setRegisterProposal(boolean registerProposal) {
		this.registerProposal = registerProposal;
	}

}
