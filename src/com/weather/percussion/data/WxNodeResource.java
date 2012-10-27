package com.weather.percussion.data;

import org.json.simple.JSONObject;

public class WxNodeResource {
    private String resourceId;
    private String resourceTitle;
    private String providerCode;
    private String platform;
    
    private String KEY_DELIMITER = "/";
    
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    
    public String getResourceTitle() {
        return resourceTitle;
    }
    
    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }
    
    public String getProviderCode() {
        return providerCode;
    }
    
    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getKey() {
        return new StringBuilder(this.platform).append(KEY_DELIMITER).append(this.resourceId).toString();
    }
    
    public void setKey(String key) {
        String[] parts = key.split(KEY_DELIMITER);
        if (parts.length == 2) {
            this.platform = parts[0];
            this.resourceId = parts[1];
        }
    }
    
    public String toJSON() {
        //("json", "{\"title\":\"This is a test title\",\"providercode\":\"This is a test code\",\"property\":\"Desktop\"}");
//        StringBuilder retVal = new StringBuilder("{\"providercode\":\"");
//        retVal.append(this.providerCode == null ? "" : this.providerCode);
//        retVal.append("\",\"platform\":\"");
//        retVal.append(this.platform == null ? "" : this.platform);
//        retVal.append("\"}");
//        return retVal.toString();
        //TODO: constants for keys
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("providercode", this.providerCode);
        jsonObj.put("platform", this.platform);
        return jsonObj.toString();
    }
    
    

}
