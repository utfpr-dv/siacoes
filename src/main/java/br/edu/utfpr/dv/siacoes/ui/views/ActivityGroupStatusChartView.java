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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.model.ActivityGroupStatus;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.StudentActivityStatusReport.StudentStage;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;

@PageTitle("Gráfico de Pontuação Média por Grupo de Atividade")
@Route(value = "activitygroupstatuschart", layout = MainLayout.class)
public class ActivityGroupStatusChartView extends ChartView {
	
	private final Select<StudentStage> comboStage;
	private final Checkbox checkStudentsWithoutPoints;
	private final Checkbox checkIncludeWithFinalSubmission;
	
	public ActivityGroupStatusChartView() {
		super(SystemModule.SIGAC);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.comboStage = new Select<StudentStage>();
		this.comboStage.setLabel("Situação do Acadêmico");
		this.comboStage.setWidth("400px");
		this.comboStage.setItems(StudentStage.REGULAR, StudentStage.FINISHINGCOURSE, StudentStage.ALMOSTGRADUATED);
		this.comboStage.setValue(StudentStage.REGULAR);
		
		this.checkStudentsWithoutPoints = new Checkbox("Filtrar apenas acadêmicos que ainda não atingiram a pontuação necessária");
		
		this.checkIncludeWithFinalSubmission = new Checkbox("Incluir acadêmicos que já foram aprovados");
		
		this.addFilterField(this.comboStage);
		this.addFilterField(this.checkStudentsWithoutPoints);
		this.addFilterField(this.checkIncludeWithFinalSubmission);
	}

	@Override
	public ApexCharts generateChart() throws Exception {
		List<ActivityGroupStatus> list = new ActivitySubmissionBO().getStudentActivityGroupStatus(Session.getSelectedDepartment().getDepartment().getIdDepartment(), (StudentStage)this.comboStage.getValue(), this.checkStudentsWithoutPoints.getValue(), this.checkIncludeWithFinalSubmission.getValue());
		
		List<String> categories = new ArrayList<String>();
		List<Integer> valuesMin = new ArrayList<Integer>();
		List<Integer> valuesMax = new ArrayList<Integer>();
		List<Double> valuesMean = new ArrayList<Double>();
		
		for(ActivityGroupStatus item : list) {
			categories.add("Grupo " + String.valueOf(item.getGroup().getSequence()));
			valuesMin.add(item.getGroup().getMinimumScore());
			valuesMax.add(item.getGroup().getMaximumScore());
			valuesMean.add(item.getAverageScore());
		}
		
		List<Series> series = new ArrayList<Series>();
		series.add(new Series<>("Pontuação Mínima do Grupo", valuesMin.toArray()));
		series.add(new Series<>("Pontuação Média dos Acadêmicos", valuesMean.toArray()));
		series.add(new Series<>("Pontuação Máxima do Grupo", valuesMax.toArray()));
		
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
                        .withText("Pontuação Média por Grupo")
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
                                .withText("Pontos")
                                .build())
                        .build())
                .withXaxis(XAxisBuilder.get().withCategories(categories).build())
                .withFill(FillBuilder.get()
                        .withOpacity(1.0).build())
                .withTooltip(TooltipBuilder.get()
                        .withY(YBuilder.get()
                                .withFormatter("function (val) {\n" +
                                        "return val + \" ponto(s)\"\n" +
                                        "}").build())
                        .build())
                .build();
		
		return barChart;
	}
	
}
