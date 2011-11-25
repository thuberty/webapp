package demo.model;

import java.util.Arrays;

import org.mybeans.dao.DAOException;
import org.mybeans.dao.GenericDAO;
import org.mybeans.factory.RollbackException;
import org.mybeans.factory.Transaction;

public class UserDAO extends GenericDAO<User> {
	public UserDAO(String appName) {
		super(User.class, appName+"_users", "userName");

		// Long running web apps need to clean up idle database connections.
		// So we can tell each BeanTable to clean them up.  (You would only notice
		// a problem after leaving your web app running for several hours.)
		getTable().setIdleConnectionCleanup(true);
	}

	public void setPassword(String userName, String password) throws DAOException {
		try {
			Transaction.begin();
			User dbUser = getFactory().lookup(userName);

			if (dbUser == null) {
				throw new DAOException("User "+userName+" no longer exists");
			}

			dbUser.setPassword(password);
			Transaction.commit();
		} catch (RollbackException e) {
			throw new DAOException(e);
		} finally {
			if (Transaction.isActive()) Transaction.rollback();
		}
	}

	public User lookup(String userName) throws DAOException {
		try {
			if (userName.length() == 0) return null;
			return getFactory().lookup(userName);
		} catch (RollbackException e) {
			throw new DAOException(e);
		}
	}

	public boolean verifyPassword(String userName, String password) {
		User u;
		try {
			u = lookup(userName);
		} catch (DAOException e) {
			return false;
		}
		return (u == null)||(password == null)||password.equals(u.getPassword());
	}
	
	public User[] getUsers() throws DAOException {
		try {
			Transaction.begin();
			User[] users = getFactory().match();
			Arrays.sort(users);  
			return users;
		} catch (RollbackException e) {
			throw new DAOException(e);
		}
	}
}
