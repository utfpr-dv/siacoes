package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;

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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.model.InternshipByCompany;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.CityComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.CountryComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.StateComboBox;

@PageTitle("Gráfico de Estagiários por Empresa")
@Route(value = "internshipcompanychart", layout = MainLayout.class)
public class InternshipCompanyChartView extends ChartView {
	
	private final CountryComboBox comboCountry;
	private final StateComboBox comboState;
	private final CityComboBox comboCity;
	private final Select<String> comboType;
	private final Select<String> comboStatus;
	private final Select<String> comboCompanyStatus;
	private final RadioButtonGroup<String> optionDetailType;
	
	private static final String COMPANYDETAIL = "Detalhar por Empresa";
	private static final String CITYDETAIL = "Detalhar por Cidade";
	private static final String STATEDETAIL = "Detalhar por Estado";
	private static final String COUNTRYDETAIL = "Detalhar por País";
	private static final String ALL = "Todos";
	
	private static final String COMPANYWITHAGREEMENT = "Conveniada";
	private static final String COMPANYWITHOUTAGREEMENT = "Não Conveniada";
	
	public InternshipCompanyChartView(){
		super(SystemModule.SIGES);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.optionDetailType = new RadioButtonGroup<String>();
		this.optionDetailType.setItems(COMPANYDETAIL, CITYDETAIL, STATEDETAIL, COUNTRYDETAIL);
		this.optionDetailType.setValue(COMPANYDETAIL);
		
		this.comboCity = new CityComboBox();
		this.comboCity.setLabel("Cidade da Empresa Concedente");
		
		this.comboState = new StateComboBox();
		this.comboState.setLabel("Estado da Empresa Concedente");
		this.comboState.addValueChangeListener(event -> {
			if(comboState.getStateValue() == null){
				comboCity.setIdState(0);
			}else{
				comboCity.setIdState(comboState.getStateValue().getIdState());	
			}
		});
		
		this.comboCountry = new CountryComboBox();
		this.comboCountry.setLabel("País da Empresa Concedente");
		this.comboCountry.addValueChangeListener(event -> {
			if(comboCountry.getCountry() == null){
				comboState.setIdCountry(0);
			}else{
				comboState.setIdCountry(comboCountry.getCountry().getIdCountry());	
			}
		});
		
		this.comboStatus = new Select<String>();
		this.comboStatus.setLabel("Situação do Estágio");
		this.comboStatus.setWidth("195px");
		this.comboStatus.setItems(InternshipStatus.CURRENT.toString(), InternshipStatus.FINISHED.toString(), ALL);
		this.comboStatus.setValue(InternshipStatus.CURRENT.toString());
		
		this.comboType = new Select<String>();
		this.comboType.setLabel("Tipo do Estágio");
		this.comboType.setWidth("195px");
		this.comboType.setItems(InternshipType.NONREQUIRED.toString(), InternshipType.REQUIRED.toString(), ALL);
		this.comboType.setValue(ALL);
		
		this.comboCompanyStatus = new Select<String>();
		this.comboCompanyStatus.setLabel("Situação da Empresa");
		this.comboCompanyStatus.setWidth("195px");
		this.comboCompanyStatus.setItems(COMPANYWITHAGREEMENT, COMPANYWITHOUTAGREEMENT, ALL);
		this.comboCompanyStatus.setValue(ALL);
		
		this.addFilterField(new HorizontalLayout(this.comboCountry, this.comboState, this.comboCity));
		this.addFilterField(new HorizontalLayout(this.comboType, this.comboStatus, this.comboCompanyStatus));
		this.addFilterField(this.optionDetailType);
	}

	@Override
	public ApexCharts generateChart() throws Exception {
		List<InternshipByCompany> list;
		String title;
		
		if(this.optionDetailType.getValue().equals(InternshipCompanyChartView.COUNTRYDETAIL)) {
			title = "Estagiários por País";
			list = new InternshipBO().listInternshipByCountry(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 
					(this.comboType.getValue().equals(ALL) ? -1 : InternshipType.fromDescription(this.comboType.getValue()).getValue()), 
					(this.comboStatus.getValue().equals(ALL) ? -1 : InternshipStatus.fromDescription(this.comboStatus.getValue()).getValue()),
					(this.comboCompanyStatus.getValue().equals(ALL) ? -1 : (this.comboCompanyStatus.getValue().equals(COMPANYWITHAGREEMENT) ? 1 : 0)));
		} else if(this.optionDetailType.getValue().equals(InternshipCompanyChartView.STATEDETAIL)) {
			title = "Estagiários por Estado";
			list = new InternshipBO().listInternshipByState(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 
					(this.comboCountry.getCountry() == null ? 0 : this.comboCountry.getCountry().getIdCountry()), 
					(this.comboType.getValue().equals(ALL) ? -1 : InternshipType.fromDescription(this.comboType.getValue()).getValue()), 
					(this.comboStatus.getValue().equals(ALL) ? -1 : InternshipStatus.fromDescription(this.comboStatus.getValue()).getValue()),
					(this.comboCompanyStatus.getValue().equals(ALL) ? -1 : (this.comboCompanyStatus.getValue().equals(COMPANYWITHAGREEMENT) ? 1 : 0)));
		} else if(this.optionDetailType.getValue().equals(InternshipCompanyChartView.CITYDETAIL)) {
			title = "Estagiários por Cidade";
			list = new InternshipBO().listInternshipByCity(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 
					(this.comboCountry.getCountry() == null ? 0 : this.comboCountry.getCountry().getIdCountry()), 
					(this.comboState.getStateValue() == null ? 0 : this.comboState.getStateValue().getIdState()), 
					(this.comboType.getValue().equals(ALL) ? -1 : InternshipType.fromDescription(this.comboType.getValue()).getValue()), 
					(this.comboStatus.getValue().equals(ALL) ? -1 : InternshipStatus.fromDescription(this.comboStatus.getValue()).getValue()),
					(this.comboCompanyStatus.getValue().equals(ALL) ? -1 : (this.comboCompanyStatus.getValue().equals(COMPANYWITHAGREEMENT) ? 1 : 0)));
		} else {
			title = "Estagiários por Empresa";
			list = new InternshipBO().listInternshipByCompany(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 
					(this.comboCountry.getCountry() == null ? 0 : this.comboCountry.getCountry().getIdCountry()), 
					(this.comboState.getStateValue() == null ? 0 : this.comboState.getStateValue().getIdState()), 
					(this.comboCity.getCity() == null ? 0 : this.comboCity.getCity().getIdCity()), 
					(this.comboType.getValue().equals(ALL) ? -1 : InternshipType.fromDescription(this.comboType.getValue()).getValue()), 
					(this.comboStatus.getValue().equals(ALL) ? -1 : InternshipStatus.fromDescription(this.comboStatus.getValue()).getValue()),
					(this.comboCompanyStatus.getValue().equals(ALL) ? -1 : (this.comboCompanyStatus.getValue().equals(COMPANYWITHAGREEMENT) ? 1 : 0)));
		}
		
		List<String> categories = new ArrayList<String>();
		List<Integer> values = new ArrayList<Integer>();
		
		for(InternshipByCompany item : list) {
			categories.add(item.getCompanyName());
			values.add(item.getTotalStudents());
		}
		
		List<Series> series = new ArrayList<Series>();
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
                        .withText(title)
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
                                .withText("Número de Estagiários")
                                .build())
                        .build())
                .withXaxis(XAxisBuilder.get().withCategories(categories).build())
                .withFill(FillBuilder.get()
                        .withOpacity(1.0).build())
                .withTooltip(TooltipBuilder.get()
                        .withY(YBuilder.get()
                                .withFormatter("function (val) {\n" +
                                        "return val + \" estagirário(s)\"\n" +
                                        "}").build())
                        .build())
                .build();
		
		return barChart;
	}

}
