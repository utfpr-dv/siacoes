package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class Company implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idCompany;
	private City city;
	private String name;
	private String cnpj;
	private String phone;
	private String email;
	private String agreement;
	
	public Company(){
		this.setIdCompany(0);
		this.setCity(new City());
		this.setName("");
		this.setCnpj("");
		this.setPhone("");
		this.setEmail("");
		this.setAgreement("");
	}
	
	public int getIdCompany() {
		return idCompany;
	}
	public void setIdCompany(int idCompany) {
		this.idCompany = idCompany;
	}
	public City getCity() {
		return city;
	}
	public void setCity(City city) {
		this.city = city;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAgreement() {
		return agreement;
	}
	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}
	public String toString(){
		return this.getName();
	}

}
