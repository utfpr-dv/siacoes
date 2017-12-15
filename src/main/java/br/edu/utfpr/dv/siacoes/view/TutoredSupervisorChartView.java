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
import br.edu.utfpr.dv.siacoes.bo.TutoredBO;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.TutoredBySupervisor;
import br.edu.utfpr.dv.siacoes.model.TutoredBySupervisor.TutoredGroupedBySupervisor;

public class TutoredSupervisorChartView extends ChartView {
	
	public static final String NAME = "tutoredsupervisorchart";
	
	private final YearField textInitialYear;
	private final YearField textFinalYear;
	private final StageComboBox comboStage;
	private final OptionGroup optionFilterType;

	public TutoredSupervisorChartView(){
		super(SystemModule.SIGET);
		this.setCaption("Gráfico de Orientados por Orientador");
		
		this.textInitialYear = new YearField();
		this.textInitialYear.setCaption("Ano Inicial");
		
		this.textFinalYear = new YearField();
		this.textFinalYear.setCaption("Ano Final");
		
		this.comboStage = new StageComboBox(true);
		
		this.optionFilterType = new OptionGroup();
		this.optionFilterType.addItem("Gráfico de Barras");
		this.optionFilterType.addItem("Gráfico de Linhas");
		this.optionFilterType.select(this.optionFilterType.getItemIds().iterator().next());
		
		this.addFilterField(new HorizontalLayout(this.textInitialYear, this.textFinalYear, this.comboStage));
		this.addFilterField(this.optionFilterType);
	}
	
	@Override
	public DCharts generateChart() throws Exception {
		List<TutoredGroupedBySupervisor> list = new TutoredBO().listTutoredGroupedBySupervisor(Session.getUser().getDepartment().getIdDepartment(), this.textInitialYear.getYear(), this.textFinalYear.getYear(), this.comboStage.getStage());
		
		DataSeries dataSeries = new DataSeries();
		Series series = new Series();
		
		for(TutoredGroupedBySupervisor item : list) {
			dataSeries.newSeries();
			
			for(TutoredBySupervisor t : item.getTutored()) {
				dataSeries.add(String.valueOf(t.getSemester()) + "/" + String.valueOf(t.getYear()), t.getTotal());
			}
			
			series.addSeries(new XYseries().setLabel(item.getSupervisorName()));
		}
		
		Title title = new Title("Orientados por Orientador (TCC " + (this.comboStage.isBothSelected() ? "1 e 2" : String.valueOf(this.comboStage.getStage())) + ")");
		
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
