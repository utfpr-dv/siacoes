package br.edu.utfpr.dv.siacoes.report.dataset.v1;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.sign.SignDataset;

public class InternshipJuryRequest extends SignDataset {

private static final long serialVersionUID = 1L;

	private String company;
	private Date date;
	private String local;
	private int idStudent;
	private int idSupervisor;
	private String comments;
	private List<InternshipJuryRequestAppraiser> appraisers;

	public String getCompany() {
		return this.company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getStudent() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdStudent()) {
				return sign.getName();
			}
		}

		return "";
	}
	public InputStream getStudentRubric() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdStudent()) {
				return sign.getRubric();
			}
		}

		return null;
	}
	public String getComments() {
		return comments;
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
	public void setComments(String comments) {
		this.comments = comments;
	}
	public List<InternshipJuryRequestAppraiser> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<InternshipJuryRequestAppraiser> appraisers) {
		this.appraisers = appraisers;
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

	public InternshipJuryRequest() {
		this.setCompany("");
		this.setDate(null);
		this.setLocal("");
		this.setComments("");
		this.setIdStudent(0);
		this.setIdSupervisor(0);
		this.setAppraisers(new ArrayList<InternshipJuryRequestAppraiser>());
	}

	public void addAppraiser(int idUser, String description) {
		InternshipJuryRequestAppraiser member = new InternshipJuryRequestAppraiser();

		member.setIdUser(idUser);
		member.setDescription(description);
		member.setRequest(this);

		this.getAppraisers().add(member);
	}

	public class InternshipJuryRequestAppraiser implements Serializable {

		private static final long serialVersionUID = 1L;

		private InternshipJuryRequest request;
		private int idUser;
		private String description;

		private InternshipJuryRequest getRequest() {
			return request;
		}
		private void setRequest(InternshipJuryRequest request) {
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

		public InternshipJuryRequestAppraiser() {
			this.setRequest(null);
			this.setIdUser(0);
			this.setDescription("");
		}

	}

}
