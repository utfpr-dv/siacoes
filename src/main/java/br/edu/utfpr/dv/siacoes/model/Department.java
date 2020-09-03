package br.edu.utfpr.dv.siacoes.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Department implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idDepartment;
	private Campus campus;
	private String name;
	private String fullName;
	private transient byte[] logo;
	private boolean active;
	private String site;
	private String initials;
	
	public Department(){
		this.setIdDepartment(0);
		this.setCampus(new Campus());
		this.setName("");
		this.setFullName("");
		this.setLogo(null);
		this.setActive(true);
		this.setSite("");
		this.setInitials("");
	}


	public boolean isActive() {
		return active;
	}

	public String toString(){
		return this.getName();
	}
	
}
