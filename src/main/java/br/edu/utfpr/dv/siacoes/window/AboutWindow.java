package br.edu.utfpr.dv.siacoes.window;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class AboutWindow extends Window {
	
	private final Button buttonClose;
	private final VerticalLayout layoutFields;
	private final HorizontalLayout layoutButtons;
	
	public AboutWindow(){
		this.setCaption("Sobre ...");
		
		this.buttonClose = new Button("Fechar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	close();
            }
        });
		this.buttonClose.setWidth("150px");
		
		this.layoutFields = new VerticalLayout();
		this.layoutFields.setSpacing(true);
		
		Label label1 = new Label("SIACOES");
		label1.setStyleName("Title");
		this.layoutFields.addComponent(label1);
		
		Label label2 = new Label("Sistema Integrado de Atividades Complementares, Orientações e Estágios");
		label2.setStyleName("SubTitle");
		this.layoutFields.addComponent(label2);
		
		this.layoutFields.addComponent(new Label("Sistema desenvolvido pelo Curso de Engenharia de Software da UTFPR Câmpus Dois Vizinhos"));
		
		VerticalLayout layoutProfessors = new VerticalLayout(new Label("Professores responsáveis:"), new Label("André Roberto Ortoncelli"), new Label("Franciele Beal"), new Label("Newton Carlos Will"));
		
		this.layoutFields.addComponent(new HorizontalLayout(layoutProfessors, new Image("", new ThemeResource("images/logo_ES.png"))));
		
		this.layoutButtons = new HorizontalLayout(this.buttonClose);
		this.layoutButtons.setSpacing(true);
		this.layoutButtons.setComponentAlignment(this.buttonClose, Alignment.MIDDLE_RIGHT);
		
		VerticalLayout vl = new VerticalLayout(this.layoutFields, this.layoutButtons);
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setComponentAlignment(this.layoutButtons, Alignment.BOTTOM_RIGHT);
		
		this.setContent(vl);
		this.setWidth("750px");
		
		this.setModal(true);
        this.center();
        this.setResizable(false);
	}
	
}
