/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package tracker;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import play.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatabaseFactory {
	public static enum ProviderType {
		MySql,
		MsSql
	}

	// =========================================================
	// Data Field
	private static DatabaseFactory _instance;
	private static ScheduledExecutorService _executor;
	private ProviderType _providerType;
	private ComboPooledDataSource _source;

	// =========================================================
	// Constructor
	public DatabaseFactory() throws SQLException {
		try {
			if (Config.getLong("database.pool.maxConnections") < 2) {
				Config.set("database.pool.maxConnections", 2);
				Logger.warn("A minimum of " + Config.getLong("database.pool.maxConnections") + " database connections are required.");
			}

			_source = new ComboPooledDataSource();
			_source.setAutoCommitOnClose(true);

			_source.setInitialPoolSize(10);
			_source.setMinPoolSize(10);
			_source.setMaxPoolSize(Math.max(10, Config.getInt("database.pool.maxConnections")));

			_source.setAcquireRetryAttempts(0); // try to obtain connections indefinitely (0 = never quit)
			_source.setAcquireRetryDelay(500); // 500 milliseconds wait before try to acquire connection again
			_source.setCheckoutTimeout(0); // 0 = wait indefinitely for new connection
			// if pool is exhausted
			_source.setAcquireIncrement(5); // if pool is exhausted, get 5 more connections at a time
			// cause there is a "long" delay on acquire connection
			// so taking more than one connection at once will make connection pooling
			// more effective.

			// this "connection_test_table" is automatically created if not already there
			_source.setAutomaticTestTable("_connection_test_table");
			// _source.setTestConnectionOnCheckin(false);

			_source.setTestConnectionOnCheckin(true);
			_source.setTestConnectionOnCheckout(true);
			_source.setPreferredTestQuery("SELECT 1");


			// testing OnCheckin used with IdleConnectionTestPeriod is faster than  testing on checkout

			_source.setIdleConnectionTestPeriod(3600); // test idle connection every 60 sec
			_source.setMaxIdleTime(Config.getInt("database.pool.maxIdleTime")); // 0 = idle connections never expire
			// *THANKS* to connection testing configured above
			// but I prefer to disconnect all connections not used
			// for more than 1 hour

			// enables statement caching,  there is a "semi-bug" in c3p0 0.9.0 but in 0.9.0.2 and later it's fixed
			_source.setMaxStatementsPerConnection(100);

			_source.setBreakAfterAcquireFailure(false); // never fail if any way possible
			// setting this to true will make
			// c3p0 "crash" and refuse to work
			// till restart thus making acquire
			// errors "FATAL" ... we don't want that
			// it should be possible to recover
			_source.setDriverClass(Config.getString("database.connection.driver"));
			_source.setJdbcUrl(Config.getString("database.connection.url"));
			_source.setUser(Config.getString("database.connection.username"));
			_source.setPassword(Config.getString("database.connection.password"));

			/* Test the connection */
			_source.getConnection().close();

			Logger.debug("Database Connection Working");

			if (Config.getString("database.connection.driver").toLowerCase().contains("microsoft"))
				_providerType = ProviderType.MsSql;
			else
				_providerType = ProviderType.MySql;
		} catch (SQLException sqlException) {
			Logger.debug("Database Connection FAILED");
			throw sqlException;
		} catch (Exception exception) {
			Logger.debug("Database Connection FAILED");
			throw new SQLException("Could not init DB connection:" + exception.getMessage());
		}
	}

	// =========================================================
	// Method - Public
	public final String prepQuerySelect(String[] fields, String tableName, String whereClause, boolean returnOnlyTopRecord) {
		String msSqlTop1 = "";
		String mySqlTop1 = "";
		if (returnOnlyTopRecord) {
			if (getProviderType() == ProviderType.MsSql)
				msSqlTop1 = " Top 1 ";
			if (getProviderType() == ProviderType.MySql)
				mySqlTop1 = " Limit 1 ";
		}
		String query = "SELECT " + msSqlTop1 + safetyString(fields) + " FROM " + tableName + " WHERE " + whereClause + mySqlTop1;
		return query;
	}

	public void shutdown() {
		try {
			_source.close();
		} catch (Exception e) {
			Logger.info("", e);
		}

		try {
			_source = null;
		} catch (Exception e) {
			Logger.info("", e);
		}
	}

	public final String safetyString(String... whatToCheck) {
		// NOTE: Use brace as a safty precaution just incase name is a reserved word
		final char braceLeft;
		final char braceRight;

		if (getProviderType() == ProviderType.MsSql) {
			braceLeft = '[';
			braceRight = ']';
		} else {
			braceLeft = '`';
			braceRight = '`';
		}

		int length = 0;

		for (String word : whatToCheck) {
			length += word.length() + 4;
		}

		final StringBuilder sbResult = new StringBuilder(length);

		for (String word : whatToCheck) {
			if (sbResult.length() > 0) {
				sbResult.append(", ");
			}

			sbResult.append(braceLeft);
			sbResult.append(word);
			sbResult.append(braceRight);
		}

		return sbResult.toString();
	}

	// =========================================================
	// Property - Public
	public static DatabaseFactory getInstance() throws SQLException {
		synchronized (DatabaseFactory.class) {
			if (_instance == null) {
				_instance = new DatabaseFactory();
			}
		}
		return _instance;
	}

	public Connection getConnection() throws SQLException {
		Connection con = null;
		while (con == null) {
			con = _source.getConnection();
			getExecutor().schedule(new ConnectionCloser(con, new RuntimeException()), 60, TimeUnit.SECONDS);
		}
		return con;
	}

	private static class ConnectionCloser implements Runnable {
		private Connection c;
		private RuntimeException exp;

		public ConnectionCloser(Connection con, RuntimeException e) {
			this.c = con;
			this.exp = e;
		}

		public void run() {
			try {
				if (!c.isClosed()) {
					Logger.warn("Unclosed connection! Trace: " + exp.getStackTrace()[1], exp);
				}
			} catch (SQLException e) {
				Logger.warn("", e);
			}

		}
	}

	public static void close(Connection connection) {
		close(connection, null);
	}

	public static void close(Statement statement) {
		close(null, statement);
	}

	public static void close(Connection connection, Statement statement) {
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.warn("Failed to close database connection!", e);
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				Logger.warn("Failed to close statement!", e);
			}
		}
	}

	private static ScheduledExecutorService getExecutor() {
		if (_executor == null) {
			synchronized (DatabaseFactory.class) {
				if (_executor == null) {
					_executor = Executors.newSingleThreadScheduledExecutor();
				}
			}
		}
		return _executor;
	}

	public int getBusyConnectionCount() throws SQLException {
		return this._source.getNumBusyConnectionsDefaultUser();
	}

	public int getIdleConnectionCount() throws SQLException {
		return this._source.getNumIdleConnectionsDefaultUser();
	}

	public final ProviderType getProviderType() {
		return this._providerType;
	}
}
