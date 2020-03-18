package br.edu.utfpr.dv.siacoes.report.dataset.v1;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;
import br.edu.utfpr.dv.siacoes.model.Jury.JuryResult;
import br.edu.utfpr.dv.siacoes.sign.SignDataset;

public class InternshipJury extends SignDataset {
	
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String title;
	private String local;
	private int idStudent;
	private int idSupervisor;
	private int idAppraiser1;
	private int idAppraiser2;
	private String comments;
	private double finalScore;
	private double companySupervisorScore;
	private double companySupervisorPonderosity;
	private double supervisorPonderosity;
	private double appraisersPonderosity;
	private double supervisorScore;
	private String company;
	private JuryResult result;
	private List<InternshipJuryAppraiser> appraisers;
	private List<InternshipJuryAppraiserSignature> appraisersSignature;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public int getIdStudent() {
		return idStudent;
	}
	public void setIdStudent(int idStudent) {
		this.idStudent = idStudent;
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
	public int getIdSupervisor() {
		return idSupervisor;
	}
	public void setIdSupervisor(int idSupervisor) {
		this.idSupervisor = idSupervisor;
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
	public int getIdAppraiser1() {
		return idAppraiser1;
	}
	public void setIdAppraiser1(int idAppraiser1) {
		this.idAppraiser1 = idAppraiser1;
	}
	public double getAppraiser1Score() {
		for(InternshipJuryAppraiser appraiser : this.getAppraisers()) {
			if(appraiser.getIdUser() == this.getIdAppraiser1()) {
				return appraiser.getScore();
			}
		}
		
		return 0;
	}
	public int getIdAppraiser2() {
		return idAppraiser2;
	}
	public void setIdAppraiser2(int idAppraiser2) {
		this.idAppraiser2 = idAppraiser2;
	}
	public double getAppraiser2Score() {
		for(InternshipJuryAppraiser appraiser : this.getAppraisers()) {
			if(appraiser.getIdUser() == this.getIdAppraiser2()) {
				return appraiser.getScore();
			}
		}
		
		return 0;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public double getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(double finalScore) {
		this.finalScore = finalScore;
	}
	public double getCompanySupervisorScore() {
		return companySupervisorScore;
	}
	public void setCompanySupervisorScore(double companySupervisorScore) {
		this.companySupervisorScore = companySupervisorScore;
	}
	public double getCompanySupervisorPonderosity() {
		return companySupervisorPonderosity;
	}
	public void setCompanySupervisorPonderosity(double companySupervisorPonderosity) {
		this.companySupervisorPonderosity = companySupervisorPonderosity;
	}
	public double getSupervisorPonderosity() {
		return supervisorPonderosity;
	}
	public void setSupervisorPonderosity(double supervisorPonderosity) {
		this.supervisorPonderosity = supervisorPonderosity;
	}
	public double getAppraisersPonderosity() {
		return appraisersPonderosity;
	}
	public void setAppraisersPonderosity(double appraisersPonderosity) {
		this.appraisersPonderosity = appraisersPonderosity;
	}
	public double getSupervisorScore() {
		return supervisorScore;
	}
	public void setSupervisorScore(double supervisorScore) {
		this.supervisorScore = supervisorScore;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public JuryResult getResult() {
		return result;
	}
	public void setResult(JuryResult result) {
		this.result = result;
	}
	public List<InternshipJuryAppraiser> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<InternshipJuryAppraiser> appraisers) {
		this.appraisers = appraisers;
	}
	public List<InternshipJuryAppraiserSignature> getAppraisersSignature() {
		return appraisersSignature;
	}
	public void setAppraisersSignature(List<InternshipJuryAppraiserSignature> appraisersSignature) {
		this.appraisersSignature = appraisersSignature;
	}
	
	public InternshipJury() {
		this.setTitle("");
		this.setCompany("");
		this.setDate(null);
		this.setLocal("");
		this.setComments("");
		this.setIdStudent(0);
		this.setIdSupervisor(0);
		this.setFinalScore(0);
		this.setCompanySupervisorPonderosity(0);
		this.setSupervisorPonderosity(0);
		this.setAppraisersPonderosity(0);
		this.setSupervisorScore(0);
		this.setCompanySupervisorScore(0);
		this.setAppraisers(new ArrayList<InternshipJuryAppraiser>());
		this.setAppraisersSignature(new ArrayList<InternshipJuryAppraiserSignature>());
	}
	
	public void addAppraiser(int idUser, String description, double score, String comments, List<JuryFormAppraiserDetailReport> details) {
		InternshipJuryAppraiser appraiser = new InternshipJuryAppraiser();
		
		appraiser.setJury(this);
		appraiser.setIdUser(idUser);
		appraiser.setDescription(description);
		appraiser.setComments(comments);
		appraiser.setScore(score);
		
		for(JuryFormAppraiserDetailReport d : details) {
			appraiser.addScore(d.getOrder(), d.getEvaluationItemType(), d.getEvaluationItem(), d.getPonderosity(), d.getScore());
		}
		
		this.getAppraisers().add(appraiser);
		this.addAppraiserSignature(idUser, description);
	}
	
	private void addAppraiserSignature(int idUser, String description) {
		InternshipJuryAppraiserSignature appraiser = new InternshipJuryAppraiserSignature();
		
		appraiser.setJury(this);
		appraiser.setIdUser(idUser);
		appraiser.setDescription(description);
		
		this.getAppraisersSignature().add(appraiser);
	}
	
	public void addSupervisorSignature() {
		InternshipJuryAppraiserSignature appraiser = new InternshipJuryAppraiserSignature();
		
		appraiser.setJury(this);
		appraiser.setIdUser(this.getIdSupervisor());
		appraiser.setDescription("Orientador");
		
		this.getAppraisersSignature().add(0, appraiser);
	}
	
	public class InternshipJuryAppraiserSignature implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private InternshipJury jury;
		private int idUser;
		private String description;
		
		public InternshipJury getJury() {
			return jury;
		}
		public void setJury(InternshipJury jury) {
			this.jury = jury;
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
			for(Signature sign : this.getJury().getSignatures()) {
				if(sign.getIdUser() == this.getIdUser()) {
					return sign.getName();
				}
			}
			
			return "";
		}
		public InputStream getRubric() {
			for(Signature sign : this.getJury().getSignatures()) {
				if(sign.getIdUser() == this.getIdUser()) {
					return sign.getRubric();
				}
			}
			
			return null;
		}
		public InputStream getSignature() {
			for(Signature sign : this.getJury().getSignatures()) {
				if(sign.getIdUser() == this.getIdUser()) {
					return sign.getSignature();
				}
			}
			
			return null;
		}
		
		public InternshipJuryAppraiserSignature() {
			this.setJury(null);
			this.setIdUser(0);
			this.setDescription("");
		}
		
	}
	
	public class InternshipJuryAppraiser implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private InternshipJury jury;
		private int idUser;
		private String description;
		private String comments;
		private double score;
		private List<JuryAppraiserDetail> appraiserDetails;
		
		public InternshipJury getJury() {
			return jury;
		}
		public void setJury(InternshipJury jury) {
			this.jury = jury;
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
			for(Signature sign : this.getJury().getSignatures()) {
				if(sign.getIdUser() == this.getIdUser()) {
					return sign.getName();
				}
			}
			
			return "";
		}
		public InputStream getRubric() {
			for(Signature sign : this.getJury().getSignatures()) {
				if(sign.getIdUser() == this.getIdUser()) {
					return sign.getRubric();
				}
			}
			
			return null;
		}
		public InputStream getSignature() {
			for(Signature sign : this.getJury().getSignatures()) {
				if(sign.getIdUser() == this.getIdUser()) {
					return sign.getSignature();
				}
			}
			
			return null;
		}
		public String getTitle() {
			return this.getJury().getTitle();
		}
		public String getLocal() {
			return this.getJury().getLocal();
		}
		public Date getDate() {
			return this.getJury().getDate();
		}
		public String getStudent() {
			return this.getJury().getStudent();
		}
		public String getCompany() {
			return this.getJury().getCompany();
		}
		public boolean isUseDigitalSignature() {
			return this.getJury().isUseDigitalSignature();
		}
		public String getComments() {
			return comments;
		}
		public void setComments(String comments) {
			this.comments = comments;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}
		public List<JuryAppraiserDetail> getAppraiserDetails() {
			return appraiserDetails;
		}
		public void setAppraiserDetails(List<JuryAppraiserDetail> appraiserDetails) {
			this.appraiserDetails = appraiserDetails;
		}
		
		public InternshipJuryAppraiser() {
			this.setJury(null);
			this.setIdUser(0);
			this.setDescription("");
			this.setComments("");
			this.setScore(0);
			this.setAppraiserDetails(new ArrayList<JuryAppraiserDetail>());
		}
		
		public void addScore(int order, String evaluationItemType, String evaluationItem, double ponderosity, double score) {
			JuryAppraiserDetail detail = new JuryAppraiserDetail();
			
			detail.setOrder(order);
			detail.setEvaluationItemType(evaluationItemType);
			detail.setEvaluationItem(evaluationItem);
			detail.setPonderosity(ponderosity);
			detail.setScore(score);
			
			this.getAppraiserDetails().add(detail);
		}
		
		public class JuryAppraiserDetail implements Serializable {
			
			private static final long serialVersionUID = 1L;
			
			private String evaluationItemType;
			private int order;
			private String evaluationItem;
			private double ponderosity;
			private double score;
			
			public String getEvaluationItemType() {
				return evaluationItemType;
			}
			public void setEvaluationItemType(String evaluationItemType) {
				this.evaluationItemType = evaluationItemType;
			}
			public int getOrder() {
				return order;
			}
			public void setOrder(int order) {
				this.order = order;
			}
			public String getEvaluationItem() {
				return evaluationItem;
			}
			public void setEvaluationItem(String evaluationItem) {
				this.evaluationItem = evaluationItem;
			}
			public double getPonderosity() {
				return ponderosity;
			}
			public void setPonderosity(double ponderosity) {
				this.ponderosity = ponderosity;
			}
			public double getScore() {
				return score;
			}
			public void setScore(double score) {
				this.score = score;
			}
			
			public JuryAppraiserDetail() {
				this.setEvaluationItemType("");
				this.setEvaluationItem("");
				this.setOrder(0);
				this.setPonderosity(0);
				this.setScore(0);
			}
			
		}
		
	}

}
