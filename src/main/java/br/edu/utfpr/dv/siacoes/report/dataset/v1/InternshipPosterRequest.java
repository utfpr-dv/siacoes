package br.edu.utfpr.dv.siacoes.report.dataset.v1;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.sign.SignDataset;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipPosterRequest extends SignDataset {
	
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String city;
	private String department;
	private String manager;
	private String articles;
	private int idStudent;
	private int idSupervisor;
	private List<InternshipPosterRequestAppraiser> appraisers;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getArticles() {
		return articles;
	}
	public void setArticles(String articles) {
		this.articles = articles;
	}
	public int getIdStudent() {
		return idStudent;
	}
	public void setIdStudent(int idStudent) {
		this.idStudent = idStudent;
	}
	public int getIdSupervisor() {
		return idSupervisor;
	}
	public void setIdSupervisor(int idSupervisor) {
		this.idSupervisor = idSupervisor;
	}
	public List<InternshipPosterRequestAppraiser> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<InternshipPosterRequestAppraiser> appraisers) {
		this.appraisers = appraisers;
	}
	public String getStudent() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdStudent()) {
				return sign.getName();
			}
		}
		
		return "";
	}
	public InputStream getStudentSignature() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdStudent()) {
				return sign.getSignature();
			}
		}
		
		return null;
	}
	public String getSupervisor() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdSupervisor()) {
				return sign.getName();
			}
		}
		
		return "";
	}
	public InputStream getSupervisorSignature() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdSupervisor()) {
				return sign.getSignature();
			}
		}
		
		return null;
	}
	public String getFormattedDate() {
		return String.valueOf(DateUtils.getDay(this.getDate()) + " de " + DateUtils.getMonthName(this.getDate()) + " de " + String.valueOf(DateUtils.getYear(this.getDate())));
	}
	
	public void addAppraiser(int idUser, String description) {
		InternshipPosterRequestAppraiser appraiser = new InternshipPosterRequestAppraiser();
		
		appraiser.setRequest(this);
		appraiser.setIdUser(idUser);
		appraiser.setDescription(description);
		
		this.getAppraisers().add(appraiser);
	}
	
	public InternshipPosterRequest() {
		this.setDate(null);
		this.setIdStudent(0);
		this.setIdSupervisor(0);
		this.setDepartment("");
		this.setManager("");
		this.setCity("");
		this.setArticles("");
		this.setAppraisers(new ArrayList<InternshipPosterRequestAppraiser>());
	}
	
	public class InternshipPosterRequestAppraiser implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private InternshipPosterRequest request;
		private int idUser;
		private String description;
		
		private InternshipPosterRequest getRequest() {
			return request;
		}
		private void setRequest(InternshipPosterRequest request) {
			this.request = request;
		}
		public int getIdUser() {
			return idUser;
		}
		public void setIdUser(int idUser) {
			this.idUser = idUser;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getName() {
			for(Signature sign : this.getRequest().getSignatures()) {
				if(sign.getIdUser() == this.getIdUser()) {
					return sign.getName();
				}
			}
			
			return "";
		}
		public InputStream getRubric() {
			for(Signature sign : this.getRequest().getSignatures()) {
				if(sign.getIdUser() == this.getIdUser()) {
					return sign.getRubric();
				}
			}
			
			return null;
		}
		
		public InternshipPosterRequestAppraiser() {
			this.setRequest(null);
			this.setIdUser(0);
			this.setDescription("");
		}
		
	}

}
