package br.edu.utfpr.dv.siacoes.ui.components;

import com.vaadin.flow.component.select.Select;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class MonthComboBox extends Select<br.edu.utfpr.dv.siacoes.ui.components.MonthComboBox.Month> {
	
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
		this.setLabel("Mês");
		this.setItems(Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER);
		this.setValue(Month.valueOf(DateUtils.getMonth() + 1));
		this.setWidth("150px");
	}
	
	public Month getMonth() {
		return this.getValue();
	}
	
	public void setMonth(Month month) {
		this.setValue(month);
	}

}
