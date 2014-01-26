package src.main.java.com.fitbit.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import com.fitbit.api.common.model.user.UserInfo;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class FitbitDBUtil {

	public static void addUserInfo(UserInfo userInfo, String oauthToken,
			String oauthVerifier) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Connection connect;
			try {
				connect = DriverManager.getConnection("jdbc:mysql://54.193.91.8/market","abc","a");

				PreparedStatement ps=(PreparedStatement) connect.prepareStatement("insert into patient values(default,?,?,?);" );
				
				ps.setString(1, userInfo.getFullName());
				ps.setString(2, oauthToken);
				ps.setString(3, oauthVerifier);
				ps.setString(4, userInfo.getEncodedId());
				ps.executeUpdate();
				connect.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// TODO Auto-generated method stub
	}
	
	public static Properties getUser(String id)
	{
		Properties p=new Properties();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection connect = DriverManager.getConnection("jdbc:mysql://54.193.91.8/market","abc","a");

			Statement s=(Statement) connect.createStatement();
			
			ResultSet rs=s.executeQuery("select * from patient where encodedId='" + id + "'");
			
			rs.first();
			p.setProperty("auth_token", rs.getString(2));
			p.setProperty("auth_verifier", rs.getString(3));
			rs.close();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return p;
		
		
	}

}
