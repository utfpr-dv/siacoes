package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.util.UIUtils;

@PageTitle("Sobre o Sistema ...")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends LoggedView {

	public AboutView() {
		Image logoES = new Image(UIUtils.IMG_PATH + "logos/es.png", "");
		logoES.setMaxHeight("300px");
		logoES.setMaxWidth("500px");
		logoES.setWidth("50%");
		
		H1 label = new H1("SIACOES - Sistema Integrado de Atividades Complementares, Orientações e Estágios");
		
		H5 label2 = new H5("Sistema desenvolvido pelo Curso de Engenharia de Software da UTFPR Câmpus Dois Vizinhos");
		
		H5 label3 = new H5("Professores responsáveis:");
		
		H5 label4 = new H5("André Roberto Ortoncelli");
		
		H5 label5 = new H5("Franciele Beal");
		
		H5 label6 = new H5("Newton Carlos Will");
		
		VerticalLayout vl = new VerticalLayout(label, label, label2, label3, label4, label5, label6);
		
		HorizontalLayout hl = new HorizontalLayout(logoES, vl);
		
		this.setViewContent(hl);
	}
	
}
