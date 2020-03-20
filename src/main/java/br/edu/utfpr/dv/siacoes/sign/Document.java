package br.edu.utfpr.dv.siacoes.sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterRequestBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;
import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;

public class Document {
	
	public enum DocumentType{
		NONE(0), SUPERVISORAGREEMENT(1), APPRAISERFEEDBACK(2), JURYREQUEST(3), SUPERVISORCHANGE(4), ATTENDANCE(5), JURY(6), INTERNSHIPPOSTERREQUEST(7), INTERNSHIPJURY(8);
		
		private final int value; 
		DocumentType(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static DocumentType valueOf(int value){
			for(DocumentType p : DocumentType.values()){
				if(p.getValue() == value){
					return p;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case NONE:
					return "Nenhum";
				case SUPERVISORAGREEMENT:
					return "Concordância do Professor Orientador para Proposta de TCC 1";
				case APPRAISERFEEDBACK:
					return "Parecer da Proposta de TCC 1";
				case JURYREQUEST:
					return "Formulário de Agendamento de Banca de TCC";
				case SUPERVISORCHANGE:
					return "Requisição para Troca de Orientador";
				case ATTENDANCE:
					return "Registro de Reuniões de TCC";
				case JURY:
					return "Banca de TCC";
				case INTERNSHIPPOSTERREQUEST:
					return "Ficha de Inscrição para Evento de Avaliação de Estágio Curricular Obrigatório";
				case INTERNSHIPJURY:
					return "Banca de Estágio";
				default:
					return "Nenhum";
			}
		}
	}
	
	private int idDocument;
	private Department department;
	private String guid;
	private DocumentType type;
	private int version;
	private int idRegister;
	private byte[] report;
	private byte[] dataset;
	private Date generatedDate;
	private List<Signature> signatures;
	private boolean published;
	private Date publishedDate;
	
	public int getIdDocument() {
		return idDocument;
	}
	public void setIdDocument(int idDocument) {
		this.idDocument = idDocument;
	}
	public byte[] getReport() {
		return report;
	}
	public void setReport(byte[] report) {
		this.report = report;
	}
	public byte[] getDataset() {
		return dataset;
	}
	public void setDataset(byte[] dataset) {
		this.dataset = dataset;
	}
	public Date getGeneratedDate() {
		return generatedDate;
	}
	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}
	public List<Signature> getSignatures() {
		return signatures;
	}
	public void setSignatures(List<Signature> signatures) {
		this.signatures = signatures;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	public Date getPublishedDate() {
		return publishedDate;
	}
	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}
	public DocumentType getType() {
		return type;
	}
	public void setType(DocumentType type) {
		this.type = type;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getIdRegister() {
		return idRegister;
	}
	public void setIdRegister(int idRegister) {
		this.idRegister = idRegister;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public Document() {
		this.setIdDocument(0);
		this.setDepartment(new Department());
		this.setGuid("");
		this.setReport(null);
		this.setDataset(null);
		this.setType(DocumentType.NONE);
		this.setVersion(1);
		this.setIdRegister(0);
		this.setGeneratedDate(null);
		this.setSignatures(new ArrayList<Signature>());
		this.setPublished(false);
		this.setPublishedDate(null);
	}
	
	public static Document build(int idDepartment, DocumentType type, int idRegister, Object dataset, List<User> users) throws Exception {
		Document doc = Document.find(type, idRegister);
		
		if((doc != null) && (doc.getIdDocument() != 0)) {
			return doc;
		}
		
		doc = new Document();
		
		JasperReport jasperReport = Document.getReport(type);
		
		if(jasperReport == null) {
			throw new Exception("Documento inválido.");
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(jasperReport);
		out.flush();
		doc.setReport(bos.toByteArray());
		
		((SignDataset)dataset).setValidateUrl("");
		((SignDataset)dataset).setLegalText("");
		((SignDataset)dataset).setQrCode(null);
		for(SignDataset.Signature sign : ((SignDataset)dataset).getSignatures()) {
			sign.setSignature(null);
			sign.setRubric(null);
		}
		
		bos = new ByteArrayOutputStream();
		out = new ObjectOutputStream(bos);
		out.writeObject(dataset);
		out.flush();
		doc.setDataset(bos.toByteArray());
		
		doc.getDepartment().setIdDepartment(idDepartment);
		doc.setGuid(((SignDataset)dataset).getGuid());
		doc.setType(type);
		doc.setVersion(Document.getReportVersion(type));
		doc.setIdRegister(idRegister);
		
		for(User u : users) {
			Signature sign = new Signature();
			
			sign.setUser(u);
			
			doc.getSignatures().add(sign);
		}
		
		return doc;
	}
	
	public static byte[] getSignedDocument(DocumentType type, int idRegister) throws Exception {
		Document doc = Document.find(type, idRegister);
		
		return Document.getSignedDocument(doc);
	}
	
	public static byte[] getSignedDocument(int idDocument) throws Exception {
		Document doc = Document.find(idDocument);
		
		return Document.getSignedDocument(doc);
	}
	
	public static byte[] getSignedDocument(String guid) throws Exception {
		Document doc = Document.find(guid);
		
		return Document.getSignedDocument(doc);
	}
	
	private static byte[] getSignedDocument(Document doc) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(doc.getReport());
		ObjectInput in = new ObjectInputStream(bis);
		JasperReport jasperReport = (JasperReport)in.readObject();
		in.close();
		
		bis = new ByteArrayInputStream(doc.getDataset());
		in = new ObjectInputStream(bis);
		Object dataset = in.readObject();
		in.close();
		
		((SignDataset)dataset).setValidateUrl(AppConfig.getInstance().getHost() + "/#!authdocument/" + ((SignDataset)dataset).getGuid());
		((SignDataset)dataset).setLegalText("Documento assinado eletronicamente com fundamento no art. 6º, § 1º, do Decreto nº 8.539, de 8 de outubro de 2015. A autenticidade deste documento pode ser conferida no site " + 
				((SignDataset)dataset).getValidateUrl() + " ou efetuando a leitura do QRCode ao lado.\nCódigo de autenticação: " + ((SignDataset)dataset).getGuid());
		((SignDataset)dataset).setQrCode(new ByteArrayInputStream(Document.createQRCode(((SignDataset)dataset).getValidateUrl(), 100, 100)));
		((SignDataset)dataset).setUseDigitalSignature(true);
		
		for(Signature sign : doc.getSignatures()) {
			if(sign.getSignature() != null) {
				for(SignDataset.Signature s : ((SignDataset)dataset).getSignatures()) {
					if(sign.getUser().getIdUser() == s.getIdUser()) {
						s.setName(sign.getUser().getName());
						s.setSignature(new ByteArrayInputStream(com.will.signature.Signature.getSignature(s.getName(), com.will.signature.Signature.SignFont.PECITA, sign.isRevoked())));
						s.setRubric(new ByteArrayInputStream(com.will.signature.Signature.getSignature(s.getName().substring(0, s.getName().indexOf(" ")).trim(), com.will.signature.Signature.SignFont.PECITA, sign.isRevoked())));
					}
				}
			}
		}
		
		List<Object> list = new ArrayList<Object>();
		list.add(dataset);
		
		return new ReportUtils().createPdfStream(list, jasperReport, doc.getDepartment().getIdDepartment()).toByteArray();
	}
	
	private static byte[] createQRCode(String qrCodeData, int qrCodeheight, int qrCodewidth) throws WriterException, IOException {
		String charset = "UTF-8"; // or "ISO-8859-1"
		Map hintMap = new HashMap();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset), BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		MatrixToImageWriter.writeToStream(matrix, "jpeg", stream);
		
		return stream.toByteArray();
	}
	
	private static JasperReport getReport(DocumentType type) throws JRException {
		switch(type) {
			case SUPERVISORAGREEMENT:
				return new ReportUtils().getJasperData("SupervisorAgreement");
			case APPRAISERFEEDBACK:
				return new ReportUtils().getJasperData("ProposalFeedback");
			case JURYREQUEST:
				return new ReportUtils().getJasperData("JuryFormRequest");
			case SUPERVISORCHANGE:
				return new ReportUtils().getJasperData("SupervisorChangeStatement");
			case ATTENDANCE:
				return new ReportUtils().getJasperData("Attendances");
			case JURY:
				return new ReportUtils().getJasperData("JuryForm");
			case INTERNSHIPPOSTERREQUEST:
				return new ReportUtils().getJasperData("InternshipPosterRequest");
			case INTERNSHIPJURY:
				return new ReportUtils().getJasperData("InternshipJuryForm");
			default:
				return null;
		}
	}
	
	private static int getReportVersion(DocumentType type) {
		return 1;
	}
	
	public static int insertSigned(int idDepartment, DocumentType type, int idRegister, Object dataset, List<User> users, String login, String password) throws Exception {
		return Document.insertSigned(Document.build(idDepartment, type, idRegister, dataset, users), login, password);
	}
	
	public static int insertSigned(Document document, String login, String password) throws Exception {
		User user = new UserBO().findByLogin(login);
		boolean find = false;
		
		for(Signature u : document.getSignatures()) {
			if(u.getUser().getIdUser() == user.getIdUser()) {
				find = true;
				break;
			}
		}
		
		if(!find) {
			throw new Exception("Você não pode assinar esse documento.");
		}
		
		int idDocument = Document.insert(document);
		
		document = Document.find(idDocument);
		
		Document.sign(document, login, password);
		
		return idDocument;
	}
	
	public static int insert(int idDepartment, DocumentType type, int idRegister, Object dataset, List<User> users) throws Exception {
		return Document.insert(Document.build(idDepartment, type, idRegister, dataset, users));
	}
	
	private static int insert(Document document) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT INTO signdocument(guid, report, dataset, generateddate, type, version, idregister, iddepartment) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			document.setGeneratedDate(DateUtils.getNow().getTime());
			
			stmt.setString(1, document.getGuid());
			stmt.setBytes(2, document.getReport());
			stmt.setBytes(3, document.getDataset());
			stmt.setTimestamp(4, new java.sql.Timestamp(document.getGeneratedDate().getTime()));
			stmt.setInt(5, document.getType().getValue());
			stmt.setInt(6, document.getVersion());
			stmt.setInt(7, document.getIdRegister());
			stmt.setInt(8, document.getDepartment().getIdDepartment());
			
			stmt.execute();
			
			rs = stmt.getGeneratedKeys();
			if(rs.next()){
				document.setIdDocument(rs.getInt(1));
			}
			
			for(Signature sign : document.getSignatures()) {
				sign.setDocument(document);
				
				stmt.close();
				
				stmt = conn.prepareStatement("INSERT INTO signature(iddocument, iduser, signature, signaturedate, revoked, revokeddate, idrevokeduser) VALUES(?, ?, NULL, NULL, 0, NULL, NULL)");
				
				stmt.setInt(1, document.getIdDocument());
				stmt.setInt(2, sign.getUser().getIdUser());
				
				stmt.execute();
				
				rs.close();
				rs = stmt.getGeneratedKeys();
				if(rs.next()){
					sign.setIdSignature(rs.getInt(1));
				}
			}
			
			conn.commit();
			
			return document.getIdDocument();
		} catch(SQLException ex) {
			conn.rollback();
			
			throw ex;
		} finally {
			conn.setAutoCommit(true);
			
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static boolean revoke(int idDocument, int idUser) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("UPDATE signature SET revoked=1, revokedDate=?, revokedUser=? WHERE idDocument=?");
			
			stmt.setTimestamp(1, new java.sql.Timestamp(DateUtils.getNow().getTime().getTime()));
			stmt.setInt(2, idUser);
			stmt.setInt(3, idDocument);
			
			return stmt.execute();
		} finally {
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static void sign(int idDocument, String login, String password) throws Exception {
		Document.sign(Document.find(idDocument), login, password);
	}
	
	private static void sign(Document document, String login, String password) throws Exception {
		User user = new UserBO().findByLogin(login);
		
		for(Signature sign : document.getSignatures()) {
			if(sign.getUser().getIdUser() == user.getIdUser()) {
				if(sign.getSignature() != null) {
					throw new Exception("O documento já está assinado.");
				}
				if(sign.isRevoked()) {
					throw new Exception("A assinatura já foi revogada e não é possível assinar o documento novamente.");
				}
				
				boolean hasAllSignatures, hasNoneSignature;
				
				try {
					hasAllSignatures = Document.hasAllSignatures(document.getType(), document.getIdRegister());
				} catch(Exception e) {
					hasAllSignatures = false;
				}
				
				try {
					hasNoneSignature = Document.hasNoneSignature(document.getType(), document.getIdRegister());
				} catch(Exception e) {
					hasNoneSignature = true;
				}
				
				sign.setSignature(SignatureKey.sign(login, password, document.getDataset()));
				sign.setSignatureDate(DateUtils.getNow().getTime());
				
				Connection conn = null;
				PreparedStatement stmt = null;
				
				try {
					conn = ConnectionDAO.getInstance().getConnection();
					stmt = conn.prepareStatement("UPDATE signature SET signature=?, signaturedate=? WHERE idSignature=?");
					
					stmt.setBytes(1, sign.getSignature());
					stmt.setTimestamp(2, new java.sql.Timestamp(sign.getSignatureDate().getTime()));
					stmt.setInt(3, sign.getIdSignature());
					
					stmt.execute();
				} finally {
					if((stmt != null) && !stmt.isClosed())
						stmt.close();
					if((conn != null) && !conn.isClosed())
						conn.close();
				}
				
				try {
					if(!hasAllSignatures && Document.hasAllSignatures(document.getType(), document.getIdRegister())) {
						Document.sendNotificationToManager(document.getType(), document.getIdRegister());
					} else if(hasNoneSignature && Document.hasSignature(document.getType(), document.getIdRegister())) {
						Document.sendRequestSignatureNotification(document.getType(), document.getIdRegister(), Document.listPending(document.getIdDocument()));
					}
				} catch(Exception e) { }
				
				return;
			}
		}
	}
	
	public static List<Document> listSigned(int idUser, int idDepartment) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT signdocument.*, (SELECT COUNT(signature.*) FROM signature WHERE signature.signature IS NULL AND signature.iddocument=signdocument.iddocument) AS published, " +
					"(SELECT MAX(signaturedate) FROM signature WHERE signature.iddocument=signdocument.iddocument) AS publisheddate " +
					"FROM signdocument INNER JOIN signature sign ON sign.iddocument=signdocument.iddocument " +
					"WHERE sign.signature IS NOT NULL AND sign.iduser=" + String.valueOf(idUser) + " AND signdocument.iddepartment=" + String.valueOf(idDepartment));
			
			List<Document> list = new ArrayList<Document>();
			
			while(rs.next()) {
				list.add(Document.loadObject(rs));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static List<Document> listPending(int idUser, int idDepartment) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT signdocument.*, (SELECT COUNT(signature.*) FROM signature WHERE signature.signature IS NULL AND signature.iddocument=signdocument.iddocument) AS published, " +
					"(SELECT MAX(signaturedate) FROM signature WHERE signature.iddocument=signdocument.iddocument) AS publisheddate " +
					"FROM signdocument INNER JOIN signature sign ON sign.iddocument=signdocument.iddocument " +
					"WHERE sign.signature IS NULL AND sign.revoked=0 AND sign.iduser=" + String.valueOf(idUser) + " AND signdocument.iddepartment=" + String.valueOf(idDepartment));
			
			List<Document> list = new ArrayList<Document>();
			
			while(rs.next()) {
				list.add(Document.loadObject(rs));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private static List<User> listPending(int idDocument) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT \"user\".iduser, \"user\".name " +
					"FROM signdocument INNER JOIN signature sign ON sign.iddocument=signdocument.iddocument " +
					"INNER JOIN \"user\" ON \"user\".iduser=sign.iduser " +
					"WHERE sign.signature IS NULL AND sign.revoked=0 AND signdocument.iddocument=" + String.valueOf(idDocument));
			
			List<User> list = new ArrayList<User>();
			
			while(rs.next()) {
				User user = new User();
				
				user.setIdUser(rs.getInt("iduser"));
				user.setName(rs.getString("name"));
				
				list.add(user);
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static int getPendingDocuments(int idUser, int idDepartment) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(DISTINCT signdocument.iddocument) AS total " +
					"FROM signdocument INNER JOIN signature sign ON sign.iddocument=signdocument.iddocument " +
					"WHERE sign.signature IS NULL AND sign.revoked=0 AND sign.iduser=" + String.valueOf(idUser) + " AND signdocument.iddepartment=" + String.valueOf(idDepartment));
			
			if(rs.next()) {
				return rs.getInt("total");
			} else {
				return 0;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static List<Document> list(DocumentType type, int idRegister) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT DISTINCT signdocument.*, (SELECT COUNT(signature.*) FROM signature WHERE signature.signature IS NULL AND signature.iddocument=signdocument.iddocument) AS published, " +
					"(SELECT MAX(signaturedate) FROM signature WHERE signature.iddocument=signdocument.iddocument) AS publisheddate " +
					"FROM signdocument INNER JOIN signature sign ON sign.iddocument=signdocument.iddocument " +
					"WHERE signdocument.type=" + String.valueOf(type.getValue()) + " AND signdocument.idregister=" + String.valueOf(idRegister));
			
			List<Document> list = new ArrayList<Document>();
			
			while(rs.next()) {
				list.add(Document.loadObject(rs));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static List<Document> list(DocumentType type, int idUser, int status) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			String sql = "SELECT DISTINCT signdocument.*, (SELECT COUNT(signature.*) FROM signature WHERE signature.signature IS NULL AND signature.iddocument=signdocument.iddocument) AS published, " +
							"(SELECT MAX(signaturedate) FROM signature WHERE signature.iddocument=signdocument.iddocument) AS publisheddate " +
							"FROM signdocument INNER JOIN signature sign ON sign.iddocument=signdocument.iddocument " +
							"WHERE 1=1";
			
			if(type != DocumentType.NONE) {
				sql += " AND signdocument.type=" + String.valueOf(type.getValue());
			}
			if(idUser > 0) {
				sql += " AND sign.iduser=" + String.valueOf(idUser);
			}
			if(status == 0) {
				sql += " AND sign.signature IS NULL AND sign.revoked=0";
			} else if(status == 1) {
				sql += " AND sign.signature IS NOT NULL AND sign.revoked=0";
			} else if(status == 2) {
				sql += " AND sign.revoked=1";
			}
			
			rs = stmt.executeQuery(sql);
			
			List<Document> list = new ArrayList<Document>();
			
			while(rs.next()) {
				list.add(Document.loadObject(rs));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static Document find(DocumentType type, int idRegister) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT DISTINCT signdocument.*, (SELECT COUNT(signature.*) FROM signature WHERE signature.signature IS NULL AND signature.iddocument=signdocument.iddocument) AS published, " +
					"(SELECT MAX(signaturedate) FROM signature WHERE signature.iddocument=signdocument.iddocument) AS publisheddate " +
					"FROM signdocument INNER JOIN signature sign ON sign.iddocument=signdocument.iddocument " +
					"WHERE sign.revoked=0 AND signdocument.type=" + String.valueOf(type.getValue()) + " AND signdocument.idregister=" + String.valueOf(idRegister));
			
			if(rs.next()) {
				return Document.loadObject(rs);
			} else {
				return null;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static Document find(int idDocument) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT signdocument.*, (SELECT COUNT(signature.*) FROM signature WHERE signature.signature IS NULL AND signature.iddocument=signdocument.iddocument) AS published, " +
					"(SELECT MAX(signaturedate) FROM signature WHERE signature.iddocument=signdocument.iddocument) AS publisheddate " +
					"FROM signdocument WHERE signdocument.iddocument=" + String.valueOf(idDocument));
			
			if(rs.next()) {
				return Document.loadObject(rs);
			} else {
				return null;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static Document find(String guid) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT signdocument.*, (SELECT COUNT(signature.*) FROM signature WHERE signature.signature IS NULL AND signature.iddocument=signdocument.iddocument) AS published, " +
					"(SELECT MAX(signaturedate) FROM signature WHERE signature.iddocument=signdocument.iddocument) AS publisheddate " +
					"FROM signdocument WHERE signdocument.guid=?");
			
			stmt.setString(1, guid);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return Document.loadObject(rs);
			} else {
				return null;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static boolean hasSignature(DocumentType type, int idRegister) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT DISTINCT signdocument.iddocument " +
					"FROM signdocument INNER JOIN signature ON signature.iddocument=signdocument.iddocument " +
					"WHERE signature.signature IS NOT NULL AND signature.revoked=0 AND signdocument.idregister=" + 
					String.valueOf(idRegister) + " AND signdocument.type=" + String.valueOf(type.getValue()));
			
			return rs.next();
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static boolean hasSignature(DocumentType type, int idRegister, int idUser) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT DISTINCT signdocument.iddocument " +
					"FROM signdocument INNER JOIN signature ON signature.iddocument=signdocument.iddocument " +
					"WHERE signature.signature IS NOT NULL AND signature.revoked=0 AND signdocument.idregister=" + 
					String.valueOf(idRegister) + " AND signdocument.type=" + String.valueOf(type.getValue()) + " AND signature.iduser=" + String.valueOf(idUser));
			
			return rs.next();
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static boolean hasAllSignatures(DocumentType type, int idRegister) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT signdocument.iddocument, signature.signature " +
					"FROM signdocument INNER JOIN signature ON signature.iddocument=signdocument.iddocument " +
					"WHERE signature.revoked=0 AND signdocument.idregister=? AND signdocument.type=?", ResultSet.TYPE_SCROLL_SENSITIVE);
			
			stmt.setInt(1, idRegister);
			stmt.setInt(2, type.getValue());
			
			rs = stmt.executeQuery();
			
			if(!rs.next()) {
				return false;
			}
			
			rs.beforeFirst();
			while(rs.next()) {
				if(rs.getBytes("signature") == null) {
					return false;
				}
			}
			
			return true;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static boolean hasNoneSignature(DocumentType type, int idRegister) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT signdocument.iddocument, signature.signature " +
					"FROM signdocument INNER JOIN signature ON signature.iddocument=signdocument.iddocument " +
					"WHERE signature.revoked=0 AND signdocument.idregister=" + 
					String.valueOf(idRegister) + " AND signdocument.type=" + String.valueOf(type.getValue()));
			
			if(!rs.next()) {
				return true;
			}
			
			rs.beforeFirst();
			while(rs.next()) {
				if(rs.getBytes("signature") != null) {
					return false;
				}
			}
			
			return true;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private static Document loadObject(ResultSet rs) throws SQLException {
		Document doc = new Document();
		
		doc.setIdDocument(rs.getInt("iddocument"));
		doc.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		doc.setGuid(rs.getString("guid"));
		doc.setReport(rs.getBytes("report"));
		doc.setDataset(rs.getBytes("dataset"));
		doc.setType(DocumentType.valueOf(rs.getInt("type")));
		doc.setVersion(rs.getInt("version"));
		doc.setIdRegister(rs.getInt("idregister"));
		doc.setGeneratedDate(rs.getTimestamp("generateddate"));
		doc.setPublished(rs.getInt("published") == 0);
		if(doc.isPublished()) {
			doc.setPublishedDate(rs.getTimestamp("publisheddate"));
		} else {
			doc.setPublishedDate(null);
		}
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs2 = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs2 = stmt.executeQuery("SELECT signature.*, \"user\".name AS username, \"user\".login " +
					"FROM signature INNER JOIN \"user\" ON \"user\".iduser=signature.iduser " +
					"WHERE iddocument=" + String.valueOf(doc.getIdDocument()) +
					" ORDER BY \"user\".name");
			
			while(rs2.next()) {
				Signature sign = new Signature();
				
				sign.setDocument(doc);
				sign.setIdSignature(rs2.getInt("idsignature"));
				sign.getUser().setIdUser(rs2.getInt("iduser"));
				sign.getUser().setName(rs2.getString("username"));
				sign.getUser().setLogin(rs2.getString("login"));
				sign.setSignature(rs2.getBytes("signature"));
				sign.setSignatureDate(rs2.getTimestamp("signaturedate"));
				sign.setRevoked(rs2.getInt("revoked") == 1);
				sign.setRevokedDate(rs2.getTimestamp("revokeddate"));
				sign.getRevokedUser().setIdUser(rs2.getInt("idrevokeduser"));
				
				doc.getSignatures().add(sign);
			}
		} finally {
			if((rs2 != null) && !rs2.isClosed())
				rs2.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
		
		return doc;
	}
	
	@SuppressWarnings("incomplete-switch")
	private static void sendNotificationToManager(DocumentType type, int idRegister) throws Exception {
		switch(type) {
			case SUPERVISORAGREEMENT:
				new ProposalBO().sendSupervisorFeedbackSignedMessage(idRegister);
				return;
			case APPRAISERFEEDBACK:
				new ProposalAppraiserBO().sendAppraiserFeedbackSignedMessage(idRegister);
				return;
			case JURYREQUEST:
				new JuryRequestBO().sendRequestSignedMessage(idRegister);
				return;
			case SUPERVISORCHANGE:
				new SupervisorChangeBO().sendSupervisorChangeMessage(idRegister);
				return;
			case ATTENDANCE:
				new AttendanceBO().sendAttendanceSignedMessage(idRegister);
				return;
			case JURY:
				new JuryBO().sendJuryFormSignedMessage(idRegister);
				return;
			case INTERNSHIPPOSTERREQUEST:
				new InternshipPosterRequestBO().sendRequestSignedMessage(idRegister);
				return;
			case INTERNSHIPJURY:
				new InternshipJuryBO().sendJuryFormSignedMessage(idRegister);
				return;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private static void sendRequestSignatureNotification(DocumentType type, int idRegister, List<User> users) throws Exception {
		switch(type) {
			case JURYREQUEST:
				new JuryRequestBO().sendRequestSignJuryRequestMessage(idRegister, users);
				return;
			case ATTENDANCE:
				new AttendanceBO().sendRequestSignAttendanceMessage(idRegister, users);
				return;
			case JURY:
				new JuryBO().sendRequestSignJuryFormMessage(idRegister, users);
				return;
			case INTERNSHIPPOSTERREQUEST:
				new InternshipPosterRequestBO().sendRequestSignInternshipPosterRequestMessage(idRegister, users);
				return;
			case INTERNSHIPJURY:
				new InternshipJuryBO().sendRequestSignJuryFormMessage(idRegister, users);
				return;
		}
	}

}
