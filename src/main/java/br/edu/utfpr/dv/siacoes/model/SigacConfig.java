package br.edu.utfpr.dv.siacoes.model;

import br.edu.utfpr.dv.siacoes.components.ByteSizeField;

public class SigacConfig {
	
	public Department department;
	public double minimumScore;
	private int maxFileSize;
	
	public SigacConfig(){
		this.setDepartment(new Department());
		this.setMinimumScore(70);
		this.setMaxFileSize(0);
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
			return "Tam. Máx. " + ByteSizeField.getSizeAsString(this.getMaxFileSize());
		}
	}

}
