package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserScoreReport;

public class JuryGradeDataSource {

	private String description;
	private String appraiser;
	private double writing;
	private double oral;
	private double argumentation;
	private double total;
	private double ponderosity;
	
	public JuryGradeDataSource() {
		this.setDescription("");
		this.setAppraiser("");
		this.setWriting(0);
		this.setOral(0);
		this.setArgumentation(0);
		this.setTotal(0);
		this.setPonderosity(0);
	}
	
	public JuryGradeDataSource(JuryFormAppraiserScoreReport score) {
		this.setDescription(score.getDescription());
		this.setAppraiser(score.getName());
		this.setWriting(score.getScoreWriting());
		this.setOral(score.getScoreOral());
		this.setArgumentation(score.getScoreArgumentation());
		this.setTotal(score.getScoreWriting() + score.getScoreOral() + score.getScoreArgumentation());
		this.setPonderosity(0);
	}
	
	public JuryGradeDataSource(JuryFormAppraiserReport score) {
		this.setDescription("Avaliador");
		this.setAppraiser(score.getName());
		this.setTotal(score.getScore());
	}
	
	public static List<JuryGradeDataSource> load(List<JuryFormAppraiserScoreReport> list) {
		List<JuryGradeDataSource> ret = new ArrayList<JuryGradeDataSource>();
		
		for(JuryFormAppraiserScoreReport score : list) {
			ret.add(new JuryGradeDataSource(score));
		}
		
		return ret;
	}
	
	public static List<JuryGradeDataSource> load(InternshipJuryFormReport report) {
		List<JuryGradeDataSource> ret = new ArrayList<JuryGradeDataSource>();
		int appraiser = 1, totalAppraisers = 0;
		
		for(JuryFormAppraiserReport score : report.getAppraisers()) {
			if(!score.getName().equals(report.getSupervisor())) {
				totalAppraisers++;
			}
		}
		
		for(JuryFormAppraiserReport score : report.getAppraisers()) {
			if(!score.getName().equals(report.getSupervisor())) {
				JuryGradeDataSource a = new JuryGradeDataSource(score);
				
				a.setDescription("Avaliador " + String.valueOf(appraiser));
				a.setAppraiser(score.getName());
				a.setPonderosity(report.getAppraisersPonderosity() / totalAppraisers);
				
				ret.add(a);
				
				appraiser++;
			}
		}
		
		JuryGradeDataSource company = new JuryGradeDataSource();
		company.setDescription("Supervisor da Empresa");
		company.setAppraiser("");
		company.setTotal(report.getCompanySupervisorScore());
		company.setPonderosity(report.getCompanySupervisorPonderosity());
		ret.add(company);
		
		JuryGradeDataSource supervisor = new JuryGradeDataSource();
		supervisor.setDescription("Orientador");
		supervisor.setAppraiser(report.getSupervisor());
		supervisor.setTotal(report.getSupervisorScore());
		supervisor.setPonderosity(report.getSupervisorPonderosity());
		ret.add(supervisor);
		
		return ret;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAppraiser() {
		return appraiser;
	}
	public void setAppraiser(String appraiser) {
		this.appraiser = appraiser;
	}
	public double getWriting() {
		return writing;
	}
	public void setWriting(double writing) {
		this.writing = writing;
	}
	public double getOral() {
		return oral;
	}
	public void setOral(double oral) {
		this.oral = oral;
	}
	public double getArgumentation() {
		return argumentation;
	}
	public void setArgumentation(double argumentation) {
		this.argumentation = argumentation;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getPonderosity() {
		return ponderosity;
	}
	public void setPonderosity(double ponderosity) {
		this.ponderosity = ponderosity;
	}
	
}
