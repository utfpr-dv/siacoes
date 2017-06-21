package br.edu.utfpr.dv.siacoes.components;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.ComboBox;

import br.edu.utfpr.dv.siacoes.bo.CompanyBO;
import br.edu.utfpr.dv.siacoes.model.Company;

public class CompanyComboBox extends ComboBox {

	private List<Company> list;
	
	public CompanyComboBox(){
		super("Empresa");
		this.setNullSelectionAllowed(false);
		this.setWidth("400px");
		this.loadComboCompany();
	}
	
	public Company getCompany(){
		return (Company)this.getValue();
	}
	
	public void setCompany(Company c){
		if(c == null){
			this.setValue(null);
			return;
		}
		
		boolean find = false;
		
		for(Company company : this.list){
			if(c.getIdCompany() == company.getIdCompany()){
				this.setValue(company);
				find = true;
				break;
			}
		}
		
		if(!find){
			try {
				CompanyBO bo = new CompanyBO();
				Company company = bo.findById(c.getIdCompany());
				
				this.addItem(company);
				this.setValue(company);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void loadComboCompany(){
		try {
			CompanyBO bo = new CompanyBO();
			
			this.list = bo.listAll();
			
			this.removeAllItems();
			this.addItems(this.list);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
