package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.MessageBO;
import br.edu.utfpr.dv.siacoes.model.Message;

public class MessageView extends BasicView {
	
	public static final String NAME = "messageview";
	
	private static final String LISTALL = "Litar todas as mensagens";
	private static final String LISTREAD = "Listar apenas mensagens lidas";
	private static final String LISTUNREAD = "Listar apenas mensagens não lidas";
	
	List<Message> list;
	private Grid grid;
	private int idMessage;
	private final VerticalLayout layoutListMessages;
	private final OptionGroup optionFilterType;
	private final Button buttonMarkAsRead;
	private final Button buttonMarkAsUnread;
	private final TextField textSender;
	private final TextField textTitle;
	private final TextArea textMessage;
	
	public MessageView() {
		this.setCaption("Mensagens do Sistema");
		
		this.list = new ArrayList<Message>();
		this.idMessage = 0;
		
		this.optionFilterType = new OptionGroup();
		this.optionFilterType.addItem(LISTALL);
		this.optionFilterType.addItem(LISTREAD);
		this.optionFilterType.addItem(LISTUNREAD);
		this.optionFilterType.select(LISTALL);
		this.optionFilterType.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		this.optionFilterType.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				listMessages();
			}
		});
		
		this.buttonMarkAsRead = new Button();
		this.buttonMarkAsRead.setIcon(FontAwesome.ENVELOPE);
		this.buttonMarkAsRead.setDescription("Marcar como lida");
		this.buttonMarkAsRead.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	
            }
        });
		
		this.buttonMarkAsUnread = new Button();
		this.buttonMarkAsUnread.setIcon(FontAwesome.ENVELOPE);
		this.buttonMarkAsUnread.setDescription("Marcar como não lida");
		this.buttonMarkAsUnread.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	
            }
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
		
		VerticalLayout layoutMessage = new VerticalLayout(this.textSender, this.textTitle, this.textMessage);
		layoutMessage.setSpacing(true);
		layoutMessage.setMargin(true);
		layoutMessage.setSizeFull();
		layoutMessage.setExpandRatio(this.textMessage, 1.0f);
		
		this.layoutListMessages = new VerticalLayout();
		this.layoutListMessages.setSizeFull();
		
		HorizontalLayout hl = new HorizontalLayout(this.layoutListMessages, layoutMessage);
		hl.setExpandRatio(this.layoutListMessages, 0.5f);
		hl.setExpandRatio(layoutMessage, 0.5f);
		hl.setSizeFull();
		hl.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.optionFilterType);
		h2.setMargin(true);
		
		Panel panelFilter = new Panel("Filtros");
		panelFilter.setSizeFull();
		panelFilter.setHeight("80px");
		panelFilter.setContent(h2);
		
		Panel panelMessages = new Panel("Mensagens");
		panelMessages.setSizeFull();
		panelMessages.setContent(hl);
		
		VerticalLayout layoutContent = new VerticalLayout(panelFilter, panelMessages);
		layoutContent.setExpandRatio(panelMessages, 1.0f);
		layoutContent.setSizeFull();
		
		this.setContent(layoutContent);
		
		this.listMessages();
	}
	
	private void listMessages() {
		try {
			if(this.optionFilterType.isSelected(LISTALL)) {
				this.list = new MessageBO().listByUser(Session.getUser().getIdUser());
			} else {
				this.list = new MessageBO().listRead(Session.getUser().getIdUser(), this.optionFilterType.isSelected(LISTREAD));
			}
			
			this.loadGrid();
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Mensagens", e.getMessage());
		}
	}
	
	private void loadGrid() {
		this.grid = new Grid();
    	this.grid.setSizeFull();
    	this.grid.setSelectionMode(SelectionMode.SINGLE);
    	this.grid.addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				loadMessage();
			}
		});
    	this.layoutListMessages.removeAllComponents();
    	
    	this.grid.addColumn("Remetente", String.class);
    	this.grid.addColumn("Assunto", String.class);
    	this.grid.addColumn("Data e Hora", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
    	this.grid.getColumns().get(0).setWidth(100);
    	this.grid.getColumns().get(1).setRenderer(new HtmlRenderer());
    	this.grid.getColumns().get(2).setWidth(150);
    	
    	for(Message message : list) {
			this.grid.addRow(message.getModule().getShortDescription(), (message.isRead() ? "" : "<b>") + message.getTitle() + (message.isRead() ? "" : "</b>"), message.getDate());
		}
    	
    	this.layoutListMessages.addComponent(this.grid);
		this.layoutListMessages.setExpandRatio(this.grid, 1.0f);
	}
	
	private void loadMessage() {
		int index = -1;
		Object itemId = this.grid.getSelectedRow();
		
		if(itemId != null) {
			index = (int)itemId - 1;
		}
		
		if((index >= 0) && (index < this.list.size())) {
			if(!this.list.get(index).isRead()) {
				this.list.get(index).setRead(true);
				try {
					new MessageBO().markAsRead(this.list.get(index).getIdMessage(), true);
				} catch (Exception e) {
					Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				}
				this.loadGrid();
				((SingleSelectionModel)this.grid.getSelectionModel()).select(itemId);
			}
			
			Message message = this.list.get(index);
			
			this.idMessage = message.getIdMessage();
			
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

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
