package bai7;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/fileUpdloadDBServlet")
@MultipartConfig(maxFileSize = 16177215)
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int BUFFER_SIZE = 4096;
	private static final String SAVE_DIR = "images";
	private String db = "jdbc:sqlserver://localhost:1433;databaseName=UploadFileServletDB";
	private String dbUser = "sa";
	private String dbPassword = "123";

	public Servlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// doGet(request, response);
		String fname = request.getParameter("fname");
		String lname = request.getParameter("lname");
		InputStream inputStream = null;
		Part part = request.getPart("photo");
		String fileUploadName = "";
		if (part != null) {
			fileUploadName = part.getName();
			inputStream = part.getInputStream();
		}
		Connection con = null;
		String message = null;
		String filePath = "F:/MyIUH/FIT/Java/WWW2020/ServletUploadFile/upload/" + fileUploadName + ".jpg";

		try {
			DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
			con = DriverManager.getConnection(db, dbUser, dbPassword);
			System.out.println("ketnoithanhcong");
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("Connect DB failed!");
//		}
//		
			String sql = "INSERT INTO dbo.contacts( first_name, last_name, photo )VALUES  (?,?,?)";
//		try {
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, fname);
			statement.setString(2, lname);
			if (inputStream != null) {
				statement.setBlob(3, inputStream);

			}
			int row = statement.executeUpdate();
			if (row > 0) {
				message = "Save successed!";
			}
			String sql1 = "SELECT photo FROM dbo.contacts WHERE first_name=? AND last_name = ?";
			statement = con.prepareStatement(sql1);
			statement.setString(1, fname);
			statement.setString(2, lname);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				Blob blob = result.getBlob("photo");
				inputStream = blob.getBinaryStream();
				OutputStream out = new FileOutputStream(filePath);
				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					System.out.println("da luu file vao o cung");
				}
				inputStream.close();
				out.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("insert failed");
			message = "ERROR:" + e.getMessage();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
			}
			request.setAttribute("message", message);
			getServletContext().getRequestDispatcher("/messageServletSQL").forward(request, response);
		}

	}

}
