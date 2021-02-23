package br.edu.utfpr.dv.siacoes.ui.windows;

import com.vaadin.flow.component.textfield.TextArea;

public class CommentWindow extends BasicWindow {
	
	public CommentWindow(String title, String comments){
		super(title);
		
		TextArea text = new TextArea();
		text.setWidth("800px");
		text.setHeight("500px");
		text.setValue(comments);
		
		this.add(text);
	}

}
