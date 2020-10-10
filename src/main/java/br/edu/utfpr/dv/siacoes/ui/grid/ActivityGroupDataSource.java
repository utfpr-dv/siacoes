package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ActivityGroup;

public class ActivityGroupDataSource extends BasicDataSource {

	private String description;
	private int maximum;
	private int minimum;
	
	public ActivityGroupDataSource(ActivityGroup group) {
		this.setDescription(group.getDescription());
		this.setMaximum(group.getMaximumScore());
		this.setMinimum(group.getMinimumScore());
	}
	
	public static List<ActivityGroupDataSource> load(List<ActivityGroup> list) {
		List<ActivityGroupDataSource> ret = new ArrayList<ActivityGroupDataSource>();
		
		for(ActivityGroup group : list) {
			ret.add(new ActivityGroupDataSource(group));
		}
		
		return ret;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getMaximum() {
		return maximum;
	}
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
	public int getMinimum() {
		return minimum;
	}
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}
	
}
