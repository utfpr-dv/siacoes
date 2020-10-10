package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ActivityUnit;

public class ActivityUnitDataSource extends BasicDataSource {
	
	private String description;
	private String fillAmount;
	
	public ActivityUnitDataSource(ActivityUnit unit) {
		this.setId(unit.getIdActivityUnit());
		this.setDescription(unit.getDescription());
		this.setFillAmount(unit.isFillAmount() ? "Sim" : "Não");
	}
	
	public static List<ActivityUnitDataSource> load(List<ActivityUnit> list) {
		List<ActivityUnitDataSource> ret = new ArrayList<ActivityUnitDataSource>();
		
		for(ActivityUnit unit : list) {
			ret.add(new ActivityUnitDataSource(unit));
		}
		
		return ret;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFillAmount() {
		return fillAmount;
	}
	public void setFillAmount(String fillAmount) {
		this.fillAmount = fillAmount;
	}

}
