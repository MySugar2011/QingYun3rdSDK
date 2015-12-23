package com.emacle.qingyunsdk.internal.operation;

import static com.emacle.qingyunsdk.internal.OSSUtils.genUrl;
import static com.emacle.qingyunsdk.common.utils.CodingUtils.assertParameterNotNull;
import static com.emacle.qingyunsdk.internal.OSSUtils.ensureBucketNameValid;
import static com.emacle.qingyunsdk.internal.OSSUtils.ensureObjectKeyValid;
import static com.emacle.qingyunsdk.internal.OSSUtils.joinETags;
import static com.emacle.qingyunsdk.internal.OSSUtils.populateResponseHeaderParameters;
import static com.emacle.qingyunsdk.internal.ResponseParsers.putObjectReponseParser;
import static com.emacle.qingyunsdk.internal.OSSUtils.addDateHeader;
import static com.emacle.qingyunsdk.internal.OSSUtils.addStringListHeader;
import com.emacle.qingyunsdk.internal.ResponseParsers.GetObjectResponseParser;


import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import com.emacle.qingyunsdk.common.auth.CredentialsProvider;
import com.emacle.qingyunsdk.common.comm.HttpMethod;
import com.emacle.qingyunsdk.common.comm.RequestMessage;
import com.emacle.qingyunsdk.common.comm.ServiceClient;
import com.emacle.qingyunsdk.common.utils.DateUtil;
import com.emacle.qingyunsdk.common.utils.RangeSpec;
import com.emacle.qingyunsdk.exception.ClientException;
import com.emacle.qingyunsdk.exception.OSSException;
import com.emacle.qingyunsdk.internal.OSSHeaders;
import com.emacle.qingyunsdk.internal.OSSOperation;
import com.emacle.qingyunsdk.internal.OSSRequestMessageBuilder;
import com.emacle.qingyunsdk.model.HeadObjectRequest;
import com.emacle.qingyunsdk.model.OSSObject;
import com.emacle.qingyunsdk.model.PutObjectResult;
import com.emacle.qingyunsdk.model.request.GetObjectRequest;

public class OSSObjectOperation extends OSSOperation{

	public OSSObjectOperation(ServiceClient client, CredentialsProvider credsProvider) {
		super(client, credsProvider);
	}
	
    
    /**
     * Upload input stream to oss by using url .
     */
    public PutObjectResult putObject(URI endpoint ,String bucketName,String objectid, InputStream requestContent, long contentLength,
    		Map<String, String> requestHeaders) throws OSSException, ClientException {

        assertParameterNotNull(requestContent, "requestContent");
        
        if (requestHeaders == null) {
        	requestHeaders = new HashMap<String, String>();
        }
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
        .setMethod(HttpMethod.PUT)
    	.setAbsoluteUrl(genUrl(endpoint, bucketName, objectid))
    	.setUseUrl(true)
    	.setInputStream(requestContent)
    	.setInputSize(contentLength)
    	.setHeaders(requestHeaders)
    	.build();
    	
    	return doOperation(request, putObjectReponseParser, bucketName, objectid, true);
    }


	public OSSObject getObject(GetObjectRequest getObjectRequest) {
		assertParameterNotNull(getObjectRequest, "getObjectRequest");
		
    	String bucketName = null;
    	String key = null;
    	RequestMessage request = null;
    	if (!getObjectRequest.isUseUrlSignature()) {
        	assertParameterNotNull(getObjectRequest, "getObjectRequest");

            bucketName = getObjectRequest.getBucketName();
            key = getObjectRequest.getKey();
            
            assertParameterNotNull(bucketName, "bucketName");
            assertParameterNotNull(key, "key");
            ensureBucketNameValid(bucketName);
            ensureObjectKeyValid(key);

            Map<String, String> headers = new HashMap<String, String>();
            populateGetObjectRequestHeaders(getObjectRequest, headers);

            Map<String, String> params = new HashMap<String, String>();
            populateResponseHeaderParameters(params, getObjectRequest.getResponseHeaders());
            
            request = new OSSRequestMessageBuilder(getInnerClient())
	                .setEndpoint(getEndpoint())
	                .setMethod(HttpMethod.GET)
	                .setBucket(bucketName)
	                .setKey(key)
	                .setHeaders(headers)
	                .setParameters(params)
	                .build();
        } else {
        	request = new RequestMessage();
        	request.setMethod(HttpMethod.GET);
        	request.setAbsoluteUrl(getObjectRequest.getAbsoluteUri());
        	request.setUseUrlSignature(true);
        	request.setHeaders(getObjectRequest.getHeaders());
        }

        return doOperation(request, new GetObjectResponseParser(bucketName, key), 
        		bucketName, key, true);
	}
	
	private static void populateGetObjectRequestHeaders(GetObjectRequest getObjectRequest,
    		Map<String, String> headers) {
    	
    	if (getObjectRequest.getRange() != null) {
            addGetObjectRangeHeader(getObjectRequest.getRange(), headers);
        }

        if (getObjectRequest.getModifiedSinceConstraint() != null) {
            headers.put(OSSHeaders.GET_OBJECT_IF_MODIFIED_SINCE, 
            		DateUtil.formatRfc822Date(getObjectRequest.getModifiedSinceConstraint()));
        }
        
        if (getObjectRequest.getUnmodifiedSinceConstraint() != null) {
            headers.put(OSSHeaders.GET_OBJECT_IF_UNMODIFIED_SINCE, 
            		DateUtil.formatRfc822Date(getObjectRequest.getUnmodifiedSinceConstraint()));
        }
        
        if (getObjectRequest.getMatchingETagConstraints().size() > 0) {
            headers.put(OSSHeaders.GET_OBJECT_IF_MATCH,
                    joinETags(getObjectRequest.getMatchingETagConstraints()));
        }
        
        if (getObjectRequest.getNonmatchingETagConstraints().size() > 0) {
            headers.put(OSSHeaders.GET_OBJECT_IF_NONE_MATCH,
                    joinETags(getObjectRequest.getNonmatchingETagConstraints()));
        }
        
        // Populate all standard HTTP headers provided by SDK users
        headers.putAll(getObjectRequest.getHeaders());
    }
	
    private static void addGetObjectRangeHeader(long[] range, Map<String, String> headers) {    	
    	RangeSpec rangeSpec = RangeSpec.parse(range);
        headers.put(OSSHeaders.RANGE, rangeSpec.toString());
    }


	public void deleteObject(String bucketName, String key) {
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setKey(key)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, key);
	}

	/**
     * Check if the object key exists under the specified bucket.
     */
	public void headObject(HeadObjectRequest headObjectRequest) {
		assertParameterNotNull(headObjectRequest, "headObjectRequest");
    	
    	String bucketName = headObjectRequest.getBucketName();
    	String key = headObjectRequest.getKey();
    	
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        
        Map<String, String> headers = new HashMap<String, String>();
        addDateHeader(headers, OSSHeaders.HEAD_OBJECT_IF_MODIFIED_SINCE,
    			headObjectRequest.getModifiedSinceConstraint());
    	addDateHeader(headers, OSSHeaders.HEAD_OBJECT_IF_UNMODIFIED_SINCE,
    			headObjectRequest.getUnmodifiedSinceConstraint());
    	
    	addStringListHeader(headers, OSSHeaders.HEAD_OBJECT_IF_MATCH,
    			headObjectRequest.getMatchingETagConstraints());
    	addStringListHeader(headers, OSSHeaders.HEAD_OBJECT_IF_NONE_MATCH,
    			headObjectRequest.getNonmatchingETagConstraints());
    	
    	RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		    	.setEndpoint(getEndpoint())
		    	.setMethod(HttpMethod.HEAD)
		    	.setBucket(bucketName)
		    	.setKey(key)
		    	.setHeaders(headers)
		    	.build();
    	
    	doOperation(request, emptyResponseParser, bucketName, key);
	}
}
