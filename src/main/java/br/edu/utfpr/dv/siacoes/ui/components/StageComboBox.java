package br.edu.utfpr.dv.siacoes.ui.components;

import com.vaadin.flow.component.select.Select;

public class StageComboBox extends Select<String> {
	
	private boolean showingBoth;

	public StageComboBox(){
		this.setLabel("TCC");
		this.init(false);
	}
	
	public StageComboBox(boolean showBoth){
		this.setLabel("TCC");
		this.init(showBoth);
	}
	
	private void init(boolean showBoth){
		this.setShowBoth(showBoth);
		this.setValue("1");
		this.setWidth("120px");
	}
	
	public boolean isBothSelected(){
		return this.getValue().equals("Ambos");
	}
	
	public int getStage(){
		if(this.isBothSelected()) {
			return 0;
		} else {
			return Integer.parseInt(this.getValue());
		}
	}
	
	public void setStage(int stage){
		this.setValue(String.valueOf(stage));
	}
	
	public void selectBoth(){
		if(this.isShowingBoth()){
			this.setValue("Ambos");
		}
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
