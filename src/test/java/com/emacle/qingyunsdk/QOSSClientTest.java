package com.emacle.qingyunsdk;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;

import com.emacle.qingyunsdk.model.Bucket;
import com.emacle.qingyunsdk.model.CompleteMultipartUploadResult;
import com.emacle.qingyunsdk.model.HeadObjectRequest;
import com.emacle.qingyunsdk.model.InitiateMultipartUploadRequest;
import com.emacle.qingyunsdk.model.ListMultipartUploadsRequest;
import com.emacle.qingyunsdk.model.MultipartUploadListing;
import com.emacle.qingyunsdk.model.OSSObject;
import com.emacle.qingyunsdk.model.ObjectMetadata;
import com.emacle.qingyunsdk.model.PartETag;
import com.emacle.qingyunsdk.model.PartListing;
import com.emacle.qingyunsdk.model.UploadPartResult;
import com.emacle.qingyunsdk.model.request.AbortMultipartUploadRequest;
import com.emacle.qingyunsdk.model.request.CompleteMultipartUploadRequest;
import com.emacle.qingyunsdk.model.request.InitiateMultipartUploadResult;
import com.emacle.qingyunsdk.model.request.ListPartsRequest;
import com.emacle.qingyunsdk.model.request.UploadPartRequest;

public class QOSSClientTest {
	
	private static QOSSClient qsc = null;
	private static String ACCESS_ID= "NMVAXXTSCXWLBFHAROXB";
	private static String ACCESS_KEY = "IE3hyXgTeSFvdmUMcbvfFHbZGNhPQbl8nKXoxwnR";
	private static String bucket = "sugaross";
	private static String tmpUploadId = "0a273ea68d2e3dff94e18ca06a6f2d37";
	private static String testfile1 = "/Users/sugar/test/te.txt";
	private static String testfile2 = "/Users/sugar/test/test.py";
	private static String testobject1 = "te.txt";
	private static String testobject2 = "test.py";
	private static String testmuliobject = "MultipartUpload5.txt";
	
	
	@BeforeClass
	public static void init(){
		System.out.println("init");
		qsc = new QOSSClient(ACCESS_ID, ACCESS_KEY);
	}
	
//	@Test
//	public void testPutBucket(){
//		Bucket buc = qsc.putBucket(bucket);
//		assertEquals(bucket, buc.getName());
//	}
//	
//	@Test
//	public void testPutObject(){
//		File file = new File(testfile1);
////		qsc.putObject(bucket, file);
//		qsc.putObject(bucket, "hhaxx.txt", file);
//	}
	
//	@Test
//	public void testGetObject(){
//		String bucketName = "sugaross";
//		String key = "te.txt";
//		OSSObject ob= qsc.getObject(bucketName, key);
//		
//	}
	
//	@Test
//	public void testDeleteObject(){
//		String bucketName = "sugaross";
//		String key = "te.txt";
//		qsc.deleteObject(bucketName, key);
//		
//	}
//	
//	@Test
//	public void testDoesObjectExist(){
//		String bucketName = "sugaross";
//		String key = "te.txt";
//		System.out.println(qsc.doesObjectExist(bucketName, key));
//		assertFalse(qsc.doesObjectExist(bucketName, key));
//		
//	}
	
//	@Test
//	public void testHeadObjectExist(){
//		HeadObjectRequest request = new HeadObjectRequest(bucket, testmuliobject);
//		qsc.
//		System.out.println(qsc.doesObjectExist(bucket, testmuliobject));
////		assertFalse(qsc.doesObjectExist(bucketName, key));
//		
//	}
	
//	@Test
//	public void testgetObjectMeta(){
//		HeadObjectRequest request = new HeadObjectRequest(bucket, testobject1);
//		 ObjectMetadata objMeta = qsc.getObjectMetadata(bucket, testobject1);
//		 
//		 
//		 System.out.println(objMeta.getContentLength());
////		 printJson("###objMeta", objMeta);
////		System.out.println(qsc.doesObjectExist(bucket, testmuliobject));
////		assertFalse(qsc.doesObjectExist(bucketName, key));
//		
//	}
	
//	@Test
//	public void testInitiateMultipartUpload(){
//		InitiateMultipartUploadRequest initUploadRequest = new InitiateMultipartUploadRequest(
//				bucket, testmuliobject);
//		InitiateMultipartUploadResult imu =qsc.initiateMultipartUpload(initUploadRequest);
//		tmpUploadId = imu.getUploadId();
//		printJson("####imu",imu);
//	}
	
//	@Test
//	public void testUploadPart(){
//		// 没有关于etag的设置，那么是不是有可能要在appserver内做此操作,uploadid判断分块？
//		FileInputStream fis = null;
//		try {
//			fis = new FileInputStream(new File(testfile1));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		UploadPartRequest uploadRequest = new UploadPartRequest();
//		uploadRequest.setBucketName(bucket);
//		uploadRequest.setKey(testmuliobject);
//		uploadRequest.setPartNumber(1);
//		uploadRequest.setInputStream(fis);
//		uploadRequest.setUploadId(tmpUploadId);
//		UploadPartResult uploadpartResult = qsc.uploadPart(uploadRequest);
//		printJson("####uploadpartResult",uploadpartResult);
//	}
	
//	@Test
//	public void testListParts(){
//		ListPartsRequest listPartsRequest = new ListPartsRequest(bucket, testmuliobject, tmpUploadId);
//		PartListing listpartsResult = qsc.listParts(listPartsRequest);
//		printJson("###listpartsResult",listpartsResult);
//	}
	
//	@Test // 弃用
//	public void testlistMultipartUploads(){
//		ListMultipartUploadsRequest listMultipartUploadsRequest = new ListMultipartUploadsRequest(bucket);
//		MultipartUploadListing multipartUploadListing  = qsc.listMultipartUploads(listMultipartUploadsRequest);
//		printJson("###multipartUploadListing",multipartUploadListing);
//	}
	
//	@Test
//	public void testcompleteMultipartUpload(){
//		List<PartETag> list =new ArrayList<PartETag>();
//		//考虑去掉etag
//		PartETag pe = new PartETag(1, "\"d42468f933664e0173a6347f52e92209\"");
//		list.add(pe);
//		CompleteMultipartUploadRequest request  = new CompleteMultipartUploadRequest(bucket, testmuliobject, tmpUploadId, list,"\"d42468f933664e0173a6347f52e92209\"");
//		CompleteMultipartUploadResult result = qsc.completeMultipartUpload(request);
//		printJson("###CompleteMultipartUploadResult",result);
//	}
	
//	@Test
//	public void testabortMultipartUpload(){
//		AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucket, testmuliobject, tmpUploadId);
//		qsc.abortMultipartUpload(request);
//	}
	
	public static void printJson(String commetn,Object obj){
		ObjectMapper om  = new ObjectMapper();
		try {
			String json = om.writeValueAsString(obj);
			System.out.println(commetn+json);
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		} 
		
	}
}
