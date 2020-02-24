package br.edu.utfpr.dv.siacoes.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;

public class AuthDocumentView extends CustomComponent implements View {
	
	public static final String NAME = "authdocument";
	
	public AuthDocumentView() {
		
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if(event.getParameters() != null){
			String guid = event.getParameters();
			
			if(!guid.isEmpty()){
				//this.textGuid.setValue(guid);
				
				//this.validate();	
			}
		}
	}

}
