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

import com.emacle.qingyunsdk.exception.ClientException;

import java.io.UnsupportedEncodingException;

import com.emacle.qingyunsdk.common.auth.Credentials;
import com.emacle.qingyunsdk.common.auth.RequestSigner;
import com.emacle.qingyunsdk.common.auth.ServiceSignature;
import com.emacle.qingyunsdk.common.comm.RequestMessage;

public class OSSRequestSigner implements RequestSigner {

    private String httpMethod;
    
    /* Note that resource path should not have been url-encoded. */
    private String resourcePath;
    private Credentials creds;

    public OSSRequestSigner(String httpMethod, String resourcePath, Credentials creds) {
        this.httpMethod = httpMethod;
        this.resourcePath = resourcePath;
        this.creds = creds;
    }

    @Override
    public void sign(RequestMessage request) throws ClientException {
    	String accessKeyId = creds.getAccessKeyId();
        String secretAccessKey = creds.getSecretAccessKey();

        if (accessKeyId.length() > 0 && secretAccessKey.length() > 0) {
            String canonicalString = SignUtils.buildCanonicalString(httpMethod, resourcePath, request, null);
            System.out.println("当前请求计算出签名串为：\n"+canonicalString.toString());
            String signature = ServiceSignature.create().computeSignature(secretAccessKey, canonicalString);
            // 暂时先这样处理 后面改成为统一的ClientException
            try {
				signature =   java.net.URLEncoder.encode(signature,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}  
            request.addHeader(OSSHeaders.AUTHORIZATION, OSSUtils.composeRequestAuthorization(accessKeyId, signature));
        } 
    }
}
