package br.edu.utfpr.dv.siacoes.ui.components;

import com.vaadin.flow.component.textfield.IntegerField;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class YearField extends IntegerField {
	
	public YearField(){
		super("Ano");
		this.setMin(0);
		this.setMax(9999);
		this.setWidth("100px");
		this.setYear(DateUtils.getYear());
	}
	
	public int getYear(){
		try{
			return this.getValue();
		}catch(Exception e){
			return 0;
		}
	}
	
	public void setYear(int year){
		this.setValue(year);
	}

}
