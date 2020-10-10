package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.UUID;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.Notification;

/**
 * A view frame that establishes app design guidelines. It consists of three
 * parts:
 * <ul>
 * <li>Topmost {@link #setViewHeader(Component...) header}</li>
 * <li>Center {@link #setViewContent(Component...) content}</li>
 * <li>Bottom {@link #setViewFooter(Component...) footer}</li>
 * </ul>
 */
@CssImport("./styles/components/view-frame.css")
public class ViewFrame extends Composite<Div> implements HasStyle {

	private String CLASS_NAME = "view-frame";

	private Div header;
	private Div content;
	private Div footer;

	public ViewFrame() {
		setClassName(CLASS_NAME);

		header = new Div();
		header.setClassName(CLASS_NAME + "__header");

		content = new Div();
		content.setClassName(CLASS_NAME + "__content");

		footer = new Div();
		footer.setClassName(CLASS_NAME + "__footer");

		getContent().add(header, content, footer);
	}

	/**
	 * Sets the header slot's components.
	 */
	public void setViewHeader(Component... components) {
		header.removeAll();
		header.add(components);
	}

	/**
	 * Sets the content slot's components.
	 */
	public void setViewContent(Component... components) {
		content.removeAll();
		content.add(components);
	}

	/**
	 * Sets the footer slot's components.
	 */
	public void setViewFooter(Component... components) {
		footer.removeAll();
		footer.add(components);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		if(MainLayout.get() != null) {
			MainLayout.get().getAppBar().reset();
		}
	}
    
    protected void showReport(byte[] pdfReport){
    	if(pdfReport == null) {
    		this.showErrorNotification("Visualizar Arquivo", "O arquivo solicitado não foi encontrado.");
    	} else {
        	String id = UUID.randomUUID().toString();
        	
        	Session.putReport(pdfReport, id);
    		
        	UI.getCurrent().getPage().open("pdf/session-" + id, "_blank");
    	}
    }
    
    protected void showSuccessNotification(String title, String message) {
		Notification.showSuccessNotification(title, message);
	}
	
	protected void showWarningNotification(String title, String message) {
		Notification.showWarningNotification(title, message);
	}
	
	protected void showErrorNotification(String title, String message) {
		Notification.showErrorNotification(title, message);
	}

}
