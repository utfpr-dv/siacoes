package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.MessageBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Message;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;

@PageTitle("Mensagens do Sistema")
@Route(value = "message", layout = MainLayout.class)
public class MessageView extends LoggedView {
	
	private static final String LISTALL = "Litar todas as mensagens";
	private static final String LISTREAD = "Listar apenas mensagens lidas";
	private static final String LISTUNREAD = "Listar apenas mensagens não lidas";
	
	List<Message> list;
	private Grid<Message> grid;
	private final VerticalLayout layoutListMessages;
	private final RadioButtonGroup<String> optionFilterType;
	private final Button buttonMarkAsRead;
	private final Button buttonMarkAsUnread;
	private final TextField textSender;
	private final TextField textTitle;
	private final TextArea textMessage;
	
	public MessageView() {
		this.list = new ArrayList<Message>();
		
		this.optionFilterType = new RadioButtonGroup<String>();
		this.optionFilterType.setItems(LISTALL, LISTREAD, LISTUNREAD);
		this.optionFilterType.setValue(LISTALL);
		this.optionFilterType.addValueChangeListener(event -> {
			listMessages();
		});
		
		this.buttonMarkAsRead = new Button("Marcar como lida");
		this.buttonMarkAsRead.setIcon(new Icon(VaadinIcon.ENVELOPE_OPEN_O));
		this.buttonMarkAsRead.addClickListener(event -> {
            
        });
		
		this.buttonMarkAsUnread = new Button("Marcar como não lida");
		this.buttonMarkAsUnread.setIcon(new Icon(VaadinIcon.ENVELOPE_O));
		this.buttonMarkAsUnread.addClickListener(event -> {
            
        });
		
		this.textSender = new TextField("Remetente");
		this.textSender.setWidth("100%");
		this.textSender.setReadOnly(true);
		
		this.textTitle = new TextField("Assunto");
		this.textTitle.setWidth("100%");
		this.textTitle.setReadOnly(true);
		
		this.textMessage = new TextArea("Mensagem");
		this.textMessage.setSizeFull();
		this.textMessage.setReadOnly(true);
		
		HorizontalLayout layoutButtons = new HorizontalLayout(this.buttonMarkAsRead, this.buttonMarkAsUnread);
		layoutButtons.setSpacing(true);
		layoutButtons.setPadding(false);
		
		VerticalLayout layoutMessage = new VerticalLayout(this.textSender, this.textTitle, this.textMessage);
		layoutMessage.setSpacing(false);
		layoutMessage.setMargin(false);
		layoutMessage.setPadding(false);
		layoutMessage.setSizeFull();
		layoutMessage.expand(this.textMessage);
		
		this.grid = new Grid<Message>();
		this.grid.addComponentColumn(item -> createSender(this.grid, item)).setHeader("Remetente").setFlexGrow(0).setWidth("100px");
		this.grid.addComponentColumn(item -> createSubject(this.grid, item)).setHeader("Assunto");
		this.grid.addColumn(Message::getDate).setHeader("Data e Hora").setFlexGrow(0).setWidth("125px");
		this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.grid.setSelectionMode(SelectionMode.SINGLE);
		this.grid.setSizeFull();
		this.grid.addItemClickListener(event -> {
			loadMessage(event.getItem());
		});
		
		this.layoutListMessages = new VerticalLayout();
		this.layoutListMessages.setSizeFull();
		this.layoutListMessages.addAndExpand(this.grid);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.addAndExpand(this.layoutListMessages, layoutMessage);
		hl.setSizeFull();
		hl.setSpacing(true);
		hl.setMargin(false);
		hl.setPadding(false);
		
		HorizontalLayout h2 = new HorizontalLayout(this.optionFilterType);
		h2.setMargin(true);
		
		Details panelFilter = new Details();
		panelFilter.setSummaryText("Filtros");
		panelFilter.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panelFilter.setOpened(true);
		panelFilter.setContent(h2);
		panelFilter.getElement().getStyle().set("width", "100%");
		
		VerticalLayout layoutContent = new VerticalLayout(panelFilter, hl);
		layoutContent.expand(hl);
		layoutContent.setSizeFull();
		layoutContent.setSpacing(false);
		layoutContent.setMargin(false);
		layoutContent.setPadding(false);
		
		this.setViewContent(layoutContent);
		
		this.listMessages();
	}
	
	private Component createSubject(Grid<Message> grid, Message item) {
		Label label = new Label(item.getTitle());
		
		if(!item.isRead()) {
			//label.the
		}
		
		return label;
	}
	
	private Component createSender(Grid<Message> grid, Message item) {
		Label label = new Label(item.getModule().getShortDescription());
		
		return label;
	}
	
	private void listMessages() {
		try {
			if(this.optionFilterType.getValue().equals(LISTALL)) {
				this.list = new MessageBO().listByUser(Session.getUser().getIdUser());
			} else {
				this.list = new MessageBO().listRead(Session.getUser().getIdUser(), this.optionFilterType.getValue().equals(LISTREAD));
			}
			
			this.loadGrid();
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Mensagens", e.getMessage());
		}
	}
	
	private void loadGrid() {
		this.grid.setItems(this.list);
	}
	
	private void loadMessage(Message message) {
		if(message == null)
			return;
		
		
		if(!message.isRead()) {
			try {
				new MessageBO().markAsRead(message.getIdMessage(), true);
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
			}
			this.loadGrid();
		}
		
		this.textSender.setReadOnly(false);
		this.textTitle.setReadOnly(false);
		this.textMessage.setReadOnly(false);
		
		this.textSender.setValue(message.getModule().getDescription());
		this.textTitle.setValue(message.getTitle());
		this.textMessage.setValue(message.getMessage());
		
		this.textSender.setReadOnly(true);
		this.textTitle.setReadOnly(true);
		this.textMessage.setReadOnly(true);
		
	}

}
