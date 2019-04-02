package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ActivityValidationReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idActivity;
	private String description;
	private int group;
	private int submitted;
	private int validated;
	
	public ActivityValidationReport(){
		this.setIdActivity(0);
		this.setDescription("");
		this.setGroup(0);
		this.setSubmitted(0);
		this.setValidated(0);
	}
	
	public int getIdActivity() {
		return idActivity;
	}
	public void setIdActivity(int idActivity) {
		this.idActivity = idActivity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getSubmitted() {
		return submitted;
	}
	public void setSubmitted(int submitted) {
		this.submitted = submitted;
	}
	public int getValidated() {
		return validated;
	}
	public void setValidated(int validated) {
		this.validated = validated;
	}
	public float getPercentageValidate() {
		BigDecimal bd = new BigDecimal(((float)this.getValidated() / this.getSubmitted()) * 100);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.floatValue();
	}

}
