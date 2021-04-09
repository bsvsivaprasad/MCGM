package com.atlantis.analytics.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.atlantis.analytics.core.OfflineDownloadCore;


@Path("/")
public class AnalyticsRestService {

    private static final Logger LOGGER = Logger.getLogger(AnalyticsRestService.class);

    private static final String FAIL_RESPONSE = "{\"message\": \"fail\"}";
    private static final String ALLOW_ORIGIN_KEY = "Access-Control-Allow-Origin";
    private static final String ALLOW_ORIGIN_VALUE = "*";
    private static final String ALLOW_HEADERS_KEY = "Access-Control-Allow-Headers";
    private static final String ALLOW_HEADERS_VALUE = "Origin, Content-Type, X-Auth-Token";
    private static final String ALLOW_METHODS_KEY = "Access-Control-Allow-Methods";
    private static final String ALLOW_METHODS_VALUE = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    private static final String ERROR_KEY = "error";
    private static final String NO_DATA = "noData";
    private static final String PASS_KEY = "password";
    private static final String USER_ID_KEY = "userid";
    private static final String JOIN_ID_KEY = "joinid";
    private static final String USERNAME_KEY = "username";
    private static final String SUCCESS_KEY = "success";
    private static final String PROJECT_ID_KEY = "projectid";
    private static final String PROJECT_NAME_KEY = "projectname";
    private static final String DATASOURCE_ID_KEY = "datasourceid";
    private static final String GRAPH_DESC_KEY = "graphdescription";
    private static final String DATA_RESPONSE_PREFIX_KEY = "{\"data\":\"";
    private static final String ERROR_LOG_PREFIX = "Reason for Failed:  ";
    private static final String UNAUTH_RESPONSE = "Unauthorized";
    private static final String BAD_REQUEST_MESSAGE = "Bad Request";
    private static final String SERVER_ERROR_MESSAGE = "Something went wrong!";
    private static final int OK_STATUS = Response.Status.OK.getStatusCode();
    private static final int NO_CONTENT = Response.Status.NO_CONTENT.getStatusCode();
    private static final int BAD_REQUEST_STATUS = Response.Status.BAD_REQUEST.getStatusCode();
    private static final int UNAUTHORIZED_STATUS = Response.Status.UNAUTHORIZED.getStatusCode();
    private static final int INTERNAL_SERVER_ERROR_STATUS = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

    @GET
    @Path("/info")
    public Response verifyRunning() {
        return Response.status(Response.Status.OK).entity("Up, up and away!!!")
                .header(ALLOW_ORIGIN_KEY, ALLOW_ORIGIN_VALUE).build();
    }

    /**
     * 
     * @param incomingData
     * @return
     */
    @POST
    @Path("/generatereport")
    public Response generateReport(InputStream incomingData) {
        LOGGER.info("In createdatasource() Method");
        StringBuilder sb = new StringBuilder("");
        Response.Status status = Response.Status.UNAUTHORIZED;
        String response = "";
        String responseJSON = "";
        String reportName="",tableName="",fromDate="",toDate="";
        String emails[];
        StringBuilder jsonData = new StringBuilder("");
        
        try {
        	OfflineDownloadCore odc = new OfflineDownloadCore();
        	BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
    		String line = null;
            while ((line = in.readLine()) != null) {
                jsonData.append(line).append("\n");
            }
	        JSONObject obj = new JSONObject(jsonData.toString());
	        reportName = obj.getString("reportname");
	        tableName = obj.getString("tablename");
	        if(!obj.isNull("fromdate")) {
	        fromDate = obj.getString("fromdate");
	        }
	        if(!obj.isNull("todate")) {
	        toDate = obj.getString("todate");
	        }
	        
	        emails = obj.getString("emails").split(";");
	        response = odc.generateFile(reportName,tableName,fromDate,toDate,emails);
	        if(SUCCESS_KEY.equals(response)) {
	        	 status = Response.Status.OK;
	             responseJSON = "{\"data\":\"Report generated\"}";	
	        }else {
	        	return sendFailedResponse(BAD_REQUEST_STATUS, BAD_REQUEST_MESSAGE);	
	        }
           
        } catch (Exception e) {
            LOGGER.error(ERROR_LOG_PREFIX, e);
            status = Response.Status.BAD_REQUEST;
            return sendFailedResponse(INTERNAL_SERVER_ERROR_STATUS, SERVER_ERROR_MESSAGE);
        }
        return Response.status(status).entity(responseJSON).header(ALLOW_ORIGIN_KEY, ALLOW_ORIGIN_VALUE).build();
    }

   

    public Response sendFailedResponse(int code, String message) {
        String failMessage = message;
        if (message == null || message.isEmpty()) {
            failMessage = SERVER_ERROR_MESSAGE;
        }

        String failResponse;
        try {
            failResponse = new JSONObject().put(ERROR_KEY, code).put("message", failMessage).toString();
            return Response.status(code).entity(failResponse).header(ALLOW_ORIGIN_KEY, ALLOW_ORIGIN_VALUE).build();
        } catch (JSONException jsone) {
            LOGGER.error("Error creating failed response: ");
            LOGGER.error(ExceptionUtils.getStackTrace(jsone));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header(ALLOW_ORIGIN_KEY, ALLOW_ORIGIN_VALUE)
                    .build();
        }
    }

    public Object getKey(JSONObject jsonObject, String key) {
        Object value = null;
        try {
            value = jsonObject.get(key);
        } catch (JSONException jsone) {
            LOGGER.error("This is just a check. Nothing to worry about. JSON key missing: ", jsone);
        }

        return value;
    }
}