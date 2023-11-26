package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.EvaluationItemBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisFormatBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem;
import br.edu.utfpr.dv.siacoes.model.ThesisFormat;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.EvaluationItemDataSource;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditThesisFormatWindow extends EditWindow {
	
	private final ThesisFormat format;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textDescription;
	private final Checkbox checkActive;
	
	private final Tabs tab;
	private final VerticalLayout tabFormat;
	private final VerticalLayout tabStage1;
	private final VerticalLayout tabStage2;
	
	private Grid<EvaluationItemDataSource> gridStage1;
	private final Button buttonAddItemStage1;
	private final Button buttonEditItemStage1;
	private final Button buttonRemoveItemStage1;
	private final Button buttonMoveUpStage1;
	private final Button buttonMoveDownStage1;
	
	private Grid<EvaluationItemDataSource> gridStage2;
	private final Button buttonAddItemStage2;
	private final Button buttonEditItemStage2;
	private final Button buttonRemoveItemStage2;
	private final Button buttonMoveUpStage2;
	private final Button buttonMoveDownStage2;
	
	public EditThesisFormatWindow(ThesisFormat format, ListView parentView) {
		super("Editar Formato", parentView);
		
		if(format == null) {
			this.format = new ThesisFormat();
			this.format.setDepartment(Session.getSelectedDepartment().getDepartment());
		} else {
			this.format = format;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setWidth("800px");
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setWidth("800px");
		this.comboDepartment.setEnabled(false);
		
		this.textDescription = new TextField("Descrição");
		this.textDescription.setWidth("800px");
		this.textDescription.setMaxLength(255);
		this.textDescription.setRequired(true);
		
		this.checkActive = new Checkbox("Ativo");
		
		this.tabFormat = new VerticalLayout();
		this.tabFormat.setSpacing(false);
		this.tabFormat.setMargin(false);
		this.tabFormat.setPadding(false);
		
		this.tabFormat.add(this.comboCampus);
		this.tabFormat.add(this.comboDepartment);
		this.tabFormat.add(this.textDescription);
		this.tabFormat.add(this.checkActive);
		
		this.buttonAddItemStage1 = new Button("Adicionar", new Icon(VaadinIcon.PLUS), event -> {
            addEvaluationItem(1);
        });
		this.buttonAddItemStage1.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonAddItemStage1.setWidth("150px");
		
		this.buttonEditItemStage1 = new Button("Editar", new Icon(VaadinIcon.EDIT), event -> {
            editEvaluationItem(1);
        });
		this.buttonEditItemStage1.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.buttonEditItemStage1.setWidth("150px");
		
		this.buttonRemoveItemStage1 = new Button("Remover", new Icon(VaadinIcon.TRASH), event -> {
            removeEvaluationItem(1);
        });
		this.buttonRemoveItemStage1.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonRemoveItemStage1.setWidth("150px");
		
		this.buttonMoveUpStage1 = new Button("Para Cima", new Icon(VaadinIcon.ARROW_UP), event -> {
			moveUpEvaluationItem(1);
        });
		this.buttonMoveUpStage1.setWidth("150px");
    	
    	this.buttonMoveDownStage1 = new Button("Para Baixo", new Icon(VaadinIcon.ARROW_DOWN), event -> {
    		moveDownEvaluationItem(1);
        });
    	this.buttonMoveDownStage1.setWidth("150px");
		
		HorizontalLayout h2 = new HorizontalLayout(this.buttonAddItemStage1, this.buttonEditItemStage1, this.buttonRemoveItemStage1, this.buttonMoveUpStage1, this.buttonMoveDownStage1);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		
		this.gridStage1 = new Grid<EvaluationItemDataSource>();
    	this.gridStage1.setSelectionMode(SelectionMode.SINGLE);
    	this.gridStage1.setWidth("820px");
		this.gridStage1.setHeight("250px");
		this.gridStage1.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridStage1.addItemDoubleClickListener(event -> {
			if(this.buttonEditItemStage1.isVisible()){
				this.editEvaluationItem(1);
			}
		});
		this.gridStage1.addColumn(EvaluationItemDataSource::getType).setHeader("Avaliação").setFlexGrow(0).setWidth("200px");
		this.gridStage1.addColumn(EvaluationItemDataSource::getDescription).setHeader("Quesito");
		
		this.tabStage1 = new VerticalLayout(this.gridStage1, h2);
		this.tabStage1.setSpacing(false);
		this.tabStage1.setMargin(false);
		this.tabStage1.setPadding(false);
		this.tabStage1.setVisible(false);
		
		this.buttonAddItemStage2 = new Button("Adicionar", new Icon(VaadinIcon.PLUS), event -> {
            addEvaluationItem(2);
        });
		this.buttonAddItemStage2.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonAddItemStage2.setWidth("150px");
		
		this.buttonEditItemStage2 = new Button("Editar", new Icon(VaadinIcon.EDIT), event -> {
            editEvaluationItem(2);
        });
		this.buttonEditItemStage2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.buttonEditItemStage2.setWidth("150px");
		
		this.buttonRemoveItemStage2 = new Button("Remover", new Icon(VaadinIcon.TRASH), event -> {
            removeEvaluationItem(2);
        });
		this.buttonRemoveItemStage2.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonRemoveItemStage2.setWidth("150px");
		
		this.buttonMoveUpStage2 = new Button("Para Cima", new Icon(VaadinIcon.ARROW_UP), event -> {
			moveUpEvaluationItem(2);
        });
		this.buttonMoveUpStage2.setWidth("150px");
    	
    	this.buttonMoveDownStage2 = new Button("Para Baixo", new Icon(VaadinIcon.ARROW_DOWN), event -> {
    		moveDownEvaluationItem(2);
        });
    	this.buttonMoveDownStage2.setWidth("150px");
		
		HorizontalLayout h3 = new HorizontalLayout(this.buttonAddItemStage2, this.buttonEditItemStage2, this.buttonRemoveItemStage2, this.buttonMoveUpStage2, this.buttonMoveDownStage2);
		h3.setSpacing(true);
		h3.setMargin(false);
		h3.setPadding(false);
		
		this.gridStage2 = new Grid<EvaluationItemDataSource>();
    	this.gridStage2.setSelectionMode(SelectionMode.SINGLE);
    	this.gridStage2.setWidth("820px");
		this.gridStage2.setHeight("250px");
		this.gridStage2.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridStage2.addItemDoubleClickListener(event -> {
			if(this.buttonEditItemStage2.isVisible()){
				this.editEvaluationItem(2);
			}
		});
		this.gridStage2.addColumn(EvaluationItemDataSource::getType).setHeader("Avaliação").setFlexGrow(0).setWidth("200px");
		this.gridStage2.addColumn(EvaluationItemDataSource::getDescription).setHeader("Quesito");
		
		this.tabStage2 = new VerticalLayout(this.gridStage2, h3);
		this.tabStage2.setSpacing(false);
		this.tabStage2.setMargin(false);
		this.tabStage2.setPadding(false);
		this.tabStage2.setVisible(false);
		
		Tab tab1 = new Tab("Formato");
		Tab tab2 = new Tab("Quesitos TCC 1");
		Tab tab3 = new Tab("Quesitos TCC 2");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1, this.tabFormat);
		tabsToPages.put(tab2, this.tabStage1);
		tabsToPages.put(tab3, this.tabStage2);
		Div pages = new Div(this.tabFormat, this.tabStage1, this.tabStage2);
		
		this.tab = new Tabs(tab1, tab2, tab3);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(tab1);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setSpacing(false);
		layout.setWidth("820px");
		layout.setHeight("370px");
		
		this.addField(layout);
		
		this.loadThesisFormat();
		this.textDescription.focus();
	}
	
	private void loadThesisFormat() {
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.format.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(this.format.getDepartment());
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textDescription.setValue(this.format.getDescription());
		this.checkActive.setValue(this.format.isActive());
		
		this.loadEvaluationItems();
		this.disableItemButtons();
	}
	
	private void loadEvaluationItems() {
		List<EvaluationItemDataSource> dsStage1 = new ArrayList<EvaluationItemDataSource>();
		List<EvaluationItemDataSource> dsStage2 = new ArrayList<EvaluationItemDataSource>();
		
		for(EvaluationItem item : this.format.getItems()) {
			if(item.getStage() == 1) {
				dsStage1.add(new EvaluationItemDataSource(item));
			} else {
				dsStage2.add(new EvaluationItemDataSource(item));
			}
		}
		
		this.gridStage1.setItems(dsStage1);
		this.gridStage2.setItems(dsStage2);
	}
	
	private void disableItemButtons() {
		if(this.format.getIdThesisFormat() > 0) {
			boolean disable = false;
			
			for(EvaluationItem item : this.format.getItems()) {
				if(new EvaluationItemBO().hasScores(item.getIdEvaluationItem())) {
					disable = true;
					break;
				}
			}
			
			if(disable) {
				this.buttonAddItemStage1.setEnabled(false);
				this.buttonEditItemStage1.setEnabled(false);
				this.buttonRemoveItemStage1.setEnabled(false);
				this.buttonMoveUpStage1.setEnabled(false);
				this.buttonMoveDownStage1.setEnabled(false);
				this.buttonAddItemStage2.setEnabled(false);
				this.buttonEditItemStage2.setEnabled(false);
				this.buttonRemoveItemStage2.setEnabled(false);
				this.buttonMoveUpStage2.setEnabled(false);
				this.buttonMoveDownStage2.setEnabled(false);
			}
		}
	}
	
	public void saveEvaluationItem(EvaluationItem item, int index) {
		if(index < 0) {
			this.format.getItems().add(item);
		} else {
			this.format.getItems().set(index, item);
		}
		
		this.loadEvaluationItems();
	}
	
	private int getIndexEvaluationItem(EvaluationItemDataSource ds) {
		if(ds.getId() > 0) {
			for(int i = 0; i < this.format.getItems().size(); i++) {
				if(ds.getId() == this.format.getItems().get(i).getIdEvaluationItem()) {
					return i;
				}
			}
		} else {
			for(int i = 0; i < this.format.getItems().size(); i++) {
				if((ds.getStage() == this.format.getItems().get(i).getStage()) && ds.getType().equals(this.format.getItems().get(i).getType().toString()) && ds.getDescription().equals(this.format.getItems().get(i).getDescription())) {
					return i;
				}
			}
		}
		
		return -1;
	}

	@Override
	public void save() {
		try {
			ThesisFormatBO bo = new ThesisFormatBO();
			
			this.format.setDescription(this.textDescription.getValue());
			this.format.setActive(this.checkActive.getValue());
			
			bo.save(Session.getIdUserLog(), this.format);
			
			this.showSuccessNotification("Salvar Formato", "Formato salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Formato", e.getMessage());
		}
	}
	
	private void addEvaluationItem(int stage) {
		EvaluationItem item = new EvaluationItem();
		
		this.format.setDescription(this.textDescription.getValue());
		item.setFormat(this.format);
		item.setStage(stage);
		
		EditEvaluationItemWindow window = new EditEvaluationItemWindow(item, this, -1);
		window.open();
	}
	
	private void editEvaluationItem(int stage) {
		EvaluationItemDataSource ds;
		
		if(stage == 1) {
			ds = this.gridStage1.asSingleSelect().getValue();
		} else {
			ds = this.gridStage2.asSingleSelect().getValue();
		}
		
		if(ds == null) {
			this.showWarningNotification("Editar Quesito", "Selecione o quesito para editar.");
		} else {
			try {
				int index = this.getIndexEvaluationItem(ds);
				
				if(index < 0) {
					this.showWarningNotification("Editar Quesito", "Selecione o quesito para editar.");
				} else {
					EvaluationItem item = new EvaluationItem();
					
					this.format.setDescription(this.textDescription.getValue());
					
					item = this.format.getItems().get(index);
					item.setFormat(this.format);
					
					EditEvaluationItemWindow window = new EditEvaluationItemWindow(item, this, index);
					window.open();
				}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Editar Quesito", e.getMessage());
			}
		}
	}
	
	private void removeEvaluationItem(int stage) {
		EvaluationItemDataSource ds;
		
		if(stage == 1) {
			ds = this.gridStage1.asSingleSelect().getValue();
		} else {
			ds = this.gridStage2.asSingleSelect().getValue();
		}
		
		if(ds == null) {
			this.showWarningNotification("Remover Quesito", "Selecione o quesito para remover.");
		} else {
			try {
				ConfirmDialog.createQuestion()
    			.withIcon(new Icon(VaadinIcon.TRASH))
    	    	.withCaption("Confirma a Exclusão?")
    	    	.withMessage("Confirma a remoção do perfil do usuário?")
    	    	.withOkButton(() -> {
    	    		int index = this.getIndexEvaluationItem(ds);
    				
    				if(index < 0) {
    					this.showWarningNotification("Remover Quesito", "Selecione o quesito para remover.");
    				} else {
    					boolean hasScores = false;
    					
    					if(this.format.getItems().get(index).getIdEvaluationItem() > 0) {
    						hasScores = new EvaluationItemBO().hasScores(this.format.getItems().get(index).getIdEvaluationItem());
    					}
    					
    					if(hasScores) {
    						this.showErrorNotification("Remover Quesito", "Este quesito já tem notas lançadas e não pode ser excluído.");
    					} else {
    						this.format.getItems().remove(index);
        					
        					this.loadEvaluationItems();	
    					}
    				}
    	    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
    	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
    	    	.open();
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Remover Quesito", e.getMessage());
			}
		}
	}
	
	private void moveUpEvaluationItem(int stage) {
		EvaluationItemDataSource ds;
		
		if(stage == 1) {
			ds = this.gridStage1.asSingleSelect().getValue();
		} else {
			ds = this.gridStage2.asSingleSelect().getValue();
		}
		
		if(ds == null) {
			this.showWarningNotification("Mover Quesito", "Selecione o quesito para mover.");
		} else {
			int index = this.getIndexEvaluationItem(ds);
			
			if(index < 0) {
				this.showWarningNotification("Mover Quesito", "Selecione o quesito para mover.");
			} else if(index > 0) {
				int stageUp;
				
				do {
					stageUp = this.format.getItems().get(index - 1).getStage();
					EvaluationItem temp = this.format.getItems().get(index - 1);
					
					this.format.getItems().set(index - 1, this.format.getItems().get(index));
					this.format.getItems().set(index, temp);
					
					index = index - 1;
				} while((stageUp != stage) && (index > 0));
				
				this.loadEvaluationItems();
			}
		}
	}
	
	private void moveDownEvaluationItem(int stage) {
		EvaluationItemDataSource ds;
		
		if(stage == 1) {
			ds = this.gridStage1.asSingleSelect().getValue();
		} else {
			ds = this.gridStage2.asSingleSelect().getValue();
		}
		
		if(ds == null) {
			this.showWarningNotification("Mover Quesito", "Selecione o quesito para mover.");
		} else {
			int index = this.getIndexEvaluationItem(ds);
			
			if(index < 0) {
				this.showWarningNotification("Mover Quesito", "Selecione o quesito para mover.");
			} else if(index < this.format.getItems().size() - 1) {
				int stageDown;
				
				do {
					stageDown = this.format.getItems().get(index + 1).getStage();
					EvaluationItem temp = this.format.getItems().get(index + 1);
					
					this.format.getItems().set(index + 1, this.format.getItems().get(index));
					this.format.getItems().set(index, temp);
					
					index = index + 1;
				} while((stageDown != stage) && (index < this.format.getItems().size() - 1));
				
				this.loadEvaluationItems();
			}
		}
	}

}
