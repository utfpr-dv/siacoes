package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.util.StringUtils;

public class SigetConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
	
	public enum AttendanceFrequency {
		WEEKLY(0), BIWEEKLY(1), MONTHLY(2), BIMONTHLY(3), QUARTERLY(4);
		
		private final int value; 
		AttendanceFrequency(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static AttendanceFrequency valueOf(int value){
			for(AttendanceFrequency u : AttendanceFrequency.values()){
				if(u.getValue() == value){
					return u;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case WEEKLY:
					return "Semanal";
				case BIWEEKLY:
					return "Quinzenal";
				case MONTHLY:
					return "Mensal";
				case BIMONTHLY:
					return "Bimestral";
				case QUARTERLY:
					return "Trimestral";
				default:
					return "";
			}
		}
	}
	
	private Department department;
	private double minimumScore;
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
	private boolean supervisorJuryAgreement;
	private boolean validateAttendances;
	private AttendanceFrequency attendanceFrequency;
	private int maxFileSize;
	private int minimumJuryMembers;
	private int minimumJurySubstitutes;
	private int juryTimeStage1;
	private int juryTimeStage2;
	private boolean supervisorAssignsGrades;
	private boolean appraiserFillsGrades;
	private boolean useDigitalSignature;
	
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
		this.setSupervisorJuryAgreement(false);
		this.setValidateAttendances(false);
		this.setAttendanceFrequency(AttendanceFrequency.MONTHLY);
		this.setMaxFileSize(0);
		this.setMinimumJuryMembers(0);
		this.setMinimumJurySubstitutes(0);
		this.setJuryTimeStage1(0);
		this.setJuryTimeStage2(0);
		this.setSupervisorAssignsGrades(false);
		this.setUseDigitalSignature(false);
		this.setAppraiserFillsGrades(false);
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
	public boolean isSupervisorJuryAgreement() {
		return supervisorJuryAgreement;
	}
	public void setSupervisorJuryAgreement(boolean supervisorJuryAgreement) {
		this.supervisorJuryAgreement = supervisorJuryAgreement;
	}
	public boolean isValidateAttendances() {
		return validateAttendances;
	}
	public void setValidateAttendances(boolean validateAttendances) {
		this.validateAttendances = validateAttendances;
	}
	public AttendanceFrequency getAttendanceFrequency() {
		return attendanceFrequency;
	}
	public void setAttendanceFrequency(AttendanceFrequency attendanceFrequency) {
		this.attendanceFrequency = attendanceFrequency;
	}
	public int getMaxFileSize() {
		return maxFileSize;
	}
	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	public String getMaxFileSizeAsString() {
		if(this.getMaxFileSize() <= 0) {
			return "Tamanho Ilimitado";
		} else {
			return "Tam. Máx. " + StringUtils.getFormattedBytes(this.getMaxFileSize());
		}
	}
	public int getMinimumJuryMembers() {
		return minimumJuryMembers;
	}
	public void setMinimumJuryMembers(int minimumJuryMembers) {
		this.minimumJuryMembers = minimumJuryMembers;
	}
	public int getMinimumJurySubstitutes() {
		return minimumJurySubstitutes;
	}
	public void setMinimumJurySubstitutes(int minimumJurySubstitutes) {
		this.minimumJurySubstitutes = minimumJurySubstitutes;
	}
	public int getJuryTimeStage1() {
		return juryTimeStage1;
	}
	public void setJuryTimeStage1(int juryTimeStage1) {
		this.juryTimeStage1 = juryTimeStage1;
	}
	public int getJuryTimeStage2() {
		return juryTimeStage2;
	}
	public void setJuryTimeStage2(int juryTimeStage2) {
		this.juryTimeStage2 = juryTimeStage2;
	}
	public boolean isSupervisorAssignsGrades() {
		return supervisorAssignsGrades;
	}
	public void setSupervisorAssignsGrades(boolean supervisorAssignsGrades) {
		this.supervisorAssignsGrades = supervisorAssignsGrades;
	}
	public boolean isUseDigitalSignature() {
		return useDigitalSignature;
	}
	public void setUseDigitalSignature(boolean useDigitalSignature) {
		this.useDigitalSignature = useDigitalSignature;
	}
	public boolean isAppraiserFillsGrades() {
		return appraiserFillsGrades;
	}
	public void setAppraiserFillsGrades(boolean appraiserFillsGrades) {
		this.appraiserFillsGrades = appraiserFillsGrades;
	}

}
