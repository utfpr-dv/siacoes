package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.log.LoginEvent;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.UserComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.LoginLogDataSource;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Registro de Acessos")
@Route(value = "loginlog", layout = MainLayout.class)
public class LoginLogView extends ListView<LoginLogDataSource> {
	
	private final UserComboBox comboUser;
	private final DatePicker textStartDate;
	private final DatePicker textEndDate;
	
	public LoginLogView() {
		super(SystemModule.GENERAL);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.getGrid().addColumn(LoginLogDataSource::getDate).setHeader("Registro").setFlexGrow(0).setWidth("150px");
		this.getGrid().addColumn(LoginLogDataSource::getType).setHeader("Operação").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(LoginLogDataSource::getIp).setHeader("IP Origem").setFlexGrow(0).setWidth("200px");
		this.getGrid().addColumn(LoginLogDataSource::getBrowser).setHeader("Navegador/Sistema Origem");
		
		this.comboUser = new UserComboBox("Usuário");
		
		this.textStartDate = new DatePicker("Data Inicial");
		//this.textStartDate.setDateFormat("dd/MM/yyyy");
		
		this.textEndDate = new DatePicker("Data Final");
		//this.textEndDate.setDateFormat("dd/MM/yyyy");
		
		this.addFilterField(this.comboUser);
		this.addFilterField(this.textStartDate);
		this.addFilterField(this.textEndDate);
		
		this.setActionsVisible(false);
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		if((this.comboUser.getUser() != null) && (this.comboUser.getUser().getIdUser() != 0)) {
			try {
				List<LoginEvent> list = LoginEvent.list(this.comboUser.getUser().getIdUser(), DateUtils.convertToDate(this.textStartDate.getValue()), DateUtils.convertToDate(this.textEndDate.getValue()));
				
				this.getGrid().setItems(LoginLogDataSource.load(list));
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Listar Acessos", e.getMessage());
			}
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		if((this.comboUser.getUser() == null) || (this.comboUser.getUser().getIdUser() == 0)) {
			throw new Exception("Selecione um usuário.");
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
