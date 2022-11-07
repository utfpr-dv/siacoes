package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemDAO {
	
	public long getDatabaseSize() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT SUM(pg_total_relation_size(c.oid)) AS total " +
					"FROM pg_class c LEFT JOIN pg_namespace n ON n.oid = c.relnamespace " +
					"WHERE relkind = 'r'");
			
			if(rs.next()) {
				return rs.getLong("total");
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
	
	public long getCertificatesSize() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT pg_total_relation_size(c.oid) AS total " +
					"FROM pg_class c LEFT JOIN pg_namespace n ON n.oid = c.relnamespace " +
					"WHERE relkind = 'r' AND relname = 'certificate'");
			
			if(rs.next()) {
				return rs.getLong("total");
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

	public long getSigacSize() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT SUM(pg_total_relation_size(c.oid)) AS total " +
					"FROM pg_class c LEFT JOIN pg_namespace n ON n.oid = c.relnamespace " +
					"WHERE relkind = 'r' AND relname IN ('activitysubmission', 'finalsubmission')");
			
			if(rs.next()) {
				return rs.getLong("total");
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
	
	public long getSigesSize() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT SUM(pg_total_relation_size(c.oid)) AS total " +
					"FROM pg_class c LEFT JOIN pg_namespace n ON n.oid = c.relnamespace " +
					"WHERE relkind = 'r' AND relname IN ('internship', 'internshipfinaldocument', 'internshipjuryappraiser', 'internshipreport')");
			
			if(rs.next()) {
				return rs.getLong("total");
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
	
	public long getSigetSize() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT SUM(pg_total_relation_size(c.oid)) AS total " +
					"FROM pg_class c LEFT JOIN pg_namespace n ON n.oid = c.relnamespace " +
					"WHERE relkind = 'r' AND relname IN ('finaldocument', 'juryappraiser', 'project', 'proposal', 'proposalappraiser', 'thesis')");
			
			if(rs.next()) {
				return rs.getLong("total");
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
	
	public long getLogSize() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT SUM(pg_total_relation_size(c.oid)) AS total " +
					"FROM pg_class c LEFT JOIN pg_namespace n ON n.oid = c.relnamespace " +
					"WHERE relkind = 'r' AND relname IN ('loginlog', 'eventlog')");
			
			if(rs.next()) {
				return rs.getLong("total");
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
	
	public long getSignatureSize() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT SUM(pg_total_relation_size(c.oid)) AS total " +
					"FROM pg_class c LEFT JOIN pg_namespace n ON n.oid = c.relnamespace " +
					"WHERE relkind = 'r' AND relname IN ('signature', 'signaturekey', 'signdocument')");
			
			if(rs.next()) {
				return rs.getLong("total");
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
	
}
