package br.edu.utfpr.dv.siacoes.view;

import java.util.List;

import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.config.ChartConfig;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.TutoredBO;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.TutoredBySupervisor;
import br.edu.utfpr.dv.siacoes.model.TutoredBySupervisor.TutoredGroupedBySupervisor;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class TutoredSupervisorChartView extends ChartView {
	
	public static final String NAME = "tutoredsupervisorchart";
	
	private final YearField textInitialYear;
	private final YearField textFinalYear;
	private final StageComboBox comboStage;
	private final OptionGroup optionFilterType;

	public TutoredSupervisorChartView(){
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
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
	public ChartConfig generateChart() throws Exception {
		List<TutoredGroupedBySupervisor> list = new TutoredBO().listTutoredGroupedBySupervisor(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textInitialYear.getYear(), this.textFinalYear.getYear(), this.comboStage.getStage());
		String title = "Orientados por Orientador (TCC " + (this.comboStage.isBothSelected() ? "1 e 2" : String.valueOf(this.comboStage.getStage())) + ")";
		
		if(this.optionFilterType.isSelected(this.optionFilterType.getItemIds().iterator().next())) {
			BarChartConfig config = new BarChartConfig();
			
			config.data().extractLabelsFromDataset(true);
			
			for(TutoredGroupedBySupervisor item : list) {
				BarDataset ds = new BarDataset().type().label(item.getSupervisorName()).backgroundColor(ColorUtils.randomColor(0.7));
				
				for(TutoredBySupervisor t : item.getTutored()) {
					ds.addLabeledData(String.valueOf(t.getSemester()) + "/" + String.valueOf(t.getYear()), (double)t.getTotal());
				}
				
				config.data().addDataset(ds);
			}
			
			config.data().and();
			
			config.
		        options()
		            .responsive(true)
		            .title()
		                .display(true)
		                .position(Position.TOP)
		                .fontSize(24)
		                .text(title)
		                .and()
		           .done();
			
			return config;
		} else {
			LineChartConfig config = new LineChartConfig();
			
			config.data().extractLabelsFromDataset(true);
			
			for(TutoredGroupedBySupervisor item : list) {
				LineDataset ds = new LineDataset().type().label(item.getSupervisorName()).fill(false).backgroundColor(ColorUtils.randomColor(0.7));
				
				for(TutoredBySupervisor t : item.getTutored()) {
					ds.addLabeledData(String.valueOf(t.getSemester()) + "/" + String.valueOf(t.getYear()), (double)t.getTotal());
				}
				
				config.data().addDataset(ds);
			}
			
			config.data().and();
			
			config.
		        options()
		            .responsive(true)
		            .title()
		                .display(true)
		                .position(Position.TOP)
		                .fontSize(24)
		                .text(title)
		                .and()
		           .done();
			
			return config;
		}
	}

}
