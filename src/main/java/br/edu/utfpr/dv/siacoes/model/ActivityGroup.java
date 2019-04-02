package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class ActivityGroup implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idActivityGroup;
	private String description;
	private int sequence;
	private int minimumScore;
	private int maximumScore;
	
	public ActivityGroup(){
		this.setIdActivityGroup(0);
		this.setDescription("");
		this.setSequence(0);
		this.setMinimumScore(0);
		this.setMaximumScore(0);
	}
	
	public int getIdActivityGroup() {
		return idActivityGroup;
	}
	public void setIdActivityGroup(int idActivityGroup) {
		this.idActivityGroup = idActivityGroup;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public int getMinimumScore() {
		return minimumScore;
	}
	public void setMinimumScore(int minimumScore) {
		this.minimumScore = minimumScore;
	}
	public int getMaximumScore() {
		return maximumScore;
	}
	public void setMaximumScore(int maximumScore) {
		this.maximumScore = maximumScore;
	}
	
	public String toString(){
		return this.getDescription();
	}
	
	@Override
    public int hashCode() {
        return this.getIdActivityGroup();
    }
	
	@Override
    public boolean equals(final Object object) {
        if (!(object instanceof ActivityGroup)) {
            return false;
        }else if(this.getIdActivityGroup() == ((ActivityGroup)object).getIdActivityGroup()){
        	return true;
        }else{
        	return false;
        }
    }

}
