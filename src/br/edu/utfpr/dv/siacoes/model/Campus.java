package br.edu.utfpr.dv.siacoes.model;

public class Campus {
	
	private int idCampus;
	private String name;
	private String address;
	private byte[] logo;
	private boolean active;
	private String site;
	
	public Campus(){
		this.setIdCampus(0);
		this.setName("");
		this.setAddress("");
		this.setLogo(null);
		this.setActive(true);
		this.setSite("");
	}
	
	public int getIdCampus() {
		return idCampus;
	}
	public void setIdCampus(int idCampus) {
		this.idCampus = idCampus;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getSite(){
		return site;
	}
	public void setSite(String site){
		this.site = site;
	}
	
	public String toString(){
		return this.getName();
	}

}
