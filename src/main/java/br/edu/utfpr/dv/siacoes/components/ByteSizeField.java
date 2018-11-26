package br.edu.utfpr.dv.siacoes.components;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ByteSizeField extends VerticalLayout {

	private final Label labelCaption;
	private final TextField textSize;
	private final NativeSelect comboUnit;
	
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
	
	public ByteSizeField() {
		this.labelCaption = new Label();
		
		this.textSize = new TextField();
		this.textSize.setWidth("70px");
		this.textSize.setCaption(null);
		
		this.comboUnit = new NativeSelect();
		this.comboUnit.addItem(Unit.BYTE);
		this.comboUnit.addItem(Unit.KBYTE);
		this.comboUnit.addItem(Unit.MBYTE);
		this.comboUnit.addItem(Unit.GBYTE);
		this.comboUnit.select(Unit.BYTE);
		this.comboUnit.setNullSelectionAllowed(false);
		this.comboUnit.setWidth("70px");
		this.comboUnit.setCaption(null);
		
		HorizontalLayout hl = new HorizontalLayout(this.textSize, this.comboUnit);
		
		this.addComponent(this.labelCaption);
		this.addComponent(hl);
	}
	
	public ByteSizeField(String caption) {
		this();
		
		this.labelCaption.setCaption(caption);
	}
	
	public void setValue(long bytes) {
		if(bytes >= Unit.GBYTE.getValue()) {
			this.comboUnit.setValue(Unit.GBYTE);
			this.textSize.setValue(String.valueOf((double)bytes / Unit.GBYTE.getValue()));
		} else if(bytes >= Unit.MBYTE.getValue()) {
			this.comboUnit.setValue(Unit.MBYTE);
			this.textSize.setValue(String.valueOf((double)bytes / Unit.MBYTE.getValue()));
		} else if(bytes >= Unit.KBYTE.getValue()) {
			this.comboUnit.setValue(Unit.KBYTE);
			this.textSize.setValue(String.valueOf((double)bytes / Unit.KBYTE.getValue()));
		} else {
			this.comboUnit.setValue(Unit.BYTE);
			this.textSize.setValue(String.valueOf(bytes));
		}
	}
	
	public void setValue(double value, Unit unit) {
		this.textSize.setValue(String.valueOf(value));
		this.comboUnit.setValue(unit);
	}
	
	public long getValue() {
		try{
			return (long)(Double.parseDouble(this.textSize.getValue().replace(",", ".")) * ((Unit)this.comboUnit.getValue()).getValue());
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
