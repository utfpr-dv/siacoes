package br.edu.utfpr.dv.siacoes.view;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class Error404View extends BasicView {
	
	public static final String NAME = "404";
	
	public Error404View() {
		Image image = new Image();
		image.setIcon(new ThemeResource("images/error_404.png"));
		
		Label title = new Label("Ops!");
    	title.setStyleName("Title");
    	
    	Label label = new Label("A página que você procura não foi encontrada.");
    	label.setStyleName("CenterText");
    	label.addStyleName("BoldText");
    	
    	VerticalLayout layoutMain = new VerticalLayout(image, title, label);
    	layoutMain.setSpacing(true);
    	layoutMain.setExpandRatio(image, 1);
    	layoutMain.setSizeFull();
    	layoutMain.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
    	layoutMain.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
    	layoutMain.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
    	
    	this.setContent(layoutMain);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
