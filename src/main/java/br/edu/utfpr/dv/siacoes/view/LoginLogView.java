package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.DateField;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.components.UserComboBox;
import br.edu.utfpr.dv.siacoes.log.LoginEvent;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class LoginLogView extends ListView {
	
	public static final String NAME = "loginlog";
	
	private final UserComboBox comboUser;
	private final DateField textStartDate;
	private final DateField textEndDate;
	
	public LoginLogView() {
		super(SystemModule.GENERAL);
		
		this.setCaption("Registro de Acessos");
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.comboUser = new UserComboBox("Usuário");
		
		this.textStartDate = new DateField("Data Inicial");
		this.textStartDate.setDateFormat("dd/MM/yyyy");
		
		this.textEndDate = new DateField("Data Final");
		this.textEndDate.setDateFormat("dd/MM/yyyy");
		
		this.addFilterField(this.comboUser);
		this.addFilterField(this.textStartDate);
		this.addFilterField(this.textEndDate);
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Registro", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")));
		this.getGrid().addColumn("Operação", String.class);
		this.getGrid().addColumn("IP Origem", String.class);
		this.getGrid().addColumn("Navegador/Sistema Origem", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(150);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(2).setWidth(200);
		
		if((this.comboUser.getUser() != null) && (this.comboUser.getUser().getIdUser() != 0)) {
			try {
				List<LoginEvent> list = LoginEvent.list(this.comboUser.getUser().getIdUser(), this.textStartDate.getValue(), this.textEndDate.getValue());
				
				for(LoginEvent c : list) {
					Object itemId = this.getGrid().addRow(c.getDate(), c.getEvent().toString(), c.getSource(), c.getDevice());
					this.addRowId(itemId, c.getIdLog());
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Listar Acessos", e.getMessage());
			}
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		if((this.comboUser.getUser() == null) || (this.comboUser.getUser().getIdUser() == 0)) {
			throw new Exception("Selecione um usuário.");
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
