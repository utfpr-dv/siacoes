package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;

public class ActivitySubmissionItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idActivitySubmissionItem;
	private Activity activity;
	private ActivitySubmission submission;
	private ActivityFeedback feedback;
	
	public ActivitySubmissionItem(){
		this.setIdActivitySubmissionItem(0);
		this.setActivity(new Activity());
		this.setSubmission(new ActivitySubmission());
		this.setFeedback(ActivityFeedback.NONE);
	}
	
	public int getIdActivitySubmissionItem() {
		return idActivitySubmissionItem;
	}
	public void setIdActivitySubmissionItem(int idActivitySubmissionItem) {
		this.idActivitySubmissionItem = idActivitySubmissionItem;
	}
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public ActivitySubmission getSubmission() {
		return submission;
	}
	public void setSubmission(ActivitySubmission submission) {
		this.submission = submission;
	}
	public ActivityFeedback getFeedback() {
		return feedback;
	}
	public void setFeedback(ActivityFeedback feedback) {
		this.feedback = feedback;
	}
	
}
