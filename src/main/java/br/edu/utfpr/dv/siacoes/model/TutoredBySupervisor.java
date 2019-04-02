package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class TutoredBySupervisor implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idSupervisor;
	private String supervisorName;
	private int semester;
	private int year;
	private int total;
	
	public TutoredBySupervisor() {
		this.setIdSupervisor(0);
		this.setSupervisorName("");
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setTotal(0);
	}
	
	public int getIdSupervisor() {
		return idSupervisor;
	}
	public void setIdSupervisor(int idSupervisor) {
		this.idSupervisor = idSupervisor;
	}
	public String getSupervisorName() {
		return supervisorName;
	}
	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	public TutoredGroupedBySupervisor getGrouped() {
		TutoredGroupedBySupervisor t = new TutoredGroupedBySupervisor();
		
		t.setIdSupervisor(this.getIdSupervisor());
		t.setSupervisorName(this.getSupervisorName());
		t.getTutored().add(this);
		
		return t;
	}
	
	public class TutoredGroupedBySupervisor{
		
		private int idSupervisor;
		private String supervisorName;
		List<TutoredBySupervisor> tutored;
		
		public TutoredGroupedBySupervisor() {
			this.setIdSupervisor(0);
			this.setSupervisorName("");
			this.setTutored(new ArrayList<TutoredBySupervisor>());
		}
		
		public int getIdSupervisor() {
			return idSupervisor;
		}
		public void setIdSupervisor(int idSupervisor) {
			this.idSupervisor = idSupervisor;
		}
		public String getSupervisorName() {
			return supervisorName;
		}
		public void setSupervisorName(String supervisorName) {
			this.supervisorName = supervisorName;
		}
		public List<TutoredBySupervisor> getTutored() {
			return tutored;
		}
		public void setTutored(List<TutoredBySupervisor> tutored) {
			this.tutored = tutored;
		}
		
	}

}
