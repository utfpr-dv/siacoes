package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.github.appreciated.apexcharts.ApexCharts;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public abstract class ChartView extends LoggedView {
	
	private final VerticalLayout layoutFields;
	private final VerticalLayout layoutChart;
	private final Details panelFilter;
	private final Button buttonChart;
	
	public ChartView(SystemModule module){
		this.setProfilePerimissions(UserProfile.STUDENT);
    	
    	this.buttonChart = new Button("Gerar Gráfico", new Icon(VaadinIcon.CHART));
    	this.buttonChart.addClickListener(event -> {
        	try{
        		plotChart();
        	}catch(Exception e){
        		Logger.log(Level.SEVERE, e.getMessage(), e);
        		
        		showErrorNotification("Gerar Gráfico", e.getMessage());
        	}
        });
		this.buttonChart.setWidth("200px");
		this.buttonChart.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		this.layoutFields = new VerticalLayout();
		this.layoutFields.setSpacing(false);
		this.layoutFields.setMargin(false);
		this.layoutFields.setPadding(false);
		
		VerticalLayout vl = new VerticalLayout(this.layoutFields, this.buttonChart);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		
		this.panelFilter = new Details();
		this.panelFilter.setSummaryText("Filtros");
		this.panelFilter.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panelFilter.setOpened(true);
		this.panelFilter.getElement().getStyle().set("width", "100%");
		this.panelFilter.setContent(vl);
		
		this.layoutChart = new VerticalLayout();
		this.layoutChart.setSpacing(false);
		this.layoutChart.setMargin(false);
		this.layoutChart.setPadding(false);
		this.layoutChart.setSizeFull();
		
		VerticalLayout layout = new VerticalLayout(this.panelFilter, this.layoutChart);
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setSizeFull();
		layout.expand(this.layoutChart);
    	
    	this.setModule(module);
    	
    	this.setViewContent(layout);
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
    
    private void plotChart() throws Exception {
    	ApexCharts chart = generateChart();
    	chart.setWidth("100%");
    	
    	this.layoutChart.removeAll();
    	this.layoutChart.add(chart);
    }
    
    public static int randomColorFactor() {
        return (int)Math.round(Math.random() * 255);
    }

    public static String randomColor() {
    	return "#" + Integer.toHexString(randomColorFactor()) + Integer.toHexString(randomColorFactor()) + Integer.toHexString(randomColorFactor());
    }
    
    public static String[] getChartColors(int count) {
    	List<String> list = new ArrayList<String>();
    	
    	for(int i = 0; i < count; i++) {
    		list.add(randomColor());
    	}
    	
    	return list.toArray(new String[0]);
    }
    
    public abstract ApexCharts generateChart() throws Exception;
	
}
