package com.emacle.qingyunsdk.internal.operation;

import static com.emacle.qingyunsdk.common.parser.RequestMarshallers.deleteObjectsRequestMarshaller;
import static com.emacle.qingyunsdk.common.utils.CodingUtils.assertParameterNotNull;
import static com.emacle.qingyunsdk.common.utils.CodingUtils.assertTrue;
import static com.emacle.qingyunsdk.common.utils.IOUtils.checkFile;
import static com.emacle.qingyunsdk.common.utils.IOUtils.newRepeatableInputStream;
import static com.emacle.qingyunsdk.common.utils.IOUtils.safeClose;
import static com.emacle.qingyunsdk.common.utils.LogUtils.getLog;
import static com.emacle.qingyunsdk.common.utils.LogUtils.logException;
import static com.emacle.qingyunsdk.internal.OSSConstants.DEFAULT_BUFFER_SIZE;
import static com.emacle.qingyunsdk.internal.OSSConstants.DEFAULT_CHARSET_NAME;
import static com.emacle.qingyunsdk.internal.OSSUtils.OSS_RESOURCE_MANAGER;
import static com.emacle.qingyunsdk.internal.OSSUtils.addDateHeader;
import static com.emacle.qingyunsdk.internal.OSSUtils.genUrl;
import static com.emacle.qingyunsdk.internal.OSSUtils.addHeader;
import static com.emacle.qingyunsdk.internal.OSSUtils.addStringListHeader;
import static com.emacle.qingyunsdk.internal.OSSUtils.determineInputStreamLength;
import static com.emacle.qingyunsdk.internal.OSSUtils.ensureBucketNameValid;
import static com.emacle.qingyunsdk.internal.OSSUtils.ensureObjectKeyValid;
import static com.emacle.qingyunsdk.internal.OSSUtils.joinETags;
import static com.emacle.qingyunsdk.internal.OSSUtils.populateRequestMetadata;
import static com.emacle.qingyunsdk.internal.OSSUtils.populateResponseHeaderParameters;
import static com.emacle.qingyunsdk.internal.OSSUtils.removeHeader;
import static com.emacle.qingyunsdk.internal.OSSUtils.safeCloseResponse;
import static com.emacle.qingyunsdk.internal.RequestParameters.ENCODING_TYPE;
import static com.emacle.qingyunsdk.internal.RequestParameters.SUBRESOURCE_ACL;
import static com.emacle.qingyunsdk.internal.RequestParameters.SUBRESOURCE_DELETE;
import static com.emacle.qingyunsdk.internal.ResponseParsers.appendObjectResponseParser;
import static com.emacle.qingyunsdk.internal.ResponseParsers.copyObjectResponseParser;
import static com.emacle.qingyunsdk.internal.ResponseParsers.deleteObjectsResponseParser;
import static com.emacle.qingyunsdk.internal.ResponseParsers.getObjectAclResponseParser;
import static com.emacle.qingyunsdk.internal.ResponseParsers.getObjectMetadataResponseParser;
import static com.emacle.qingyunsdk.internal.ResponseParsers.putObjectReponseParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.emacle.qingyunsdk.common.auth.CredentialsProvider;
import com.emacle.qingyunsdk.common.comm.HttpMethod;
import com.emacle.qingyunsdk.common.comm.RequestMessage;
import com.emacle.qingyunsdk.common.comm.ServiceClient;
import com.emacle.qingyunsdk.common.comm.io.RepeatableFileInputStream;
import com.emacle.qingyunsdk.internal.ResponseParsers.GetObjectResponseParser;
import com.emacle.qingyunsdk.common.parser.ResponseParser;
import com.emacle.qingyunsdk.common.utils.DateUtil;
import com.emacle.qingyunsdk.common.utils.RangeSpec;
import com.emacle.qingyunsdk.exception.ClientException;
import com.emacle.qingyunsdk.exception.OSSException;
import com.emacle.qingyunsdk.internal.Mimetypes;
import com.emacle.qingyunsdk.internal.OSSHeaders;
import com.emacle.qingyunsdk.internal.OSSOperation;
import com.emacle.qingyunsdk.internal.OSSRequestMessageBuilder;
import com.emacle.qingyunsdk.internal.RequestParameters;
import com.emacle.qingyunsdk.model.AppendObjectRequest;
import com.emacle.qingyunsdk.model.DeleteObjectsResult;
import com.emacle.qingyunsdk.model.HeadObjectRequest;
import com.emacle.qingyunsdk.model.OSSObject;
import com.emacle.qingyunsdk.model.ObjectMetadata;
import com.emacle.qingyunsdk.model.PutObjectRequest;
import com.emacle.qingyunsdk.model.PutObjectResult;
import com.emacle.qingyunsdk.model.request.DeleteObjectsRequest;
import com.emacle.qingyunsdk.model.request.GetObjectRequest;

public class OSSObjectOperation extends OSSOperation{

	public OSSObjectOperation(ServiceClient client, CredentialsProvider credsProvider) {
		super(client, credsProvider);
	}
	
	/**
     * Upload input stream or file to oss.
     */
	public PutObjectResult putObject(PutObjectRequest putObjectRequest) 
    		throws OSSException, ClientException {
    	assertParameterNotNull(putObjectRequest, "putObjectRequest");
    	return writeObjectInternal(WriteMode.OVERWRITE, putObjectRequest, putObjectReponseParser);
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


	public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
		
		return null;
	}
	
	 /**
     * An enum to represent different modes the client may specify to upload specified file or inputstream.
     */
    private static enum WriteMode {
    	
    	/* If object already not exists, create it. otherwise, append it with the new input */
    	APPEND("APPEND"),
    	
    	/* No matter object exists or not, just overwrite it with the new input */
    	OVERWRITE("OVERWRITE");
    	
    	private final String modeAsString;
    	
    	private WriteMode(String modeAsString) {
    		this.modeAsString = modeAsString;
    	}
    	
    	@Override
    	public String toString() {
    		return this.modeAsString;
    	}
    	
    	public static HttpMethod getMappingMethod(WriteMode mode) {
    		switch (mode) {
			case APPEND:
				return HttpMethod.POST;
			
			case OVERWRITE:
				return HttpMethod.PUT;
				
			default:
				throw new IllegalArgumentException("Unsuported write mode" + mode.toString());
			}
    	}
    }
    
    private <RequestType extends PutObjectRequest, ResponseType> 
	ResponseType writeObjectInternal(WriteMode mode, RequestType originalRequest, 
			ResponseParser<ResponseType> responseParser) {
	
	final String bucketName = originalRequest.getBucketName();
	final String key = originalRequest.getKey();
	InputStream originalInputStream = originalRequest.getInputStream();
	ObjectMetadata metadata = originalRequest.getMetadata();
	if (metadata == null) {
		metadata = new ObjectMetadata();
	}
	
	assertParameterNotNull(bucketName, "bucketName");
	assertParameterNotNull(key, "key");
	ensureBucketNameValid(bucketName);
    ensureObjectKeyValid(key);
	
    InputStream repeatableInputStream = null;
	if (originalRequest.getFile() != null) {
    	File toUpload = originalRequest.getFile();
    	
    	if (!checkFile(toUpload)) {
    		getLog().info("Illegal file path: " + toUpload.getPath());
			throw new ClientException("Illegal file path: " + toUpload.getPath());
		}
    	
    	metadata.setContentLength(toUpload.length());
    	if (metadata.getContentType() == null) {
    		metadata.setContentType(Mimetypes.getInstance().getMimetype(toUpload));
    	}
    	
    	try {
    		repeatableInputStream = new RepeatableFileInputStream(toUpload);
		} catch (IOException ex) {
			logException("Cannot locate file to upload: ", ex);
			throw new ClientException("Cannot locate file to upload: ", ex);
		}
    } else {
    	assertTrue(originalInputStream != null, "Please specify input stream or file to upload");
    	
    	if (metadata.getContentType() == null) {
    		metadata.setContentType(Mimetypes.DEFAULT_MIMETYPE);
    	}
    	
    	try {
			repeatableInputStream = newRepeatableInputStream(originalInputStream);
		} catch (IOException ex) {
			logException("Cannot wrap to repeatable input stream: ", ex);
			throw new ClientException("Cannot wrap to repeatable input stream: ", ex);
		}
    }
    
    Map<String, String> headers = new HashMap<String, String>();
    populateRequestMetadata(headers, metadata);
    Map<String, String> params = new LinkedHashMap<String, String>();
    populateWriteObjectParams(mode, originalRequest, params);
    
    RequestMessage httpRequest = new OSSRequestMessageBuilder(getInnerClient())
	        .setEndpoint(getEndpoint())
	        .setMethod(WriteMode.getMappingMethod(mode))
	        .setBucket(bucketName)
	        .setKey(key)
	        .setHeaders(headers)
	        .setParameters(params)
	        .setInputStream(repeatableInputStream)
	        .setInputSize(determineInputStreamLength(repeatableInputStream, metadata.getContentLength()))
	        .build();
    
    return doOperation(httpRequest, responseParser, bucketName, key, true);
    }	
    private static void populateWriteObjectParams(WriteMode mode, PutObjectRequest originalRequest, 
    		Map<String, String> params) {
    	
    	if (mode == WriteMode.OVERWRITE) {
    		return;
    	}
    	
    	assert (originalRequest instanceof AppendObjectRequest);
    	params.put(RequestParameters.SUBRESOURCE_APPEND, null);
    	AppendObjectRequest appendObjectRequest = (AppendObjectRequest)originalRequest;
    	if (appendObjectRequest.getPosition() != null) {
    		params.put(RequestParameters.POSITION, String.valueOf(appendObjectRequest.getPosition()));    		
    	}
    }
}
