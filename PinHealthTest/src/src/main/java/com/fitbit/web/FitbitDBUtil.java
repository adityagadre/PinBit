package src.main.java.com.fitbit.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import com.fitbit.api.common.model.user.UserInfo;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class FitbitDBUtil {

	public static void addUserInfo(Properties p, String oauthToken,
			String oauthVerifier) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Connection connect;
			boolean b=false;
			try {
				connect = DriverManager.getConnection("jdbc:mysql://54.193.91.8/market","abc","a");

				Statement s=(Statement) connect.createStatement();
				ResultSet rs=s.executeQuery("select * from patient where encodedId='" + p.getProperty("encodedId") + "'");
			if(rs.next())
			{
				b=rs.getBoolean("uploaded");
			}
				/*if(rs.next())
				{
					
					PreparedStatement ps=(PreparedStatement) connect.prepareStatement("update patient set auth_token=?,auth_verifier=? where encodedId=?" );
					ps.setString(1, oauthToken);
					ps.setString(2, oauthVerifier);
					ps.setString(3, userInfo.getEncodedId());
					ps.executeUpdate();
					
				}
				else*/
				{
					PreparedStatement ps=(PreparedStatement) connect.prepareStatement("insert into patient values(default,?,?,?,?,?,?,?,?,?,?,?,?,?);" );
					
					ps.setString(1, p.getProperty("fullname"));
					ps.setString(2, oauthToken);
					ps.setString(3, oauthVerifier);
					ps.setString(4, p.getProperty("encodedId"));
					ps.setString(5, p.getProperty("blood_pressure"));
					ps.setString(6, p.getProperty("heart_beat"));
					ps.setString(7, p.getProperty("weight"));
					ps.setString(8, p.getProperty("height"));
					LocalDate d=new LocalDate();
					LocalDate d1=LocalDate.parse(p.getProperty("dob"));
					ps.setInt(9,  Years.yearsBetween(d1,d).getYears());
					ps.setString(10, p.getProperty("sleeptime"));
					ps.setString(11, p.getProperty("distance"));
					ps.setString(12, p.getProperty("floors"));
					ps.setBoolean(13, b);
					ps.executeUpdate();	
				}
				
				
				connect.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// TODO Auto-generated method stub
	}
	
	public static Properties getUserProperties(String id)
	{
		Properties p=new Properties();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection connect = DriverManager.getConnection("jdbc:mysql://54.193.91.8/market","abc","a");

			Statement s=(Statement) connect.createStatement();
			ResultSet rs=s.executeQuery("select * from patient where encodedId='" + id + "'");
			
			if(rs.next())
			{
				p.setProperty("auth_token", rs.getString(3));
				p.setProperty("auth_verifier", rs.getString(4));
			}
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
