package com.weather.percussion.springmvc;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.weather.percussion.data.WxNodeResource;
import com.weather.percussion.db.LFCOpenContentWellDAO;

public class LFCOpenContentWellController extends MultiActionController {
    
    private static final Log log = LogFactory.getLog(LFCOpenContentWellController.class);
    
    /**
     * getLastModified was throwing exceptions from super, couldn't figure out 
     * why and it didn't cause any problems except filling up the logs since we aren't
     * using the <method>LastModified() handlers
     */
    public long getLastModified(HttpServletRequest request) {
        return -1L;
    }
    
    public ModelAndView resourceAutocomplete(HttpServletRequest request, HttpServletResponse response) {
        String inputTerm = request.getParameter(AUTOCOMPLETE_TERM_PARAM);
        log.debug("LFCOpenContentWellController.resourceAutocomplete() with term: " + inputTerm);
        
        //TODO: Query for resource starting with this resource id
        
        //TODO: Remove after db hooked up
        StringBuilder dummyData = new StringBuilder("[{\"id\":\"foot\",\"label\":\"foot\",\"value\":\"foot\",\"title\":\"foot\"},");
        dummyData.append("{\"id\":\"fool\",\"label\":\"fool\",\"value\":\"fool\",\"title\":\"fool\"},");
        dummyData.append("{\"id\":\"football\",\"label\":\"football\",\"value\":\"football\",\"title\":\"football\"},");
        dummyData.append("{\"id\":\"jets\",\"label\":\"jets\",\"value\":\"jets\",\"title\":\"jets\"},");
        dummyData.append("{\"id\":\"texans\",\"label\":\"texans\",\"value\":\"texans\",\"title\":\"texans\"},");
        dummyData.append("{\"id\":\"texas\",\"label\":\"texas\",\"value\":\"texas\",\"title\":\"texas\"}]");
        
        HashMap<String, String> model = new HashMap<String, String>();
        model.put(MODEL_JSON_KEY, dummyData.toString());
        return new ModelAndView("getautocompletesearch", MODEL_KEY, model);        
    }
    
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        log.debug("LFCOpenContentWellController.create()");
        WxNodeResource wxNodeResource = loadResourceFromRequest(request);
                
        HashMap<String, String> model = new HashMap<String, String>();
        JSONObject jsonObj = new JSONObject();                                    
        try {
            LFCOpenContentWellDAO dao = new LFCOpenContentWellDAO();
            if (!resourceExists(dao, wxNodeResource)) {
                dao.createResource(wxNodeResource);                
                jsonObj.put(JSON_MSG_KEY, "Resource successfully created in document store.");  
            }
            else {
                jsonObj.put(JSON_ERROR_KEY, "A resource with this Resource Id and Platform already exists.");
            }
        }
        catch(Exception e) {
            log.error("Error creating wxnode resource", e);
            jsonObj.put(JSON_ERROR_KEY, "There was an unknown error creating the resource, please consult the server logs for details.");
        }                        
        model.put(MODEL_JSON_KEY, jsonObj.toString());
        return new ModelAndView(MODEL_AND_VIEW_NAME, MODEL_KEY, model);
        
    }
    
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response)
        throws Exception {        
        log.debug("LFCOpenContentWellController.update()");
        WxNodeResource wxNodeResource = loadResourceFromRequest(request);
    
        HashMap<String, String> model = new HashMap<String, String>();
        JSONObject jsonObj = new JSONObject();
        try {
            LFCOpenContentWellDAO dao = new LFCOpenContentWellDAO();
            if (resourceExists(dao, wxNodeResource)) {
                dao.updateResource(wxNodeResource);
                jsonObj.put(JSON_MSG_KEY, "Resource successfully updated in document store.");  
            }
            else {
                jsonObj.put(JSON_ERROR_KEY, "No resource could be found for the given Resource Id and Platform in the document store.");
            }
        }
        catch(Exception e) {
            log.error("Error updating wxnode resource", e);
            jsonObj.put(JSON_ERROR_KEY, "There was an unknown error updating the resource, please consult the server logs for details.");
        }
        model.put(MODEL_JSON_KEY, jsonObj.toString());            
        return new ModelAndView(MODEL_AND_VIEW_NAME, MODEL_KEY, model);    
    }  
    
    public ModelAndView get(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        log.debug("LFCOpenContentWellController.get()");
        log.debug("Resource: " + request.getParameter(RESOURCE_REQUEST_PARAM)); 
        WxNodeResource wxNodeResource = loadResourceFromRequest(request);
        
        HashMap<String, String> model = new HashMap<String, String>();
        JSONObject jsonObj = new JSONObject();
        try {
            LFCOpenContentWellDAO dao = new LFCOpenContentWellDAO();
            WxNodeResource fetchedResource = dao.getResourceByKey(wxNodeResource.getKey());
            if (fetchedResource != null) {
                //model.put(MODEL_JSON_KEY, fetchedResource.toJSON());
                jsonObj = fetchedResource.getJSONObject();
                jsonObj.put(JSON_MSG_KEY, "Successfully loaded resource from document store.");
            }
            else {
                jsonObj.put(JSON_ERROR_KEY, "No resource could be found for the given Resource Id and Platform in the document store.");
            }
        }
        catch(Exception e) {
            log.error("Error getting wxnode resource", e);            
            jsonObj.put(JSON_ERROR_KEY, "There was an unknown error getting the resource, please consult the server logs for details.");
            
        }
        model.put(MODEL_JSON_KEY, jsonObj.toString());         
        return new ModelAndView(MODEL_AND_VIEW_NAME, MODEL_KEY, model);

    }     
    
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        log.debug("LFCOpenContentWellController.delete()");
        log.debug("Resource: " + request.getParameter(RESOURCE_REQUEST_PARAM));        
        WxNodeResource wxNodeResource = loadResourceFromRequest(request);
        
        HashMap<String, String> model = new HashMap<String, String>();
        JSONObject jsonObj = new JSONObject();        
        try {
            LFCOpenContentWellDAO dao = new LFCOpenContentWellDAO();
            if (resourceExists(dao, wxNodeResource)) {
                dao.deleteResource(wxNodeResource.getKey());
                jsonObj.put(JSON_MSG_KEY, "Resource successfully deleted from document store");                   
            }
            else {
                jsonObj.put(JSON_ERROR_KEY, "No resource could be found for the given Resource Id and Platform in the document store.");
            }
        
        }
        catch(Exception e) {
            log.error("Error deleting wxnode resource", e);
            jsonObj.put(JSON_ERROR_KEY, "There was an unknown error deleting the resource, please consult the server logs for details.");
        }
        model.put(MODEL_JSON_KEY, jsonObj.toString());                            
        return new ModelAndView(MODEL_AND_VIEW_NAME, MODEL_KEY, model);

    } 
    
    private boolean isResourceIdUnique(String resourceId) {
        boolean isUnique = true;
        
        //TODO: Query db for resource with this id
        
        return isUnique;
    }
    
    private WxNodeResource loadResourceFromRequest(HttpServletRequest request) {
        WxNodeResource wxNodeResource = new WxNodeResource();
        wxNodeResource.setResourceTitle(request.getParameter(TITLE_REQUEST_PARAM));
        log.debug(new StringBuilder("Resource Title: ").append(wxNodeResource.getResourceTitle()).toString());
        wxNodeResource.setPlatform(request.getParameter(PLATFORM_REQUEST_PARAM));
        log.debug(new StringBuilder("Platform: ").append(wxNodeResource.getPlatform()).toString());
        wxNodeResource.setProviderCode(request.getParameter(PROVIDER_CODE_REQUEST_PARAM));
        log.debug(new StringBuilder("Provider Code: ").append(wxNodeResource.getProviderCode()).toString());
        wxNodeResource.setResourceId(request.getParameter(RESOURCE_REQUEST_PARAM));
        log.debug(new StringBuilder("Resource Id: ").append(wxNodeResource.getResourceId()).toString());
        return wxNodeResource;
    }
    
    private boolean resourceExists(LFCOpenContentWellDAO dao, WxNodeResource resourceFromRequest) throws Exception {
        WxNodeResource fetchedResource = dao.getResourceByKey(resourceFromRequest.getKey());
        if (fetchedResource != null) {
            return true;
        }
        else {
            return false;
        }
    }
    
    private static final String TITLE_REQUEST_PARAM = "title";
    private static final String PLATFORM_REQUEST_PARAM = "platform";
    private static final String PROVIDER_CODE_REQUEST_PARAM = "providercode";
    private static final String RESOURCE_REQUEST_PARAM = "resource";
    private static final String AUTOCOMPLETE_TERM_PARAM = "term";
    
    private static final String JSON_MSG_KEY = "msg";
    private static final String JSON_ERROR_KEY = "error";
    private static final String MODEL_JSON_KEY = "json";
    private static final String MODEL_KEY = "model";
    //Note, this model and view name comes from taxonomy.  There is a view that just displays model.json, which is what we need.
    private static final String MODEL_AND_VIEW_NAME = "getautocompletesearch";

}
