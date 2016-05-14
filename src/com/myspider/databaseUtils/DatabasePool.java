package com.myspider.databaseUtils;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import Config.Config;

public class DatabasePool {
	private static DatabasePool dPool = new DatabasePool();
	// private ConcurrentHashMap<K, V>;
	private ComboPooledDataSource cpds;

	private DatabasePool() {
		InitC3P0();
	}

	public static DatabasePool GetInstance() {
		return dPool;
	}

	public Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}

	public void InitC3P0() {
		cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(Config.DriverClass);
			cpds.setJdbcUrl(Config.JDBCURL);
			cpds.setUser(Config.USER);
			cpds.setPassword(Config.PWASSWORD);
			cpds.setMaxStatements(Config.MaxStatements);
			cpds.setInitialPoolSize(Config.InitialPoolSize);
			cpds.setMaxPoolSize(Config.MaxPoolSize);
			cpds.setMinPoolSize(Config.MinPoolSize);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
}
