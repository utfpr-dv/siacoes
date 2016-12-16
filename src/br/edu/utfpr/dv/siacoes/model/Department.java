package br.edu.utfpr.dv.siacoes.model;

public class Department {
	
	private int idDepartment;
	private Campus campus;
	private String name;
	private String fullName;
	private byte[] logo;
	private boolean active;
	private double sigacMinimumScore;
	private double sigetMinimumScore;
	private String site;
	private boolean sigetRegisterProposal;
	
	public Department(){
		this.setIdDepartment(0);
		this.setCampus(new Campus());
		this.setName("");
		this.setFullName("");
		this.setLogo(null);
		this.setActive(true);
		this.setSigacMinimumScore(70);
		this.setSigetMinimumScore(6);
		this.setSite("");
		this.setSigetRegisterProposal(false);
	}
	
	public int getIdDepartment() {
		return idDepartment;
	}
	public void setIdDepartment(int idDepartment) {
		this.idDepartment = idDepartment;
	}
	public Campus getCampus() {
		return campus;
	}
	public void setCampus(Campus campus) {
		this.campus = campus;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFullName(){
		return fullName;
	}
	public void setFullName(String fullName){
		this.fullName = fullName;
	}
	public byte[] getLogo() {
		return logo;
	}
	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public double getSigacMinimumScore(){
		return sigacMinimumScore;
	}
	public void setSigacMinimumScore(double sigacMinimumScore){
		this.sigacMinimumScore = sigacMinimumScore;
	}
	public double getSigetMinimumScore(){
		return sigetMinimumScore;
	}
	public void setSigetMinimumScore(double sigetMinimumScore){
		this.sigetMinimumScore = sigetMinimumScore;
	}
	public String getSite(){
		return site;
	}
	public void setSite(String site){
		this.site = site;
	}
	public boolean isSigetRegisterProposal(){
		return sigetRegisterProposal;
	}
	public void setSigetRegisterProposal(boolean sigetRegisterProposal){
		this.sigetRegisterProposal = sigetRegisterProposal;
	}
	
	public String toString(){
		return this.getName();
	}
	
}
