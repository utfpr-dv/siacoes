package br.edu.utfpr.dv.siacoes.view;

import java.util.List;

import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.base.elements.XYseries;
import org.dussan.vaadin.dcharts.data.DataSeries;
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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.JuryBySemester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class JurySemesterChartView extends ChartView {
	
	public static final String NAME = "jurysemesterchart";
	
	private final YearField textInitialYear;
	private final YearField textFinalYear;
	private final OptionGroup optionFilterType;
	
	public JurySemesterChartView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Gráfico de Bancas por Semestre");
		
		this.textInitialYear = new YearField();
		this.textInitialYear.setCaption("Ano Inicial");
		
		this.textFinalYear = new YearField();
		this.textFinalYear.setCaption("Ano Final");
		
		this.optionFilterType = new OptionGroup();
		this.optionFilterType.addItem("Gráfico de Barras");
		this.optionFilterType.addItem("Gráfico de Linhas");
		this.optionFilterType.select(this.optionFilterType.getItemIds().iterator().next());
		
		this.addFilterField(new HorizontalLayout(this.textInitialYear, this.textFinalYear));
		this.addFilterField(this.optionFilterType);
	}

	@Override
	public DCharts generateChart() throws Exception {
		List<JuryBySemester> list = new JuryBO().listJuryBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textInitialYear.getYear(), this.textFinalYear.getYear());
		
		DataSeries dataSeries = new DataSeries();
		Series series = new Series();
		
		dataSeries.newSeries();
		series.addSeries(new XYseries().setLabel("TCC 1"));
		for(JuryBySemester item : list) {
			dataSeries.add(String.valueOf(item.getSemester()) + "/" + String.valueOf(item.getYear()), item.getJuryStage1());
		}
		
		dataSeries.newSeries();
		series.addSeries(new XYseries().setLabel("TCC 2"));
		for(JuryBySemester item : list) {
			dataSeries.add(String.valueOf(item.getSemester()) + "/" + String.valueOf(item.getYear()), item.getJuryStage2());
		}
		
		Title title = new Title("Bancas por Semestre");
		
		Legend legend = new Legend().setShow(true).setRendererOptions(new EnhancedLegendRenderer().setSeriesToggle(SeriesToggles.SLOW).setSeriesToggleReplot(true)).setPlacement(LegendPlacements.OUTSIDE_GRID);
		
		SeriesDefaults seriesDefaults;
		if(this.optionFilterType.isSelected(this.optionFilterType.getItemIds().iterator().next())) {
			seriesDefaults = new SeriesDefaults().setRenderer(SeriesRenderers.BAR);
		} else {
			seriesDefaults = new SeriesDefaults().setRenderer(SeriesRenderers.LINE);
		}
		
		Axes axes = new Axes().addAxis(new XYaxis().setRenderer(AxisRenderers.CATEGORY));
		
		Highlighter highlighter = new Highlighter().setShow(true).setShowTooltip(true).setTooltipAlwaysVisible(true).setKeepTooltipInsideChart(true).setTooltipLocation(TooltipLocations.NORTH).setTooltipAxes(TooltipAxes.XY_BAR);

		Options options = new Options().setTitle(title).setSeriesDefaults(seriesDefaults).setAxes(axes).setHighlighter(highlighter).setSeries(series).setLegend(legend);

		DCharts chart = new DCharts().setDataSeries(dataSeries).setOptions(options);
		
		return chart;
	}

}
