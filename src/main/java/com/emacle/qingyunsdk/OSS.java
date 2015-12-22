package com.emacle.qingyunsdk;

import com.emacle.qingyunsdk.exception.ClientException;
import com.emacle.qingyunsdk.exception.OSSException;
import com.emacle.qingyunsdk.model.Bucket;
import com.emacle.qingyunsdk.model.request.CreateBucketRequest;
import com.emacle.qingyunsdk.model.request.GetServiceRequest;

/**
 * 青云object存储服务SDK
 * @author sugar
 *	
 */
public interface OSS {
	/**
	 * 关闭Client实例，并释放所有正在使用的资源。
	 * 一旦关闭，将不再处理任何发往OSS的请求。
	 */
	public void shutdown();
	
    /**
     * 创建{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     */
    public Bucket putBucket(String bucketName)
    		throws OSSException, ClientException;
    
    /**
     * 创建{@link Bucket}。
     * @param createBucketRequest
     *          请求参数{@link CreateBucketRequest}。
     */
    public Bucket putBucket(CreateBucketRequest createBucketRequest) 
    		throws OSSException, ClientException;
    
    public void getService(GetServiceRequest gsr)
    		throws OSSException, ClientException;

	void getService() throws OSSException, ClientException;
    
}
