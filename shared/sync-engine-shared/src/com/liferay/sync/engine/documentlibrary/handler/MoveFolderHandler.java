/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.sync.engine.documentlibrary.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.sync.engine.documentlibrary.event.Event;
import com.liferay.sync.engine.model.SyncFile;
import com.liferay.sync.engine.service.SyncFileService;
import com.liferay.sync.engine.util.FilePathNameUtil;

/**
 * @author Shinn Lok
 */
public class MoveFolderHandler extends BaseJSONHandler {

	public MoveFolderHandler(Event event) {
		super(event);
	}

	@Override
	protected void processResponse(String response) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		SyncFile remoteSyncFile = objectMapper.readValue(
			response, new TypeReference<SyncFile>() {});

		SyncFile parentLocalSyncFile = SyncFileService.fetchSyncFile(
			remoteSyncFile.getParentFolderId(),
			remoteSyncFile.getRepositoryId(), getSyncAccountId());

		String filePathName = null;

		if (parentLocalSyncFile != null) {
			filePathName = FilePathNameUtil.getFilePathName(
				parentLocalSyncFile.getFilePathName(),
				remoteSyncFile.getName());
		}

		SyncFile localSyncFile = (SyncFile)getParameterValue("syncFile");

		localSyncFile.setFilePathName(filePathName);
		localSyncFile.setModifiedTime(remoteSyncFile.getModifiedTime());

		SyncFileService.update(localSyncFile);
	}

}