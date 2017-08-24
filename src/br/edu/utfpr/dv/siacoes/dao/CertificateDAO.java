package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.edu.utfpr.dv.siacoes.model.Certificate;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class CertificateDAO {
	
	public Certificate findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT certificate.*, department.name AS departmentName, \"user\".name AS userName " +
					"FROM certificate INNER JOIN department ON department.iddepartment=certificate.iddepartment " +
					"INNER JOIN \"user\" ON \"user\".iduser=certificate.iduser " +
					"WHERE certificate.idcertificate = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public Certificate findByGuid(String guid) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT certificate.*, department.name AS departmentName, \"user\".name AS userName " +
					"FROM certificate INNER JOIN department ON department.iddepartment=certificate.iddepartment " +
					"INNER JOIN \"user\" ON \"user\".iduser=certificate.iduser " +
					"WHERE certificate.guid = ?");
		
			stmt.setString(1, guid);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(Certificate certificate) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("INSERT INTO certificate(iddepartment, iduser, module, date, guid, file) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		
			stmt.setInt(1, certificate.getDepartment().getIdDepartment());
			stmt.setInt(2, certificate.getUser().getIdUser());
			stmt.setInt(3, certificate.getModule().getValue());
			stmt.setTimestamp(4, new java.sql.Timestamp(certificate.getDate().getTime()));
			stmt.setString(5, certificate.getGuid());
			stmt.setBytes(6, certificate.getFile());
			
			stmt.execute();
			
			rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				certificate.setIdCertificate(rs.getInt(1));
			}
			
			return certificate.getIdCertificate();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Certificate loadObject(ResultSet rs) throws SQLException{
		Certificate certificate = new Certificate();
		
		certificate.setIdCertificate(rs.getInt("idcertificate"));
		certificate.getDepartment().setIdDepartment(rs.getInt("iddepartment"));
		certificate.getDepartment().setName(rs.getString("departmentName"));
		certificate.getUser().setIdUser(rs.getInt("iduser"));
		certificate.getUser().setName(rs.getString("userName"));
		certificate.setModule(SystemModule.valueOf(rs.getInt("module")));
		certificate.setDate(rs.getTimestamp("date"));
		certificate.setGuid(rs.getString("guid"));
		certificate.setFile(rs.getBytes("file"));
		
		return certificate;
	}

}
