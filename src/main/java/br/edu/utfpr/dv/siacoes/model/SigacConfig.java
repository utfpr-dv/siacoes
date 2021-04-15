package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.util.StringUtils;

public class SigacConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Department department;
	private double minimumScore;
	private int maxFileSize;
	private boolean useDigitalSignature;
	private boolean notifyActivityFeedback;
	
	public SigacConfig(){
		this.setDepartment(new Department());
		this.setMinimumScore(70);
		this.setMaxFileSize(0);
		this.setUseDigitalSignature(false);
	}
	
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public double getMinimumScore() {
		return minimumScore;
	}
	public void setMinimumScore(double minimumScore) {
		this.minimumScore = minimumScore;
	}
	public int getMaxFileSize() {
		return maxFileSize;
	}
	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	public String getMaxFileSizeAsString() {
		if(this.getMaxFileSize() <= 0) {
			return "Tamanho Ilimitado";
		} else {
			return "Tam. Máx. " + StringUtils.getFormattedBytes(this.getMaxFileSize());
		}
	}
	public boolean isUseDigitalSignature() {
		return useDigitalSignature;
	}
	public void setUseDigitalSignature(boolean useDigitalSignature) {
		this.useDigitalSignature = useDigitalSignature;
	}
	public boolean isNotifyActivityFeedback() {
		return notifyActivityFeedback;
	}
	public void setNotifyActivityFeedback(boolean notifyActivityFeedback) {
		this.notifyActivityFeedback = notifyActivityFeedback;
	}

}
