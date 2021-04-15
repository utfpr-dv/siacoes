package br.edu.utfpr.dv.siacoes.report.dataset.v1;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionDetailReport;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionFooterReport;
import br.edu.utfpr.dv.siacoes.sign.SignDataset;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class ActivityFinalSubmission extends SignDataset {
	
	private static final long serialVersionUID = 1L;
	
	private int idStudent;
	private String studentCode;
	private int registerSemester;
	private int registerYear;
	private double totalScore;
	private String situation;
	private Date date;
	private List<ActivityFinalSubmissionDetail> details;
	private List<ActivityFinalSubmissionFooter> footer;
	
	public int getIdStudent() {
		return idStudent;
	}
	public void setIdStudent(int idStudent) {
		this.idStudent = idStudent;
	}
	public String getStudentCode() {
		return studentCode;
	}
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}
	public int getRegisterSemester() {
		return registerSemester;
	}
	public void setRegisterSemester(int registerSemester) {
		this.registerSemester = registerSemester;
	}
	public int getRegisterYear() {
		return registerYear;
	}
	public void setRegisterYear(int registerYear) {
		this.registerYear = registerYear;
	}
	public double getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}
	public String getSituation(){
		return situation;
	}
	public void setSituation(String situation){
		this.situation = situation;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<ActivityFinalSubmissionDetail> getDetails() {
		return details;
	}
	public void setDetails(List<ActivityFinalSubmissionDetail> details) {
		this.details = details;
	}
	public List<ActivityFinalSubmissionFooter> getFooter() {
		return footer;
	}
	public void setFooter(List<ActivityFinalSubmissionFooter> footer) {
		this.footer = footer;
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
	
	public void addDetail(ActivitySubmissionDetailReport detail) {
		ActivityFinalSubmissionDetail d = new ActivityFinalSubmissionDetail();
		
		d.setIdActivitySubmission(detail.getIdActivitySubmission());
		d.setActivity(detail.getActivity());
		d.setGroup(detail.getGroup());
		d.setUnit(detail.getUnit());
		d.setScore(detail.getScore());
		d.setAmount(detail.getAmount());
		d.setTotal(detail.getTotal());
		d.setSemester(detail.getSemester());
		d.setYear(detail.getYear());
		
		this.getDetails().add(d);
	}
	
	public void addFooter(ActivitySubmissionFooterReport footer) {
		ActivityFinalSubmissionFooter f = new ActivityFinalSubmissionFooter();
		
		f.setIdActivityGroup(footer.getIdActivityGroup());
		f.setGroup(footer.getGroup());
		f.setTotal(footer.getTotal());
		f.setMinimum(footer.getMinimum());
		f.setMaximum(footer.getMaximum());
		f.setSituation(footer.getSituation());
		f.setSequence(footer.getSequence());
		
		this.getFooter().add(f);
	}
	
	public ActivityFinalSubmission() {
		this.setIdStudent(0);
		this.setStudentCode("");
		this.setRegisterSemester(DateUtils.getSemester());
		this.setRegisterYear(DateUtils.getYear());
		this.setDate(DateUtils.getToday().getTime());
		this.setTotalScore(0);
		this.setSituation("");
		this.setDetails(new ArrayList<ActivityFinalSubmissionDetail>());
		this.setFooter(new ArrayList<ActivityFinalSubmissionFooter>());
	}
	
	public class ActivityFinalSubmissionDetail implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private int idActivitySubmission;
		private String activity;
		private int group;
		private String unit;
		private double score;
		private double amount;
		private double total;
		private int semester;
		private int year;
		
		public ActivityFinalSubmissionDetail(){
			this.setIdActivitySubmission(0);
			this.setActivity("");
			this.setGroup(0);
			this.setUnit("");
			this.setScore(0);
			this.setAmount(0);
			this.setTotal(0);
			this.setSemester(DateUtils.getSemester());
			this.setYear(DateUtils.getYear());
		}
		
		public int getIdActivitySubmission() {
			return idActivitySubmission;
		}
		public void setIdActivitySubmission(int idActivitySubmission) {
			this.idActivitySubmission = idActivitySubmission;
		}
		public String getActivity() {
			return activity;
		}
		public void setActivity(String activity) {
			this.activity = activity;
		}
		public int getGroup() {
			return group;
		}
		public void setGroup(int group) {
			this.group = group;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public double getTotal() {
			return total;
		}
		public void setTotal(double total) {
			this.total = total;
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
		
	}
	
	public class ActivityFinalSubmissionFooter implements Serializable {
	
		private static final long serialVersionUID = 1L;
		
		private int idActivityGroup;
		private String group;
		private double total;
		private int minimum;
		private int maximum;
		private String situation;
		private int sequence;
		
		public ActivityFinalSubmissionFooter(){
			this.setIdActivityGroup(0);
			this.setGroup("");
			this.setTotal(0);
			this.setMinimum(0);
			this.setMaximum(0);
			this.setSituation("");
			this.setSequence(0);
		}
		
		public int getIdActivityGroup() {
			return idActivityGroup;
		}
		public void setIdActivityGroup(int idActivityGroup) {
			this.idActivityGroup = idActivityGroup;
		}
		public String getGroup() {
			return group;
		}
		public void setGroup(String group) {
			this.group = group;
		}
		public double getTotal() {
			return total;
		}
		public void setTotal(double total) {
			this.total = total;
		}
		public int getMinimum() {
			return minimum;
		}
		public void setMinimum(int minimum) {
			this.minimum = minimum;
		}
		public int getMaximum() {
			return maximum;
		}
		public void setMaximum(int maximum) {
			this.maximum = maximum;
		}
		public String getSituation() {
			return situation;
		}
		public void setSituation(String situation) {
			this.situation = situation;
		}
		public int getSequence() {
			return sequence;
		}
		public void setSequence(int sequence) {
			this.sequence = sequence;
		}
		
	}

}
