package br.edu.utfpr.dv.siacoes.view;

import java.util.ArrayList;
import java.util.List;

import com.byteowls.vaadin.chartjs.config.ChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.ui.HorizontalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.ActivityScore;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class ActivityHighScoreChartView extends ChartView {
	
	public static final String NAME = "activityhighscorechart";
	
	private final YearField textInitialYear;
	private final YearField textFinalYear;
	
	public ActivityHighScoreChartView() {
		super(SystemModule.SIGAC);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Gráfico de Bancas por Semestre");
		
		this.textInitialYear = new YearField();
		this.textInitialYear.setCaption("Ano Inicial");
		
		this.textFinalYear = new YearField();
		this.textFinalYear.setCaption("Ano Final");
		
		this.addFilterField(new HorizontalLayout(this.textInitialYear, this.textFinalYear));
	}

	@Override
	public ChartConfig generateChart() throws Exception {
		List<ActivityScore> list = new ActivitySubmissionBO().getActivityScore(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textInitialYear.getYear(), this.textFinalYear.getYear(), 10);
		List<String> colors = new ArrayList<>();
		
		PieChartConfig config = new PieChartConfig();
		
		config.data().extractLabelsFromDataset(true);
		
		PieDataset ds = new PieDataset();
		
		for(ActivityScore item : list){
        	ds.addLabeledData(item.getActivity(), item.getScore());
        	colors.add(ColorUtils.randomColor(0.7));
        }
		
		ds.backgroundColor(colors.toArray(new String[colors.size()]));
		
		config.data().addDataset(ds).and();
		
		config.
	        options()
	            .responsive(true)
	            .title()
	                .display(true)
	                .fontSize(24)
	                .text("Atividades mais Pontuadas")
	                .and()
	            .animation()
	                //.animateScale(true)
	                .animateRotate(true)
	                .and()
	           .done();
		
		return config;
	}
	
}
