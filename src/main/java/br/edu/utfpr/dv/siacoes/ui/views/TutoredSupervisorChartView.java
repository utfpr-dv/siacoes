package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.DataLabelsBuilder;
import com.github.appreciated.apexcharts.config.builder.FillBuilder;
import com.github.appreciated.apexcharts.config.builder.GridBuilder;
import com.github.appreciated.apexcharts.config.builder.PlotOptionsBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.TitleSubtitleBuilder;
import com.github.appreciated.apexcharts.config.builder.TooltipBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.RowBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.tooltip.builder.YBuilder;
import com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.TutoredBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.TutoredBySupervisor;
import br.edu.utfpr.dv.siacoes.model.TutoredBySupervisor.TutoredGroupedBySupervisor;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;

@PageTitle("Gráfico de Orientados por Orientador")
@Route(value = "tutoredsupervisorchart", layout = MainLayout.class)
public class TutoredSupervisorChartView extends ChartView {
	
	private final YearField textInitialYear;
	private final YearField textFinalYear;
	private final StageComboBox comboStage;
	private final RadioButtonGroup<String> optionFilterType;
	
	private static final String BAR_CHART = "Gráfico de Barras";
	private static final String LINE_CHART = "Gráfico de Linhas";

	public TutoredSupervisorChartView(){
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.textInitialYear = new YearField();
		this.textInitialYear.setLabel("Ano Inicial");
		
		this.textFinalYear = new YearField();
		this.textFinalYear.setLabel("Ano Final");
		
		this.comboStage = new StageComboBox(true);
		
		this.optionFilterType = new RadioButtonGroup<String>();
		this.optionFilterType.setItems(BAR_CHART, LINE_CHART);
		this.optionFilterType.setValue(BAR_CHART);
		this.optionFilterType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
		
		this.addFilterField(new HorizontalLayout(this.textInitialYear, this.textFinalYear, this.comboStage));
		this.addFilterField(this.optionFilterType);
	}
	
	@Override
	public ApexCharts generateChart() throws Exception {
		List<TutoredGroupedBySupervisor> list = new TutoredBO().listTutoredGroupedBySupervisor(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textInitialYear.getYear(), this.textFinalYear.getYear(), this.comboStage.getStage());
		String title = "Orientados por Orientador (TCC " + (this.comboStage.isBothSelected() ? "1 e 2" : String.valueOf(this.comboStage.getStage())) + ")";
		
		List<Series> series = new ArrayList<Series>();
		List<String> categories = new ArrayList<String>();
		
		for(TutoredGroupedBySupervisor supervisor : list) {
			String name = supervisor.getSupervisorName();
			List<Integer> values = new ArrayList<Integer>();
			
			for(TutoredBySupervisor item : supervisor.getTutored()) {
				values.add(item.getTotal());
			}
			
			series.add(new Series<>(name, values.toArray()));
		}
		
		if(list.size() > 0) {
			for(TutoredBySupervisor item : list.get(0).getTutored()) {
				categories.add(String.valueOf(item.getSemester()) + "/" + String.valueOf(item.getYear()));
			}
		}
		
		if(this.optionFilterType.getValue().equals(BAR_CHART)) {
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
	                                .withText("Número de Orientados")
	                                .build())
	                        .build())
	                .withXaxis(XAxisBuilder.get().withCategories(categories).build())
	                .withFill(FillBuilder.get()
	                        .withOpacity(1.0).build())
	                .withTooltip(TooltipBuilder.get()
	                        .withY(YBuilder.get()
	                                .withFormatter("function (val) {\n" +
	                                        "return val + \" orientado(s)\"\n" +
	                                        "}").build())
	                        .build())
	                .build();
			
			return barChart;
		} else {
			ApexCharts lineChart = ApexChartsBuilder.get()
					.withChart(ChartBuilder.get()
	                        .withType(Type.line)
	                        .withZoom(ZoomBuilder.get()
	                                .withEnabled(false)
	                                .build())
	                        .build())
					.withStroke(StrokeBuilder.get()
	                        .withCurve(Curve.straight)
	                        .build())
					.withColors(ChartView.getChartColors(series.size()))
	                .withTitle(TitleSubtitleBuilder.get()
	                        .withText(title)
	                        .withAlign(Align.center)
	                        .build())
	                .withGrid(GridBuilder.get()
	                        .withRow(RowBuilder.get()
	                                .withColors("#f3f3f3", "transparent")
	                                .withOpacity(0.5).build()
	                        ).build())
	                .withSeries(series.toArray(new Series[0]))
	                .withYaxis(YAxisBuilder.get()
	                        .withTitle(TitleBuilder.get()
	                                .withText("Número de Orientados")
	                                .build())
	                        .build())
	                .withXaxis(XAxisBuilder.get().withCategories(categories).build())
	                .build();
			
			return lineChart;
		}
	}

}
