package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.model.InternshipByCompany;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class InternshipCompanyChartView extends ChartView {
	
	public static final String NAME = "internshipcompanychart";
	
	public InternshipCompanyChartView(){
		super(SystemModule.SIGES);
		this.setCaption("Gráfico de Estagiários por Empresa");
	}

	@Override
	public byte[] generateChart() throws Exception {
		List<InternshipByCompany> list = new InternshipBO().listInternshipByCompany(Session.getUser().getDepartment().getIdDepartment());
		
	    CategoryChartBuilder builder = new CategoryChartBuilder();
	    builder.width(800);
	    builder.height(400);
	    builder.title("Estagiários por Empresa");
	    builder.yAxisTitle("Estagiários");
	    
	    CategoryChart chart = builder.build();
	    chart.getStyler().setHasAnnotations(true);

	    //List<String> xData = new ArrayList<String>();
	    //List<Number> yData = new ArrayList<Number>();
		
        for(InternshipByCompany item : list){
        	//xData.add(item.getCompanyName());
        	//yData.add(item.getTotalStudents());
        	
        	chart.addSeries((item.getCompanyName().length() > 30 ? item.getCompanyName().substring(0, 30) : item.getCompanyName()), new int[]{0}, new int[]{item.getTotalStudents()});
        }
        
        //CategorySeries serie = chart.addSeries("T", xData, yData);
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        BitmapEncoder.saveBitmap(chart, output, BitmapFormat.JPG);
        
        return output.toByteArray();
	}

}
