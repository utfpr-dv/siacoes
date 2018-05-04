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
	private int supervisorIndication;
	private int maxTutoredStage1;
	private int maxTutoredStage2;
	private boolean requestFinalDocumentStage1;
	private String repositoryLink;
	private boolean supervisorJuryRequest;
	private boolean supervisorAgreement;
	
	public SigetConfig(){
		this.setDepartment(new Department());
		this.setMinimumScore(6);
		this.setRegisterProposal(false);
		this.setShowGradesToStudent(false);
		this.setSupervisorFilter(SupervisorFilter.DEPARTMENT);
		this.setCosupervisorFilter(SupervisorFilter.DEPARTMENT);
		this.setSupervisorIndication(0);
		this.setMaxTutoredStage1(0);
		this.setMaxTutoredStage2(0);
		this.setRequestFinalDocumentStage1(false);
		this.setRepositoryLink("");
		this.setSupervisorAgreement(false);
		this.setSupervisorJuryRequest(false);
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
	public int getSupervisorIndication() {
		return supervisorIndication;
	}
	public void setSupervisorIndication(int supervisorIndication) {
		this.supervisorIndication = supervisorIndication;
	}
	public int getMaxTutoredStage1() {
		return maxTutoredStage1;
	}
	public void setMaxTutoredStage1(int maxTutoredStage1) {
		this.maxTutoredStage1 = maxTutoredStage1;
	}
	public int getMaxTutoredStage2() {
		return maxTutoredStage2;
	}
	public void setMaxTutoredStage2(int maxTutoredStage2) {
		this.maxTutoredStage2 = maxTutoredStage2;
	}
	public boolean isRequestFinalDocumentStage1() {
		return requestFinalDocumentStage1;
	}
	public void setRequestFinalDocumentStage1(boolean requestFinalDocumentStage1) {
		this.requestFinalDocumentStage1 = requestFinalDocumentStage1;
	}
	public String getRepositoryLink() {
		return repositoryLink;
	}
	public void setRepositoryLink(String repositoryLink) {
		this.repositoryLink = repositoryLink;
	}
	public boolean isSupervisorJuryRequest() {
		return supervisorJuryRequest;
	}
	public void setSupervisorJuryRequest(boolean supervisorJuryRequest) {
		this.supervisorJuryRequest = supervisorJuryRequest;
	}
	public boolean isSupervisorAgreement() {
		return supervisorAgreement;
	}
	public void setSupervisorAgreement(boolean supervisorAgreement) {
		this.supervisorAgreement = supervisorAgreement;
	}

}
