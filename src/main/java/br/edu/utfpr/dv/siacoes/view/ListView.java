package br.edu.utfpr.dv.siacoes.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public abstract class ListView extends BasicView {

    private Grid grid;
    private final Button buttonAdd;
    private final Button buttonEdit;
    private final Button buttonDelete;
    private final VerticalLayout layoutButtons;
    private final HorizontalLayout layoutGrid;
    private final HorizontalLayout layoutFields;
    private final VerticalLayout layoutFilter;
    private final Button buttonFilter;
    private final List<GridItem> gridItems;
    private final Label labelGridRecords;
    private final Panel panelFilter;
    private final VerticalLayout layoutActions;
    
    private int gridRowCount;
    
    public ListView(SystemModule module){
    	this.setProfilePerimissions(UserProfile.STUDENT);
    	
    	this.gridItems = new ArrayList<GridItem>();
    	
    	this.grid = new Grid();
    	this.grid.setSelectionMode(SelectionMode.SINGLE);
		((SingleSelectionModel)this.grid.getSelectionModel()).setDeselectAllowed(false);
		this.grid.setSizeFull();
		this.grid.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick() && isEditVisible()){
					edit();
				}
			}
		});
		
		this.buttonAdd = new Button("Adicionar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addClick();
            }
        });
		this.buttonAdd.setWidth("150px");
		this.buttonAdd.setIcon(FontAwesome.PLUS);
		this.buttonAdd.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		
		this.buttonEdit = new Button("Editar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	edit();
            }
        });
		this.buttonEdit.setWidth("150px");
		this.buttonEdit.setIcon(FontAwesome.EDIT);
		this.buttonEdit.addStyleName(ValoTheme.BUTTON_PRIMARY);
		
		this.buttonDelete = new Button("Excluir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	delete();
            }
        });
		this.buttonDelete.setWidth("150px");
		this.buttonDelete.setIcon(FontAwesome.TRASH_O);
		this.buttonDelete.addStyleName(ValoTheme.BUTTON_DANGER);
		
		this.layoutButtons = new VerticalLayout(buttonAdd, buttonEdit, buttonDelete);
		//this.layoutButtons.setWidth("150px");
		this.layoutButtons.setSpacing(true);
		this.layoutButtons.setMargin(true);
		this.layoutButtons.setSizeFull();
		
		this.buttonFilter = new Button("Filtrar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	try{
            		filterClick();
            		
            		refreshGrid();
            		
            		if(gridRowCount <= 0) {
            			showWarningNotification("Listar Registros", "Não há registros para serem exibidos.");
                	}
            	}catch(Exception e){
            		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            		
            		showErrorNotification("Filtrar", e.getMessage());
            	}
            }
        });
		this.buttonFilter.setWidth("150px");
		this.buttonFilter.setIcon(FontAwesome.FILTER);
		
		this.labelGridRecords = new Label();
		
		this.layoutFields = new HorizontalLayout();
		this.layoutFields.setSpacing(true);
		
		this.layoutFilter = new VerticalLayout(this.layoutFields, this.buttonFilter);
		this.layoutFilter.setSpacing(true);
		this.layoutFilter.setMargin(true);
		
		this.layoutGrid = new HorizontalLayout();
		this.layoutGrid.setSizeFull();
		//this.layoutGrid.setSpacing(true);
		
		this.panelFilter = new Panel("Filtros");
		
		this.layoutActions = new VerticalLayout();
		this.layoutActions.setWidth("170px");
		this.layoutActions.setHeight("100%");
		//this.layoutActions.setSpacing(true);
		
		Panel panelButtons = new Panel("Ações");
    	panelButtons.setContent(this.layoutButtons);
    	
    	this.layoutActions.addComponent(panelButtons);
    	
    	this.setSizeFull();
    	
    	this.panelFilter.setContent(this.layoutFilter);
    	
    	VerticalLayout vl = new VerticalLayout(this.panelFilter, this.layoutGrid);
		vl.setSizeFull();
		vl.setExpandRatio(this.layoutGrid, 1);
		//vl.setSpacing(true);
		this.setContent(vl);
		
		this.setModule(module);
    }
    
    protected abstract void loadGrid();
    
    public void refreshGrid(){
    	this.gridRowCount = 0;
    	this.grid = new Grid();
    	this.grid.setSizeFull();
    	this.loadGrid();
    	this.layoutGrid.removeAllComponents();
    	
    	VerticalLayout v1 = new VerticalLayout(this.grid, this.labelGridRecords);
    	v1.setExpandRatio(this.grid, 1);
    	v1.setExpandRatio(this.labelGridRecords, 0);
    	v1.setSizeFull();
    	
    	this.layoutGrid.addComponent(v1);
    	this.layoutGrid.addComponent(this.layoutActions);
    	this.layoutGrid.setComponentAlignment(this.layoutActions, Alignment.TOP_RIGHT);
    	this.layoutGrid.setExpandRatio(v1, 1);
    	
    	this.labelGridRecords.setCaption("Listando " + String.valueOf(this.gridRowCount) + " registro(s).");
    }
    
    public void addFilterField(Component c){
    	if(c instanceof HorizontalLayout){
			((HorizontalLayout)c).setSpacing(true);
		}else if(c instanceof VerticalLayout){
			((VerticalLayout)c).setSpacing(true);
		}
    	
    	layoutFields.addComponent(c);
    }
    
    public void addActionButton(Button button){
    	button.setWidth("150px");
    	this.layoutButtons.addComponent(button);
    }
    
    public void addActionPanel(Panel panel){
    	panel.setWidth("170px");
    	this.layoutActions.addComponent(panel);
    	this.layoutActions.setExpandRatio(panel, 1);
    }
    
    public Grid getGrid(){
    	return grid;
    }
    
    public void setFiltersVisible(boolean visible){
    	this.panelFilter.setVisible(visible);
    }
    
    public boolean isFiltersVisible(){
    	return this.panelFilter.isVisible();
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
    	this.buttonAdd.setCaption(caption);
    }
    
    public void setAddIcon(Resource icon){
    	this.buttonAdd.setIcon(icon);
    }
    
    public void setEditCaption(String caption){
    	this.buttonEdit.setCaption(caption);
    }
    
    public void setEditIcon(Resource icon){
    	this.buttonEdit.setIcon(icon);
    }
    
    public void setDeleteCaption(String caption){
    	this.buttonDelete.setCaption(caption);
    }
    
    public void setDeleteIcon(Resource icon){
    	this.buttonDelete.setIcon(icon);
    }
    
    public String getAddCaption(){
    	return this.buttonAdd.getCaption();
    }
    
    public Resource getAddIcon(){
    	return this.buttonAdd.getIcon();
    }
    
    public String getEditCaption(){
    	return this.buttonEdit.getCaption();
    }
    
    public Resource getEditIcon(){
    	return this.buttonEdit.getIcon();
    }
    
    public String getDeleteCaption(){
    	return this.buttonDelete.getCaption();
    }
    
    public Resource getDeleteIcon(){
    	return this.buttonDelete.getIcon();
    }
    
    public void addRowId(Object itemId, Object value){
    	this.gridItems.add(new GridItem(itemId, value));
    	this.gridRowCount++;
    }
    
    public Object getIdSelected(){
    	Object itemId = grid.getSelectedRow();
    	Object value = null;
    	
    	for(GridItem i : this.gridItems){
    		if(i.itemId == itemId){
    			value = i.value;
    		}
    	}
    	
    	return value;
    }
    
    private void edit(){
    	Object value = this.getIdSelected();
    	
    	if(value == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para edição.");
    	}else{
    		this.editClick(value);	
    	}
    }
    
    private void delete(){
		Object value = this.getIdSelected();
    	
    	if(value == null){
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para exclusão.");
    	}else{
    		ConfirmDialog.show(UI.getCurrent(), "Confirma a exclusão do registro?", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	deleteClick(value);
                    }
                }
            });
    	}
    }
    
    public abstract void addClick();
    
    public abstract void editClick(Object id);
    
    public abstract void deleteClick(Object id);
    
    public abstract void filterClick() throws Exception;

	@Override
	public void enter(ViewChangeEvent event){
		this.refreshGrid();
	}
	
	private class GridItem{
		public Object itemId;
		public Object value;
		
		public GridItem(Object itemId, Object value){
			this.itemId = itemId;
			this.value = value;
		}
	}
	
}
