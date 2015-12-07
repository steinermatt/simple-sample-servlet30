package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = "/TestServlet", loadOnStartup = 1)
public class TestServlet extends HttpServlet
{
	private static final Logger logger = LoggerFactory.getLogger(TestServlet.class);
	
	
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Resource(lookup="jdbc/DefaultDB")
    private javax.sql.DataSource ds;
    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException
	{
		PrintWriter writer = response.getWriter();
		writer.println("A servlet without web.xml: " + getServletName());
		
		if (ds != null)
		{
			try
			{
				DatabaseMetaData metaData = ds.getConnection().getMetaData();
				writer.println(new DBInformation(metaData).toString());
				
				logger.info("DB metadata found: {}", new DBInformation(metaData).toString());
			}
			catch (Exception ex)
			{
				logger.error("An error occured:", ex);
			}
		}
	}
	
	/**
	 * Provides meta information about the underlying DB of the bound {@link DataSource}. 
	 * 
	 * @see DataSource#getConnection()
	 * @see Connection#getMetaData()
	 */
	public static class DBInformation
	{
		String url = null;

		String dbName = null;
		int dbMajorVersion = 0;
		int dbMinorVersion = 0;

		String driverName = null;
		String driverVersion = null;

		String userName = null;

		public DBInformation(DatabaseMetaData metaData) throws SQLException
		{
			url = metaData.getURL();
			dbName = metaData.getDatabaseProductName();
			dbMajorVersion = metaData.getDatabaseMajorVersion();
			dbMinorVersion = metaData.getDatabaseMinorVersion();

			driverName = metaData.getDriverName();
			driverVersion = metaData.getDriverVersion();

			userName = metaData.getUserName();
		}

		public String getUrl()
		{
			return this.url;
		}

		public String getDbName()
		{
			return this.dbName;
		}

		public int getDbMajorVersion()
		{
			return this.dbMajorVersion;
		}

		public int getDbMinorVersion()
		{
			return this.dbMinorVersion;
		}

		public String getDriverName()
		{
			return this.driverName;
		}

		public String getDriverVersion()
		{
			return driverVersion;
		}

		public String getUserName()
		{
			return userName;
		}
		
		public String toString()
		{
			final String CRLF = System.getProperty("line.separator");
			
			StringWriter str = new StringWriter();
			
			str.write(CRLF);
			str.write(MessageFormat.format("Driver Name: {0}{1}", this.driverName, CRLF));
			str.write(MessageFormat.format("Driver Version: {0}{1}", this.driverVersion, CRLF));
			str.write(MessageFormat.format("URL: {0}{1}", this.url, CRLF));
			str.write(MessageFormat.format("DB Name: {0}{1}", this.dbName, CRLF));
			str.write(MessageFormat.format("Username: {0}{1}", this.userName, CRLF));	
		
			return str.toString();
		}
		
	}
}
