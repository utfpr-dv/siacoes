package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Erro Interno")
@Route(value = "500")
public class Error500View extends ViewFrame {
	
	public Error500View() {
		Image image = new Image();
		image.setSrc("images/500.png");
		image.setMaxHeight("500px");
    	
		H1 title = new H1("Ops!");
		
    	H5 label1 = new H5("Parece que os nossos servidores de última geração não estão se comportando bem.");
    	H5 label2 = new H5("Mas não se preocupe, vamos deixá-los uma semana sem PlayStation para que aprendam bons modos.");
    	H5 label3 = new H5("Esperamos que eles aprendam a lição.");
    	
    	VerticalLayout layoutMain = new VerticalLayout(image, title, label1, label2, label3);
    	layoutMain.setSpacing(false);
    	layoutMain.setMargin(false);
    	layoutMain.setPadding(false);
    	layoutMain.setSizeFull();
    	layoutMain.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	
    	this.setViewContent(layoutMain);
	}

}
