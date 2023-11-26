package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ThesisFormat;

public class ThesisFormatDataSource extends BasicDataSource {

	private String description;
	private String active;
	
	public ThesisFormatDataSource(ThesisFormat format) {
		this.setId(format.getIdThesisFormat());
		this.setDescription(format.getDescription());
		this.setActive(format.isActive() ? "Sim" : "Não");
	}
	
	public static List<ThesisFormatDataSource> load(List<ThesisFormat> list) {
		List<ThesisFormatDataSource> ret = new ArrayList<ThesisFormatDataSource>();
		
		for(ThesisFormat item : list) {
			ret.add(new ThesisFormatDataSource(item));
		}
		
		return ret;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	
}
