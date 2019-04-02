package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Company;

public class CompanyDAO {
	
	public List<Company> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT company.*, city.name AS cityName " +
					"FROM company INNER JOIN city ON city.idcity=company.idcity ORDER BY name");
			
			List<Company> list = new ArrayList<Company>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public Company findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT company.*, city.name AS cityName " +
				"FROM company INNER JOIN city ON city.idcity=company.idcity WHERE idcompany=?");
		
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
	
	public int save(int idUser, Company company) throws SQLException{
		boolean insert = (company.getIdCompany() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO company(idcity, name, cnpj, phone, email, agreement) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE company SET idcity=?, name=?, cnpj=?, phone=?, email=?, agreement=? WHERE idcompany=?");
			}
			
			stmt.setInt(1, company.getCity().getIdCity());
			stmt.setString(2, company.getName());
			stmt.setString(3, company.getCnpj());
			stmt.setString(4, company.getPhone());
			stmt.setString(5, company.getEmail());
			stmt.setString(6, company.getAgreement().trim());
			
			if(!insert){
				stmt.setInt(7, company.getIdCompany());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					company.setIdCompany(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, company);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, company);
			}
			
			return company.getIdCompany();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Company loadObject(ResultSet rs) throws SQLException{
		Company company = new Company();
		
		company.setIdCompany(rs.getInt("idcompany"));
		company.getCity().setIdCity(rs.getInt("idcity"));
		company.getCity().setName(rs.getString("cityName"));
		company.setName(rs.getString("name"));
		company.setCnpj(rs.getString("cnpj"));
		company.setPhone(rs.getString("phone"));
		company.setEmail(rs.getString("email"));
		company.setAgreement(rs.getString("agreement"));
		
		return company;
	}
	
	public long getActiveCompanies() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(idCompany) AS total FROM company");
			
			if(rs.next()) {
				return rs.getLong("total");
			} else {
				return 0;
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
	
	public long getActiveCompaniesWithAgreement() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(idCompany) AS total FROM company WHERE agreement <> ''");
			
			if(rs.next()) {
				return rs.getLong("total");
			} else {
				return 0;
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

}
