package br.edu.utfpr.dv.siacoes.model;

import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipReport {
	
	public enum ReportType{
		STUDENT(0), SUPERVISOR(1), COMPANY(2);
		
		private final int value; 
		ReportType(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static ReportType valueOf(int value){
			for(ReportType d : ReportType.values()){
				if(d.getValue() == value){
					return d;
				}
			}
			
			return null;
		}
		
		public String toString(){
			return this.getDescription();
		}
		
		public String getDescription(){
			switch(this){
				case STUDENT:
					return "Aluno";
				case SUPERVISOR:
					return "Orientador";
				case COMPANY:
					return "Supervisor";
				default:
					return "Aluno";
			}
		}
		
	}
	
	private int idInternshipReport;
	private Internship internship;
	private ReportType type;
	private byte[] report;
	private Date date;
	
	public InternshipReport(){
		this.setIdInternshipReport(0);
		this.setInternship(new Internship());
		this.setType(ReportType.STUDENT);
		this.setReport(null);
		this.setDate(DateUtils.getToday().getTime());
	}
	
	public int getIdInternshipReport() {
		return idInternshipReport;
	}
	public void setIdInternshipReport(int idInternshipReport) {
		this.idInternshipReport = idInternshipReport;
	}
	public Internship getInternship() {
		return internship;
	}
	public void setInternship(Internship internship) {
		this.internship = internship;
	}
	public ReportType getType() {
		return type;
	}
	public void setType(ReportType type) {
		this.type = type;
	}
	public byte[] getReport() {
		return report;
	}
	public void setReport(byte[] report) {
		this.report = report;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

}
