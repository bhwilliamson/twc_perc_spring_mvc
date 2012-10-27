package com.weather.percussion.springmvc;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
        model.put("json", dummyData.toString());
        return new ModelAndView("getautocompletesearch", "model", model);        
    }
    
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        log.debug("LFCOpenContentWellController.create()");
        WxNodeResource wxNodeResource = loadResourceFromRequest(request);
                
        HashMap<String, String> model = new HashMap<String, String>();
        if (!isResourceIdUnique(wxNodeResource.getResourceId())) {
            model.put("json", "{\"error\":\"Resource must be unique\"}");
        }
        else {
            LFCOpenContentWellDAO dao = new LFCOpenContentWellDAO();
            dao.createResource(wxNodeResource);
            //TODO: Need to catch exception and return msg to ui
            model.put("json", "{\"msg\":\"Resource successfully created in document store\"}");            
            
        }
        return new ModelAndView("getautocompletesearch", "model", model);
        
    }
    
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response)
        throws Exception {        
        log.debug("LFCOpenContentWellController.update()");
        WxNodeResource wxNodeResource = loadResourceFromRequest(request);
    
        LFCOpenContentWellDAO dao = new LFCOpenContentWellDAO();
        dao.updateResource(wxNodeResource);
        HashMap<String, String> model = new HashMap<String, String>();
        //TODO: wrap dao call in try/catch and return appropriate results, not success every time
        model.put("json", "{\"msg\":\"Resource successfully updated in document store\"}");        
        return new ModelAndView("getautocompletesearch", "model", model);
    
    }  
    
    public ModelAndView get(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        log.debug("LFCOpenContentWellController.get()");
        log.debug("Resource: " + request.getParameter(RESOURCE_REQUEST_PARAM)); 
        WxNodeResource wxNodeResource = loadResourceFromRequest(request);
        LFCOpenContentWellDAO dao = new LFCOpenContentWellDAO();
        WxNodeResource fetchedResource = dao.getResourceByKey(wxNodeResource.getKey());
        HashMap<String, String> model = new HashMap<String, String>();
        if (fetchedResource != null) {
            model.put("json", fetchedResource.toJSON());   
        }
        else {
            model.put("json", "");
        }
        return new ModelAndView("getautocompletesearch", "model", model);

    }     
    
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        log.debug("LFCOpenContentWellController.delete()");
        log.debug("Resource: " + request.getParameter(RESOURCE_REQUEST_PARAM));        
        WxNodeResource wxNodeResource = loadResourceFromRequest(request);
        LFCOpenContentWellDAO dao = new LFCOpenContentWellDAO();
        dao.deleteResource(wxNodeResource.getKey());
        HashMap<String, String> model = new HashMap<String, String>();
        //TODO: Handle exceptions from dao and return appropriate msg
        model.put("json", "{\"msg\":\"Resource successfully deleted from document store\"}");        
        return new ModelAndView("getautocompletesearch", "model", model);

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
    
    private static final String TITLE_REQUEST_PARAM = "title";
    private static final String PLATFORM_REQUEST_PARAM = "platform";
    private static final String PROVIDER_CODE_REQUEST_PARAM = "providercode";
    private static final String RESOURCE_REQUEST_PARAM = "resource";
    private static final String AUTOCOMPLETE_TERM_PARAM = "term";

}
