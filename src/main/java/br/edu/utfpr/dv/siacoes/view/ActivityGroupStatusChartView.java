package br.edu.utfpr.dv.siacoes.view;

import java.util.List;

import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.config.ChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.ui.CheckBox;
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
	private final CheckBox checkStudentsWithoutPoints;
	
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
		
		this.checkStudentsWithoutPoints = new CheckBox("Filtrar apenas acadêmicos que ainda não atingiram a pontuação necessária");
		
		this.addFilterField(this.comboStage);
		this.addFilterField(this.checkStudentsWithoutPoints);
	}

	@Override
	public ChartConfig generateChart() throws Exception {
		List<ActivityGroupStatus> list = new ActivitySubmissionBO().getStudentActivityGroupStatus(Session.getSelectedDepartment().getDepartment().getIdDepartment(), (StudentStage)this.comboStage.getValue(), this.checkStudentsWithoutPoints.getValue());
		
		BarChartConfig config = new BarChartConfig();
		
		config.data().extractLabelsFromDataset(true);
		
		BarDataset ds1 = new BarDataset().type().label("Pontuação Mínima do Grupo").backgroundColor(ColorUtils.randomColor(0.7));
		for(ActivityGroupStatus item : list) {
			ds1.addLabeledData("Grupo " + String.valueOf(item.getGroup().getSequence()), (double)item.getGroup().getMinimumScore());
		}
		config.data().addDataset(ds1);
		
		BarDataset ds2 = new BarDataset().type().label("Pontuação Média dos Acadêmicos").backgroundColor(ColorUtils.randomColor(0.7));
		for(ActivityGroupStatus item : list) {
			ds2.addLabeledData("Grupo " + String.valueOf(item.getGroup().getSequence()), (double)item.getAverageScore());
		}
		config.data().addDataset(ds2);
		
		BarDataset ds3 = new BarDataset().type().label("Pontuação Máxima do Grupo").backgroundColor(ColorUtils.randomColor(0.7));
		for(ActivityGroupStatus item : list) {
			ds3.addLabeledData("Grupo " + String.valueOf(item.getGroup().getSequence()), (double)item.getGroup().getMaximumScore());
		}
		config.data().addDataset(ds3);
		
		config.data().and();
		
		config.
	        options()
	            .responsive(true)
	            .title()
	                .display(true)
	                .position(Position.TOP)
	                .fontSize(24)
	                .text("Pontuação Média por Grupo")
	                .and()
	           .done();
		
		return config;
	}
	
}
