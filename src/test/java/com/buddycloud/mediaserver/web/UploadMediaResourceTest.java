package com.buddycloud.mediaserver.web;

import static junit.framework.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;

public class UploadMediaResourceTest extends MediaResourceTest {
	
	public void testTearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + 
				File.separator + BASE_CHANNEL));
	}
	
	@Override
	protected void testSetUp() throws Exception {}
	
	@Test
	public void anonymousSuccessfulUpload() throws Exception {
		// file fields
		String title = "Test Image";
		String description = "My Test Image";
		
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL);
		
		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		form.getEntries().add(new FormData(Constants.NAME_FIELD, 
				new StringRepresentation(TESTFILE_NAME)));
		form.getEntries().add(new FormData(Constants.TITLE_FIELD, 
				new StringRepresentation(title)));	
		form.getEntries().add(new FormData(Constants.DESC_FIELD, 
				new StringRepresentation(description)));
		form.getEntries().add(new FormData(Constants.AUTHOR_FIELD, 
				new StringRepresentation(BASE_USER)));
		
		form.getEntries().add(new FormData(Constants.FILE_FIELD, 
				new FileRepresentation(TESTFILE_PATH, MediaType.IMAGE_JPEG)));
		
		Representation result = client.post(form);
		Media media = gson.fromJson(result.getText(), Media.class);
		
		// verify if resultant media has the passed attributes
		assertEquals(TESTFILE_NAME, media.getFileName());
		assertEquals(title, media.getTitle());
		assertEquals(description, media.getDescription());
		assertEquals(BASE_USER, media.getAuthor());
		
		// delete metadata
		dataSource.deleteMedia(media.getId());
	}

}
