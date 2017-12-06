package br.edu.utfpr.dv.siacoes.components;

import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class YearField extends TextField {
	
	public YearField(){
		super("Ano");
		this.setMaxLength(4);
		this.setWidth("100px");
		this.setYear(DateUtils.getYear());
	}
	
	public int getYear(){
		try{
			return Integer.parseInt(this.getValue());
		}catch(Exception e){
			return 0;
		}
	}
	
	public void setYear(int year){
		this.setValue(String.valueOf(year));
	}

}
