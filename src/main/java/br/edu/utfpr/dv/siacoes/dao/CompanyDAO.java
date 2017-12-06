package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
	
	public int save(Company company) throws SQLException{
		boolean insert = (company.getIdCompany() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO company(idcity, name, cnpj, phone, email) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE company SET idcity=?, name=?, cnpj=?, phone=?, email=? WHERE idcompany=?");
			}
			
			stmt.setInt(1, company.getCity().getIdCity());
			stmt.setString(2, company.getName());
			stmt.setString(3, company.getCnpj());
			stmt.setString(4, company.getPhone());
			stmt.setString(5, company.getEmail());
			
			if(!insert){
				stmt.setInt(6, company.getIdCompany());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					company.setIdCompany(rs.getInt(1));
				}
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
		
		return company;
	}

}
