package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.CompanyBO;
import br.edu.utfpr.dv.siacoes.model.Company;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditCompanyWindow;

public class CompanyView extends ListView {

	public static final String NAME = "company";
	
	public CompanyView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Empresas");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("Cidade", String.class);
		this.getGrid().addColumn("Telefone", String.class);
		this.getGrid().addColumn("E-mail", String.class);
		
		try{
			CompanyBO bo = new CompanyBO();
			List<Company> list = bo.listAll();
			
			for(Company c : list){
				Object itemId = this.getGrid().addRow(c.getName(), c.getCity().getName(), c.getPhone(), c.getEmail());
				this.addRowId(itemId, c.getIdCompany());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Empresas", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditCompanyWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			CompanyBO bo = new CompanyBO();
			Company company = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditCompanyWindow(company, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Empresa", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
