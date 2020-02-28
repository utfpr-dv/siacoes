package br.edu.utfpr.dv.siacoes.report.dataset.v1;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;
import br.edu.utfpr.dv.siacoes.sign.SignDataset;

public class Jury extends SignDataset {
	
	private static final long serialVersionUID = 1L;
	
	private int stage;
	private String title;
	private Date date;
	private String local;
	private int idStudent;
	private String comments;
	private double score;
	private String evaluationText;
	private boolean requestFinalDocumentStage1;
	private List<JuryAppraiser> appraisers;
	
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getEvaluationText() {
		return evaluationText;
	}
	public void setEvaluationText(String evaluationText) {
		this.evaluationText = evaluationText;
	}
	public boolean isRequestFinalDocumentStage1() {
		return requestFinalDocumentStage1;
	}
	public void setRequestFinalDocumentStage1(boolean requestFinalDocumentStage1) {
		this.requestFinalDocumentStage1 = requestFinalDocumentStage1;
	}
	
	public List<JuryAppraiser> getAppraisers() {
		return appraisers;
	}
	public void setAppraisers(List<JuryAppraiser> appraisers) {
		this.appraisers = appraisers;
	}
	
	public Jury() {
		this.setStage(1);
		this.setTitle("");
		this.setDate(null);
		this.setLocal("");
		this.setComments("");
		this.setIdStudent(0);
		this.setScore(0);
		this.setEvaluationText("");
		this.setRequestFinalDocumentStage1(false);
		this.setAppraisers(new ArrayList<JuryAppraiser>());
	}
	
	public void addAppraiser(int idUser, String description, double scoreWriting, double scoreOral, double scoreArgumentation, double score, String comments, List<JuryFormAppraiserDetailReport> details) {
		JuryAppraiser appraiser = new JuryAppraiser();
		
		appraiser.setJury(this);
		appraiser.setIdUser(idUser);
		appraiser.setDescription(description);
		appraiser.setComments(comments);
		appraiser.setScoreWriting(scoreWriting);
		appraiser.setScoreOral(scoreOral);
		appraiser.setScoreArgumentation(scoreArgumentation);
		appraiser.setScore(score);
		
		for(JuryFormAppraiserDetailReport d : details) {
			appraiser.addScore(d.getOrder(), d.getEvaluationItemType(), d.getEvaluationItem(), d.getPonderosity(), d.getScore());
		}
		
		this.getAppraisers().add(appraiser);
	}
	
	public class JuryAppraiser implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private Jury jury;
		private int idUser;
		private String description;
		private String comments;
		private double scoreWriting;
		private double scoreOral;
		private double scoreArgumentation;
		private double score;
		private List<JuryAppraiserDetail> appraiserDetails;
		
		private Jury getJury() {
			return jury;
		}
		private void setJury(Jury jury) {
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
		public int getStage() {
			return this.getJury().getStage();
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
		public double getScoreWriting() {
			return scoreWriting;
		}
		public void setScoreWriting(double scoreWriting) {
			this.scoreWriting = scoreWriting;
		}
		public double getScoreOral() {
			return scoreOral;
		}
		public void setScoreOral(double scoreOral) {
			this.scoreOral = scoreOral;
		}
		public double getScoreArgumentation() {
			return scoreArgumentation;
		}
		public void setScoreArgumentation(double scoreArgumentation) {
			this.scoreArgumentation = scoreArgumentation;
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
		
		public JuryAppraiser() {
			this.setJury(null);
			this.setIdUser(0);
			this.setDescription("");
			this.setComments("");
			this.setScoreWriting(0);
			this.setScoreOral(0);
			this.setScoreArgumentation(0);
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
