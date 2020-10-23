package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.grid.BasicDataSource;

public abstract class ListView<T> extends LoggedView implements AfterNavigationObserver {

    private final Grid<T> grid;
    private final Button buttonAdd;
    private final Button buttonEdit;
    private final Button buttonDelete;
    private final VerticalLayout layoutButtons;
    private final HorizontalLayout layoutGrid;
    private final HorizontalLayout layoutFields;
    private final VerticalLayout layoutFilter;
    private final Button buttonFilter;
    private final Label labelGridRecords;
    private final Details panelFilter;
    private final VerticalLayout layoutActions;
    
    private int gridRowCount;
    
    public ListView(SystemModule module){
    	this.setProfilePerimissions(UserProfile.STUDENT);
    	
    	this.grid = new Grid<T>();
    	this.grid.setSelectionMode(SelectionMode.SINGLE);
		this.grid.setSizeFull();
		//this.grid.removeColumnByKey("id");
		this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.grid.setColumnReorderingAllowed(false);
		this.grid.addItemDoubleClickListener(event -> {
			if(this.isEditVisible()){
				this.edit();
			}
		});
		
		this.buttonAdd = new Button("Adicionar", new Icon(VaadinIcon.PLUS), event -> {
			addClick();
        });
		this.buttonAdd.setWidth("210px");
		this.buttonAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		
		this.buttonEdit = new Button("Editar", new Icon(VaadinIcon.PENCIL), event -> {
			edit();
        });
		this.buttonEdit.setWidth("210px");
		this.buttonEdit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		this.buttonDelete = new Button("Excluir", new Icon(VaadinIcon.TRASH), event -> {
			delete();
        });
		this.buttonDelete.setWidth("210px");
		this.buttonDelete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		
		this.layoutButtons = new VerticalLayout(buttonAdd, buttonEdit, buttonDelete);
		//this.layoutButtons.setWidth("150px");
		this.layoutButtons.setSpacing(false);
		this.layoutButtons.setMargin(false);
		this.layoutButtons.setPadding(false);
		this.layoutButtons.setSizeFull();
		
		this.buttonFilter = new Button("Filtrar", new Icon(VaadinIcon.FILTER), event -> {
        	try{
        		filterClick();
        		
        		refreshGrid();
        		
        		if(gridRowCount <= 0) {
        			showWarningNotification("Listar Registros", "Não há registros para serem exibidos.");
            	}
        	}catch(Exception e){
        		Logger.log(Level.SEVERE, e.getMessage(), e);
        		
        		showErrorNotification("Filtrar", e.getMessage());
        	}
        });
		this.buttonFilter.setWidth("210px");
		
		this.labelGridRecords = new Label();
		this.labelGridRecords.getElement().getStyle().set("margin-left", "5px");
		
		this.layoutFields = new HorizontalLayout();
		this.layoutFields.setSpacing(true);
		this.layoutFields.setMargin(false);
		this.layoutFields.setPadding(false);
		this.layoutFields.setSizeFull();
		
		this.layoutFilter = new VerticalLayout(this.layoutFields, this.buttonFilter);
		this.layoutFilter.setSpacing(false);
		this.layoutFilter.setMargin(false);
		this.layoutFilter.setPadding(false);
		this.layoutFilter.setWidthFull();
		
		this.layoutGrid = new HorizontalLayout();
		this.layoutGrid.setSizeFull();
		this.layoutGrid.setSpacing(false);
		this.layoutGrid.setMargin(false);
		this.layoutGrid.setPadding(false);
		
		this.panelFilter = new Details();
		this.panelFilter.setSummaryText("Filtros");
		this.panelFilter.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panelFilter.setOpened(true);
		this.panelFilter.getElement().getStyle().set("width", "100%");
		
		this.layoutActions = new VerticalLayout();
		this.layoutActions.setWidth("250px");
		this.layoutActions.setHeight("100%");
		this.layoutActions.setSpacing(false);
		this.layoutActions.setMargin(false);
		this.layoutActions.setPadding(false);
		
		VerticalLayout v1 = new VerticalLayout(this.grid, this.labelGridRecords);
    	v1.expand(this.grid);
    	v1.setSizeFull();
    	v1.setSpacing(false);
    	v1.setMargin(false);
    	v1.setPadding(false);
    	
    	this.layoutGrid.add(v1);
    	this.layoutGrid.add(this.layoutActions);
    	this.layoutGrid.setAlignItems(Alignment.START);
    	this.layoutGrid.expand(v1);
		
    	Details panelButtons = new Details();
    	panelButtons.setSummaryText("Ações");
    	panelButtons.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
    	panelButtons.setOpened(true);
    	panelButtons.getElement().getStyle().set("width", "100%");
    	panelButtons.setContent(this.layoutButtons);
    	
    	this.layoutActions.add(panelButtons);
    	
    	//this.setSizeFull();
    	
    	this.panelFilter.setContent(this.layoutFilter);
    	
    	VerticalLayout vl = new VerticalLayout(this.panelFilter, this.layoutGrid);
		vl.setSizeFull();
		vl.expand(this.panelFilter);
		vl.expand(this.layoutGrid);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		this.setViewContent(vl);
		
		this.setModule(module);
    }
    
    protected abstract void loadGrid();
    
    public void refreshGrid(){
    	this.gridRowCount = 0;
		
    	this.loadGrid();
    	
    	try {
    		this.gridRowCount = this.grid.getListDataView().getItemCount();
    	} catch(Exception e1) {
    		try {
    			this.gridRowCount = this.grid.getLazyDataView().getItemCountEstimate();
    		} catch(Exception e2) {
    			try {
    				this.gridRowCount = this.grid.getDataProvider().size(null);
    			} finally {}
    		}
    	}
    	
    	this.labelGridRecords.setText("Listando " + String.valueOf(this.gridRowCount) + " registro(s).");
    }
    
    public void addFilterField(Component c){
    	if(c instanceof HorizontalLayout){
			((HorizontalLayout)c).setSpacing(true);
			((HorizontalLayout)c).setMargin(false);
			((HorizontalLayout)c).setPadding(false);
		}else if(c instanceof VerticalLayout){
			((VerticalLayout)c).setSpacing(false);
			((VerticalLayout)c).setMargin(false);
			((VerticalLayout)c).setPadding(false);
		}
    	
    	this.layoutFields.add(c);
    }
    
    public void addActionButton(Button button){
    	button.setWidth("210px");
    	this.layoutButtons.add(button);
    }
    
    public void addActionButton(Anchor anchor){
    	this.layoutButtons.add(anchor);
    }
    
    public void addActionPanel(Details component){
    	component.setOpened(true);
    	component.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
    	component.getElement().getStyle().set("width", "100%");
    	this.layoutActions.add(component);
    }
    
    public Grid<T> getGrid(){
    	return grid;
    }
    
    public void setFiltersVisible(boolean visible){
    	this.panelFilter.setVisible(visible);
    }
    
    public boolean isFiltersVisible(){
    	return this.panelFilter.isVisible();
    }
    
    public void setActionsVisible(boolean visible) {
    	this.layoutActions.setVisible(false);
    }
    
    public boolean isActionsVisible() {
    	return this.layoutActions.isVisible();
    }
    
    public void setAddVisible(boolean visible){
    	buttonAdd.setVisible(visible);
    }
    
    public void setEditVisible(boolean visible){
    	buttonEdit.setVisible(visible);
    }
    
    public void setDeleteVisible(boolean visible){
    	buttonDelete.setVisible(visible);
    }
    
    public boolean isAddVisible(){
    	return buttonAdd.isVisible();
    }
    
    public boolean isEditVisible(){
    	return buttonEdit.isVisible();
    }
    
    public boolean isDeleteVisible(){
    	return buttonDelete.isVisible();
    }
    
    public void setAddCaption(String caption){
    	this.buttonAdd.setText(caption);
    }
    
    public void setAddIcon(Component icon){
    	this.buttonAdd.setIcon(icon);
    }
    
    public void setEditCaption(String caption){
    	this.buttonEdit.setText(caption);
    }
    
    public void setEditIcon(Component icon){
    	this.buttonEdit.setIcon(icon);
    }
    
    public void setDeleteCaption(String caption){
    	this.buttonDelete.setText(caption);
    }
    
    public void setDeleteIcon(Component icon){
    	this.buttonDelete.setIcon(icon);
    }
    
    public String getAddCaption(){
    	return this.buttonAdd.getText();
    }
    
    public Component getAddIcon(){
    	return this.buttonAdd.getIcon();
    }
    
    public String getEditCaption(){
    	return this.buttonEdit.getText();
    }
    
    public Component getEditIcon(){
    	return this.buttonEdit.getIcon();
    }
    
    public String getDeleteCaption(){
    	return this.buttonDelete.getText();
    }
    
    public Component getDeleteIcon(){
    	return this.buttonDelete.getIcon();
    }
    
    public Object getIdSelected(){
    	BasicDataSource selected = (BasicDataSource)this.grid.asSingleSelect().getValue();
    	
    	if(selected != null) {
    		return selected.getId();
    	} else {
    		return null;
    	}
    }
    
    private void edit(){
    	Object value = this.getIdSelected();
    	
    	if(value == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para edição.");
    	}else{
    		this.editClick((int)value);	
    	}
    }
    
    private void delete() {
		Object value = this.getIdSelected();
    	
    	if(value == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para exclusão.");
    	} else {
    		ConfirmDialog.createQuestion()
    			.withIcon(new Icon(VaadinIcon.TRASH))
    	    	.withCaption("Confirma a Exclusão?")
    	    	.withMessage("Confirma a exclusão do registro?")
    	    	.withOkButton(() -> {
    	    		deleteClick((int)value);
    	    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
    	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
    	    	.open();
    	}
    }
    
    public abstract void addClick();
    
    public abstract void editClick(int id);
    
    public abstract void deleteClick(int id);
    
    public abstract void filterClick() throws Exception;
    
    @Override
	public void afterNavigation(AfterNavigationEvent event) {
		this.refreshGrid();
	}
	
}
