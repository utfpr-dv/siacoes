package br.edu.utfpr.dv.siacoes.model;

public class Country {
	
	private int idCountry;
	private String name;
	
	public Country(){
		this.setIdCountry(0);
		this.setName("");
	}
	
	public int getIdCountry() {
		return idCountry;
	}
	public void setIdCountry(int idCountry) {
		this.idCountry = idCountry;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.getName();
	}

}
