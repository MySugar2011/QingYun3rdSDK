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

package com.emacle.qingyunsdk.common.comm;

import com.emacle.qingyunsdk.internal.OSSHeaders;

public class ResponseMessage extends HttpMesssage {
	
	private static final int HTTP_SUCCESS_STATUS_CODE = 200;
    
	private String uri;
    private int statusCode;

    private ServiceClient.Request request;
    
    // For convenience of logging invalid response
    private String errorResponseAsString;
    
    public ResponseMessage(ServiceClient.Request request) {
    	this.request = request;
    }

    public String getUri() {
        return uri;
    }

    public void setUrl(String uri) {
        this.uri = uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
 
    public String getRequestId() { // 需修改，不一定会返回什么东西，待会儿全部都记录下吧
       return getHeaders().get(OSSHeaders.OSS_HEADER_REQUEST_ID);
    }

    public ServiceClient.Request getRequest() {
		return request;
	}
    
	public boolean isSuccessful(){//20x 为成功，学到了
        return statusCode / 100 == HTTP_SUCCESS_STATUS_CODE / 100;
    }

    public String getErrorResponseAsString() {
		return errorResponseAsString;
	}

	public void setErrorResponseAsString(String errorResponseAsString) {
		this.errorResponseAsString = errorResponseAsString;
	}
}
