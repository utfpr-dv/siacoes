package br.edu.utfpr.dv.siacoes.model;

public class ActivityGroupStatus {
	
	private ActivityGroup group;
	private double averageScore;
	
	public ActivityGroupStatus() {
		this.setGroup(new ActivityGroup());
		this.setAverageScore(0);
	}
	
	public ActivityGroup getGroup() {
		return group;
	}
	public void setGroup(ActivityGroup group) {
		this.group = group;
	}
	public double getAverageScore() {
		return averageScore;
	}
	public void setAverageScore(double averageScore) {
		this.averageScore = averageScore;
	}

}
