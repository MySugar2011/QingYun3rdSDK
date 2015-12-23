package com.emacle.qingyunsdk.common.parser;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.emacle.qingyunsdk.common.comm.ResponseMessage;
import com.emacle.qingyunsdk.common.utils.IOUtils;
import com.emacle.qingyunsdk.internal.model.OSSErrorResult;

public class JsonResponseParseer implements ResponseParser<Object> {
	
    // It allows to specify the class type, if the class type is specified,
    // the contextPath will be ignored.
    private Class<?> modelClass;
    
    private static final ObjectMapper objectMapper =  new ObjectMapper();
    private static JsonGenerator jsonGenerator = null;
    
    static{
    	try {
			jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
    public JsonResponseParseer(Class<?> modelClass)  {
        assert (modelClass != null);
        this.modelClass = modelClass;
    }

	@Override
	public Object parse(ResponseMessage response) throws ResponseParseException {
		 assert (response!= null && response.getContent() != null);
		 
	     return getObject(response);
	}

	// 没时间暂时只对OSSErrorResult支持
	private Object getObject(ResponseMessage response) {
		OSSErrorResult oer = new OSSErrorResult();
		oer.Code = response.getStatusCode()+"";
		oer.Header = response.getHeaders()+"";
		oer.HostId = response.getUri();
		oer.Message = response.getErrorResponseAsString();
		oer.Method = response.getRequest().getMethod()+"";
		oer.RequestId = response.getRequestId();
		oer.ResourceType = "";
		try {
			String s = IOUtils.readStreamAsString(response.getContent(), "utf-8");
			System.out.println(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return oer;
	}

}
