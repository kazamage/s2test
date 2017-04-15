package jp.pgw.develop.swallow.sample.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.sql.DataSource;

import jp.pgw.develop.swallow.sample.service.DBService;

@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DBServiceImpl implements DBService {

	DataSource dataSource;

	@Override
	public void insert() {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = dataSource.getConnection();
			stmt = connection.createStatement();
			System.out.println(stmt.execute("insert into persons (first_name, last_name, age) values ('first_name', 'last_name', 37)"));
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e2) {
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e2) {
				}
			}
		}
	}

	@Override
	public int query() {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		int result = -1;
		try {
			connection = dataSource.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select count(*) from persons");
			rs.next();
			result = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e2) {
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e2) {
				}
			}
		}
		return result;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
