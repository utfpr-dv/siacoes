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

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.model.InternshipByCompany;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class InternshipCompanyChartView extends ChartView {
	
	public static final String NAME = "internshipcompanychart";
	
	public InternshipCompanyChartView(){
		super(SystemModule.SIGES);
		this.setCaption("Gráfico de Estagiários por Empresa");
	}

	@Override
	public DCharts generateChart() throws Exception {
		List<InternshipByCompany> list = new InternshipBO().listInternshipByCompany(Session.getUser().getDepartment().getIdDepartment());
		
		DataSeries dataSeries = new DataSeries();
		
		SeriesDefaults seriesDefaults = new SeriesDefaults().setRenderer(SeriesRenderers.BAR);
		Series series = new Series();
		
		for(InternshipByCompany item : list){
        	dataSeries.add(item.getTotalStudents());
        	series.addSeries(new XYseries().setLabel(item.getCompanyName()));
        }
		
		Title title = new Title("Estagiários por Empresa");
		
		Legend legend = new Legend().setShow(true).setRendererOptions(new EnhancedLegendRenderer().setSeriesToggle(SeriesToggles.SLOW).setSeriesToggleReplot(true)).setPlacement(LegendPlacements.OUTSIDE_GRID);

		Axes axes = new Axes().addAxis(new XYaxis().setRenderer(AxisRenderers.CATEGORY).setTicks(new Ticks().add("")));

		Highlighter highlighter = new Highlighter().setShow(true).setShowTooltip(true).setTooltipAlwaysVisible(true).setKeepTooltipInsideChart(true).setTooltipLocation(TooltipLocations.NORTH).setTooltipAxes(TooltipAxes.XY_BAR);

		Options options = new Options().setTitle(title).setSeriesDefaults(seriesDefaults).setAxes(axes).setHighlighter(highlighter).setSeries(series).setLegend(legend);

		DCharts chart = new DCharts().setDataSeries(dataSeries).setOptions(options);
		
		return chart;
	}

}
