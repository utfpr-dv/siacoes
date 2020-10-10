package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.ui.MainLayout;

@PageTitle("Acesso Negado")
@Route(value = "403", layout = MainLayout.class)
public class Error403View extends LoggedView {
	
	public static final String NAME = "403";
	
	public Error403View() {
		Image image = new Image();
		image.setSrc("images/403.png");
		image.setMaxHeight("500px");
		
		H1 title = new H1("You shall not pass!");
    	
    	H5 label = new H5("Você não tem acesso à página solicitada.");
    	
    	VerticalLayout layoutMain = new VerticalLayout(image, title, label);
    	layoutMain.setSpacing(false);
    	layoutMain.setMargin(false);
    	layoutMain.setPadding(false);
    	layoutMain.setSizeFull();
    	layoutMain.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	
    	this.setViewContent(layoutMain);
	}

}
