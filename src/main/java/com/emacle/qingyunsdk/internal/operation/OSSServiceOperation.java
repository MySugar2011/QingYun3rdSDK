package com.emacle.qingyunsdk.internal.operation;


import java.util.HashMap;
import java.util.Map;

import com.emacle.qingyunsdk.common.auth.CredentialsProvider;
import com.emacle.qingyunsdk.common.comm.HttpMethod;
import com.emacle.qingyunsdk.common.comm.RequestMessage;
import com.emacle.qingyunsdk.common.comm.ServiceClient;
import com.emacle.qingyunsdk.internal.OSSOperation;
import com.emacle.qingyunsdk.internal.OSSRequestMessageBuilder;
import com.emacle.qingyunsdk.model.request.GetServiceRequest;

public class OSSServiceOperation extends OSSOperation {

	public OSSServiceOperation(ServiceClient client, CredentialsProvider credsProvider) {
		super(client, credsProvider);
	}

	public void getService(GetServiceRequest GetServiceReques){
        
        Map<String, String> headers = new HashMap<String, String>();
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setHeaders(headers)
                .build();

        doOperation(request, emptyResponseParser, "", null);
	}
	
}
