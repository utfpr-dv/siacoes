package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;

import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.ui.grid.InternshipDataSource;

public class StudentInternshipsWindow extends BasicWindow {
	
	private final Grid<InternshipDataSource> gridInternships;
	
	public StudentInternshipsWindow(int idStudent, int idDepartment) {
		super("Estágios do Acadêmico");
		
		this.gridInternships = new Grid<InternshipDataSource>();
		this.gridInternships.setSelectionMode(SelectionMode.SINGLE);
		this.gridInternships.setSizeFull();
		this.gridInternships.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridInternships.addColumn(InternshipDataSource::getCompany).setHeader("Empresa");
		this.gridInternships.addColumn(InternshipDataSource::getType).setHeader("Tipo").setFlexGrow(0).setWidth("160px");
		this.gridInternships.addColumn(new LocalDateRenderer<>(InternshipDataSource::getStartDate, "dd/MM/yyyy")).setHeader("Início").setFlexGrow(0).setWidth("125px");
		this.gridInternships.addColumn(new LocalDateRenderer<>(InternshipDataSource::getEndDate, "dd/MM/yyyy")).setHeader("Término").setFlexGrow(0).setWidth("125px");
		this.gridInternships.addColumn(InternshipDataSource::getStatus).setHeader("Situação").setFlexGrow(0).setWidth("150px");
		
		VerticalLayout layout = new VerticalLayout(this.gridInternships);
		layout.expand(this.gridInternships);
		layout.setSpacing(true);
		layout.setSizeFull();
		
		this.add(layout);
		
		this.setWidth("900px");
		this.setHeight("400px");
		
		this.loadInternships(idStudent, idDepartment);
	}
	
	private void loadInternships(int idStudent, int idDepartment) {
		try {
			List<Internship> list = new InternshipBO().listByStudent(idStudent, idDepartment);
			
			this.gridInternships.setItems(InternshipDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Estágios", e.getMessage());
		}
	}

}
