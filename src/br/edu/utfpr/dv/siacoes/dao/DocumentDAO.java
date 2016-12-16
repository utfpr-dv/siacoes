package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Document;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class DocumentDAO {
	
	public List<Document> listAll() throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM document ORDER BY sequence");
		
		List<Document> list = new ArrayList<Document>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<Document> listByModule(int idDepartment, SystemModule module) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM document WHERE idDepartment=" + String.valueOf(idDepartment) + " AND module IN (0, " + String.valueOf(module.getValue()) + ") ORDER BY sequence");
		
		List<Document> list = new ArrayList<Document>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public Document findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT * FROM document WHERE idDocument = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;	
		}
	}
	
	public int save(Document document) throws SQLException{
		boolean insert = (document.getIdDocument() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO document(idDepartment, name, type, sequence, file, module) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			Statement stmt2 = ConnectionDAO.getInstance().getConnection().createStatement();
			ResultSet rs = stmt2.executeQuery("SELECT COUNT(*) as total FROM document WHERE idDepartment=" + String.valueOf(document.getDepartment().getIdDepartment()) + " AND module=" + String.valueOf(document.getModule().getValue()));
			rs.next();
			document.setSequence(rs.getInt("total") + 1);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE document SET idDepartment=?, name=?, type=?, sequence=?, file=?, module=? WHERE idDocument=?");
		}
		
		stmt.setInt(1, document.getDepartment().getIdDepartment());
		stmt.setString(2, document.getName());
		stmt.setInt(3, document.getType().getValue());
		stmt.setInt(4, document.getSequence());
		stmt.setBytes(5, document.getFile());
		stmt.setInt(6, document.getModule().getValue());
		
		if(!insert){
			stmt.setInt(7, document.getIdDocument());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				document.setIdDocument(rs.getInt(1));
			}
		}
		
		return document.getIdDocument();
	}
	
	public boolean delete(int id) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		return stmt.execute("DELETE FROM document WHERE idDocument = " + String.valueOf(id));
	}
	
	private Document loadObject(ResultSet rs) throws SQLException{
		Document doc = new Document();
		
		doc.setIdDocument(rs.getInt("idDocument"));
		doc.setName(rs.getString("name"));
		doc.setType(DocumentType.valueOf(rs.getInt("type")));
		doc.setSequence(rs.getInt("sequence"));
		doc.setFile(rs.getBytes("file"));
		doc.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		doc.setModule(SystemModule.valueOf(rs.getInt("module")));
		
		return doc;
	}
	
	public void moveUp(int idDocument) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT sequence, idDepartment, module FROM document WHERE idDocument=?");
		stmt.setInt(1, idDocument);
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			int sequence = rs.getInt("sequence");
			int idDepartment = rs.getInt("idDepartment");
			int module = rs.getInt("module");
			
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT idDocument FROM document WHERE idDepartment=? AND module=? AND sequence < ? ORDER BY sequence DESC");
			stmt.setInt(1, idDepartment);
			stmt.setInt(2, module);
			stmt.setInt(3, sequence);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int idDocument2 = rs.getInt("idDocument");
				
				try{
					ConnectionDAO.getInstance().getConnection().setAutoCommit(false);
					
					stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE document SET sequence=? WHERE idDocument=?");
					stmt.setInt(1, sequence);
					stmt.setInt(2, idDocument2);
					stmt.execute();
					
					stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE document SET sequence=? WHERE idDocument=?");
					stmt.setInt(1, sequence - 1);
					stmt.setInt(2, idDocument);
					stmt.execute();
					
					ConnectionDAO.getInstance().getConnection().commit();
				}catch(SQLException e){
					ConnectionDAO.getInstance().getConnection().rollback();
					
					throw e;
				}finally{
					ConnectionDAO.getInstance().getConnection().setAutoCommit(true);
				}
			}
		}
	}
	
	public void moveDown(int idDocument) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT sequence, idDepartment, module FROM document WHERE idDocument=?");
		stmt.setInt(1, idDocument);
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			int sequence = rs.getInt("sequence");
			int idDepartment = rs.getInt("idDepartment");
			int module = rs.getInt("module");
			
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT idDocument FROM document WHERE idDepartment=? AND module=? AND sequence > ? ORDER BY sequence");
			stmt.setInt(1, idDepartment);
			stmt.setInt(2, module);
			stmt.setInt(3, sequence);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int idDocument2 = rs.getInt("idDocument");
				
				try{
					ConnectionDAO.getInstance().getConnection().setAutoCommit(false);
					
					stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE document SET sequence=? WHERE idDocument=?");
					stmt.setInt(1, sequence);
					stmt.setInt(2, idDocument2);
					stmt.execute();
					
					stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE document SET sequence=? WHERE idDocument=?");
					stmt.setInt(1, sequence + 1);
					stmt.setInt(2, idDocument);
					stmt.execute();
					
					ConnectionDAO.getInstance().getConnection().commit();
				}catch(SQLException e){
					ConnectionDAO.getInstance().getConnection().rollback();
					
					throw e;
				}finally{
					ConnectionDAO.getInstance().getConnection().setAutoCommit(true);
				}
			}
		}
	}

}
