package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Página não Encontrada")
@Route(value = "404")
public class Error404View extends ViewFrame {
	
	public Error404View() {
		Image image = new Image();
		image.setSrc("images/404.png");
		image.setMaxWidth("500px");
    	
    	H5 label1 = new H5("Parece que a página que você procura não existe.");
    	H5 label2 = new H5("Ou então o nosso servidor ficou com preguiça de procurá-la.");
    	H5 label3 = new H5("Mas é provável que ela não exista mesmo.");
    	
    	VerticalLayout layoutMain = new VerticalLayout(image, label1, label2, label3);
    	layoutMain.setSpacing(false);
    	layoutMain.setMargin(false);
    	layoutMain.setPadding(false);
    	layoutMain.setSizeFull();
    	layoutMain.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	
    	this.setViewContent(layoutMain);
	}
}
