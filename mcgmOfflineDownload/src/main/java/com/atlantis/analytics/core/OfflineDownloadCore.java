package com.atlantis.analytics.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTP;
//import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.atlantis.analytics.dataimport.DatabaseConnection;
import com.atlantis.analytics.util.PropertiesLoader;
import com.opencsv.CSVWriter;

public class OfflineDownloadCore {

	private static PropertiesLoader propLoader = new PropertiesLoader();	
	private static final Logger LOGGER = Logger.getLogger(OfflineDownloadCore.class);
	private static final String ERROR_LOG_PREFIX = "Reason for Failed: ";
	private static final String SUCCESS_KEY = "success";
	
	/**
     * 
     * @param reportName
     * @param tableName
     * @param fromDate
     * @param toDate
     * @param emails
     * @return
     */
    public String generateFile(String reportName, String tableName, String fromDate, String toDate, String[] emails) {
    	LOGGER.info("In generateFile method......"+tableName);
    	String executeQuery = "",filePath="",fileName;
    	ResultSet results = null;
        Statement st = null;
        String reportstatus = SUCCESS_KEY;
    	try {
    		
    	DatabaseConnection dbconnection = new DatabaseConnection(); 
    	Connection connection = dbconnection.getConnection(propLoader.getPostgreDBName());
    	executeQuery = propLoader.getQuery(reportName);
    	if(executeQuery.contains("fromdate")) {
    		executeQuery = executeQuery.replaceAll( "fromdate", " to_date('"+fromDate+"','YYYYMMDD') ");
    	}
    	if(executeQuery.contains("todate")) {
    		executeQuery = executeQuery.replaceAll("todate"," to_date('"+toDate+"','YYYYMMDD') ");
    	}
    	LOGGER.info("query:" + executeQuery);
        st = connection.createStatement();
        results =  st.executeQuery(executeQuery);
        fileName = reportName.toUpperCase()+"_"+fromDate+"_"+toDate;
        filePath = "D:/"+fileName+".csv";
        File file = new File(filePath);
    	CSVWriter csvWriter = new CSVWriter(new FileWriter(file), ',');
    	csvWriter.writeAll(results, true);
        csvWriter.close();
        
          String server = propLoader.getFTPServer();
		  int port = Integer.valueOf(propLoader.getFTPPort());
		  String user = propLoader.getFTPuserName();
		  String pass = propLoader.getFTPpassword();
		  
		  FTPClient ftpClient = new FTPClient();
		  ftpClient.connect(server, port);
		  ftpClient.login(user, pass);
		  ftpClient.enterLocalPassiveMode();
		  ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		  
		  // uploads file using an InputStream
		  File firstLocalFile = new File(filePath);
		  
		  String firstRemoteFile = filePath; 
		  InputStream inputStream = new FileInputStream(firstLocalFile);
		  
		  System.out.println("Start uploading file");
		  boolean done = ftpClient.storeFile(firstRemoteFile, inputStream); inputStream.close(); 
		  if(done) {
			  System.out.println("The file is uploaded successfully."); 
			  }
		  reportstatus = sendReportMail(filePath,fileName,emails);
		  file.delete();
		}
    	catch(Exception e) {
    		LOGGER.error(ERROR_LOG_PREFIX, e);
            LOGGER.error(ExceptionUtils.getStackTrace(e));
            reportstatus = "fail";
        }
    	return reportstatus;
    }
    
     public String sendReportMail(String filePath, String fileName,String[] emails) {
    	 String reportstatus =  SUCCESS_KEY;
    	 LOGGER.info("In sendReportMail method....");
    	 try {
    		  Properties properties = System.getProperties();  
		      properties.setProperty("mail.smtp.host", propLoader.getMailServerHost()); 
		      properties.setProperty("mail.smtp.port", propLoader.getMailServerPort());
		      properties.setProperty("mail.smtp.username", propLoader.getMailServerUserName());
		      properties.setProperty("mail.smtp.password", propLoader.getMailServerPassword());
		      properties.put("mail.smtp.starttls.enable", propLoader.getMailServerUseTLS());
		      properties.put("mail.smtp.auth", "true");
		      if(propLoader.getMailServerUseSSL().equals("true")) {
		      properties.put("mail.smtp.socketFactory.port", propLoader.getMailServerProtocols());
		      properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		      properties.put("mail.smtp.socketFactory.fallback", "false");
		      }
		      
		      Session session = Session.getDefaultInstance(properties,  
		    		    new javax.mail.Authenticator() {
		    		       protected PasswordAuthentication getPasswordAuthentication() {  
		    		       return new PasswordAuthentication(propLoader.getMailServerUserName(),propLoader.getMailServerPassword());  
		    		   }  
		    		   });
		  
		      	 //compose the message  
		         MimeMessage message = new MimeMessage(session);  
		         message.setFrom(new InternetAddress(propLoader.getMailFrom())); 
		         for(String mailid: emails) {
		        	 message.addRecipient(Message.RecipientType.TO,new InternetAddress(mailid));   
		         }
		         
		         message.setSubject("MCGM "+fileName+" Report");  
		         
		         Multipart multipart = new MimeMultipart();
		         MimeBodyPart messageBodyPart1 = new MimeBodyPart();
		         messageBodyPart1.setText("Hello, here we are sharing generated report.");
				 message.setContent(multipart);
				 MimeBodyPart messageBodyPart2 = new MimeBodyPart();
				 DataSource source = new FileDataSource(filePath);
				 messageBodyPart2.setDataHandler(new DataHandler(source));
				 messageBodyPart2.setFileName(fileName);
				 multipart.addBodyPart(messageBodyPart1);
				 multipart.addBodyPart(messageBodyPart2);
				 
		         // Send message  
		         Transport.send(message);  
		 }
    	 catch(Exception e) {
    		 LOGGER.error(ERROR_LOG_PREFIX, e);
             LOGGER.error(ExceptionUtils.getStackTrace(e));
             reportstatus = "fail"; 
    	 }
    	 
    	return reportstatus;
    }
}
