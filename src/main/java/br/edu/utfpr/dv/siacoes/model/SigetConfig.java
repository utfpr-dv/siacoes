package br.edu.utfpr.dv.siacoes.model;

public class SigetConfig {
	
	public enum SupervisorFilter {
		DEPARTMENT(0), CAMPUS(1), INSTITUTION(2), EVERYONE(3);
		
		private final int value; 
		SupervisorFilter(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static SupervisorFilter valueOf(int value){
			for(SupervisorFilter u : SupervisorFilter.values()){
				if(u.getValue() == value){
					return u;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case DEPARTMENT:
					return "Departamento/Coordenação";
				case CAMPUS:
					return "Câmpus";
				case INSTITUTION:
					return "Instituição";
				case EVERYONE:
					return "Todos";
				default:
					return "";
			}
		}
	}
	
	public Department department;
	public double minimumScore;
	private boolean registerProposal;
	private boolean showGradesToStudent;
	private SupervisorFilter supervisorFilter;
	private SupervisorFilter cosupervisorFilter;
	
	public SigetConfig(){
		this.setDepartment(new Department());
		this.setMinimumScore(6);
		this.setRegisterProposal(false);
		this.setShowGradesToStudent(false);
		this.setSupervisorFilter(SupervisorFilter.DEPARTMENT);
		this.setCosupervisorFilter(SupervisorFilter.DEPARTMENT);
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
	public SupervisorFilter getSupervisorFilter() {
		return supervisorFilter;
	}
	public void setSupervisorFilter(SupervisorFilter supervisorFilter) {
		this.supervisorFilter = supervisorFilter;
	}
	public SupervisorFilter getCosupervisorFilter() {
		return cosupervisorFilter;
	}
	public void setCosupervisorFilter(SupervisorFilter cosupervisorFilter) {
		this.cosupervisorFilter = cosupervisorFilter;
	}

}
