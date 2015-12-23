package com.emacle.qingyunsdk;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.emacle.qingyunsdk.model.OSSObject;

public class QOSSClientTest {
	
	private static QOSSClient qsc = null;
	private static String ACCESS_ID= "NMVAXXTSCXWLBFHAROXB";
	private static String ACCESS_KEY = "IE3hyXgTeSFvdmUMcbvfFHbZGNhPQbl8nKXoxwnR";
	
	
	@BeforeClass
	public static void init(){
		System.out.println("init");
		qsc = new QOSSClient(ACCESS_ID, ACCESS_KEY);
	}
	
//	@Test
//	public void testPutBucket(){
//		qsc.putBucket("sugaross");
//	}
//	
//	@Test
//	public void testPutObject(){
//		String bucketName = "sugaross";
//		File file = new File("/Users/sugar/test/te.txt");
//		qsc.putObject(bucketName, file);
//	}
	
	@Test
	public void testGetObject(){
		String bucketName = "sugaross";
		String key = "te.txt";
		OSSObject ob= qsc.getObject(bucketName, key);
		
	}
}
