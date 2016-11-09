package com.dx.jwfm.framework.core.wrapper;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ParameterRequestWrapper extends HttpServletRequestWrapper {  
  
    private Map<String,String> params;  
  
    public ParameterRequestWrapper(HttpServletRequest request, Map<String,String> newParams) {  
        super(request);  
        this.params = newParams;  
    }  
  
    public Map<String,String> getParameterMap() {  
        return params;  
    }  
  
    public Enumeration<String> getParameterNames() {  
        Vector<String> l = new Vector<String>(params.keySet());  
        return l.elements();  
    }  
  
    public String[] getParameterValues(String name) {  
        Object v = params.get(name);  
        if (v == null) {  
            return null;  
        } else if (v instanceof String[]) {  
            return (String[]) v;  
        } else if (v instanceof String) {  
            return new String[] { (String) v };  
        } else {  
            return new String[] { v.toString() };  
        }  
    }  
  
    public String getParameter(String name) {  
        Object v = params.get(name);  
        if (v == null) {  
            return null;  
        } else if (v instanceof String[]) {  
            String[] strArr = (String[]) v;  
            if (strArr.length > 0) {  
                return strArr[0];  
            } else {  
                return null;  
            }  
        } else if (v instanceof String) {  
            return (String) v;  
        } else {  
            return v.toString();  
        }  
    }  
}  