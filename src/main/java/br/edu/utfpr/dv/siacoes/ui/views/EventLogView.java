package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.UserComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.EventLogDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EventLogWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Registro de Eventos")
@Route(value = "eventlog", layout = MainLayout.class)
public class EventLogView extends ListView<EventLogDataSource> {
	
	private final UserComboBox comboUser;
	private final ComboBox<String> comboClassName;
	private final DatePicker textStartDate;
	private final DatePicker textEndDate;
	
	public EventLogView() {
		super(SystemModule.GENERAL);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.getGrid().addColumn(new LocalDateTimeRenderer<>(EventLogDataSource::getDate, "dd/MM/yyyy HH:mm"), "Date").setHeader("Data e Hora").setFlexGrow(0).setWidth("150px");
		this.getGrid().addColumn(EventLogDataSource::getType, "Type").setHeader("Operação").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(EventLogDataSource::getUser, "User").setHeader("Usuário");
		this.getGrid().addColumn(EventLogDataSource::getTable, "Table").setHeader("Tabela");
		
		this.comboUser = new UserComboBox("Usuário");
		
		this.comboClassName = new ComboBox<String>();
		this.comboClassName.setLabel("Tabela");
		this.comboClassName.setWidth("300px");
		
		this.textStartDate = new DatePicker("Data Inicial");
		//this.textStartDate.setDateFormat("dd/MM/yyyy");
		
		this.textEndDate = new DatePicker("Data Final");
		//this.textEndDate.setDateFormat("dd/MM/yyyy");
		
		this.addFilterField(this.comboUser);
		this.addFilterField(this.comboClassName);
		this.addFilterField(this.textStartDate);
		this.addFilterField(this.textEndDate);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.setEditCaption("Visualizar");
		this.setEditIcon(new Icon(VaadinIcon.SEARCH));
		
		this.loadComboClassName();
	}
	
	private void loadComboClassName() {
		try {
			this.comboClassName.setItems(UpdateEvent.listClassNames());
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Tabelas", e.getMessage());
		}
	}

	@Override
	protected void loadGrid() {
		if(((this.comboUser.getUser() != null) && (this.comboUser.getUser().getIdUser() != 0)) || ((this.comboClassName.getValue() != null) && !this.comboClassName.getValue().toString().trim().isEmpty())) {
			try {
				List<UpdateEvent> list = UpdateEvent.list((this.comboUser.getUser() != null ? this.comboUser.getUser().getIdUser() : 0), (this.comboClassName.getValue() != null ? this.comboClassName.getValue().toString() : ""), DateUtils.convertToDate(this.textStartDate.getValue()), DateUtils.convertToDate(this.textEndDate.getValue()));
				
				this.getGrid().setItems(EventLogDataSource.load(list));
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Listar Eventos", e.getMessage());
			}
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try {
			EventLogWindow window = new EventLogWindow(UpdateEvent.findEvent((long)id));
			window.open();
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Visualizar Registro", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		if(((this.comboUser.getUser() == null) || (this.comboUser.getUser().getIdUser() == 0)) && ((this.comboClassName.getValue() == null) && this.comboClassName.getValue().toString().trim().isEmpty())) {
			throw new Exception("Selecione um usuário ou uma tabela para filtrar os registros.");
		}
		if((this.textStartDate.getValue() == null) || (this.textEndDate.getValue() == null) || (this.textStartDate.getValue().getYear() < 2000) || (this.textEndDate.getValue().getYear() < 2000)) {
			throw new Exception("Informe um intervalo de datas.");
		}
		if(this.textEndDate.getValue().isBefore(this.textStartDate.getValue())) {
			throw new Exception("A data final tem que ser posterior à data inicial.");
		}
		if(DateUtils.getDifferenceInMonths(this.textStartDate.getValue(), this.textEndDate.getValue()) > 1) {
			throw new Exception("O intervalo de datas não pode ser superior a um mês.");
		}
	}

}
