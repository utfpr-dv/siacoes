package br.edu.utfpr.dv.siacoes.ui.components;

import com.vaadin.flow.component.select.Select;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class SemesterComboBox extends Select<String> {
	
	private boolean showingBoth;

	public SemesterComboBox(){
		this.setLabel("Semestre");
		this.init(false);
	}
	
	public SemesterComboBox(boolean showBoth){
		this.setLabel("Semestre");
		this.init(showBoth);
	}
	
	private void init(boolean showBoth){
		this.setShowBoth(showBoth);
		this.setWidth("100px");
		this.setSemester(DateUtils.getSemester());
	}
	
	public boolean isBothSelected(){
		return this.getValue().equals("Ambos");
	}
	
	public int getSemester(){
		if(this.isBothSelected()) {
			return 0;
		} else {
			return Integer.parseInt(this.getValue());
		}
	}
	
	public void setSemester(int semester){
		this.setValue(String.valueOf(semester));
	}
	
	public void selectBoth(){
		if(this.isShowingBoth()){
			this.setValue("Ambos");
		}
	}
	
	public void setValue(int semester) {
		this.setValue(String.valueOf(semester));
	}
	
	public void setShowBoth(boolean showBoth){
		this.showingBoth = showBoth;
		
		if(showBoth){
			this.setItems("1", "2", "Ambos");
		}else{
			this.setItems("1", "2");
		}
	}
	
	public boolean isShowingBoth(){
		return this.showingBoth;
	}
	
}
