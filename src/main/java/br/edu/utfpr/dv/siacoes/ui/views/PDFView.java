package br.edu.utfpr.dv.siacoes.ui.views;

import java.io.ByteArrayInputStream;

import org.vaadin.alejandro.PdfBrowserViewer;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;

@PageTitle("Visualizar PDF")
@Route(value = "pdf")
public class PDFView extends ViewFrame implements HasUrlParameter<String> {
	
	private void showPdf(byte[] report){
		StreamResource s = new StreamResource("report.pdf", () -> new ByteArrayInputStream(report));
		
		PdfBrowserViewer viewer = new PdfBrowserViewer(s);
		viewer.setWidth("100%");
		viewer.setHeight("100%");
		
		VerticalLayout vl = new VerticalLayout(viewer);
		vl.setSizeFull();
		vl.setMargin(false);
		vl.setPadding(false);
		vl.setSpacing(false);
		
		this.setViewContent(vl);
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if(parameter != null) {
			if(parameter.startsWith("session")){
				this.showPdf(Session.getReport(parameter.replace("session-", "")));
			}
		}
	}

}
