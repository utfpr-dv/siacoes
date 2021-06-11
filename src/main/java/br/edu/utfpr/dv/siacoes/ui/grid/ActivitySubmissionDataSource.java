package br.edu.utfpr.dv.siacoes.ui.grid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;

public class ActivitySubmissionDataSource extends BasicDataSource {

	private String student;
	private int semester;
	private int year;
	private int group;
	private String activity;
	private String description;
	private String feedback;
	private double score;
	private int stage;
	
	public ActivitySubmissionDataSource(ActivitySubmission submission) {
		this.setId(submission.getIdActivitySubmission());
		this.setStudent(submission.getStudent().getName());
		this.setSemester(submission.getSemester());
		this.setYear(submission.getYear());
		this.setGroup(submission.getActivity().getGroup().getSequence());
		this.setActivity(submission.getActivity().getDescription());
		this.setDescription(submission.getDescription());
		this.setFeedback(submission.getFeedback().toString());
		this.setScore(submission.getScore());
		this.setStage(submission.getStage());
	}
	
	public static List<ActivitySubmissionDataSource> load(List<ActivitySubmission> list) {
		List<ActivitySubmissionDataSource> ret = new ArrayList<ActivitySubmissionDataSource>();
		
		for(ActivitySubmission submission : list) {
			ret.add(new ActivitySubmissionDataSource(submission));
		}
		
		return ret;
	}
	
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
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
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = this.round(score);
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	
	private double round(double value){
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}
