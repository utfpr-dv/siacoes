package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class ReminderConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum Frequency {
		DAILY(0), WEEKLY(1), BIWEEKLY(2), MONTHLY(3), BIMONTHLY(4), QUARTERLY(5);
		
		private final int value; 
		Frequency(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static Frequency valueOf(int value){
			for(Frequency u : Frequency.values()){
				if(u.getValue() == value){
					return u;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case DAILY:
					return "Diariamente";
				case WEEKLY:
					return "Semanalmente";
				case BIWEEKLY:
					return "Quinzenalmente";
				case MONTHLY:
					return "Mensalmente";
				case BIMONTHLY:
					return "Bimestralmente";
				case QUARTERLY:
					return "Trimestralmente";
				default:
					return "";
			}
		}
	}
	
	public enum StartType {
		BEFORE(0), AFTER(1);
		
		private final int value; 
		StartType(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static StartType valueOf(int value){
			for(StartType u : StartType.values()){
				if(u.getValue() == value){
					return u;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case BEFORE:
					return "Antes";
				case AFTER:
					return "Após";
				default:
					return "";
			}
		}
	}
	
	private int idReminderConfig;
	private Department department;
	private ReminderMessage reminder;
	private boolean enabled;
	private int startDays;
	private StartType startType;
	private Frequency frequency;
	
	public ReminderConfig() {
		this.setIdReminderConfig(0);
		this.setDepartment(new Department());
		this.setReminder(new ReminderMessage());
		this.setEnabled(false);
		this.setStartDays(0);
		this.setStartType(StartType.BEFORE);
		this.setFrequency(Frequency.DAILY);
	}
	
	public int getIdReminderConfig() {
		return idReminderConfig;
	}
	public void setIdReminderConfig(int idReminderConfig) {
		this.idReminderConfig = idReminderConfig;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public ReminderMessage getReminder() {
		return reminder;
	}
	public void setReminder(ReminderMessage reminder) {
		this.reminder = reminder;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getStartDays() {
		return startDays;
	}
	public void setStartDays(int startDays) {
		this.startDays = startDays;
	}
	public Frequency getFrequency() {
		return frequency;
	}
	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}
	public StartType getStartType() {
		return startType;
	}
	public void setStartType(StartType startType) {
		this.startType = startType;
	}

}
