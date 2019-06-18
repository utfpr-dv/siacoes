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
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.JuryBySemester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class JurySemesterChartView extends ChartView {
	
	public static final String NAME = "jurysemesterchart";
	
	private final YearField textInitialYear;
	private final YearField textFinalYear;
	private final OptionGroup optionFilterType;
	
	public JurySemesterChartView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Gráfico de Bancas por Semestre");
		
		this.textInitialYear = new YearField();
		this.textInitialYear.setCaption("Ano Inicial");
		
		this.textFinalYear = new YearField();
		this.textFinalYear.setCaption("Ano Final");
		
		this.optionFilterType = new OptionGroup();
		this.optionFilterType.addItem("Gráfico de Barras");
		this.optionFilterType.addItem("Gráfico de Linhas");
		this.optionFilterType.select(this.optionFilterType.getItemIds().iterator().next());
		
		this.addFilterField(new HorizontalLayout(this.textInitialYear, this.textFinalYear));
		this.addFilterField(this.optionFilterType);
	}

	@Override
	public ChartConfig generateChart() throws Exception {
		List<JuryBySemester> list = new JuryBO().listJuryBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textInitialYear.getYear(), this.textFinalYear.getYear());
		
		if(this.optionFilterType.isSelected(this.optionFilterType.getItemIds().iterator().next())) {
			BarChartConfig config = new BarChartConfig();
			
			config.data().extractLabelsFromDataset(true);
			
			BarDataset ds1 = new BarDataset().type().label("TCC 1").backgroundColor(ColorUtils.randomColor(0.7));
			for(JuryBySemester item : list) {
				ds1.addLabeledData(String.valueOf(item.getSemester()) + "/" + String.valueOf(item.getYear()), (double)item.getJuryStage1());
			}
			config.data().addDataset(ds1);
			
			BarDataset ds2 = new BarDataset().type().label("TCC 2").backgroundColor(ColorUtils.randomColor(0.7));
			for(JuryBySemester item : list) {
				ds2.addLabeledData(String.valueOf(item.getSemester()) + "/" + String.valueOf(item.getYear()), (double)item.getJuryStage2());
			}
			config.data().addDataset(ds2);
			
			config.data().and();
			
			config.
		        options()
		            .responsive(true)
		            .title()
		                .display(true)
		                .position(Position.TOP)
		                .text("Bancas por Semestre")
		                .and()
		           .done();
			
			return config;
		} else {
			LineChartConfig config = new LineChartConfig();
			
			config.data().extractLabelsFromDataset(true);
			
			LineDataset ds1 = new LineDataset().type().label("TCC 1").backgroundColor(ColorUtils.randomColor(0.7));
			for(JuryBySemester item : list) {
				ds1.addLabeledData(String.valueOf(item.getSemester()) + "/" + String.valueOf(item.getYear()), (double)item.getJuryStage1());
			}
			config.data().addDataset(ds1);
			
			LineDataset ds2 = new LineDataset().type().label("TCC 2").backgroundColor(ColorUtils.randomColor(0.7));
			for(JuryBySemester item : list) {
				ds2.addLabeledData(String.valueOf(item.getSemester()) + "/" + String.valueOf(item.getYear()), (double)item.getJuryStage2());
			}
			config.data().addDataset(ds2);
			
			config.data().and();
			
			config.
		        options()
		            .responsive(true)
		            .title()
		                .display(true)
		                .position(Position.TOP)
		                .text("Bancas por Semestre")
		                .and()
		           .done();
			
			return config;
		}
	}

}
