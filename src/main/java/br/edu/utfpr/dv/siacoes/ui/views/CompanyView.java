package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.CompanyBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Company;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.CompanyDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditCompanyWindow;

@PageTitle("Empresas")
@Route(value = "company", layout = MainLayout.class)
public class CompanyView extends ListView<CompanyDataSource> {
	
	public CompanyView(){
		super(SystemModule.SIGES);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(CompanyDataSource::getName, "Name").setHeader("Nome");
		this.getGrid().addColumn(CompanyDataSource::getCity, "City").setHeader("Cidade");
		this.getGrid().addColumn(CompanyDataSource::getPhone).setHeader("Telefone");
		this.getGrid().addColumn(CompanyDataSource::getEmail).setHeader("E-mail");
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}
	
	@Override
	protected void loadGrid() {
		try{
			CompanyBO bo = new CompanyBO();
			List<Company> list = bo.listAll();
			
			this.getGrid().setItems(CompanyDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Empresas", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditCompanyWindow window = new EditCompanyWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			CompanyBO bo = new CompanyBO();
			Company company = bo.findById((int)id);
			
			EditCompanyWindow window = new EditCompanyWindow(company, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Empresa", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
