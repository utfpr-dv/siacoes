package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;

public class JuryAppraiserGradeDataSource {

	private String evaluationItem;
	private double ponderosity;
	private double score;
	
	public JuryAppraiserGradeDataSource(JuryFormAppraiserDetailReport score) {
		this.setEvaluationItem(score.getEvaluationItem());
		this.setPonderosity(score.getPonderosity());
		this.setScore(score.getScore());
	}
	
	public static List<JuryAppraiserGradeDataSource> load(List<JuryFormAppraiserDetailReport> list) {
		List<JuryAppraiserGradeDataSource> ret = new ArrayList<JuryAppraiserGradeDataSource>();
		
		for(JuryFormAppraiserDetailReport score : list) {
			ret.add(new JuryAppraiserGradeDataSource(score));
		}
		
		return ret;
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
	
}
