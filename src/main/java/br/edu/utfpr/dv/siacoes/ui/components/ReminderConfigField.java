package br.edu.utfpr.dv.siacoes.ui.components;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;

import br.edu.utfpr.dv.siacoes.model.ReminderConfig;
import br.edu.utfpr.dv.siacoes.model.ReminderConfig.Frequency;
import br.edu.utfpr.dv.siacoes.model.ReminderConfig.StartType;

public class ReminderConfigField extends CustomField<ReminderConfig> {
	
	private static final long serialVersionUID = 1L;
	
	private ReminderConfig config;
	
	public enum DaysMultiplier {
		DAYS(1), WEEKS(7), MONTHS(30);
		
		private final int value; 
		DaysMultiplier(int value){ 
			this.value = value; 
		}
		
		public int getValue() { 
			return value;
		}
		
		public static DaysMultiplier valueOf(int value) {
			for(DaysMultiplier u : DaysMultiplier.values()) {
				if(u.getValue() == value) {
					return u;
				}
			}
			
			return null;
		}
		
		public String toString() {
			switch(this){
				case DAYS:
					return "Dias";
				case WEEKS:
					return "Semanas";
				case MONTHS:
					return "Meses";
				default:
					return "";
			}
		}
	}
	
	private final Checkbox check;
	private final IntegerField textDays;
	private final Select<Frequency> comboFrequency;
	private final Select<StartType> comboStartType;
	private final Select<DaysMultiplier> comboDaysMultiplier;
	private final Label label;
	
	public ReminderConfigField(ReminderConfig config) {
		if(config != null) {
			this.config = config;
		} else {
			this.config = new ReminderConfig();
		}
		
		this.check = new Checkbox(this.config.getReminder().getIdReminderMessage().toString() + ", iniciando");
		
		this.textDays = new IntegerField();
		this.textDays.setWidth("50px");
		this.textDays.setLabel(null);
		
		this.comboDaysMultiplier = new Select<DaysMultiplier>();
		this.comboDaysMultiplier.setItems(DaysMultiplier.DAYS, DaysMultiplier.WEEKS, DaysMultiplier.MONTHS);
		this.comboDaysMultiplier.setWidth("125px");
		this.comboDaysMultiplier.setLabel(null);
		
		this.comboStartType = new Select<StartType>();
		this.comboStartType.setItems(StartType.BEFORE, StartType.AFTER);
		this.comboStartType.setWidth("100px");
		this.comboStartType.setLabel(null);
		
		this.label = new Label("do prazo, e relembrando");
		
		this.comboFrequency = new Select<Frequency>();
		this.comboFrequency.setItems(Frequency.DAILY, Frequency.WEEKLY, Frequency.BIWEEKLY, Frequency.MONTHLY, Frequency.BIMONTHLY, Frequency.QUARTERLY);
		this.comboFrequency.setWidth("150px");
		this.comboFrequency.setLabel(null);
		
		HorizontalLayout hl = new HorizontalLayout(this.check, this.textDays, this.comboDaysMultiplier, this.comboStartType, this.label, this.comboFrequency);
		hl.setPadding(false);
		hl.setMargin(false);
		hl.setSpacing(false);
		
		this.setValues();
		
		this.add(hl);
	}
	
	@Override
	public void setValue(ReminderConfig config) {
		this.config = config;
		this.setValues();
	}
	
	@Override
	public ReminderConfig getValue() {
		this.getValues();
		return this.config;
	}

	@Override
	protected ReminderConfig generateModelValue() {
		return this.getValue();
	}

	@Override
	protected void setPresentationValue(ReminderConfig newPresentationValue) {
		this.setValue(newPresentationValue);
	}
	
	private void setValues() {
		this.check.setValue(this.config.isEnabled());
		this.check.setLabel(this.config.getReminder().getIdReminderMessage().toString() + ", iniciando");
		if((this.config.getStartDays() >= 30) && (this.config.getStartDays() % 30 == 0)) {
			this.textDays.setValue(this.config.getStartDays() / 30);
			this.comboDaysMultiplier.setValue(DaysMultiplier.MONTHS);
		} else if((this.config.getStartDays() >= 7) && (this.config.getStartDays() % 7 == 0)) {
			this.textDays.setValue(this.config.getStartDays() / 7);
			this.comboDaysMultiplier.setValue(DaysMultiplier.WEEKS);
		} else {
			this.textDays.setValue(this.config.getStartDays());
			this.comboDaysMultiplier.setValue(DaysMultiplier.DAYS);
		}
		this.comboStartType.setValue(this.config.getStartType());
		this.comboFrequency.setValue(this.config.getFrequency());
	}
	
	private void getValues() {
		this.config.setEnabled(this.check.getValue());
		this.config.setStartDays(this.textDays.getValue() * this.comboDaysMultiplier.getValue().getValue());
		this.config.setStartType(this.comboStartType.getValue());
		this.config.setFrequency(this.comboFrequency.getValue());
	}

}
