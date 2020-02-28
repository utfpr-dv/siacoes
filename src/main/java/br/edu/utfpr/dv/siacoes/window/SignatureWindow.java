package br.edu.utfpr.dv.siacoes.window;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.view.SignatureView;

public class SignatureWindow extends BasicWindow {
	
	private final TextField textReport;
	private final TextField textUser;
	private final PasswordField textPassword;
	private final Button buttonSign;
	
	private Document document;
	private EditWindow parentWindow;
	private SignatureView parentView;
	
	private SignatureWindow() {
		super("Assinar Documento");
		
		this.textReport = new TextField("Documento");
		this.textReport.setWidth("400px");
		this.textReport.setEnabled(false);
		
		this.textUser = new TextField("Usuário");
		this.textUser.setValue(Session.getUser().getName());
		this.textUser.setWidth("400px");
		this.textUser.setEnabled(false);
		
		this.textPassword = new PasswordField("Senha");
		this.textPassword.setWidth("400px");
		this.textPassword.setNullRepresentation("");
    	this.textPassword.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
    	this.textPassword.setIcon(FontAwesome.LOCK);
    	
    	this.buttonSign = new Button("Assinar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	sign();
            	buttonSign.setEnabled(true);
            }
        });
    	this.buttonSign.setWidth("150px");
		this.buttonSign.setIcon(FontAwesome.PENCIL);
		this.buttonSign.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		//this.buttonSign.setClickShortcut(KeyCode.ENTER);
		this.buttonSign.setDisableOnClick(true);
		
		VerticalLayout layout = new VerticalLayout(this.textReport, this.textUser, this.textPassword, this.buttonSign);
		layout.setSpacing(true);
		layout.setMargin(true);
		
		this.setContent(layout);
		
		this.textPassword.focus();
	}
	
	public SignatureWindow(int idDocument, EditWindow parentWindow, SignatureView parentView) throws SQLException {
		this();
		
		this.document = Document.find(idDocument);
		this.parentWindow = parentWindow;
		this.parentView = parentView;
		
		this.loadInfo();
	}
	
	public SignatureWindow(DocumentType type, int idRegister, Object dataset, List<User> users, EditWindow parentWindow, SignatureView parentView) throws Exception {
		this();
		
		this.document = Document.build(Session.getSelectedDepartment().getDepartment().getIdDepartment(), type, idRegister, dataset, users);
		this.parentWindow = parentWindow;
		this.parentView = parentView;
		
		this.loadInfo();
	}
	
	private void loadInfo() {
		this.textReport.setValue(this.document.getType().toString());
	}
	
	private void sign() {
		try {
			int idDocument = 0;
			
			if(this.document.getIdDocument() == 0) {
				idDocument = Document.insertSigned(document, Session.getUser().getLogin(), this.textPassword.getValue());
			} else {
				Document.sign(this.document.getIdDocument(), Session.getUser().getLogin(), this.textPassword.getValue());
				idDocument = this.document.getIdDocument();
			}
			
			this.parentViewRefreshGrid();
			this.parentDisableButtons();
			this.showReport(Document.getSignedDocument(idDocument));
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Assinar Documento", e.getMessage());
		}
	}
	
	private void parentViewRefreshGrid() {
		if(this.parentView != null) {
			this.parentView.loadGrids();
		}
	}
	
	private void parentDisableButtons() {
		if(this.parentWindow != null) {
			this.parentWindow.disableButtons();
		}
	}

}
