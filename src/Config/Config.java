package Config;

public class Config {
	public final static int MAX_THREADS = 10;
	
	public final static int USER_URL = 1;
	public final static int MOVIE_URL = 2;
	
	//DATABASE
	public final static String DriverClass = "com.mysql.jdbc.Driver";
	public final static String JDBCURL = "jdbc:mysql:///douban";
	public final static String USER = "root";
	public final static String PWASSWORD = "9562";
	public final static int MaxStatements = 50;
	public final static int InitialPoolSize = 10;
	public final static int MaxPoolSize = 50;
	public final static int MinPoolSize = 5;

	
}
