package com.emacle.qingyunsdk;

import com.emacle.qingyunsdk.model.Bucket;
import com.emacle.qingyunsdk.model.DeleteObjectsResult;
import com.emacle.qingyunsdk.model.HeadObjectRequest;
import com.emacle.qingyunsdk.model.OSSObject;
import com.emacle.qingyunsdk.model.PutObjectResult;
import com.emacle.qingyunsdk.model.request.CreateBucketRequest;
import com.emacle.qingyunsdk.model.request.DeleteObjectsRequest;
import com.emacle.qingyunsdk.model.request.GetObjectRequest;
import com.emacle.qingyunsdk.model.request.GetServiceRequest;

import static com.emacle.qingyunsdk.internal.OSSConstants.DEFAULT_OSS_ENDPOINT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import com.emacle.qingyunsdk.common.auth.CredentialsProvider;
import com.emacle.qingyunsdk.common.auth.DefaultCredentialProvider;
import com.emacle.qingyunsdk.common.comm.DefaultServiceClient;
import com.emacle.qingyunsdk.common.comm.ServiceClient;
import com.emacle.qingyunsdk.exception.ClientException;
import com.emacle.qingyunsdk.exception.OSSErrorCode;
import com.emacle.qingyunsdk.exception.OSSException;
import com.emacle.qingyunsdk.internal.operation.OSSBucketOperation;
import com.emacle.qingyunsdk.internal.operation.OSSObjectOperation;
import com.emacle.qingyunsdk.internal.operation.OSSServiceOperation;

/**
 * 访问青云对象存储服务的入口类。
 */
public class QOSSClient implements OSS{
	
	/* The default credentials provider */
	private CredentialsProvider credsProvider;

	/* The valid endpoint for accessing to OSS services */
	private URI endpoint;

	/* The default service client */
	private ServiceClient serviceClient;

	/* The miscellaneous OSS operations */
	private OSSBucketOperation bucketOperation;
	private OSSServiceOperation serviceOperation;
	private OSSObjectOperation objectOperation;
//	private OSSMultipartOperation multipartOperation;
//	private CORSOperation corsOperation;
	
	/**
	 * 使用默认的OSS Endpoint(OSSConstants.DEFAULT_OSS_ENDPOINT)及
	 * Access Id/Access Key构造一个新的{@link OSSClient}对象。
	 * 
	 * @param accessKeyId
	 *            访问OSS的Access Key ID。
	 * @param secretAccessKey
	 *            访问OSS的Secret Access Key。
	 */
	@Deprecated
	public QOSSClient(String accessKeyId, String secretAccessKey) {
		this(DEFAULT_OSS_ENDPOINT, new DefaultCredentialProvider(accessKeyId, secretAccessKey));
	}
	/**
	 * 使用指定的OSS Endpoint、阿里云颁发的Access Id/Access Key构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。
	 * @param accessKeyId
	 *            访问OSS的Access Key ID。
	 * @param secretAccessKey
	 *            访问OSS的Secret Access Key。
	 */
	public QOSSClient(String endpoint, String accessKeyId, String secretAccessKey) {
		this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey), null);
	}
	
	/**
	 * 使用指定的OSS Endpoint、STS提供的临时Token信息(Access Id/Access Key/Security Token)
	 * 构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。
	 * @param accessKeyId
	 *            STS提供的临时访问ID。
	 * @param secretAccessKey
	 *            STS提供的访问密钥。
	 * @param securityToken
	 * 			  STS提供的安全令牌。
	 */
	public QOSSClient(String endpoint, String accessKeyId, String secretAccessKey, String securityToken) {
		this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken), null);
	}
	
	/**
	 * 使用指定的OSS Endpoint、阿里云颁发的Access Id/Access Key、客户端配置
	 * 构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。
	 * @param accessKeyId
	 *            访问OSS的Access Key ID。
	 * @param secretAccessKey
	 *            访问OSS的Secret Access Key。
	 * @param config
	 *            客户端配置 {@link ClientConfiguration}。 如果为null则会使用默认配置。
	 */
	public QOSSClient(String endpoint, String accessKeyId, String secretAccessKey, 
			ClientConfiguration config) {
		this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey), config);
	}
	
	/**
	 * 使用指定的OSS Endpoint、STS提供的临时Token信息(Access Id/Access Key/Security Token)、
	 * 客户端配置构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。
	 * @param accessKeyId
	 *            STS提供的临时访问ID。
	 * @param secretAccessKey
	 *            STS提供的访问密钥。
	 * @param securityToken
	 * 			  STS提供的安全令牌。
	 * @param config
	 *            客户端配置 {@link ClientConfiguration}。 如果为null则会使用默认配置。
	 */
	public QOSSClient(String endpoint, String accessKeyId, String secretAccessKey, String securityToken, 
			ClientConfiguration config) {
		this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken), config);
	}

	/**
	 * 使用默认配置及指定的{@link CredentialsProvider}与Endpoint构造一个新的{@link OSSClient}对象。
	 * @param endpoint OSS services的Endpoint。
	 * @param credsProvider Credentials提供者。
	 */
	public QOSSClient(String endpoint, CredentialsProvider credsProvider) {
		this(endpoint, credsProvider, null);
	}
	
	/**
	 * 使用指定的{@link CredentialsProvider}、配置及Endpoint构造一个新的{@link OSSClient}对象。
	 * @param endpoint OSS services的Endpoint。
	 * @param credsProvider Credentials提供者。
	 * @param config client配置。
	 */
	public QOSSClient(String endpoint, CredentialsProvider credsProvider, ClientConfiguration config) {
		this.credsProvider = credsProvider;
		this.serviceClient = new DefaultServiceClient(config == null ? new ClientConfiguration() : config);
		initOperations();
		setEndpoint(endpoint);
	}
	
	/**
	 * 获取OSS services的Endpoint。
	 * @return OSS services的Endpoint。
	 */
	public synchronized URI getEndpoint() {
		return URI.create(endpoint.toString());
	}
	
	/**
	 * 设置OSS services的Endpoint。
	 * @param endpoint OSS services的Endpoint。
	 */
	public synchronized void setEndpoint(String endpoint) {
		URI uri = toURI(endpoint);
		this.endpoint = uri;
		
		this.bucketOperation.setEndpoint(uri);
		this.serviceOperation.setEndpoint(uri);
		this.objectOperation.setEndpoint(uri);
//		this.multipartOperation.setEndpoint(uri);
//		this.corsOperation.setEndpoint(uri);
	}
	
    private URI toURI(String endpoint) throws IllegalArgumentException {    	
        if (!endpoint.contains("://")) {
        	ClientConfiguration conf = this.serviceClient.getClientConfiguration();
            endpoint = conf.getProtocol().toString() + "://" + endpoint;
        }

        try {
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private void initOperations() {
    	this.bucketOperation = new OSSBucketOperation(this.serviceClient, this.credsProvider);
    	this.serviceOperation = new OSSServiceOperation(this.serviceClient, this.credsProvider);
    	this.objectOperation = new OSSObjectOperation(this.serviceClient, this.credsProvider);
//    	this.multipartOperation = new OSSMultipartOperation(this.serviceClient, this.credsProvider);
//    	this.corsOperation = new CORSOperation(this.serviceClient, this.credsProvider);
    }
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Bucket putBucket(String bucketName) {
		return this.putBucket(new CreateBucketRequest(bucketName));
	}
	@Override
	public Bucket putBucket(CreateBucketRequest createBucketRequest) throws OSSException, ClientException {
		return bucketOperation.createBucket(createBucketRequest);
	}
	
	@Override
	public void getService() throws OSSException, ClientException {
		serviceOperation.getService(new GetServiceRequest());
	}
	
	@Override
	public void getService(GetServiceRequest gsr) throws OSSException, ClientException {
		serviceOperation.getService(gsr);
	}
	@Override
	public PutObjectResult putObject(String bucketName,  File file) throws OSSException, ClientException {
		FileInputStream fis = null;
		String objectName = file.getName();
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		long contentLength = file.length();
		return objectOperation.putObject(endpoint,bucketName,objectName, fis, contentLength, null);
	}
	
    /**
     * Pull an object from oss.
     */
	@Override
    public OSSObject getObject(String bucketName, String key)
            throws OSSException, ClientException {
		return this.getObject(new GetObjectRequest(bucketName, key));
    }
	
	@Override
	public OSSObject getObject(GetObjectRequest getObjectRequest) 
			throws OSSException, ClientException {
		return objectOperation.getObject(getObjectRequest);
	}
	
	@Override
	public void deleteObject(String bucketName, String key) throws OSSException, ClientException {
		objectOperation.deleteObject(bucketName, key);
	}
	
	@Override
	public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
			throws OSSException, ClientException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean doesObjectExist(String bucketName, String key) throws OSSException, ClientException {
		return doesObjectExist(new HeadObjectRequest(bucketName, key));
	}
	@Override
	public boolean doesObjectExist(HeadObjectRequest headObjectRequest) throws OSSException, ClientException {
		try {
			headObject(headObjectRequest);
			return true;
		} catch (OSSException e) {// 这儿待改动的
			if (e.getErrorCode() == OSSErrorCode.NO_SUCH_BUCKET 
					|| e.getErrorCode() == OSSErrorCode.NO_SUCH_KEY) {
				return false;
			}
			throw e;
		}
	}
	
	private void headObject(HeadObjectRequest headObjectRequest)
			throws OSSException, ClientException {
		objectOperation.headObject(headObjectRequest);
	}
	
}
