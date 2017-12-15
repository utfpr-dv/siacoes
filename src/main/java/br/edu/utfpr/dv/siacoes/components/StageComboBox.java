package br.edu.utfpr.dv.siacoes.components;

import com.vaadin.ui.NativeSelect;

public class StageComboBox extends NativeSelect {

	public StageComboBox(){
		super("TCC");
		this.init();
	}
	
	public StageComboBox(boolean showBoth){
		super("TCC");
		this.init();
		this.setShowBoth(showBoth);
	}
	
	private void init(){
		this.setNullSelectionAllowed(false);
		this.addItem(1);
		this.addItem(2);
		this.select(1);
		this.setWidth("100px");
	}
	
	public boolean isBothSelected(){
		return this.getValue().equals("Ambos");
	}
	
	public int getStage(){
		if(this.isBothSelected()) {
			return 0;
		} else {
			return (int)this.getValue();	
		}
	}
	
	public void setStage(int stage){
		this.setValue(stage);
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
