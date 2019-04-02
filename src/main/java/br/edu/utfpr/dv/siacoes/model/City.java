package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class City implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idCity;
	private State state;
	private String name;
	
	public City(){
		this.setIdCity(0);
		this.setState(new State());
		this.setName("");
	}
	
	public int getIdCity() {
		return idCity;
	}
	public void setIdCity(int idCity) {
		this.idCity = idCity;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String toString(){
		if((this.getState() == null) || (this.getState().getInitials().isEmpty())){
			return this.getName();	
		}else{
			return this.getName() + " / " + this.getState().getInitials();
		}
	}

}
