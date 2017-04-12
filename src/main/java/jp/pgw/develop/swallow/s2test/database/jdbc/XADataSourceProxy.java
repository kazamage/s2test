package jp.pgw.develop.swallow.s2test.database.jdbc;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.seasar.extension.dbcp.impl.XAConnectionImpl;

public class XADataSourceProxy implements XADataSource {

	final XADataSource delegate;

	public XADataSourceProxy(final XADataSource delegate) {
		this.delegate = delegate;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return delegate.getLogWriter();
	}

	@Override
	public void setLogWriter(final PrintWriter out) throws SQLException {
		delegate.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(final int seconds) throws SQLException {
		delegate.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return delegate.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return delegate.getParentLogger();
	}

	@Override
	public XAConnection getXAConnection() throws SQLException {
		return getXAConnection(delegate.getXAConnection());
	}

	@Override
	public XAConnection getXAConnection(final String user, final String password) throws SQLException {
		return getXAConnection(delegate.getXAConnection(user, password));
	}

	protected XAConnection getXAConnection(final XAConnection delegate) throws SQLException {
		if (!(delegate instanceof XAConnectionImpl)) {
			return delegate;
		}
		return new XAConnectionImpl(DataSourceProxy.newConnectionProxy(delegate.getConnection()));
	}

}
