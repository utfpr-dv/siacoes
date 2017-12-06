package br.edu.utfpr.dv.siacoes.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;

public class ExtensionUtils {

	public void extendToDownload(String fileName, byte[] fileData, Button buttonToExtend){
		this.extendToDownload(fileName, fileData, buttonToExtend, true);
	}
	
	public void extendToDownload(String fileName, byte[] fileData, Button buttonToExtend, boolean removeExtensions){
		StreamResource sr = this.downloadFile(fileName, fileData);
    	FileDownloader fileDownloader = new FileDownloader(sr);
    	
    	if(removeExtensions){
	    	this.removeAllExtensions(buttonToExtend);
    	}
    	
    	fileDownloader.extend(buttonToExtend);
	}
	
	public void removeAllExtensions(Button button){
		while(button.getExtensions().size() > 0){
			button.removeExtension((Extension)button.getExtensions().toArray()[0]);
        }
	}
	
	private StreamResource downloadFile(String fileName, byte[] content){
		StreamResource.StreamSource source = new StreamResource.StreamSource() {
            public InputStream getStream() {
                InputStream input = new ByteArrayInputStream(content);
                return input;
            }
        };
        
        StreamResource resource = new StreamResource(source, fileName);
        
        return resource;
	}
	
}
