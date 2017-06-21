package br.edu.utfpr.dv.siacoes.model;

public class State {
	
	private int idState;
	private Country country;
	private String name;
	private String initials;
	
	public State(){
		this.setIdState(0);
		this.setCountry(new Country());
		this.setName("");
		this.setInitials("");
	}
	
	public int getIdState() {
		return idState;
	}
	public void setIdState(int idState) {
		this.idState = idState;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInitials() {
		return initials;
	}
	public void setInitials(String initials) {
		this.initials = initials;
	}
	public String toString(){
		return this.getName();
	}

}
