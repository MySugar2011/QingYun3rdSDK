/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.emacle.qingyunsdk.internal;

import static com.emacle.qingyunsdk.internal.OSSUtils.safeCloseResponse;
import static com.emacle.qingyunsdk.internal.OSSUtils.COMMON_RESOURCE_MANAGER;

import org.apache.http.HttpStatus;

import com.emacle.qingyunsdk.exception.ClientException;
import com.emacle.qingyunsdk.exception.OSSErrorCode;
import com.emacle.qingyunsdk.exception.OSSException;
import com.emacle.qingyunsdk.common.comm.ResponseHandler;
import com.emacle.qingyunsdk.common.comm.ResponseMessage;
import com.emacle.qingyunsdk.common.parser.JAXBResponseParser;
import com.emacle.qingyunsdk.common.parser.JsonResponseParseer;
import com.emacle.qingyunsdk.common.parser.ResponseParseException;
import com.emacle.qingyunsdk.common.utils.ExceptionFactory;
import com.emacle.qingyunsdk.internal.model.OSSErrorResult;

/**
 * Used to handle error response from oss, when HTTP status code is not 2xx, then throws
 * <code>OSSException</code> with detailed error information(such as request id, error code).  
 */
public class OSSErrorResponseHandler implements ResponseHandler {
    
	public void handle(ResponseMessage response)
            throws OSSException, ClientException {

        if (response.isSuccessful()) {
            return;
        }

        String requestId = response.getRequestId();
        int statusCode = response.getStatusCode();
        if (response.getContent() == null) {
        	/**
        	 * When HTTP response body is null, handle status code 404 Not Found, 304 Not Modified, 
        	 * 412 Precondition Failed especially.
        	 */
            if (statusCode == HttpStatus.SC_NOT_FOUND) {
            	throw ExceptionFactory.createOSSException(requestId, OSSErrorCode.NO_SUCH_KEY, "Not Found");
            } else if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
            	throw ExceptionFactory.createOSSException(requestId, OSSErrorCode.NOT_MODIFIED, "Not Modified");
            } else if (statusCode == HttpStatus.SC_PRECONDITION_FAILED) {
            	throw ExceptionFactory.createOSSException(requestId, OSSErrorCode.PRECONDITION_FAILED, "Precondition Failed");
            } else {
            	throw ExceptionFactory.createInvalidResponseException(requestId,
            			COMMON_RESOURCE_MANAGER.getString("ServerReturnsUnknownError"));            	
            }
        }
        // 这个地方不应该再使用xml，而改用json xxx
        JsonResponseParseer parser = new JsonResponseParseer(OSSErrorResult.class);
//        JAXBResponseParser parser = new JAXBResponseParser(OSSErrorResult.class);
        try {
            OSSErrorResult errorResult = (OSSErrorResult)parser.parse(response);
            throw ExceptionFactory.createOSSException(errorResult, response.getErrorResponseAsString());
        } catch (ResponseParseException e) {
        	throw ExceptionFactory.createInvalidResponseException(requestId, response.getErrorResponseAsString(), e);
        } finally {            
            safeCloseResponse(response);
        }
    }
    
}
