package com.emacle.qingyunsdk;

public class BucketTest {
	public static void main(String[] args) {
		QOSSClient qsc = new QOSSClient("NMVAXXTSCXWLBFHAROXB", "IE3hyXgTeSFvdmUMcbvfFHbZGNhPQbl8nKXoxwnR");
		qsc.putBucket("sugaross");
		//service 暂时为permission_denied
//		qsc.getService();
	}
}
