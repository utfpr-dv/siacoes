package br.edu.utfpr.dv.siacoes.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;

import br.edu.utfpr.dv.siacoes.components.MenuBar;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public abstract class ListView extends CustomComponent implements View {

	private MenuBar menu;
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
    
    private UserProfile profilePermissions;
    
    private SystemModule module;
    
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
		
		this.buttonEdit = new Button("Editar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	edit();
            }
        });
		this.buttonEdit.setWidth("150px");
		
		this.buttonDelete = new Button("Excluir", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	delete();
            }
        });
		this.buttonDelete.setWidth("150px");
		
		this.layoutButtons = new VerticalLayout(buttonAdd, buttonEdit, buttonDelete);
		this.layoutButtons.setWidth("150px");
		this.layoutButtons.setSpacing(true);
		
		this.buttonFilter = new Button("Filtrar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	try{
            		filterClick();
            		
            		refreshGrid();
            	}catch(Exception e){
            		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            		
            		Notification.show("Filtrar", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            	}
            }
        });
		this.buttonFilter.setWidth("150px");
		
		this.layoutFields = new HorizontalLayout();
		this.layoutFields.setSpacing(true);
		
		this.layoutFilter = new VerticalLayout(this.layoutFields, this.buttonFilter);
		this.layoutFilter.setSpacing(true);
		
		this.layoutGrid = new HorizontalLayout();
		this.layoutGrid.setSizeFull();
		this.layoutGrid.setSpacing(true);
		
		this.setModule(module);
    }
    
    public void setModule(SystemModule module){
    	this.setCaption(module.getDescription());
    	this.module = module;
    	this.menu = new MenuBar(module);
    	this.setSizeFull();
		VerticalLayout vl = new VerticalLayout(this.menu, this.layoutFilter, this.layoutGrid);
		vl.setSizeFull();
		vl.setExpandRatio(this.layoutGrid, 1);
		vl.setSpacing(true);
		this.setCompositionRoot(vl);
    }
    
    public SystemModule getModule(){
    	return this.module;
    }
    
    protected abstract void loadGrid();
    
    public void refreshGrid(){
    	this.grid = new Grid();
    	this.grid.setSizeFull();
    	this.loadGrid();
    	this.layoutGrid.removeAllComponents();
    	this.layoutGrid.addComponent(this.grid);
    	this.layoutGrid.addComponent(this.layoutButtons);
    	this.layoutGrid.setComponentAlignment(this.layoutButtons, Alignment.TOP_RIGHT);
    	this.layoutGrid.setExpandRatio(this.grid, 1);
    }
    
    public void setProfilePerimissions(UserProfile profile){
    	this.profilePermissions = profile;
    }
    
    public UserProfile getProfilePermissions(){
    	return this.profilePermissions;
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
    	layoutButtons.addComponent(button);
    }
    
    public Grid getGrid(){
    	return grid;
    }
    
    public void setFiltersVisible(boolean visible){
    	layoutFilter.setVisible(visible);
    }
    
    public boolean isFiltersVisible(){
    	return layoutFilter.isVisible();
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
    
    public void setEditCaption(String caption){
    	this.buttonEdit.setCaption(caption);
    }
    
    public void setDeleteCaption(String caption){
    	this.buttonDelete.setCaption(caption);
    }
    
    public String getAddCaption(){
    	return this.buttonAdd.getCaption();
    }
    
    public String getEditCaption(){
    	return this.buttonEdit.getCaption();
    }
    
    public String getDeleteCaption(){
    	return this.buttonDelete.getCaption();
    }
    
    public void addRowId(Object itemId, Object value){
    	this.gridItems.add(new GridItem(itemId, value));
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
    		Notification.show("Selecionar Registro", "Selecione o registro para edição.", Notification.Type.WARNING_MESSAGE);
    	}else{
    		this.editClick(value);	
    	}
    }
    
    private void delete(){
		Object value = this.getIdSelected();
    	
    	if(value == null){
    		Notification.show("Selecionar Registro", "Selecione o registro para exclusão.", Notification.Type.WARNING_MESSAGE);
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
