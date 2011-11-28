/**
 * Fall 2011 - 15-437
 * Tyler Huberty
 * Jack Phelan
 * 
 * Preference DAO
 */

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
 * Data access object for preference tables in db and operations related to preferables.
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
			System.out.println("ignore already exists error");
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
	public List<Preferable> getUserPreferences(int uid) throws MyDAOException {
		Connection con = null;
		System.out.println("getting preferences for "+uid);
		try {
			con = getConnection();

			PreparedStatement pstmt = con.prepareStatement("SELECT * FROM " + appname + "_user_preferences AS u LEFT JOIN " 
					+ appname + "_preferables AS p ON (p.pid = u.pid) WHERE uid = ? ORDER BY p.pid");
			//PreparedStatement pstmt = con.prepareStatement("SELECT * FROM " + appname + "_user_preferences WHERE uid = ?");
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

			return preferables;      
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

		if (lookupPreferable(preferable)==null){
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
	}

	/*
	 * Looks up preferable in db and fills in either missing pid or term
	 */
	public Preferable lookupPreferable(Preferable preferable) throws MyDAOException {
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

	/*
	 * returns a users preference towards a certain perferable
	 */
	public Integer lookupUserPreference(Preferable preferable) throws MyDAOException {
		Connection con = null;
		Integer result = null;

		try {
			con = getConnection();
			PreparedStatement pstmt;

			// lookup by pid, uid
			pstmt = con.prepareStatement("SELECT * FROM " + appname + "_user_preferences WHERE pid = ? AND uid = ?");
			pstmt.setInt(1, preferable.getPid());
			pstmt.setInt(2, preferable.getUser().getUid());

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt("preference");
			}

			rs.close();
			pstmt.close();
			releaseConnection(con);

			return result;      
		} catch (Exception e) {
			System.out.println("5");
			try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
			throw new MyDAOException(e);
		}
	}

	/*
	 * Commits a preferable to the db, either updating it or inserting it for the user
	 */
	public void setPreference(Preferable preferable) throws MyDAOException {
		Connection con = null;

		try {
			con = getConnection();
			int count;
			// try updating in the case it exists
			if(lookupUserPreference(preferable)!=null) {
				System.out.println("preference exists");
				PreparedStatement pstmtInsert = con.prepareStatement("UPDATE " + appname + "_user_preferences SET preference = ? WHERE pid = ? AND uid = ?");
				pstmtInsert.setInt(1, preferable.getPreference());
				pstmtInsert.setInt(2, preferable.getPid());
				pstmtInsert.setInt(3, preferable.getUser().getUid());
				count = pstmtInsert.executeUpdate();
				pstmtInsert.close();
			} else {
				// insert if it doesn't exist yet (will throw error because of constraint of uniqueness on table)
				System.out.println("preference does not exist");
				PreparedStatement pstmtUpdate = con.prepareStatement("INSERT INTO " + appname + "_user_preferences (preference, pid, uid) VALUES (?,?,?)");
				pstmtUpdate.setInt(1, preferable.getPreference());
				pstmtUpdate.setInt(2, preferable.getPid());
				pstmtUpdate.setInt(3, preferable.getUser().getUid());

				count = pstmtUpdate.executeUpdate();
				pstmtUpdate.close();
			}
			if (count != 1) throw new SQLException("Update updated "+count+" rows");

			releaseConnection(con);

		} catch (Exception e) {
			try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
			System.out.println(e);
			throw new MyDAOException(e);
		}
	}
}
