package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.model.AppConfig;

public class MobileView extends CustomComponent implements View {

	public static final String NAME = "mobile";
	
	private final Image imageQrCode;
	
	public MobileView() {
		this.imageQrCode = new Image();
		this.imageQrCode.setWidth("200px");
		this.imageQrCode.setHeight("200px");
		
		VerticalLayout imageSmartphone = new VerticalLayout(this.imageQrCode);
		imageSmartphone.setComponentAlignment(this.imageQrCode, Alignment.MIDDLE_CENTER);
		imageSmartphone.setHeight("500px");
		imageSmartphone.setWidth("248px");
		
		CssLayout css = new CssLayout() {
		    @Override
		    protected String getCss(Component c) {
		        if (c instanceof VerticalLayout) {
		            return "background: url(VAADIN/themes/" + UI.getCurrent().getTheme() + "/images/smartphone.png)";
		        } else {
		        	return null;	
		        }
		    }
		};
		css.addComponent(imageSmartphone);
		
		Link linkAppStore = new Link(null, new ExternalResource("https://www.apple.com/br/ios/app-store"));
		linkAppStore.setIcon(new ThemeResource("images/appstore170.png"));
		linkAppStore.setWidth("170px");
		linkAppStore.setHeight("50px");
		
		Link linkPlayStore = new Link(null, new ExternalResource("https://play.google.com/store"));
		linkPlayStore.setIcon(new ThemeResource("images/playstore170.png"));
		linkPlayStore.setWidth("170px");
		linkPlayStore.setHeight("50px");
		
		Label l1 = new Label("Efetue o download do SIACOES Mobile na sua loja de aplicativos.");
		l1.setStyleName("SubTitle");
		
		HorizontalLayout h1 = new HorizontalLayout(linkAppStore, linkPlayStore);
		h1.setSpacing(true);
		
		Label l2 = new Label("Após efetuar o download, abra o aplicativo e leia o QRCode ao lado para proceder com o login.");
		l2.setStyleName("SubTitle");
		
		VerticalLayout v1 = new VerticalLayout(l1, h1, l2);
		v1.setSpacing(true);
		v1.setSizeFull();
		v1.setComponentAlignment(h1, Alignment.MIDDLE_CENTER);
		v1.setComponentAlignment(l1, Alignment.BOTTOM_CENTER);
		v1.setComponentAlignment(l2, Alignment.TOP_CENTER);
		
		HorizontalLayout h2 = new HorizontalLayout(v1, css);
		h2.setSpacing(true);
		h2.setSizeFull();
		h2.setComponentAlignment(v1, Alignment.MIDDLE_CENTER);
		h2.setComponentAlignment(css, Alignment.MIDDLE_CENTER);
		h2.setExpandRatio(v1, 1f);
		h2.setExpandRatio(css, 1f);
		
		this.setCompositionRoot(h2);
		this.setSizeFull();
		
		this.buildQrCode();
	}
	
	private void buildQrCode() {
		StreamResource resource = new StreamResource(
            new StreamResource.StreamSource() {
                @Override
                public InputStream getStream() {
                    try {
						return new ByteArrayInputStream(AppConfig.getInstance().getHostQRCode(200, 200));
					} catch (Exception e) {
						return null;
					}
                }
            }, "qrcode.png");

	    this.imageQrCode.setSource(resource);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if(!AppConfig.getInstance().isMobileEnabled()) {
			UI.getCurrent().getNavigator().navigateTo(MainView.NAME);
		}
	}

}
