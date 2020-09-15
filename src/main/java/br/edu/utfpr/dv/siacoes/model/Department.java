package br.edu.utfpr.dv.siacoes.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data @Builder
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
}
