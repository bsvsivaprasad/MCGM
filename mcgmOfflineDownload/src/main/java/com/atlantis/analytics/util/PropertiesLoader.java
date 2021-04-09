package com.atlantis.analytics.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlantis.analytics.exception.MissingConfigException;

public class PropertiesLoader {

    
    private static final String ENV_POSTGRE_HOST = "POSTGRE_HOST";
    private static final String ENV_POSTGRE_PORT = "POSTGRE_PORT";
    private static final String ENV_POSTGRE_USERNAME = "POSTGRE_USERNAME";
    private static final String ENV_POSTGRE_PASS = "POSTGRE_PASS";
    private static final String ENV_POSTGRE_DBNAME = "POSTGRE_DBNAME";
    
   private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);
    private static Properties configProp = new Properties();

    public PropertiesLoader() {
        initConfigProperties();
    }

    private static void initConfigProperties() {
        InputStream in = PropertiesLoader.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            configProp.load(in);
            in.close();
        } catch (IOException ioe) {
            LOGGER.error(ExceptionUtils.getStackTrace(ioe));
        }
    }

    public String getPostgreHost() {
        String host = (System.getenv(ENV_POSTGRE_HOST) != null && !System.getenv(ENV_POSTGRE_HOST).isEmpty())
                ? System.getenv(ENV_POSTGRE_HOST) : configProp.getProperty("postgre.host");
        if (host == null || host.isEmpty()) {
            throw new MissingConfigException("Could not find postgre.host property or value.");
        }

        return host;
    }

    public String getPostgrePort() {
        String port = (System.getenv(ENV_POSTGRE_PORT) != null && !System.getenv(ENV_POSTGRE_PORT).isEmpty())
                ? System.getenv(ENV_POSTGRE_PORT) : configProp.getProperty("postgre.port");
        if (port == null || port.isEmpty()) {
            throw new MissingConfigException("Could not find postgre.port property or value.");
        }

        return port;
    }

    public String getPostgreUsername() {
        String username = (System.getenv(ENV_POSTGRE_USERNAME) != null
                && !System.getenv(ENV_POSTGRE_USERNAME).isEmpty()) ? System.getenv(ENV_POSTGRE_USERNAME)
                        : configProp.getProperty("postgre.username");
        if (username == null || username.isEmpty()) {
            throw new MissingConfigException("Could not find postgre.username property or value.");
        }

        return username;
    }

    public String getPostgrePassword() {
        Decryption dec = new Decryption();
        String password = (System.getenv(ENV_POSTGRE_PASS) != null && !System.getenv(ENV_POSTGRE_PASS).isEmpty())
                ? System.getenv(ENV_POSTGRE_PASS) : configProp.getProperty("postgre.password");
        if (password == null || password.isEmpty()) {
            throw new MissingConfigException("Could not find postgre.password property or value.");
        }

        return dec.getPlainPwd(password);
    }

    public String getPostgreDBName() {
        String dbName = (System.getenv(ENV_POSTGRE_DBNAME) != null && !System.getenv(ENV_POSTGRE_DBNAME).isEmpty())
                ? System.getenv(ENV_POSTGRE_DBNAME) : configProp.getProperty("postgre.dbName");
        if (dbName == null || dbName.isEmpty()) {
            throw new MissingConfigException("Could not find postgre.dbName property or value.");
        }

        return dbName;
    }
    
    public String getQuery(String reportName) {
        
    	String query = configProp.getProperty(reportName+".query");	
    	if (query == null || query.isEmpty()) {
            throw new MissingConfigException("Could not find "+reportName+".columns property or value.");
        }
    	return query;
    	
    }
    
    public String getMailServerHost() {
        String mailHost =  configProp.getProperty("mail.server.host");
      return mailHost;
    }
    
    public String getMailServerPort() {
        String mailPort =  configProp.getProperty("mail.server.port");
      return mailPort;
    }
    
    public String getMailServerUseSSL() {
        String mailssl =  configProp.getProperty("mail.server.usessl");
      return mailssl;
    }
    
    public String getMailServerUseTLS() {
        String mailtls =  configProp.getProperty("mail.server.usetls");
      return mailtls;
    }
    
    public String getMailServerUserName() {
        String mailusername =  configProp.getProperty("mail.server.username");
      return mailusername;
    }
    
    public String getMailServerPassword() {
    	 Decryption dec = new Decryption();
        String mailpassword =  configProp.getProperty("mail.server.password");
      return dec.getPlainPwd(mailpassword);
    }
    
    public String getMailServerProtocols() {
        String mailprotocol =  configProp.getProperty("mail.server.protocols");
      return mailprotocol;
    }
   
    public String getMailTo() {
        String mailto =  configProp.getProperty("mail.to");
      return mailto;
    }
    
    public String getMailFrom() {
        String mailfrom =  configProp.getProperty("mail.from");
      return mailfrom;
    }
    
    public String getFTPServer() {
        String ftpserver =  configProp.getProperty("ftp.server");
      return ftpserver;
    }
    
    public String getFTPPort() {
        String ftpport =  configProp.getProperty("ftp.port");
      return ftpport;
    }
    
    public String getFTPuserName() {
        String ftpusername =  configProp.getProperty("ftp.username");
      return ftpusername;
    }
    
    public String getFTPpassword() {
        String ftppassword =  configProp.getProperty("ftp.password");
      return ftppassword;
    }

}