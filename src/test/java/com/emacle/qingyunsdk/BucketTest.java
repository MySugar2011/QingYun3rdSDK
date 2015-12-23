package com.emacle.qingyunsdk;

import java.io.File;

public class BucketTest {
	public static void main(String[] args) {
		QOSSClient qsc = new QOSSClient("NMVAXXTSCXWLBFHAROXB", "IE3hyXgTeSFvdmUMcbvfFHbZGNhPQbl8nKXoxwnR");
//		qsc.putBucket("sugaross");
		//service 暂时为permission_denied
//		qsc.getService();
		String bucketName = "sugaross";
		File file = new File("/Users/sugar/test/te.txt");
		qsc.putObject(bucketName, file);
	}
}
