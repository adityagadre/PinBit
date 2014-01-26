package src.main.java.com.fitbit.web;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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



/**
 * Created by IntelliJ IDEA.
 * User: Kiryl
 * Date: 6/22/11
 * Time: 7:05 AM
 */
public class FitbitApiAuthExampleServlet extends HttpServlet {

	private static Logger LOGGER = Logger.getLogger(FitbitApiAuthExampleServlet.class.getName());
    public static final String OAUTH_TOKEN = "oauth_token";
    public static final String OAUTH_VERIFIER = "oauth_verifier";

    private FitbitAPIEntityCache entityCache = new FitbitApiEntityCacheMapImpl();
    private FitbitApiCredentialsCache credentialsCache = new FitbitApiCredentialsCacheMapImpl();
    private FitbitApiSubscriptionStorage subscriptionStore = new FitbitApiSubscriptionStorageInMemoryImpl();

    private String apiBaseUrl;
    private String fitbitSiteBaseUrl;
    private String exampleBaseUrl;
    private String clientConsumerKey;
    private String clientSecret;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            apiBaseUrl = properties.getProperty("apiBaseUrl");
            fitbitSiteBaseUrl = properties.getProperty("fitbitSiteBaseUrl");
            exampleBaseUrl = properties.getProperty("exampleBaseUrl").replace("/app", "");
            clientConsumerKey = properties.getProperty("clientConsumerKey");
            clientSecret = properties.getProperty("clientSecret");
        } catch (IOException e) {
            throw new ServletException("Exception during loading properties", e);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        FitbitAPIClientService<FitbitApiClientAgent> apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
                new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl, credentialsCache),
                clientConsumerKey,
                clientSecret,
                credentialsCache,
                entityCache,
                subscriptionStore
        );
        LOGGER.info("INTO DO GET");
        if (request.getParameter("completeAuthorization") != null) {
            String tempTokenReceived = request.getParameter(OAUTH_TOKEN);
            String tempTokenVerifier = request.getParameter(OAUTH_VERIFIER);
            LOGGER.info("OAUTH_TOKEN = " + tempTokenReceived + " and OAUTH_VERIFIER = " +tempTokenVerifier );
            APIResourceCredentials resourceCredentials = apiClientService.getResourceCredentialsByTempToken(tempTokenReceived);
            if (resourceCredentials == null) {
                throw new ServletException("Unrecognized temporary token when attempting to complete authorization: " + tempTokenReceived);
            }
            // Get token credentials only if necessary:
            if (!resourceCredentials.isAuthorized()) {
                // The verifier is required in the request to get token credentials:
            	LOGGER.info("resource credentials not authorized, now getting token credentials");
                resourceCredentials.setTempTokenVerifier(tempTokenVerifier);
                try {
                    // Get token credentials for user:
                    apiClientService.getTokenCredentials(new LocalUserDetail(resourceCredentials.getLocalUserId()));
                } catch (FitbitAPIException e) {
                    throw new ServletException("Unable to finish authorization with Fitbit.", e);
                }
            }
            try {
                UserInfo userInfo = apiClientService.getClient().getUserInfo(new LocalUserDetail(resourceCredentials.getLocalUserId()));
                FitbitDBUtil.addUserInfo(userInfo,tempTokenReceived,tempTokenVerifier);
                request.setAttribute("userInfo", userInfo);
                request.getRequestDispatcher("/fitbitApiAuthExample.jsp").forward(request, response);
            } catch (FitbitAPIException e) {
                throw new ServletException("Exception during getting user info", e);
            }
        } else {
            try {
            	LOGGER.info("response sendRedirect test");
//            	  String url = apiClientService.getResourceOwnerAuthorizationURL(new LocalUserDetail("rsachinnair@gmail.com"), "");
//            	  LOGGER.info(url);
//            	apiClientService.getRe
                response.sendRedirect(apiClientService.getResourceOwnerAuthorizationURL(new LocalUserDetail("rsachinnair@gmail.com"),exampleBaseUrl+"/PinHealthTest/fitbitApiAuthExample?completeAuthorization="));
                for(String vals: response.getHeaderNames()){
                	LOGGER.info("header is " + vals);
                }
                LOGGER.info("response sendRedirect done");
            } catch (FitbitAPIException e) {
                throw new ServletException("Exception during performing authorization", e);
            }
        }
    }
}
