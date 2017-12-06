package br.edu.utfpr.dv.siacoes.window;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

import br.edu.utfpr.dv.siacoes.model.User;

public class SupervisorAreaWindow extends Window {

	private final TextArea textResearch;
	private final Link linkLattes;
	
	public SupervisorAreaWindow(User user){
		super("Áreas de Pesquisa");
		
		this.textResearch = new TextArea();
		this.textResearch.setWidth("600px");
		this.textResearch.setHeight("400px");
		this.textResearch.setValue(user.getResearch());
		this.textResearch.setReadOnly(true);
		
		this.linkLattes = new Link(user.getLattes(), new ExternalResource(user.getLattes()));
		
		this.setContent(this.textResearch);
		
		this.setModal(true);
        this.center();
        this.setResizable(false);
	}
	
}
