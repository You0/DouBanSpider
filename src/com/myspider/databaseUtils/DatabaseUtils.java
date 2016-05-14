package com.myspider.databaseUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class DatabaseUtils {
	public static DatabasePool dPool = DatabasePool.GetInstance();

	public static Connection getConnection() {
		try {
			return dPool.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Connection getConnection_Nopool() {
		String driverClass = "com.mysql.jdbc.Driver";
		// JDBC URL
		String jdbcUrl = "jdbc:mysql:///douban";
		// user
		String user = "root";
		// password
		String password = "9562";
		// 2. �������ݿ���������(��Ӧ�� Driver ʵ��������ע�������ľ�̬�����.)
		try {
			Class.forName(driverClass);
			Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
			return connection;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 3. ͨ�� DriverManager �� getConnection() ������ȡ���ݿ�����.
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// ����һ��int���ݣ���sql��ѯ�Ľ��ֻ����һ��int�����򽫳���
	public static int queryInt(String sql, Connection connection, Object... objs) {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				preparedStatement.setObject(i + 1, objs[i]);
			}
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			releaseDB(resultSet, preparedStatement);
		}

		return 0;
	}

	public static <T> ArrayList<T> queryList(Class<T> clazz, String sql, Connection connection, Object... objs) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ResultSetMetaData rData;
		try {
			preparedStatement = connection.prepareStatement(sql);
			SetValues(preparedStatement, objs);
			resultSet = preparedStatement.executeQuery();
			rData = resultSet.getMetaData();
			HashMap<String, Object> hashMap = new HashMap<>();
			ArrayList<T> arrayList = new ArrayList<T>();

			while (resultSet.next()) {
				for (int i = 0; i < rData.getColumnCount(); i++) {
					hashMap.put(rData.getColumnName(i + 1), resultSet.getObject(i + 1));
				}

				T entity = clazz.newInstance();

				for (Entry<String, Object> entry : hashMap.entrySet()) {
					ReflectionUtils.setFieldValue(entity, entry.getKey(), entry.getValue());
				}

				arrayList.add(entity);
			}

			return arrayList;

		} catch (SQLException e) {

			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			releaseDB(resultSet, preparedStatement);
		}

		return null;
	}

	public static void update(String sql, Connection connection, Object... objs) {
		PreparedStatement preparedStatement = null;

		try {
			DatabaseUtils.beginTx(connection);

			preparedStatement = connection.prepareStatement(sql);
			for (int i = 0; i < objs.length; i++) {
				preparedStatement.setObject(i + 1, objs[i]);
			}
			if (!preparedStatement.execute()) {
				DatabaseUtils.commit(connection);
			} else {
				DatabaseUtils.rollback(connection);
			}

		} catch (Exception e) {
			e.printStackTrace();
			DatabaseUtils.rollback(connection);
		} finally {
			DatabaseUtils.releaseDB(null, preparedStatement);
		}

	}

	private static void SetValues(PreparedStatement preparedStatement, Object... objs) {
		for (int i = 0; i < objs.length; i++) {
			try {
				preparedStatement.setObject(i + 1, objs[i]);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// �ύ����
	public static void commit(Connection connection) {
		if (connection != null) {
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	// �ع�����
	public static void rollback(Connection connection) {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// ��ʼ����
	public static void beginTx(Connection connection) {
		if (connection != null) {
			try {
				connection.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				// ���ݿ����ӳص� Connection ������� close ʱ
				// ��������Ľ��йر�, ���ǰѸ����ݿ����ӻ�黹�����ݿ����ӳ���.
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	

	public static void releaseDB(ResultSet resultSet, Statement statement) {

		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
