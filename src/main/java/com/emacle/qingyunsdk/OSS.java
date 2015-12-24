package com.emacle.qingyunsdk;

import java.io.File;
import java.io.InputStream;

import com.emacle.qingyunsdk.exception.ClientException;
import com.emacle.qingyunsdk.exception.OSSException;
import com.emacle.qingyunsdk.model.Bucket;
import com.emacle.qingyunsdk.model.CompleteMultipartUploadResult;
import com.emacle.qingyunsdk.model.DeleteObjectsResult;
import com.emacle.qingyunsdk.model.HeadObjectRequest;
import com.emacle.qingyunsdk.model.InitiateMultipartUploadRequest;
import com.emacle.qingyunsdk.model.ListMultipartUploadsRequest;
import com.emacle.qingyunsdk.model.MultipartUploadListing;
import com.emacle.qingyunsdk.model.OSSObject;
import com.emacle.qingyunsdk.model.ObjectMetadata;
import com.emacle.qingyunsdk.model.PartListing;
import com.emacle.qingyunsdk.model.PutObjectRequest;
import com.emacle.qingyunsdk.model.PutObjectResult;
import com.emacle.qingyunsdk.model.UploadPartResult;
import com.emacle.qingyunsdk.model.request.AbortMultipartUploadRequest;
import com.emacle.qingyunsdk.model.request.CompleteMultipartUploadRequest;
import com.emacle.qingyunsdk.model.request.CreateBucketRequest;
import com.emacle.qingyunsdk.model.request.DeleteObjectsRequest;
import com.emacle.qingyunsdk.model.request.GetObjectRequest;
import com.emacle.qingyunsdk.model.request.GetServiceRequest;
import com.emacle.qingyunsdk.model.request.InitiateMultipartUploadResult;
import com.emacle.qingyunsdk.model.request.ListPartsRequest;
import com.emacle.qingyunsdk.model.request.UploadPartRequest;

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
     * 上传指定的{@link OSSObject}到OSS中指定的{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          object的key。
     * @param input
     *          输入流。
     * @param metadata
     *          object的元信息{@link ObjectMetadata}，若该元信息未包含Content-Length，
     *          则采用chunked编码传输请求数据。
     */
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) 
    		throws OSSException, ClientException;
    
    /**
     * 上传指定文件到OSS中指定的{@link Bucket}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          object的key。
     * @param file
     *          指定上传文件。
     */
    public PutObjectResult putObject(String bucketName, String key, File file) 
    		throws OSSException, ClientException;
    
    /**
     * 上传指定文件或输入流至指定的{@link Bucket}。
     * @param putObjectRequest 请求参数{@link PutObjectRequest}。
     * @return 请求结果{@link PutObjectResult}实例。
     * @throws OSSException
     * @throws ClientException
     */
    public PutObjectResult putObject(PutObjectRequest putObjectRequest)
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
    
    /**
     * 删除指定的{@link OSSObject}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     */
    public void deleteObject(String bucketName, String key)
            throws OSSException, ClientException;

    /**
     * (暂不支持)批量删除指定Bucket下的{@link OSSObject}。 
     * @param deleteObjectsRequest 
     * 			请求参数{@link DeleteObjectsRequest}实例。
     * @return 批量删除结果。
     */
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
    		throws OSSException, ClientException;
    
    /**
     * 判断指定{@link Bucket}下是否存在指定的{@link OSSObject}。
     * @param bucketName 
     * 			Bucket名称。
     * @param key
     * 			Object Key。 
     * @return 
     * 			如果存在返回True，不存在则返回False。
     */
    public boolean doesObjectExist(String bucketName, String key)
    		throws OSSException, ClientException;
    
    /**
     * 判断指定的{@link OSSObject}是否存在。
     * @param headObjectRequest 
     * 			请求参数{@link HeadObjectRequest}实例。
     * @return 
     * 			如果存在返回True，不存在则返回False。
     */
    public boolean doesObjectExist(HeadObjectRequest headObjectRequest)
    		throws OSSException, ClientException;
    
    /**
     * 初始化一个Multipart上传事件。
     * <p>
     * 使用Multipart模式上传数据前，必须先调用该接口来通过OSS初始化一个Multipart上传事件。
     * 该接口会返回一个OSS服务器创建的全局唯一的Upload ID，用于标识本次Multipart上传事件。
     * 用户可以根据这个ID来发起相关的操作，如中止、查询Multipart上传等。
     * </p>
     * 
     * <p>
     * 此方法对应的操作为非幂等操作，SDK不会对其进行重试（即使设置最大重试次数大于0也不会重试）
     * </p>
     * @param request
     *          {@link InitiateMultipartUploadRequest}对象。
     * @return  InitiateMultipartUploadResult    
     * @throws ClientException
     */
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request) 
    		throws OSSException, ClientException;
    
    /**
     * 列出所有执行中的 Multipart上传事件。
     * <p>
     * 即已经被初始化的 Multipart Upload 但是未被完成或被终止的 Multipart上传事件。 
     * OSS返回的罗列结果中最多会包含1000个Multipart上传事件。
     * </p>
     * @param request
     *          {@link ListMultipartUploadsRequest}对象。
     * @return  MultipartUploadListing
     *          Multipart上传事件的列表{@link MultipartUploadListing}。
     * @throws ClientException
     */
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request) 
    		throws OSSException, ClientException;

    /**
     * 列出multipart中上传的所有part信息
     * @param request
     *          {@link ListPartsRequest}对象。
     * @return  PartListing    
     * @throws ClientException
     */
    public PartListing listParts(ListPartsRequest request) 
    		throws OSSException, ClientException;

    /**
     * 上传一个分块（Part）到指定的的Multipart上传事件中。
     * @param request
     *          {@link UploadPartRequest}对象。
     * @return  UploadPartResult 上传Part的返回结果{@link UploadPartResult}。
     * @throws ClientException
     */
    public UploadPartResult uploadPart(UploadPartRequest request)
            throws OSSException, ClientException;
    
    /**
     * 完成一个Multipart上传事件。
     * <p>
     * 在将所有数据Part 都上传完成后，可以调用 Complete Multipart Upload API
     * 来完成整个文件的 Multipart Upload。在执行该操作时，用户必须提供所有有效
     * 的数据Part的列表（包括part号码和ETAG）； OSS收到用户提交的Part列表后，
     * 会逐一验证每个数据 Part 的有效性。当所有的数据 Part 验证通过后，OSS 将把
     * 这些数据part组合成一个完整的 Object。 
     * </p>
     * 
     * <p>
     * 此方法对应的操作为非幂等操作，SDK不会对其进行重试（即使设置最大重试次数大于0也不会重试）
     * </p>
     * 
     * @param request
     *          {@link CompleteMultipartUploadRequest}对象。
     * @return  CompleteMultipartUploadResult    
     * @throws ClientException
     */
    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request) 
    		throws OSSException, ClientException;
    
    /**
     * 终止一个Multipart上传事件。
     * @param request
     *          {@link AbortMultipartUploadRequest}对象。
     * @throws ClientException
     */
    public void abortMultipartUpload(AbortMultipartUploadRequest request)
            throws OSSException, ClientException;

    
    public void getService(GetServiceRequest gsr)
    		throws OSSException, ClientException;

	void getService() throws OSSException, ClientException;

    
}
