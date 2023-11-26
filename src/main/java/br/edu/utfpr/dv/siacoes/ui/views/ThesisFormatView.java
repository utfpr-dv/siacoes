package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ThesisFormatBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.ThesisFormat;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.ThesisFormatDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditThesisFormatWindow;

@PageTitle("Formatos de TCC")
@Route(value = "thesisformat", layout = MainLayout.class)
public class ThesisFormatView extends ListView<ThesisFormatDataSource> {
	
	private final Checkbox checkActive;
	
	public ThesisFormatView() {
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(ThesisFormatDataSource::getDescription).setHeader("Formato");
		this.getGrid().addColumn(ThesisFormatDataSource::getActive).setHeader("Ativo").setFlexGrow(0).setWidth("125px");
		
		this.checkActive = new Checkbox("Listar apenas formatos ativos");
		this.checkActive.setValue(true);
		
		HorizontalLayout h = new HorizontalLayout(this.checkActive);
		
		this.addFilterField(h);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try {
			ThesisFormatBO bo = new ThesisFormatBO();
	    	List<ThesisFormat> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.checkActive.getValue());
	    	
	    	this.getGrid().setItems(ThesisFormatDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Formatos", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		ThesisFormat format = new ThesisFormat();
		
		format.setDepartment(Session.getSelectedDepartment().getDepartment());
				
		EditThesisFormatWindow window = new EditThesisFormatWindow(format, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try {
			ThesisFormatBO bo = new ThesisFormatBO();
			ThesisFormat format = bo.findById((int)id);
			
			EditThesisFormatWindow window = new EditThesisFormatWindow(format, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Formato", e.getMessage());
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
