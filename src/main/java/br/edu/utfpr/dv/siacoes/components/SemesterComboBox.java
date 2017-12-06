package br.edu.utfpr.dv.siacoes.components;

import com.vaadin.ui.NativeSelect;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class SemesterComboBox extends NativeSelect {

	public SemesterComboBox(){
		super("Semestre");
		this.init();
	}
	
	public SemesterComboBox(boolean showBoth){
		super("Semestre");
		this.init();
	}
	
	private void init(){
		this.setNullSelectionAllowed(false);
		this.addItem(1);
		this.addItem(2);
		this.select(1);
		this.setWidth("100px");
		this.setSemester(DateUtils.getSemester());
	}
	
	public int getSemester(){
		return (int)this.getValue();
	}
	
	public void setSemester(int semester){
		this.setValue(semester);
	}
	
	public void selectBoth(){
		if(this.isShowingBoth()){
			this.setValue("Ambos");
		}
	}
	
	public void setShowBoth(boolean showBoth){
		if(showBoth && !this.isShowingBoth()){
			this.addItem("Ambos");
		}else{
			this.removeItem("Ambos");
		}
	}
	
	public boolean isShowingBoth(){
		return this.getItemIds().contains("Ambos");
	}
	
}
