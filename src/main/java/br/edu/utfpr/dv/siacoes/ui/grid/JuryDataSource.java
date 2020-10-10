package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Jury;

public class JuryDataSource extends BasicDataSource {

	private Date date;
	private String local;
	private int stage;
	private String member;
	private String student;
	private String title;
	
	public JuryDataSource(Jury jury) {
		this.setId(jury.getIdJury());
		this.setDate(jury.getDate());
		this.setLocal(jury.getLocal());
		this.setStage(jury.getStage());
		this.setStudent(jury.getStudent().getName());
		this.setTitle(jury.getTitle());
	}
	
	public static List<JuryDataSource> load(List<Jury> list) {
		List<JuryDataSource> ret = new ArrayList<JuryDataSource>();
		
		for(Jury jury : list) {
			ret.add(new JuryDataSource(jury));
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
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
