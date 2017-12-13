package br.edu.utfpr.dv.siacoes.model;

public class SigetConfig {
	
	public Department department;
	public double minimumScore;
	private boolean registerProposal;
	private boolean showGradesToStudent;
	
	public SigetConfig(){
		this.setDepartment(new Department());
		this.setMinimumScore(6);
		this.setRegisterProposal(false);
		this.setShowGradesToStudent(false);
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
	public boolean isShowGradesToStudent() {
		return showGradesToStudent;
	}
	public void setShowGradesToStudent(boolean showGradesToStudent) {
		this.showGradesToStudent = showGradesToStudent;
	}

}
