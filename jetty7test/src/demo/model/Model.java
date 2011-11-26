package demo.model;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.mybeans.factory.BeanTable;

public class Model {
	private UserDAO  userDAO;
	private PreferenceDAO preferenceDAO;

	public Model(ServletConfig config) throws ServletException {

		String jdbcDriver   = config.getInitParameter("jdbcDriverName");
		String jdbcURL      = config.getInitParameter("jdbcURL");
		String jdbcUser     = config.getInitParameter("jdbcUser");
		String jdbcPassword = config.getInitParameter("jdbcPassword");
		BeanTable.useJDBC(jdbcDriver, jdbcURL, jdbcUser, jdbcPassword);
		String appName = config.getInitParameter("appName");
		if (appName == null || appName.length() == 0) {
			throw new ServletException("appName servlet configuration parameter not set: " + appName);
		}
		userDAO  = new UserDAO(appName);
		preferenceDAO = new PreferenceDAO(jdbcDriver, jdbcURL, jdbcUser, jdbcPassword, appName);
	}

	public UserDAO  getUserDAO()  { return userDAO; }
	public PreferenceDAO getPreferenceDAO() { return preferenceDAO; }
}
