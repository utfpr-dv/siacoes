package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.LegendBuilder;
import com.github.appreciated.apexcharts.config.builder.ResponsiveBuilder;
import com.github.appreciated.apexcharts.config.builder.TitleSubtitleBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.legend.Position;
import com.github.appreciated.apexcharts.config.responsive.builder.OptionsBuilder;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.model.ActivityScore;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;

@PageTitle("Gráfico de Atividades mais Pontuadas")
@Route(value = "activityhighscorechart", layout = MainLayout.class)
public class ActivityHighScoreChartView extends ChartView {
	
	private final YearField textInitialYear;
	private final YearField textFinalYear;
	
	public ActivityHighScoreChartView() {
		super(SystemModule.SIGAC);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.textInitialYear = new YearField();
		this.textInitialYear.setLabel("Ano Inicial");
		
		this.textFinalYear = new YearField();
		this.textFinalYear.setLabel("Ano Final");
		
		this.addFilterField(new HorizontalLayout(this.textInitialYear, this.textFinalYear));
	}

	@Override
	public ApexCharts generateChart() throws Exception {
		List<ActivityScore> list = new ActivitySubmissionBO().getActivityScore(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textInitialYear.getYear(), this.textFinalYear.getYear(), 10);
		
		List<String> categories = new ArrayList<String>();
		List<Double> scores = new ArrayList<Double>();
		
		for(ActivityScore item : list) {
			categories.add(item.getActivity());
			scores.add(item.getScore());
		}
		
		ApexCharts pieChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.pie).build())
                .withLabels(categories.toArray(new String[0]))
                .withLegend(LegendBuilder.get()
                        .withPosition(Position.right)
                        .build())
                .withSeries(scores.toArray(new Double[0]))
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Atividades mais Pontuadas")
                        .withAlign(Align.center)
                        .build())
                .withColors(ChartView.getChartColors(categories.size()))
                .withResponsive(ResponsiveBuilder.get()
                        .withBreakpoint(480.0)
                        .withOptions(OptionsBuilder.get()
                                .withLegend(LegendBuilder.get()
                                        .withPosition(Position.bottom)
                                        .build())
                                .build())
                        .build())
                .build();
		
		return pieChart;
	}
	
}
