package br.edu.utfpr.dv.siacoes.ui.components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;

public class ByteSizeField extends VerticalLayout {

	public enum Unit{
		BYTE(1), KBYTE(1024), MBYTE(1024 * 1024), GBYTE(1024 * 1024 * 1024);
		
		private final int value; 
		Unit(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public String toString(){
			switch(this){
				case BYTE:
					return "Bytes";
				case KBYTE:
					return "KB";
				case MBYTE:
					return "MB";
				case GBYTE:
					return "GB";
				default:
					return "";
			}
		}
	}
	
	private final Label labelCaption;
	private final NumberField textSize;
	private final Select<Unit> comboUnit;
	
	public ByteSizeField() {
		this.labelCaption = new Label();
		
		this.textSize = new NumberField();
		this.textSize.setWidth("100px");
		this.textSize.setLabel(null);
		
		this.comboUnit = new Select<Unit>();
		this.comboUnit.setItems(Unit.BYTE, Unit.KBYTE, Unit.MBYTE, Unit.GBYTE);
		this.comboUnit.setValue(Unit.BYTE);
		this.comboUnit.setWidth("100px");
		this.comboUnit.setLabel(null);
		
		HorizontalLayout hl = new HorizontalLayout(this.textSize, this.comboUnit);
		hl.setPadding(false);
		hl.setMargin(false);
		hl.setSpacing(false);
		
		this.add(this.labelCaption, hl);
		this.setPadding(false);
		this.setMargin(false);
		this.setSpacing(false);
	}
	
	public ByteSizeField(String caption) {
		this();
		
		this.labelCaption.setText(caption);
	}
	
	public void setValue(long bytes) {
		if(bytes >= Unit.GBYTE.getValue()) {
			this.comboUnit.setValue(Unit.GBYTE);
			this.textSize.setValue((double)bytes / Unit.GBYTE.getValue());
		} else if(bytes >= Unit.MBYTE.getValue()) {
			this.comboUnit.setValue(Unit.MBYTE);
			this.textSize.setValue((double)bytes / Unit.MBYTE.getValue());
		} else if(bytes >= Unit.KBYTE.getValue()) {
			this.comboUnit.setValue(Unit.KBYTE);
			this.textSize.setValue((double)bytes / Unit.KBYTE.getValue());
		} else {
			this.comboUnit.setValue(Unit.BYTE);
			this.textSize.setValue((double)bytes);
		}
	}
	
	public void setValue(double value, Unit unit) {
		this.textSize.setValue(value);
		this.comboUnit.setValue(unit);
	}
	
	public long getValue() {
		try{
			return (long)(this.textSize.getValue() * this.comboUnit.getValue().getValue());
		}catch(Exception e){
			return 0;
		}
	}
	
	public String getValueAsString() {
		return this.textSize.getValue() + " " + this.comboUnit.getValue().toString();
	}
	
	public static String getSizeAsString(long bytes) {
		if(bytes >= Unit.GBYTE.getValue()) {
			return String.valueOf((double)bytes / Unit.GBYTE.getValue()) + " " + Unit.GBYTE.toString();
		} else if(bytes >= Unit.MBYTE.getValue()) {
			return String.valueOf((double)bytes / Unit.MBYTE.getValue()) + " " + Unit.MBYTE.toString();
		} else if(bytes >= Unit.KBYTE.getValue()) {
			return String.valueOf((double)bytes / Unit.KBYTE.getValue()) + " " + Unit.KBYTE.toString();
		} else {
			return String.valueOf(bytes) + " " + Unit.BYTE.toString();
		}
	}
	
}
