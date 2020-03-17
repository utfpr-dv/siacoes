package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

public class SigesConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum JuryFormat {
		INDIVIDUAL(0), SESSION(1);
		
		private final int value; 
		JuryFormat(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static JuryFormat valueOf(int value){
			for(JuryFormat u : JuryFormat.values()){
				if(u.getValue() == value){
					return u;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case INDIVIDUAL:
					return "Banca Individual";
				case SESSION:
					return "Apresentação de Pôster/Seminário";
				default:
					return "";
			}
		}
	}
	
	private Department department;
	private double minimumScore;
	private double supervisorPonderosity;
	private double companySupervisorPonderosity;
	private boolean showGradesToStudent;
	private SupervisorFilter supervisorFilter;
	private boolean supervisorFillJuryForm;
	private int maxFileSize;
	private int juryTime;
	private boolean fillOnlyTotalHours;
	private JuryFormat juryFormat;
	private boolean appraiserFillsGrades;
	private boolean useDigitalSignature;
	private int minimumJuryMembers;
	private int minimumJurySubstitutes;
	
	public SigesConfig(){
		this.setDepartment(new Department());
		this.setMinimumScore(6);
		this.setSupervisorPonderosity(1);
		this.setCompanySupervisorPonderosity(1);
		this.setShowGradesToStudent(false);
		this.setSupervisorFilter(SupervisorFilter.DEPARTMENT);
		this.setSupervisorFillJuryForm(false);
		this.setMaxFileSize(0);
		this.setJuryTime(0);
		this.setFillOnlyTotalHours(false);
		this.setJuryFormat(JuryFormat.INDIVIDUAL);
		this.setUseDigitalSignature(false);
		this.setAppraiserFillsGrades(false);
		this.setMinimumJuryMembers(0);
		this.setMinimumJurySubstitutes(0);
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
	public double getSupervisorPonderosity() {
		return supervisorPonderosity;
	}
	public void setSupervisorPonderosity(double supervisorPonderosity) {
		this.supervisorPonderosity = supervisorPonderosity;
	}
	public double getCompanySupervisorPonderosity() {
		return companySupervisorPonderosity;
	}
	public void setCompanySupervisorPonderosity(double companySupervisorPonderosity) {
		this.companySupervisorPonderosity = companySupervisorPonderosity;
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
	public boolean isSupervisorFillJuryForm() {
		return supervisorFillJuryForm;
	}
	public void setSupervisorFillJuryForm(boolean supervisorFillJuryForm) {
		this.supervisorFillJuryForm = supervisorFillJuryForm;
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
	public int getJuryTime() {
		return juryTime;
	}
	public void setJuryTime(int juryTime) {
		this.juryTime = juryTime;
	}
	public boolean isFillOnlyTotalHours() {
		return fillOnlyTotalHours;
	}
	public void setFillOnlyTotalHours(boolean fillOnlyTotalHours) {
		this.fillOnlyTotalHours = fillOnlyTotalHours;
	}
	public JuryFormat getJuryFormat() {
		return juryFormat;
	}
	public void setJuryFormat(JuryFormat juryFormat) {
		this.juryFormat = juryFormat;
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
	
}
