package br.edu.utfpr.dv.siacoes.ui.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.select.Select;

import br.edu.utfpr.dv.siacoes.bo.ThesisFormatBO;
import br.edu.utfpr.dv.siacoes.model.ThesisFormat;

public class ThesisFormatComboBox extends Select<ThesisFormat> {
	
	private List<ThesisFormat> list;
	private int idDepartment;
	
	public ThesisFormatComboBox(String caption, int idDepartment) {
		this.setLabel(caption);
		this.setIdDepartment(idDepartment);
		this.setWidth("400px");
		this.setItemLabelGenerator(ThesisFormat::getDescription);
		this.loadComboFormat();
	}
	
	public int getIdDepartment() {
		return idDepartment;
	}

	public void setIdDepartment(int idDepartment) {
		this.idDepartment = idDepartment;
	}
	
	public ThesisFormat getFormat() {
		return this.getValue();
	}
	
	public void setFormat(ThesisFormat format) {
		if(format == null){
			this.setValue(null);
			return;
		}
		
		boolean find = false;
		
		for(ThesisFormat f : this.list){
			if(format.getIdThesisFormat() == f.getIdThesisFormat()){
				this.setValue(f);
				find = true;
				break;
			}
		}
		
		if(!find) {
			try {
				ThesisFormatBO bo = new ThesisFormatBO();
				ThesisFormat f = bo.findById(format.getIdThesisFormat());
				
				this.list.add(f);
				this.setItems(this.list);
				this.setValue(f);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void loadComboFormat() {
		try {
			ThesisFormatBO bo = new ThesisFormatBO();
			this.list = bo.listByDepartment(this.getIdDepartment(), true);
			
			this.setItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
