package br.edu.utfpr.dv.siacoes.components;

import com.vaadin.ui.NativeSelect;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class MonthComboBox extends NativeSelect {
	
	public enum Month {
		JANUARY(1), FEBRUARY(2), MARCH(3), APRIL(4), MAY(5), JUNE(6), JULY(7), AUGUST(8), SEPTEMBER(9), OCTOBER(10), NOVEMBER(11), DECEMBER(12);
		
		private final int value; 
		Month(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public String toString() {
			switch(this) {
				case JANUARY:
					return "Janeiro";
				case FEBRUARY:
					return "Fevereiro";
				case MARCH:
					return "Março";
				case APRIL:
					return "Abril";
				case MAY:
					return "Maio";
				case JUNE:
					return "Junho";
				case JULY:
					return "Julho";
				case AUGUST:
					return "Agosto";
				case SEPTEMBER:
					return "Setembro";
				case OCTOBER:
					return "Outubro";
				case NOVEMBER:
					return "Novembro";
				case DECEMBER:
					return "Dezembro";
				default:
					return "";
			}
		}
		
		public static Month valueOf(int value){
			for(Month d : Month.values()){
				if(d.getValue() == value){
					return d;
				}
			}
			
			return null;
		}
	}
	
	public MonthComboBox() {
		super("Mês");
		this.setNullSelectionAllowed(false);
		this.addItem(Month.JANUARY);
		this.addItem(Month.FEBRUARY);
		this.addItem(Month.MARCH);
		this.addItem(Month.APRIL);
		this.addItem(Month.MAY);
		this.addItem(Month.JUNE);
		this.addItem(Month.JULY);
		this.addItem(Month.AUGUST);
		this.addItem(Month.SEPTEMBER);
		this.addItem(Month.OCTOBER);
		this.addItem(Month.NOVEMBER);
		this.addItem(Month.DECEMBER);
		this.select(Month.valueOf(DateUtils.getMonth() + 1));
		this.setWidth("150px");
	}
	
	public Month getMonth() {
		return (Month)this.getValue();
	}
	
	public void setMonth(Month month) {
		this.setValue(month);
	}

}
