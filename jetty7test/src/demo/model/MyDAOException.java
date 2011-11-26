package demo.model;

public class MyDAOException extends Exception {
	public MyDAOException(Exception e) { super(e); }
	public MyDAOException(String s)    { super(s); }
}

