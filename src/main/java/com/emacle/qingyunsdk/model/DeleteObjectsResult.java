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

package com.emacle.qingyunsdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Successful response for deleting multiple objects.
 */
public class DeleteObjectsResult {
	
	/* Successfully deleted objects */
	private final List<String> deletedObjects = new ArrayList<String>();
	
	/* User specified encoding method to be applied on the response. */
	private String encodingType;
	
	public DeleteObjectsResult() { }
	
	public DeleteObjectsResult(List<String> deletedObjects) {
		if (deletedObjects != null && deletedObjects.size() > 0) {
			this.deletedObjects.addAll(deletedObjects);
		}
	}
	
	public List<String> getDeletedObjects() {
		return deletedObjects;
	}
	
	public void setDeletedObjects(List<String> deletedObjects) {
		this.deletedObjects.clear();
		this.deletedObjects.addAll(deletedObjects);
	}

	public String getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}
}