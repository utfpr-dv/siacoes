package br.edu.utfpr.dv.siacoes.components;

import org.vaadin.ui.NumberField;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class YearField extends NumberField {
	
	public YearField(){
		super("Ano");
		this.setMinValue(0);
		this.setMaxValue(9999);
		this.setMaxLength(4);
		this.setNegativeAllowed(false);
		this.setDecimalAllowed(false);
		this.setGroupingUsed(false);
		this.setWidth("100px");
		this.setYear(DateUtils.getYear());
	}
	
	public int getYear(){
		return (int)this.getDoubleValueDoNotThrow();
	}
	
	public void setYear(int year){
		this.setValue(String.valueOf(year));
	}

}
