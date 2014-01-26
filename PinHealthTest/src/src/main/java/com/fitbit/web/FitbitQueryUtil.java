package src.main.java.com.fitbit.web;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.fitbit.api.FitbitAPIException;
import com.fitbit.api.client.FitbitAPIEntityCache;
import com.fitbit.api.client.FitbitApiClientAgent;
import com.fitbit.api.client.FitbitApiCredentialsCache;
import com.fitbit.api.client.FitbitApiCredentialsCacheMapImpl;
import com.fitbit.api.client.FitbitApiEntityCacheMapImpl;
import com.fitbit.api.client.FitbitApiSubscriptionStorage;
import com.fitbit.api.client.FitbitApiSubscriptionStorageInMemoryImpl;
import com.fitbit.api.client.LocalUserDetail;
import com.fitbit.api.client.service.FitbitAPIClientService;
import com.fitbit.api.common.model.user.UserInfo;
import com.fitbit.api.model.APIResourceCredentials;

public class FitbitQueryUtil {
	private static final Logger LOGGER = Logger.getLogger(FitbitQueryUtil.class.getName());
    private static FitbitAPIEntityCache entityCache = new FitbitApiEntityCacheMapImpl();
    private static FitbitApiCredentialsCache credentialsCache = new FitbitApiCredentialsCacheMapImpl();
    private static FitbitApiSubscriptionStorage subscriptionStore = new FitbitApiSubscriptionStorageInMemoryImpl();

	private static String apiBaseUrl,fitbitSiteBaseUrl,exampleBaseUrl,clientConsumerKey,clientSecret;
	static {
            Properties properties = new Properties();
            try {
				properties.load(FitbitQueryUtil.class.getClassLoader().getResourceAsStream("config.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            apiBaseUrl = properties.getProperty("apiBaseUrl");
            fitbitSiteBaseUrl = properties.getProperty("fitbitSiteBaseUrl");
            exampleBaseUrl = properties.getProperty("exampleBaseUrl").replace("/app", "");
            clientConsumerKey = properties.getProperty("clientConsumerKey");
            clientSecret = properties.getProperty("clientSecret");
    }
	
	
	public static UserInfo getUserInfo(String encodedId, String token, String verifier){
		FitbitAPIClientService<FitbitApiClientAgent> apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
                new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl, credentialsCache),
                clientConsumerKey,
                clientSecret,
                credentialsCache,
                entityCache,
                subscriptionStore
        );
		APIResourceCredentials resourceCredentials = apiClientService.getResourceCredentialsByTempToken(token);
        if (resourceCredentials == null) {
            LOGGER.log(Level.SEVERE,"Unrecognized temporary token when attempting to complete authorization: " + token);
        }
        // Get token credentials only if necessary:
        if (!resourceCredentials.isAuthorized()) {
            // The verifier is required in the request to get token credentials:
        	LOGGER.info("resource credentials not authorized, now getting token credentials");
            resourceCredentials.setTempTokenVerifier(verifier);
            try {
                // Get token credentials for user:
                apiClientService.getTokenCredentials(new LocalUserDetail(resourceCredentials.getLocalUserId()));
            } catch (FitbitAPIException e) {
                e.printStackTrace();
            }
        }
        try {
            return apiClientService.getClient().getUserInfo(new LocalUserDetail(resourceCredentials.getLocalUserId()));
        } catch (FitbitAPIException e) {
            e.printStackTrace();
        }
        
        return null;
	}

}
