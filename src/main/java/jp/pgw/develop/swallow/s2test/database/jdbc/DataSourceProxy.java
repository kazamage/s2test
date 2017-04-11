package jp.pgw.develop.swallow.s2test.database.jdbc;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class DataSourceProxy implements DataSource {

	final DataSource delegate;

	final boolean enableCommit;

	public DataSourceProxy(DataSource delegate, boolean enableCommit) {
		this.delegate = delegate;
		this.enableCommit = enableCommit;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return delegate.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		delegate.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
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
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return delegate.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return delegate.isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(delegate.getConnection());
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnection(delegate.getConnection(username, password));
	}

	protected Connection getConnection(Connection delegate) throws SQLException {
		if (enableCommit) {
			return delegate;
		}
		delegate.setAutoCommit(false);
		return newConnectionProxy(delegate);
	}

	protected static Connection newConnectionProxy(final Connection delegate) {
		return Connection.class.cast(Proxy.newProxyInstance(Connection.class.getClassLoader(),
				new Class[] { Connection.class }, new ConnectionHandler(delegate)));
	}

	protected static class ConnectionHandler implements InvocationHandler {

		final Connection delegate;

		ConnectionHandler(Connection delegate) {
			this.delegate = delegate;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("setAutoCommit".equals(method.getName()) || "commit".equals(method.getName())) {
				return null;
			}
			return method.invoke(delegate, args);
		}

	}

}
