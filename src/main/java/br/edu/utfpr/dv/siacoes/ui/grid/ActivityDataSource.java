package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Activity;

public class ActivityDataSource extends BasicDataSource {
	
	private int group;
	private String description;
	private double score;
	private String unit;
	
	public ActivityDataSource(Activity activity) {
		this.setId(activity.getIdActivity());
		this.setGroup(activity.getGroup().getSequence());
		this.setDescription(activity.getDescription());
		this.setScore(activity.getScore());
		this.setUnit(activity.getUnit().getDescription());
	}
	
	public static List<ActivityDataSource> load(List<Activity> list) {
		List<ActivityDataSource> ret = new ArrayList<ActivityDataSource>();
		
		for(Activity activity : list) {
			ret.add(new ActivityDataSource(activity));
		}
		
		return ret;
	}
	
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
