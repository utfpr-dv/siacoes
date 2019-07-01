package br.edu.utfpr.dv.siacoes.view;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.bo.SystemBO;
import br.edu.utfpr.dv.siacoes.components.MonthComboBox;
import br.edu.utfpr.dv.siacoes.components.MonthComboBox.Month;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.log.LoginEvent;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class SystemInfoView extends BasicView {
	
	public static final String NAME = "systeminfo";
	
	private final TabSheet tab;
	private final Panel panelLoginFilter;
	private final YearField textLoginYear;
	private final MonthComboBox comboLoginMonth;
	private final OptionGroup optionFilterLogin;
	private final Button buttonLoginFilter;
	private final Panel panelLoginChart;
	
	public SystemInfoView() {
		this.setModule(SystemModule.GENERAL);
		
		this.setCaption("Informações do Sistema");
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.tab = new TabSheet();
		this.tab.setSizeFull();
		
		VerticalLayout tab1 = new VerticalLayout(this.getDatabaseInfo());
		tab1.setSizeFull();
		tab1.setSpacing(true);
		tab1.setMargin(true);
		this.tab.addTab(tab1, "Informações Gerais");
		
		this.textLoginYear = new YearField();
		
		this.comboLoginMonth = new MonthComboBox();
		
		this.optionFilterLogin = new OptionGroup();
		this.optionFilterLogin.addItem("Anual");
		this.optionFilterLogin.addItem("Mensal");
		this.optionFilterLogin.select(this.optionFilterLogin.getItemIds().iterator().next());
		
		this.buttonLoginFilter = new Button("Filtrar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	try{
            		filterLoginStat();
            	}catch(Exception e){
            		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            		
            		showErrorNotification("Filtrar", e.getMessage());
            	}
            }
        });
		this.buttonLoginFilter.setWidth("150px");
		this.buttonLoginFilter.setIcon(FontAwesome.FILTER);
		
		HorizontalLayout h1 = new HorizontalLayout(this.textLoginYear, this.comboLoginMonth, this.optionFilterLogin, this.buttonLoginFilter);
		h1.setSizeFull();
		h1.setSpacing(true);
		h1.setMargin(true);
		h1.setExpandRatio(this.buttonLoginFilter, 1.0f);
		
		this.panelLoginFilter = new Panel("Filtros");
		this.panelLoginFilter.setWidth("100%");
		this.panelLoginFilter.setContent(h1);
		
		this.panelLoginChart = new Panel("Gráfico");
		this.panelLoginChart.setSizeFull();
		
		VerticalLayout tab2 = new VerticalLayout(this.panelLoginFilter, this.panelLoginChart);
		tab2.setSizeFull();
		tab2.setSpacing(true);
		tab2.setMargin(true);
		tab2.setExpandRatio(this.panelLoginChart, 1.0f);
		this.tab.addTab(tab2, "Dados de Acesso");
		
		this.setContent(this.tab);
		
		try {
			this.filterLoginStat();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Panel getDatabaseInfo() {
		Panel panelDB = new Panel("Banco de Dados");
		
		try {
			GridLayout stats = new GridLayout();
			stats.setColumns(6);
			stats.setSizeFull();
			stats.setSpacing(true);
			stats.setMargin(true);
			
			stats.addComponent(this.createPanelStat("Tamanho do Banco", new SystemBO().getDatabaseSizeAsString()));
			stats.addComponent(this.createPanelStat("Certificados", new SystemBO().getCertificatesSizeAsString()));
			stats.addComponent(this.createPanelStat("Atividades Complementares", new SystemBO().getSigacSizeAsString()));
			stats.addComponent(this.createPanelStat("Estágios", new SystemBO().getSigesSizeAsString()));
			stats.addComponent(this.createPanelStat("TCC", new SystemBO().getSigetSizeAsString()));
			stats.addComponent(this.createPanelStat("Log de Eventos", new SystemBO().getLogSizeAsString()));
			
			panelDB.setContent(stats);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return panelDB;
	}
	
	private void filterLoginStat() throws SQLException {
		boolean isAnnual = this.optionFilterLogin.isSelected(this.optionFilterLogin.getItemIds().iterator().next());
		Map<Integer, Integer> list;
		int max = (isAnnual ? 12 : DateUtils.getDaysInMonth(this.comboLoginMonth.getMonth().getValue() - 1, this.textLoginYear.getYear()));
		
		if(isAnnual) {
			list = LoginEvent.listTotalByYear(this.textLoginYear.getYear());
		} else {
			list = LoginEvent.listTotalByMonth(this.textLoginYear.getYear(), this.comboLoginMonth.getMonth().getValue());
		}
		
		BarChartConfig config = new BarChartConfig();
		
		config.data().extractLabelsFromDataset(true);
			
		BarDataset ds = new BarDataset().type().label("");
		
		for(int i = 1; i <= max; i++) {
			ds.addLabeledData((isAnnual ? Month.valueOf(i).toString() : String.valueOf(i)), ((list.get(i) == null) ? 0.0 : (double)list.get(i)));
		}
		
		config.data().addDataset(ds).and();
		
		config.
	        options()
	            .responsive(true)
	            .title()
	                .display(true)
	                .position(Position.TOP)
	                .fontSize(24)
	                .text("Número de Acessos")
	                .and()
	            .legend().display(false).and()
	            .done();
		
		ChartJs chart = new ChartJs(config);
    	chart.setJsLoggingEnabled(true);
        chart.setWidth("100%");
        
        VerticalLayout vl = new VerticalLayout(chart);
    	vl.setWidth("100%");
    	vl.setHeight("95%");
    	
    	this.panelLoginChart.setContent(vl);
	}
	
	private Panel createPanelStat(String title, String value) {
    	Panel panel = new Panel(title);
    	Label label = new Label(value);
    	
    	label.setStyleName("Title");
    	
    	panel.setContent(label);
    	
    	return panel;
    }

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
