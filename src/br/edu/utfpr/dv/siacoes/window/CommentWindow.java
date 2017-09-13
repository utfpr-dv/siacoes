package br.edu.utfpr.dv.siacoes.window;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

public class CommentWindow extends Window {
	
	public CommentWindow(String title, String comments){
		super(title);
		
		TextArea text = new TextArea();
		text.setWidth("800px");
		text.setHeight("300px");
		text.setEnabled(false);
		text.setValue(comments);
		this.setContent(text);
		
		this.setModal(true);
        this.center();
        this.setResizable(false);
	}

}
