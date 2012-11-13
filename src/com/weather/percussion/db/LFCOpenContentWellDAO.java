package com.weather.percussion.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.percussion.utils.jdbc.IPSConnectionInfo;
import com.percussion.utils.jdbc.PSConnectionHelper;
import com.percussion.utils.jdbc.PSConnectionInfo;
import com.weather.percussion.data.WxNodeResource;

public class LFCOpenContentWellDAO {
    
    //private static final String DATASOURCE_NAME = "RhythmyxData"; //Get from spring config
    private static final String CMS_KEY_FIELD = "cms_key";
    private static final String CONTENT_FIELD = "content";
    
    private static final String CONTENT_TYPE_DEFAULT = "text/plain";
    
    private static final Log log = LogFactory.getLog(LFCOpenContentWellDAO.class);
    private String readConnection = null;
    private String writeConnection = null;
    private String statusValue = null;
    private String actionCreateValue = null;
    private String actionUpdateValue = null;
    private String actionDeleteValue = null;
    
    public List searchForResourceIds(WxNodeResource wxNodeResource) throws Exception {
        List<String> matchingResourceIds = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet results = null;        
        try {            
            IPSConnectionInfo connectionInfo = new PSConnectionInfo(readConnection);
            conn = PSConnectionHelper.getDbConnection(connectionInfo);
            stmt = conn.prepareStatement("select cms_key from cms_keystore where cms_key like ?");
            stmt.setString(1, new StringBuilder(wxNodeResource.getKey()).append("%").toString());
            results = stmt.executeQuery();
            while (results.next()) {
                WxNodeResource resource = new WxNodeResource();
                resource.setKey(results.getString(CMS_KEY_FIELD));                                
                matchingResourceIds.add(resource.getResourceId());
            }
        }
        finally {
            cleanup(stmt, results, conn);
        }        
        return matchingResourceIds;        
    }
    
    public void createResource(WxNodeResource wxNodeResource) throws Exception {
        createTempResourceRecord(wxNodeResource, actionCreateValue);
    }
    
    public void updateResource(WxNodeResource wxNodeResource) throws Exception {
        createTempResourceRecord(wxNodeResource, actionUpdateValue);
    }    
    
    public WxNodeResource getResourceByKey(String key) throws Exception {
        WxNodeResource wxNodeResource = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet results = null;        
        try {            
            IPSConnectionInfo connectionInfo = new PSConnectionInfo(readConnection);
            conn = PSConnectionHelper.getDbConnection(connectionInfo);
            stmt = conn.prepareStatement("select cms_key, content from cms_keystore where cms_key = ?");
            stmt.setString(1, key);
            results = stmt.executeQuery();
            while (results.next()) {
                wxNodeResource = getResourceFromResultSet(results);                
                break;
            }
        }
        finally {
            cleanup(stmt, results, conn);
        }        
        return wxNodeResource;
    }
    
    public void deleteResource(WxNodeResource wxNodeResource) throws Exception {
        createTempResourceRecord(wxNodeResource, actionDeleteValue);
    }    
    
    public String getReadConnection() {
        return readConnection;
    }

    public void setReadConnection(String readConnection) {
        this.readConnection = readConnection;
    }

    public String getWriteConnection() {
        return writeConnection;
    }

    public void setWriteConnection(String writeConnection) {
        this.writeConnection = writeConnection;
    }        

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public String getActionCreateValue() {
        return actionCreateValue;
    }

    public void setActionCreateValue(String actionCreateValue) {
        this.actionCreateValue = actionCreateValue;
    }

    public String getActionUpdateValue() {
        return actionUpdateValue;
    }

    public void setActionUpdateValue(String actionUpdateValue) {
        this.actionUpdateValue = actionUpdateValue;
    }

    public String getActionDeleteValue() {
        return actionDeleteValue;
    }

    public void setActionDeleteValue(String actionDeleteValue) {
        this.actionDeleteValue = actionDeleteValue;
    }
    
    private void createTempResourceRecord(WxNodeResource wxNodeResource, String action) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            
            IPSConnectionInfo connectionInfo = new PSConnectionInfo(writeConnection);
            conn = PSConnectionHelper.getDbConnection(connectionInfo);
            stmt = conn.prepareStatement("insert into cms_keystore (cms_key, content, content_type, last_modified, status, action) values (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, wxNodeResource.getKey());            
            stmt.setString(2, wxNodeResource.getProviderCode());
            stmt.setString(3, CONTENT_TYPE_DEFAULT);
            stmt.setTimestamp(4, getCurrentSqlTimestamp());
            stmt.setString(5, statusValue);
            stmt.setString(6, action);            
            stmt.executeUpdate();
        }
        finally {
            cleanup(stmt, null, conn);
        }             
    }    

    private void cleanup(PreparedStatement stmt, ResultSet results, Connection conn) {
        if (results != null) {
            try {
                results.close();  
            }
            catch(SQLException sqe) {
                log.error("Error closing ResultSet", sqe);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch(SQLException sqe2) {
                log.error("Error closing PreparedStatement", sqe2);
                sqe2.printStackTrace();                    
            }
        }
        if (conn != null) {
            try {
                conn.close();
            }
            catch(Exception ex) {
                log.error("Error closing connection", ex);
                
            }
        }        
    }
    
    private WxNodeResource getResourceFromResultSet(ResultSet result) throws Exception {
        WxNodeResource resource = new WxNodeResource();
        resource.setKey(result.getString(CMS_KEY_FIELD));
        resource.setProviderCode(result.getString(CONTENT_FIELD));
        return resource;
    }   
    
    private java.sql.Date getCurrentSqlDate() {
        Calendar cal = Calendar.getInstance();
        return new java.sql.Date((cal.getTime()).getTime());
    }
    
    private java.sql.Timestamp getCurrentSqlTimestamp() {
        Calendar cal = Calendar.getInstance();
        return new java.sql.Timestamp(cal.getTimeInMillis());
    }

}
