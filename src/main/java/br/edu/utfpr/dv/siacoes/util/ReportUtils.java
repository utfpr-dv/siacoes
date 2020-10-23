package br.edu.utfpr.dv.siacoes.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Department;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class ReportUtils {
	
	//Base path for report template
    //private String baseReportsPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/reports/";

    /**
     * Generate pdf report, and return it as a StreamResource
     * @param conn Database connection
     * @param templatePath Report template path
     * @param reportFileName Pdf output file name
     * @return StreamResource with the generated pdf report
     */
    private StreamResource createPdfResource(final List beanCollection, final String templatePath, String reportFileName, int idDepartment) {
    	StreamResource resource = new StreamResource(reportFileName, () -> {
    		ByteArrayOutputStream pdfBuffer = new ByteArrayOutputStream();

            try {
                executeReport(templatePath, beanCollection, pdfBuffer, idDepartment);
            } catch (JRException e) {
                e.printStackTrace();
            }

            return new ByteArrayInputStream(pdfBuffer.toByteArray());
    	});
		resource.setCacheTime(0);
    	
    	return resource;
    }
    
    public ByteArrayOutputStream createPdfStream(final List beanCollection, final String templatePath, int idDepartment) throws Exception {
    	ByteArrayOutputStream pdfBuffer = new ByteArrayOutputStream();

        try {
            executeReport(templatePath, beanCollection, pdfBuffer, idDepartment);
            
            return pdfBuffer;
        } catch (JRException e) {
            e.printStackTrace();
            
            throw e;
        }
    }
    
    public ByteArrayOutputStream createPdfStream(final List beanCollection, final JasperReport jasperReport, int idDepartment) throws Exception {
    	ByteArrayOutputStream pdfBuffer = new ByteArrayOutputStream();

        try {
            executeReport(jasperReport, beanCollection, pdfBuffer, idDepartment);
            
            return pdfBuffer;
        } catch (JRException e) {
            e.printStackTrace();
            
            throw e;
        }
    }

    /**
     * Convert a date to String
     * @return String with date
     */
    private String getDateAsString(){
        return(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))+
                String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1)+
                String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))+
                String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))+
                String.valueOf(Calendar.getInstance().get(Calendar.MINUTE))+
                String.valueOf(Calendar.getInstance().get(Calendar.SECOND)));
    }
    
    public JasperReport getJasperData(String templatePath) throws JRException {
    	setTempDirectory(templatePath);
        return (JasperReport)JRLoader.loadObject(this.getClass().getClassLoader().getResource("br/edu/utfpr/dv/siacoes/report/" + templatePath + ".jasper"));
    }
    
    private void executeReport(String templatePath, List beanCollection, OutputStream outputStream, int idDepartment) throws JRException {
    	this.executeReport(this.getJasperData(templatePath), beanCollection, outputStream, idDepartment);
    }
    
    private void executeReport(JasperReport jasperReport, List beanCollection, OutputStream outputStream, int idDepartment) throws JRException {
        JasperPrint jasperPrint = fillReport(jasperReport, beanCollection, idDepartment);
        exportReportToPdf(jasperPrint, outputStream);
    }

    /**
     * Load the template (defined by templatePath) and return a JasperDesign object representing the template
     * @return JasperDesign
     */
    private JasperDesign loadTemplate(String templatePath){
        JasperDesign jasperDesign = null;
        File templateFile = new File(templatePath);
        
        if(templateFile.exists()){
            try {
                jasperDesign = JRXmlLoader.load(templateFile);
            } catch (JRException e) {
            	e.printStackTrace();
            }
        }else{
        	System.out.println("Error, the file dont exists");
        }
        
        return(jasperDesign);
    }

    /**
     * Compile the report and generates a binary version of it
     * @param jasperDesign The report design
     * @return JasperReport
     */
    private JasperReport compileReport(JasperDesign jasperDesign){
        JasperReport jasperReport = null;
        
        try {
            jasperReport = JasperCompileManager.compileReport(jasperDesign);
        } catch (JRException e) {
        	e.printStackTrace();
        }
        
        return(jasperReport);
    }

    /**
     * Fill the report and generates a binary version of it
     * @param jasperReport The Compiled report design
     * @return JasperPrint
     */
    private JasperPrint fillReport(JasperReport jasperReport, List beanCollection, int idDepartment){
        JasperPrint jasperPrint = null;
        HashMap<String, Object> fillParameters = new HashMap<String, Object>();
        String campus = "";
        String department = "";
        InputStream logoUTFPR = null;
		try {
			logoUTFPR = new ByteArrayInputStream(this.readLogo("br/edu/utfpr/dv/siacoes/images/assinatura_UTFPR.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        try{
        	DepartmentBO bo = new DepartmentBO();
        	Department d = bo.findById(idDepartment);
        	
        	department = d.getFullName();
        	
        	CampusBO cbo = new CampusBO();
        	Campus c = cbo.findById(d.getCampus().getIdCampus());
        	
        	campus = "CÂMPUS " + c.getName();
        	logoUTFPR = new ByteArrayInputStream(c.getLogo());
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        InputStream brasaoRepublica = null;
		try {
			brasaoRepublica = new ByteArrayInputStream(this.readLogo("br/edu/utfpr/dv/siacoes/images/brasao_republica.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        fillParameters.put("brasao_republica", brasaoRepublica);
        fillParameters.put("logo_utfpr", logoUTFPR);
        fillParameters.put("campus", campus);
        fillParameters.put("department", department);
        
        try {
            jasperPrint = JasperFillManager.fillReport(jasperReport, fillParameters, new JRBeanCollectionDataSource(beanCollection));
        } catch (JRException e) {
        	e.printStackTrace();
        }
        
        return(jasperPrint);
    }

    /**
     * Prepare a JRExporter for the filled report (to HTML)
     * @param jasperPrint The jasperPrint
     * @return The HTML text
     */
    private void exportReportToPdf(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
    }
    
    /**
     * Set the temp directory for report generation
     */
    private void setTempDirectory(String templatePath){
        File templateFile = new File(templatePath);
        
        if(templateFile.exists()){
        	System.setProperty("jasper.reports.compile.temp", templateFile.getParent());
        }
    }
    
    private byte[] readLogo(String path) throws IOException {
    	InputStream fis = this.getClass().getClassLoader().getResourceAsStream(path);
		byte[] ret = new byte[fis.available()];
		fis.read(ret);
		return ret;
    }

}
