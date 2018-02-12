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

import com.vaadin.ui.NativeSelect;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.model.ActivityGroupStatus;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.StudentActivityStatusReport.StudentStage;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class ActivityGroupStatusChartView extends ChartView {

	public static final String NAME = "activitygroupstatuschart";
	
	private final NativeSelect comboStage;
	
	public ActivityGroupStatusChartView() {
		super(SystemModule.SIGAC);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Gráfico de Pontuação Média por Grupo de Atividade");
		
		this.comboStage = new NativeSelect("Situação do Acadêmico");
		this.comboStage.setWidth("400px");
		this.comboStage.setNullSelectionAllowed(false);
		this.comboStage.addItem(StudentStage.REGULAR);
		this.comboStage.addItem(StudentStage.FINISHINGCOURSE);
		this.comboStage.addItem(StudentStage.ALMOSTGRADUATED);
		//this.comboStage.addItem(StudentStage.GRADUATED);
		this.comboStage.setValue(StudentStage.REGULAR);
		
		this.addFilterField(this.comboStage);
	}

	@Override
	public DCharts generateChart() throws Exception {
		List<ActivityGroupStatus> list = new ActivitySubmissionBO().getStudentActivityGroupStatus(Session.getUser().getDepartment().getIdDepartment(), (StudentStage)this.comboStage.getValue());
		
		DataSeries dataSeries = new DataSeries();
		Series series = new Series();
		
		dataSeries.newSeries();
		series.addSeries(new XYseries().setLabel("Pontuação Mínima do Grupo"));
		for(ActivityGroupStatus item : list) {
			dataSeries.add("Grupo " + String.valueOf(item.getGroup().getSequence()), item.getGroup().getMinimumScore());
		}
		
		dataSeries.newSeries();
		series.addSeries(new XYseries().setLabel("Pontuação Média dos Acadêmicos"));
		for(ActivityGroupStatus item : list) {
			dataSeries.add("Grupo " + String.valueOf(item.getGroup().getSequence()), item.getAverageScore());
		}
		
		dataSeries.newSeries();
		series.addSeries(new XYseries().setLabel("Pontuação Máxima do Grupo"));
		for(ActivityGroupStatus item : list) {
			dataSeries.add("Grupo " + String.valueOf(item.getGroup().getSequence()), item.getGroup().getMaximumScore());
		}
		
		Title title = new Title("Pontuação Média por Grupo");
		
		Legend legend = new Legend().setShow(true).setRendererOptions(new EnhancedLegendRenderer().setSeriesToggle(SeriesToggles.SLOW).setSeriesToggleReplot(true)).setPlacement(LegendPlacements.OUTSIDE_GRID);
		
		SeriesDefaults seriesDefaults = new SeriesDefaults().setRenderer(SeriesRenderers.BAR);
		
		Axes axes = new Axes().addAxis(new XYaxis().setRenderer(AxisRenderers.CATEGORY));
		
		Highlighter highlighter = new Highlighter().setShow(true).setShowTooltip(true).setTooltipAlwaysVisible(true).setKeepTooltipInsideChart(true).setTooltipLocation(TooltipLocations.NORTH).setTooltipAxes(TooltipAxes.XY_BAR);

		Options options = new Options().setTitle(title).setSeriesDefaults(seriesDefaults).setAxes(axes).setHighlighter(highlighter).setSeries(series).setLegend(legend);

		DCharts chart = new DCharts().setDataSeries(dataSeries).setOptions(options);
		
		return chart;
	}
	
}
