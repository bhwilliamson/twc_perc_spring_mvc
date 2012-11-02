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
    
    private static final String DATASOURCE_NAME = "RhythmyxData";
    private static final String CMS_KEYSTORE_TABLE = "cms_keystore";
    private static final String CMS_KEY_FIELD = "cms_key";
    private static final String CONTENT_FIELD = "content";
    private static final String CONTENT_TYPE_FIELD = "content_type";
    private static final String LAST_MODIFIED_FIELD = "last_modified";
    
    private static final String CONTENT_TYPE_DEFAULT = "text/plain";
    
    private static final Log log = LogFactory.getLog(LFCOpenContentWellDAO.class);
    
    public List searchForResourceIds(WxNodeResource wxNodeResource) throws Exception {
        List<String> matchingResourceIds = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet results = null;        
        try {            
            IPSConnectionInfo connectionInfo = new PSConnectionInfo(DATASOURCE_NAME);
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
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            
            IPSConnectionInfo connectionInfo = new PSConnectionInfo(DATASOURCE_NAME);
            conn = PSConnectionHelper.getDbConnection(connectionInfo);
            stmt = conn.prepareStatement("insert into cms_keystore (cms_key, content, content_type, last_modified) values (?, ?, ?, ?)");
            stmt.setString(1, wxNodeResource.getKey());            
            stmt.setString(2, wxNodeResource.getProviderCode());
            stmt.setString(3, CONTENT_TYPE_DEFAULT);
            stmt.setTimestamp(4, getCurrentSqlTimestamp());
            stmt.executeUpdate();
        }
        finally {
            cleanup(stmt, null, conn);
        }             
    }
    
    public void updateResource(WxNodeResource wxNodeResource) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            
            IPSConnectionInfo connectionInfo = new PSConnectionInfo(DATASOURCE_NAME);
            conn = PSConnectionHelper.getDbConnection(connectionInfo);
            stmt = conn.prepareStatement("update cms_keystore set content=?, last_modified=? where cms_key = ?");
            stmt.setString(1, wxNodeResource.getProviderCode());
            stmt.setTimestamp(2, getCurrentSqlTimestamp());
            stmt.setString(3, wxNodeResource.getKey());
            stmt.executeUpdate();
        }
        finally {
            cleanup(stmt, null, conn);
        }               
    }    
    
    public WxNodeResource getResourceByKey(String key) throws Exception {
        WxNodeResource wxNodeResource = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet results = null;        
        try {            
            IPSConnectionInfo connectionInfo = new PSConnectionInfo(DATASOURCE_NAME);
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
    
    public void deleteResource(String key) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            
            IPSConnectionInfo connectionInfo = new PSConnectionInfo(DATASOURCE_NAME);
            conn = PSConnectionHelper.getDbConnection(connectionInfo);
            stmt = conn.prepareStatement("delete from cms_keystore where cms_key = ?");
            stmt.setString(1, key);
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
