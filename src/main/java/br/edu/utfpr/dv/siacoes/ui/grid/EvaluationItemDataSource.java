package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.EvaluationItem;
import br.edu.utfpr.dv.siacoes.model.InternshipEvaluationItem;

public class EvaluationItemDataSource extends BasicDataSource {

	private int stage;
	private String description;
	private String type;
	private double ponderosity;
	
	public EvaluationItemDataSource(InternshipEvaluationItem item) {
		this.setId(item.getIdInternshipEvaluationItem());
		this.setDescription(item.getDescription());
		this.setType(item.getType().toString());
		this.setPonderosity(item.getPonderosity());
	}
	
	public EvaluationItemDataSource(EvaluationItem item) {
		this.setId(item.getIdEvaluationItem());
		this.setStage(item.getStage());
		this.setDescription(item.getDescription());
		this.setType(item.getType().toString());
		this.setPonderosity(item.getPonderosity());
	}
	
	public static List<EvaluationItemDataSource> load(List<EvaluationItem> list) {
		List<EvaluationItemDataSource> ret = new ArrayList<EvaluationItemDataSource>();
		
		for(EvaluationItem item : list) {
			ret.add(new EvaluationItemDataSource(item));
		}
		
		return ret;
	}
	
	public static List<EvaluationItemDataSource> loadFromInternship(List<InternshipEvaluationItem> list) {
		List<EvaluationItemDataSource> ret = new ArrayList<EvaluationItemDataSource>();
		
		for(InternshipEvaluationItem item : list) {
			ret.add(new EvaluationItemDataSource(item));
		}
		
		return ret;
	}
	
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getPonderosity() {
		return ponderosity;
	}
	public void setPonderosity(double ponderosity) {
		this.ponderosity = ponderosity;
	}
	
}
