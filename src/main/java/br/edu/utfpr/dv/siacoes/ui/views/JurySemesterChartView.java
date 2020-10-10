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
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.model.JuryBySemester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;

@PageTitle("Gráfico de Bancas por Semestre")
@Route(value = "jurysemesterchart", layout = MainLayout.class)
public class JurySemesterChartView extends ChartView {
	
	private final YearField textInitialYear;
	private final YearField textFinalYear;
	private final RadioButtonGroup<String> optionFilterType;
	
	private static final String BAR_CHART = "Gráfico de Barras";
	private static final String LINE_CHART = "Gráfico de Linhas";
	
	public JurySemesterChartView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.textInitialYear = new YearField();
		this.textInitialYear.setLabel("Ano Inicial");
		
		this.textFinalYear = new YearField();
		this.textFinalYear.setLabel("Ano Final");
		
		this.optionFilterType = new RadioButtonGroup<String>();
		this.optionFilterType.setItems(BAR_CHART, LINE_CHART);
		this.optionFilterType.setValue(BAR_CHART);
		this.optionFilterType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
		
		this.addFilterField(new HorizontalLayout(this.textInitialYear, this.textFinalYear));
		this.addFilterField(this.optionFilterType);
	}

	@Override
	public ApexCharts generateChart() throws Exception {
		List<JuryBySemester> list = new JuryBO().listJuryBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textInitialYear.getYear(), this.textFinalYear.getYear());
		String title = "Bancas por Semestre";
		
		List<Integer> stage1 = new ArrayList<Integer>();
		List<Integer> stage2 = new ArrayList<Integer>();
		List<String> categories = new ArrayList<String>();
		
		for(JuryBySemester semester : list) {
			categories.add(String.valueOf(semester.getSemester()) + "/" + String.valueOf(semester.getYear()));
			stage1.add(semester.getJuryStage1());
			stage2.add(semester.getJuryStage2());
		}
		
		List<Series> series = new ArrayList<Series>();
		series.add(new Series<>("TCC 1", stage1.toArray()));
		series.add(new Series<>("TCC 2", stage2.toArray()));
		
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
	                                .withText("Número de Bancas")
	                                .build())
	                        .build())
	                .withXaxis(XAxisBuilder.get().withCategories(categories).build())
	                .withFill(FillBuilder.get()
	                        .withOpacity(1.0).build())
	                .withTooltip(TooltipBuilder.get()
	                        .withY(YBuilder.get()
	                                .withFormatter("function (val) {\n" +
	                                        "return val + \" banca(s)\"\n" +
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
	                                .withText("Número de Bancas")
	                                .build())
	                        .build())
	                .withXaxis(XAxisBuilder.get().withCategories(categories).build())
	                .build();
			
			return lineChart;
		}
	}

}
