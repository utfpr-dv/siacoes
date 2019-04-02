package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.components.UserComboBox;
import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.EventLogWindow;

public class EventLogView extends ListView {
	
	public static final String NAME = "eventlog";
	
	private final UserComboBox comboUser;
	private final ComboBox comboClassName;
	private final DateField textStartDate;
	private final DateField textEndDate;
	
	public EventLogView() {
		super(SystemModule.GENERAL);
		
		this.setCaption("Registro de Eventos");
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.comboUser = new UserComboBox("Usuário");
		
		this.comboClassName = new ComboBox("Tabela");
		this.comboClassName.setWidth("300px");
		
		this.textStartDate = new DateField("Data Inicial");
		this.textStartDate.setDateFormat("dd/MM/yyyy");
		
		this.textEndDate = new DateField("Data Final");
		this.textEndDate.setDateFormat("dd/MM/yyyy");
		
		this.addFilterField(this.comboUser);
		this.addFilterField(this.comboClassName);
		this.addFilterField(this.textStartDate);
		this.addFilterField(this.textEndDate);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.setEditCaption("Visualizar");
		this.setEditIcon(FontAwesome.SEARCH);
		
		this.loadComboClassName();
	}
	
	private void loadComboClassName() {
		try {
			this.comboClassName.removeAllItems();
			this.comboClassName.addItems(UpdateEvent.listClassNames());
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Tabelas", e.getMessage());
		}
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Registro", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")));
		this.getGrid().addColumn("Operação", String.class);
		this.getGrid().addColumn("Usuário", String.class);
		this.getGrid().addColumn("Tabela", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(150);
		this.getGrid().getColumns().get(1).setWidth(100);
		
		if(((this.comboUser.getUser() != null) && (this.comboUser.getUser().getIdUser() != 0)) || ((this.comboClassName.getValue() != null) && !this.comboClassName.getValue().toString().trim().isEmpty())) {
			try {
				List<UpdateEvent> list = UpdateEvent.list((this.comboUser.getUser() != null ? this.comboUser.getUser().getIdUser() : 0), (this.comboClassName.getValue() != null ? this.comboClassName.getValue().toString() : ""), this.textStartDate.getValue(), this.textEndDate.getValue());
				
				for(UpdateEvent c : list) {
					Object itemId = this.getGrid().addRow(c.getDate(), c.getEvent().toString(), c.getUser().getName(), c.getClassName());
					this.addRowId(itemId, c.getIdLog());
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Listar Eventos", e.getMessage());
			}
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			UI.getCurrent().addWindow(new EventLogWindow(UpdateEvent.findEvent((long)id)));
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Visualizar Registro", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		if(((this.comboUser.getUser() == null) || (this.comboUser.getUser().getIdUser() == 0)) && ((this.comboClassName.getValue() == null) && this.comboClassName.getValue().toString().trim().isEmpty())) {
			throw new Exception("Selecione um usuário ou uma tabela para filtrar os registros.");
		}
		if((DateUtils.getYear(this.textStartDate.getValue()) < 2000) || (DateUtils.getYear(this.textEndDate.getValue()) < 2000)) {
			throw new Exception("Informe um intervalo de datas.");
		}
		if(this.textEndDate.getValue().before(this.textStartDate.getValue())) {
			throw new Exception("A data final tem que ser posterior à data inicial.");
		}
		if(DateUtils.getDifferenceInMonths(this.textStartDate.getValue(), this.textEndDate.getValue()) > 1) {
			throw new Exception("O intervalo de datas não pode ser superior a um mês.");
		}
	}

}
