package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;

public class JuryRequestDataSource extends BasicDataSource {

	private Date date;
	private String local;
	private int stage;
	private String student;
	private String chair;
	private String confirmed;
	
	public JuryRequestDataSource(JuryRequest request) {
		this.setId(request.getIdJuryRequest());
		this.setDate(request.getDate());
		this.setLocal(request.getLocal());
		this.setStage(request.getStage());
		this.setStudent(request.getStudent());
		this.setConfirmed(request.isConfirmed() ? "Sim" : "Não");
		
		for(JuryAppraiserRequest appraiser : request.getAppraisers()) {
			if(appraiser.isChair()) {
				this.setChair(appraiser.getAppraiser().getName());
			}
		}
	}
	
	public static List<JuryRequestDataSource> load(List<JuryRequest> list, int stage) {
		List<JuryRequestDataSource> ret = new ArrayList<JuryRequestDataSource>();
		
		for(JuryRequest request : list) {
			if((stage == 0) || (stage == request.getStage())) {
				ret.add(new JuryRequestDataSource(request));
			}
		}
		
		return ret;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getChair() {
		return chair;
	}
	public void setChair(String chair) {
		this.chair = chair;
	}
	public String getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(String confirmed) {
		this.confirmed = confirmed;
	}
	
}
