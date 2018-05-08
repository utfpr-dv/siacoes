package br.edu.utfpr.dv.siacoes.view;

import java.util.List;

import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.base.elements.XYseries;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.data.Ticks;
import org.dussan.vaadin.dcharts.metadata.LegendPlacements;
import org.dussan.vaadin.dcharts.metadata.SeriesToggles;
import org.dussan.vaadin.dcharts.metadata.TooltipAxes;
import org.dussan.vaadin.dcharts.metadata.locations.TooltipLocations;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.Highlighter;
import org.dussan.vaadin.dcharts.options.Legend;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.Series;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.options.Title;
import org.dussan.vaadin.dcharts.renderers.legend.EnhancedLegendRenderer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.components.CityComboBox;
import br.edu.utfpr.dv.siacoes.components.CountryComboBox;
import br.edu.utfpr.dv.siacoes.components.StateComboBox;
import br.edu.utfpr.dv.siacoes.model.InternshipByCompany;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class InternshipCompanyChartView extends ChartView {
	
	public static final String NAME = "internshipcompanychart";
	
	private final CountryComboBox comboCountry;
	private final StateComboBox comboState;
	private final CityComboBox comboCity;
	private final NativeSelect comboType;
	private final NativeSelect comboStatus;
	private final NativeSelect comboCompanyStatus;
	private final OptionGroup optionDetailType;
	
	private static final String COMPANYDETAIL = "Detalhar por Empresa";
	private static final String CITYDETAIL = "Detalhar por Cidade";
	private static final String STATEDETAIL = "Detalhar por Estado";
	private static final String COUNTRYDETAIL = "Detalhar por País";
	
	private static final String COMPANYWITHAGREEMENT = "Conveniada";
	private static final String COMPANYWITHOUTAGREEMENT = "Não Conveniada";
	
	public InternshipCompanyChartView(){
		super(SystemModule.SIGES);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Gráfico de Estagiários por Empresa");
		
		this.optionDetailType = new OptionGroup();
		this.optionDetailType.addItem(InternshipCompanyChartView.COMPANYDETAIL);
		this.optionDetailType.addItem(InternshipCompanyChartView.CITYDETAIL);
		this.optionDetailType.addItem(InternshipCompanyChartView.STATEDETAIL);
		this.optionDetailType.addItem(InternshipCompanyChartView.COUNTRYDETAIL);
		this.optionDetailType.select(InternshipCompanyChartView.COMPANYDETAIL);
		
		this.comboCity = new CityComboBox();
		this.comboCity.setCaption("Cidade da Empresa Concedente");
		
		this.comboState = new StateComboBox();
		this.comboState.setCaption("Estado da Empresa Concedente");
		this.comboState.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(comboState.getStateValue() == null){
					comboCity.setIdState(0);
				}else{
					comboCity.setIdState(comboState.getStateValue().getIdState());	
				}
			}
		});
		
		this.comboCountry = new CountryComboBox();
		this.comboCountry.setCaption("País da Empresa Concedente");
		this.comboCountry.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(comboCountry.getCountry() == null){
					comboState.setIdCountry(0);
				}else{
					comboState.setIdCountry(comboCountry.getCountry().getIdCountry());	
				}
			}
		});
		
		this.comboStatus = new NativeSelect("Situação do Estágio");
		this.comboStatus.setWidth("195px");
		this.comboStatus.setNullSelectionAllowed(false);
		this.comboStatus.addItem(InternshipStatus.CURRENT);
		this.comboStatus.addItem(InternshipStatus.FINISHED);
		this.comboStatus.addItem("Todos");
		this.comboStatus.select(InternshipStatus.CURRENT);
		
		this.comboType = new NativeSelect("Tipo do Estágio");
		this.comboType.setWidth("195px");
		this.comboType.setNullSelectionAllowed(false);
		this.comboType.addItem(InternshipType.NONREQUIRED);
		this.comboType.addItem(InternshipType.REQUIRED);
		this.comboType.addItem("Todos");
		this.comboType.select("Todos");
		
		this.comboCompanyStatus = new NativeSelect("Situação da Empresa");
		this.comboCompanyStatus.setWidth("195px");
		this.comboCompanyStatus.setNullSelectionAllowed(false);
		this.comboCompanyStatus.addItem(InternshipCompanyChartView.COMPANYWITHAGREEMENT);
		this.comboCompanyStatus.addItem(InternshipCompanyChartView.COMPANYWITHOUTAGREEMENT);
		this.comboCompanyStatus.addItem("Todas");
		this.comboCompanyStatus.select("Todas");

		VerticalLayout vl = new VerticalLayout(this.comboCountry, this.comboState, this.comboCity);
		vl.setSpacing(true);
		
		this.addFilterField(new HorizontalLayout(vl, this.optionDetailType));
		this.addFilterField(new HorizontalLayout(this.comboType, this.comboStatus, this.comboCompanyStatus));
	}

	@Override
	public DCharts generateChart() throws Exception {
		List<InternshipByCompany> list;
		Title title;
		
		if(this.optionDetailType.getValue().equals(InternshipCompanyChartView.COUNTRYDETAIL)) {
			title = new Title("Estagiários por País");
			list = new InternshipBO().listInternshipByCountry(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 
					(this.comboType.getValue().equals("Todos") ? -1 : ((InternshipType)this.comboType.getValue()).getValue()), 
					(this.comboStatus.getValue().equals("Todos") ? -1 : ((InternshipStatus)this.comboStatus.getValue()).getValue()),
					(this.comboCompanyStatus.getValue().equals("Todas") ? -1 : (this.comboCompanyStatus.getValue().equals(InternshipCompanyChartView.COMPANYWITHAGREEMENT) ? 1 : 0)));
		} else if(this.optionDetailType.getValue().equals(InternshipCompanyChartView.STATEDETAIL)) {
			title = new Title("Estagiários por Estado");
			list = new InternshipBO().listInternshipByState(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 
					(this.comboCountry.getCountry() == null ? 0 : this.comboCountry.getCountry().getIdCountry()), 
					(this.comboType.getValue().equals("Todos") ? -1 : ((InternshipType)this.comboType.getValue()).getValue()), 
					(this.comboStatus.getValue().equals("Todos") ? -1 : ((InternshipStatus)this.comboStatus.getValue()).getValue()),
					(this.comboCompanyStatus.getValue().equals("Todas") ? -1 : (this.comboCompanyStatus.getValue().equals(InternshipCompanyChartView.COMPANYWITHAGREEMENT) ? 1 : 0)));
		} else if(this.optionDetailType.getValue().equals(InternshipCompanyChartView.CITYDETAIL)) {
			title = new Title("Estagiários por Cidade");
			list = new InternshipBO().listInternshipByCity(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 
					(this.comboCountry.getCountry() == null ? 0 : this.comboCountry.getCountry().getIdCountry()), 
					(this.comboState.getStateValue() == null ? 0 : this.comboState.getStateValue().getIdState()), 
					(this.comboType.getValue().equals("Todos") ? -1 : ((InternshipType)this.comboType.getValue()).getValue()), 
					(this.comboStatus.getValue().equals("Todos") ? -1 : ((InternshipStatus)this.comboStatus.getValue()).getValue()),
					(this.comboCompanyStatus.getValue().equals("Todas") ? -1 : (this.comboCompanyStatus.getValue().equals(InternshipCompanyChartView.COMPANYWITHAGREEMENT) ? 1 : 0)));
		} else {
			title = new Title("Estagiários por Empresa");
			list = new InternshipBO().listInternshipByCompany(Session.getSelectedDepartment().getDepartment().getIdDepartment(), 
					(this.comboCountry.getCountry() == null ? 0 : this.comboCountry.getCountry().getIdCountry()), 
					(this.comboState.getStateValue() == null ? 0 : this.comboState.getStateValue().getIdState()), 
					(this.comboCity.getCity() == null ? 0 : this.comboCity.getCity().getIdCity()), 
					(this.comboType.getValue().equals("Todos") ? -1 : ((InternshipType)this.comboType.getValue()).getValue()), 
					(this.comboStatus.getValue().equals("Todos") ? -1 : ((InternshipStatus)this.comboStatus.getValue()).getValue()),
					(this.comboCompanyStatus.getValue().equals("Todas") ? -1 : (this.comboCompanyStatus.getValue().equals(InternshipCompanyChartView.COMPANYWITHAGREEMENT) ? 1 : 0)));
		}
		
		DataSeries dataSeries = new DataSeries();
		
		SeriesDefaults seriesDefaults = new SeriesDefaults().setRenderer(SeriesRenderers.BAR);
		Series series = new Series();
		
		for(InternshipByCompany item : list){
        	dataSeries.add(item.getTotalStudents());
        	series.addSeries(new XYseries().setLabel(item.getCompanyName()));
        }
		
		Legend legend = new Legend().setShow(true).setRendererOptions(new EnhancedLegendRenderer().setSeriesToggle(SeriesToggles.SLOW).setSeriesToggleReplot(true)).setPlacement(LegendPlacements.OUTSIDE_GRID);

		Axes axes = new Axes().addAxis(new XYaxis().setRenderer(AxisRenderers.CATEGORY).setTicks(new Ticks().add("")));

		Highlighter highlighter = new Highlighter().setShow(true).setShowTooltip(true).setTooltipAlwaysVisible(true).setKeepTooltipInsideChart(true).setTooltipLocation(TooltipLocations.NORTH).setTooltipAxes(TooltipAxes.XY_BAR);

		Options options = new Options().setTitle(title).setSeriesDefaults(seriesDefaults).setAxes(axes).setHighlighter(highlighter).setSeries(series).setLegend(legend);

		DCharts chart = new DCharts().setDataSeries(dataSeries).setOptions(options);
		
		return chart;
	}

}
