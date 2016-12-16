package br.edu.utfpr.dv.siacoes.util;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;

import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;

import br.edu.utfpr.dv.siacoes.Session;
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
    private String baseReportsPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/reports/";

    /**
     * Get database connection, call report generation method and export's report to Vaadin's FileDownloader
     * @param reportTemplate Report template file name
     * @param reportOutputFilename Pdf output file name
     * @param buttonToExtend Vaadin button to extend
     */
    public void prepareForPdfReport(String reportName, String reportOutputFilename, List dataSource, Button buttonToExtend){
    	this.prepareForPdfReport(reportName, reportOutputFilename, dataSource, buttonToExtend, true);
    }
    
    public void prepareForPdfReport(String reportName, String reportOutputFilename, List dataSource, Button buttonToExtend, boolean removeExtensions){
        reportOutputFilename += ("_" + getDateAsString() + ".pdf");
        StreamResource myResource = createPdfResource(dataSource, reportName, reportOutputFilename);
        FileDownloader fileDownloader = new FileDownloader(myResource);
        
        if(removeExtensions && (buttonToExtend.getExtensions().size() > 0)){
        	while(buttonToExtend.getExtensions().size() > 0){
        		buttonToExtend.removeExtension((Extension)buttonToExtend.getExtensions().toArray()[0]);	
        	}
        }
        
        fileDownloader.extend(buttonToExtend);
    }

    /**
     * Generate pdf report, and return it as a StreamResource
     * @param conn Database connection
     * @param templatePath Report template path
     * @param reportFileName Pdf output file name
     * @return StreamResource with the generated pdf report
     */
    private StreamResource createPdfResource(final List beanCollection, final String templatePath, String reportFileName) {
        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream () {
                ByteArrayOutputStream pdfBuffer = new ByteArrayOutputStream();

                try {
                    executeReport(templatePath, beanCollection, pdfBuffer);
                } catch (JRException e) {
                    e.printStackTrace();
                }
                // Return a stream from the buffer.
                return new ByteArrayInputStream(pdfBuffer.toByteArray());
            }
        }, reportFileName);
    }
    
    public ByteArrayOutputStream createPdfStream(final List beanCollection, final String templatePath) throws Exception {
    	ByteArrayOutputStream pdfBuffer = new ByteArrayOutputStream();

        try {
            executeReport(templatePath, beanCollection, pdfBuffer);
            
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
    
    private void executeReport(String templatePath, List beanCollection, OutputStream outputStream) throws JRException {
        //JasperDesign jasperDesign = this.loadTemplate(this.baseReportsPath + templatePath + ".jrxml");
        setTempDirectory(templatePath);
        JasperReport jasperReport = (JasperReport)JRLoader.loadObject(this.getClass().getClassLoader().getResource("br/edu/utfpr/dv/siacoes/report/" + templatePath + ".jasper"));
        //JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(this.baseReportsPath + templatePath + ".jasper");
        //JasperReport jasperReport = this.compileReport(jasperDesign);
        JasperPrint jasperPrint = fillReport(jasperReport, beanCollection);
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
    private JasperPrint fillReport(JasperReport jasperReport, List beanCollection){
        JasperPrint jasperPrint = null;
        HashMap<String, Object> fillParameters = new HashMap<String, Object>();
        String campus = "";
        String department = "";
        Image logoUTFPR = new ImageIcon(this.getClass().getClassLoader().getResource("br/edu/utfpr/dv/siacoes/images/assinatura_UTFPR.png")).getImage();
        
        try{
        	DepartmentBO bo = new DepartmentBO();
        	Department d = bo.findById(Session.getUser().getDepartment().getIdDepartment());
        	
        	department = d.getFullName();
        	
        	CampusBO cbo = new CampusBO();
        	Campus c = cbo.findById(d.getCampus().getIdCampus());
        	
        	campus = "CÂMPUS " + c.getName();
        	logoUTFPR = new ImageIcon(c.getLogo()).getImage();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        Image brasaoRepublica = new ImageIcon(this.getClass().getClassLoader().getResource("br/edu/utfpr/dv/siacoes/images/brasao_republica.png")).getImage();
        
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

}
