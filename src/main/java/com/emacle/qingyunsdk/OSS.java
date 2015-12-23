package com.emacle.qingyunsdk;

import java.io.File;

import com.emacle.qingyunsdk.exception.ClientException;
import com.emacle.qingyunsdk.exception.OSSException;
import com.emacle.qingyunsdk.model.Bucket;
import com.emacle.qingyunsdk.model.OSSObject;
import com.emacle.qingyunsdk.model.PutObjectResult;
import com.emacle.qingyunsdk.model.request.CreateBucketRequest;
import com.emacle.qingyunsdk.model.request.GetObjectRequest;
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
    
    /**
     * 上传指定文件到OSS中指定的{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     * @param file
     *          指定上传文件。
     */
    public PutObjectResult putObject(String bucketName, File file) 
    		throws OSSException, ClientException;
    
    /**
     * 从OSS指定的{@link Bucket}中导出{@link OSSObject}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object Key。
     * @return 请求结果{@link OSSObject}实例。使用完之后需要手动关闭其中的ObjectContent释放请求连接。
     */
    public OSSObject getObject(String bucketName, String key) 
    		throws OSSException, ClientException;
    
    /**
     * 从OSS指定的{@link Bucket}中导出{@link OSSObject}。
     * @param getObjectRequest
     *          请求参数{@link GetObjectRequest}。
     * @return 请求结果{@link OSSObject}实例。使用完之后需要手动关闭其中的ObjectContent释放请求连接。
     */
    public OSSObject getObject(GetObjectRequest getObjectRequest) 
    		throws OSSException, ClientException;
    
    public void getService(GetServiceRequest gsr)
    		throws OSSException, ClientException;

	void getService() throws OSSException, ClientException;

    
}
