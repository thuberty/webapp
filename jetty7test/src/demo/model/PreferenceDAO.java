package demo.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class PreferenceDAO {
	private List<Connection> connectionPool = new ArrayList<Connection>();

	private String appname;
    private String jdbcDriver;
    private String jdbcURL;
    private String jdbcUser;
    private String jdbcPassword;
	

	public PreferenceDAO(String jdbcDriver, String jdbcURL, String jdbcUser,
			String jdbcPassword, String appname) {
		this.jdbcDriver   = jdbcDriver;
		this.jdbcURL      = jdbcURL;
		this.jdbcUser     = jdbcUser;
		this.jdbcPassword = jdbcPassword;
		this.appname = appname;
			
		try {
		    createTables();
		} catch (MyDAOException e) {
			System.out.println(e);
		    // Ignore ... if thrown assume it's because table already exists
		    // If it's some other problem we'll fail later on
		}
	}

	/**
	 * Fetch a connection from the synchronized pool of connections
	 * 
	 * @return
	 * @throws MyDAOException
	 */
    private synchronized Connection getConnection() throws MyDAOException {
		if (connectionPool.size() > 0) {
		    return connectionPool.remove(connectionPool.size()-1);
		}
		
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new MyDAOException(e);
        }

        try {
            return DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
        } catch (SQLException e) {
            throw new MyDAOException(e);
        }
    }
    
    /**
     * Release a connection back to the pool
     * 
     * @param con
     */
    private synchronized void releaseConnection(Connection con) {
    	connectionPool.add(con);
    }
    
    /**
     * Create preference tables in db
     * 
     * @throws MyDAOException
     */
    private void createTables() throws MyDAOException {
    	Connection con = null;
        try {
        	con = getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("CREATE TABLE " + appname + "_preferables (pid SERIAL PRIMARY KEY, term VARCHAR(255) NOT NULL UNIQUE)");
            stmt.executeUpdate("CREATE TABLE " + appname + "_user_preferences (pid INT, uid INT, preference INT, UNIQUE (pid, uid))");
            stmt.close();
        	
            releaseConnection(con);

        } catch (SQLException e) {
        	System.out.println(e);
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
	    throw new MyDAOException(e);
        }
    }
    
    /**
     * Fetch list of Preferable objects that correspond to a given user
     * 
     * @param uid
     * @return
     * @throws MyDAOException
     */
    public Preferable[] getUserPreferences(int uid) throws MyDAOException {
    	Connection con = null;
        
    	try {
		    con = getConnection();
	
		    PreparedStatement pstmt = con.prepareStatement("SELECT * FROM " + appname + "_user_preferences AS u LEFT JOIN " 
		    		+ appname + "_preferables AS p WHERE p.pid = u.pid AND uid = ? ORDER BY p.pid");
		    pstmt.setInt(1, uid);
		    ResultSet rs = pstmt.executeQuery();
		    
		    ArrayList<Preferable> preferables = new ArrayList<Preferable>();
		    
		    while (rs.next()) {
		    	Preferable preferable = new Preferable();
		    	preferable.setPid(rs.getInt("pid"));
		    	preferable.setPreference(rs.getInt("preference"));
		    	preferable.setTerm(rs.getString("term"));
		    	preferables.add(preferable);
		    }
        	
		    rs.close();
		    pstmt.close();
		    releaseConnection(con);
		    
		    Preferable[] preferablesArray = new Preferable[preferables.size()];
		    
            return preferables.toArray(preferablesArray);      
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
            throw new MyDAOException(e);
        }
    }
    
    /**
     * Create a new Preferable in db
     * 
     * @param likeable
     * @return
     * @throws MyDAOException
     */
    public void create(Preferable preferable) throws MyDAOException {
    	Connection con = null;
        
    	try {
    		con = getConnection();
    		PreparedStatement pstmt = con.prepareStatement("INSERT INTO " + appname + "_preferables (term) VALUES (?) RETURNING pid");
        	
		    pstmt.setString(1, preferable.getTerm());   
	    
		    // fetch created lid
		    ResultSet rs = pstmt.executeQuery();   
		    int pid = 0;
		    if (rs.next()) {
		    	pid = rs.getInt("pid");
		    }
		    else {
		    	throw new SQLException("Preferable couldn't be saved or probably exists");
		    }
		    
		    pstmt.close();
		    releaseConnection(con);
		    
		    // set pid in preferable
		    preferable.setPid(pid);
		    
		    return ;
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
            System.out.println(e);
            throw new MyDAOException(e);
        }
    }
    
    public Preferable lookup(Preferable preferable) throws MyDAOException {
    	Connection con = null;
    	Preferable newPreferable;
        
    	try {
		    con = getConnection();
		    
		    PreparedStatement pstmt;
		    
		    if (preferable.getPid() != 0) {
		    	// lookup by pid
		    	pstmt = con.prepareStatement("SELECT * FROM " + appname + "_preferables WHERE pid = ?");
		    	pstmt.setInt(1, preferable.getPid());
		    }
		    else {
		    	// lookup by term
		    	pstmt = con.prepareStatement("SELECT * FROM " + appname + "_preferables WHERE term = ?");
		    	pstmt.setString(1, preferable.getTerm());
		    }

		    ResultSet rs = pstmt.executeQuery();
		    
		    if (rs.next()) {
		    	newPreferable = new Preferable();
		    	newPreferable.setPid(rs.getInt("pid"));
		    	newPreferable.setTerm(rs.getString("term"));
		    }
		    else {
		    	newPreferable = null;
		    }
        	
		    rs.close();
		    pstmt.close();
		    releaseConnection(con);
		    
            return newPreferable;      
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
            throw new MyDAOException(e);
        }
    }
    
    public void setPreference(Preferable preferable) throws MyDAOException {
    	Connection con = null;
        
    	try {
		    con = getConnection();
		    
		    // try updating in the case it exists
		    PreparedStatement pstmtInsert = con.prepareStatement("UPDATE " + appname + "_user_preferences SET preference = ? WHERE pid = ? AND uid = ?");
		    pstmtInsert.setInt(1, preferable.getPreference());
		    pstmtInsert.setInt(2, preferable.getPid());
		    pstmtInsert.setInt(3, preferable.getUser().getUid());
		    
		    pstmtInsert.close();
		    
		    // insert if it doesn't exist yet (will through error because of constraint of uniqueness on table)
		    PreparedStatement pstmtUpdate = con.prepareStatement("INSERT INTO " + appname + "_user_preferences (preference, pid, uid) VALUES (?,?,?)");
		    pstmtUpdate.setInt(1, preferable.getPreference());
		    pstmtUpdate.setInt(2, preferable.getPid());
		    pstmtUpdate.setInt(3, preferable.getUser().getUid());
		    
		    int count = pstmtInsert.executeUpdate();
		    pstmtUpdate.executeUpdate();
		    
		    if (count != 1) throw new SQLException("Update updated "+count+" rows");
        	
		    pstmtInsert.close();
		    pstmtUpdate.close();
		    releaseConnection(con);
		        
        } catch (Exception e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
            System.out.println(e);
            throw new MyDAOException(e);
        }
    }
}
