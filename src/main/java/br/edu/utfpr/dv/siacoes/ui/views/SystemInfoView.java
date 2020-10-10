package br.edu.utfpr.dv.siacoes.ui.views;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.DataLabelsBuilder;
import com.github.appreciated.apexcharts.config.builder.FillBuilder;
import com.github.appreciated.apexcharts.config.builder.PlotOptionsBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.TitleSubtitleBuilder;
import com.github.appreciated.apexcharts.config.builder.TooltipBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.tooltip.builder.YBuilder;
import com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.SystemBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.log.LoginEvent;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.MonthComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.MonthComboBox.Month;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Informações do Sistema")
@Route(value = "systeminfo", layout = MainLayout.class)
public class SystemInfoView extends LoggedView {
	
	private final Tabs tab;
	private final Details panelLoginFilter;
	private final YearField textLoginYear;
	private final MonthComboBox comboLoginMonth;
	private final RadioButtonGroup<String> optionFilterLogin;
	private final Button buttonLoginFilter;
	private final VerticalLayout layoutLoginChart;
	
	public SystemInfoView() {
		this.setModule(SystemModule.GENERAL);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		VerticalLayout tab1 = new VerticalLayout(this.getDatabaseInfo());
		tab1.setSizeFull();
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		
		this.textLoginYear = new YearField();
		
		this.comboLoginMonth = new MonthComboBox();
		
		this.optionFilterLogin = new RadioButtonGroup<String>();
		this.optionFilterLogin.setItems("Anual", "Mensal");
		this.optionFilterLogin.setValue("Anual");
		this.optionFilterLogin.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
		
		this.buttonLoginFilter = new Button("Filtrar", new Icon(VaadinIcon.FILTER), event -> {
        	try{
        		filterLoginStat();
        	}catch(Exception e){
        		Logger.log(Level.SEVERE, e.getMessage(), e);
        		
        		showErrorNotification("Filtrar", e.getMessage());
        	}
        });
		this.buttonLoginFilter.setWidth("150px");
		
		HorizontalLayout h1 = new HorizontalLayout(this.textLoginYear, this.comboLoginMonth, this.optionFilterLogin, this.buttonLoginFilter);
		h1.setSizeFull();
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		h1.expand(this.buttonLoginFilter);
		
		this.panelLoginFilter = new Details();
		this.panelLoginFilter.setSummaryText("Filtros");
		this.panelLoginFilter.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panelLoginFilter.setOpened(true);
		this.panelLoginFilter.setContent(h1);
		this.panelLoginFilter.getElement().getStyle().set("width", "100%");
		
		this.layoutLoginChart = new VerticalLayout();
		this.layoutLoginChart.setSpacing(false);
		this.layoutLoginChart.setMargin(false);
		this.layoutLoginChart.setPadding(false);
		this.layoutLoginChart.setSizeFull();
		
		VerticalLayout tab2 = new VerticalLayout(this.panelLoginFilter, this.layoutLoginChart);
		tab2.setSizeFull();
		tab2.setSpacing(false);
		tab2.setMargin(false);
		tab2.setPadding(false);
		tab2.setVisible(false);
		tab2.expand(this.layoutLoginChart);
		
		Tab tabInfo = new Tab("Informações Gerais");
		Tab tabChart = new Tab("Dados de Acesso");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tabInfo, tab1);
		tabsToPages.put(tabChart, tab2);
		Div pages = new Div(tab1, tab2);
		
		this.tab = new Tabs(tabInfo, tabChart);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(tabInfo);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setSizeFull();
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.setViewContent(layout);
		
		try {
			this.filterLoginStat();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Details getDatabaseInfo() {
		Details panelDB = new Details();
		panelDB.setSummaryText("Banco de Dados");
		panelDB.setOpened(true);
		panelDB.setEnabled(false);
		panelDB.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panelDB.getElement().getStyle().set("width", "100%");
		
		try {
			VerticalLayout stats = new VerticalLayout();
			stats.setSizeFull();
			stats.setSpacing(false);
			stats.setMargin(false);
			stats.setPadding(false);
			
			HorizontalLayout h1 = new HorizontalLayout();
			h1.setWidthFull();
			h1.setSpacing(true);
			h1.setMargin(false);
			h1.setPadding(false);
			
			HorizontalLayout h2 = new HorizontalLayout();
			h2.setWidthFull();
			h2.setSpacing(true);
			h2.setMargin(false);
			h2.setPadding(false);
			
			h1.addAndExpand(this.createPanelStat("Tamanho do Banco", new SystemBO().getDatabaseSizeAsString()));
			h1.addAndExpand(this.createPanelStat("Atividades Complementares", new SystemBO().getSigacSizeAsString()));
			h1.addAndExpand(this.createPanelStat("Estágios", new SystemBO().getSigesSizeAsString()));
			h1.addAndExpand(this.createPanelStat("TCC", new SystemBO().getSigetSizeAsString()));
			h2.addAndExpand(this.createPanelStat("Certificados", new SystemBO().getCertificatesSizeAsString()));
			h2.addAndExpand(this.createPanelStat("Assinatura de Documentos", new SystemBO().getSignatureSizeAsString()));
			h2.addAndExpand(this.createPanelStat("Log de Eventos", new SystemBO().getLogSizeAsString()));
			
			stats.add(h1, h2);
			
			panelDB.setContent(stats);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return panelDB;
	}
	
	private void filterLoginStat() throws SQLException {
		boolean isAnnual = this.optionFilterLogin.getValue().equals("Anual");
		Map<Integer, Integer> list;
		int max = (isAnnual ? 12 : DateUtils.getDaysInMonth(this.comboLoginMonth.getMonth().getValue() - 1, this.textLoginYear.getYear()));
		
		if(isAnnual) {
			list = LoginEvent.listTotalByYear(this.textLoginYear.getYear());
		} else {
			list = LoginEvent.listTotalByMonth(this.textLoginYear.getYear(), this.comboLoginMonth.getMonth().getValue());
		}
		
		List<Series> series = new ArrayList<Series>();
		List<String> categories = new ArrayList<String>();
		List<Integer> values = new ArrayList<Integer>();
		
		for(int i = 1; i <= max; i++) {
			categories.add(isAnnual ? Month.valueOf(i).toString() : String.valueOf(i));
			values.add((list.get(i) == null) ? 0 : list.get(i));
		}
		
		series.add(new Series<>("", values.toArray()));
		
		ApexCharts barChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.bar)
                        .build())
                .withPlotOptions(PlotOptionsBuilder.get()
                        .withBar(BarBuilder.get()
                                .withHorizontal(false)
                                .withColumnWidth("80%")
                                .build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false).build())
                .withColors(ChartView.getChartColors(series.size()))
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Número de Acessos")
                        .withAlign(Align.center)
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withShow(true)
                        .withWidth(2.0)
                        .withColors("transparent")
                        .build())
                .withSeries(series.toArray(new Series[0]))
                .withYaxis(YAxisBuilder.get()
                        .withTitle(TitleBuilder.get()
                                .withText("Número de Acessos")
                                .build())
                        .build())
                .withXaxis(XAxisBuilder.get().withCategories(categories).build())
                .withFill(FillBuilder.get()
                        .withOpacity(1.0).build())
                .withTooltip(TooltipBuilder.get()
                        .withY(YBuilder.get()
                                .withFormatter("function (val) {\n" +
                                        "return val + \" acesso(s)\"\n" +
                                        "}").build())
                        .build())
                .build();
    	
    	this.layoutLoginChart.removeAll();
    	this.layoutLoginChart.add(barChart);
	}
	
	private Details createPanelStat(String title, String value) {
    	Details panel = new Details();
    	panel.setSummaryText(title);
    	panel.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
    	panel.setOpened(true);
    	panel.setEnabled(false);
    	panel.getElement().getStyle().set("min-width", "300px");
    	
    	H1 label = new H1(value);
    	label.getElement().getStyle().set("margin", "0");
    	
    	HorizontalLayout h = new HorizontalLayout(label);
    	h.setMargin(false);
    	h.setPadding(false);
    	h.setSpacing(false);
    	h.setSizeFull();
    	h.setAlignItems(Alignment.CENTER);
    	
    	VerticalLayout layout = new VerticalLayout(h);
    	layout.setMargin(false);
    	layout.setPadding(false);
    	layout.setSpacing(false);
    	layout.setSizeFull();
    	layout.setAlignItems(Alignment.CENTER);
    	
    	panel.setContent(layout);
    	
    	return panel;
    }

}
