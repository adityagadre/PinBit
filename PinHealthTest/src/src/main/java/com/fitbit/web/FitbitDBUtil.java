package src.main.java.com.fitbit.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
				ps.executeUpdate();
				connect.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
		
		// TODO Auto-generated method stub
		
	}

}
