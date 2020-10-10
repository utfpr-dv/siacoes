package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Campus;

public class CampusDataSource extends BasicDataSource {

	private String name;
	private String active;
	
	public CampusDataSource(Campus campus) {
		this.setId(campus.getIdCampus());
		this.setName(campus.getName());
		this.setActive(campus.isActive() ? "Sim" : "Não");
	}
	
	public static List<CampusDataSource> load(List<Campus> list) {
		List<CampusDataSource> ret = new ArrayList<CampusDataSource>();
		
		for(Campus campus : list) {
			ret.add(new CampusDataSource(campus));
		}
		
		return ret;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	
}
