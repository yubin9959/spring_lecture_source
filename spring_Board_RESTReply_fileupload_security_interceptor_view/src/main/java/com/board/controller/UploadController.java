package com.board.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.board.util.MediaUtils;
import com.board.util.UploadFileUtils;

@Controller
public class UploadController {
	
	private static final Logger logger=
			LoggerFactory.getLogger(UploadController.class);
			
	
	@Resource(name="uploadPath")
	private String uploadPath;
	
	@RequestMapping(value="/uploadForm",method=RequestMethod.GET)
	public void uploadForm(){};
	
	@RequestMapping(value="/uploadForm",method=RequestMethod.POST)
	public String upload(MultipartFile file,Model model)throws Exception{
		
		logger.info("originalName : "+file.getOriginalFilename());
		logger.info("size : "+file.getSize());
		logger.info("contentType : "+file.getContentType());
		
		String saveName=uploadFile(file.getOriginalFilename(),file.getBytes());
		model.addAttribute("saveName",saveName);
		return "uploadResult";		
	}
	
	private String uploadFile(String originalName,byte[] fileData)
									throws Exception{
		UUID uid=UUID.randomUUID();
		String saveName=uid.toString().replace("-", "")
						+"_"+originalName;
		File target=new File(uploadPath,saveName);
		FileCopyUtils.copy(fileData, target);
		
		return saveName;
	}
	
	@RequestMapping(value="/uploadAjax", method=RequestMethod.GET)
	public void uploadAjax(){}
	
	@RequestMapping(value="/uploadAjax", method=RequestMethod.POST,
					produces="text/plain;charset=utf-8")
	@ResponseBody
	public ResponseEntity<String> uploadAjax(MultipartFile file)
							throws Exception{
		
		logger.info("originalName : "+file.getOriginalFilename());
		logger.info("size : "+file.getSize());
		logger.info("contentType : "+file.getContentType());
		
		return new ResponseEntity<String>(
							UploadFileUtils.uploadFile(uploadPath, 
									file.getOriginalFilename(), 
									file.getBytes()),
							HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/displayFile",method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> displayFile(String fileName)
										throws Exception{
		
		InputStream in=null;
		ResponseEntity<byte[]> entity=null;
		
		logger.info("File name : "+fileName);
		
		try{
			String formatName=fileName.substring(fileName.lastIndexOf(".")+1);
			MediaType mType=MediaUtils.getMediaType(formatName);
			HttpHeaders headers=new HttpHeaders();
			
			fileName=fileName.replace('/', File.separatorChar);
			in=new FileInputStream(uploadPath+fileName);
			
			if(mType!=null){
				headers.setContentType(mType);
			}else{
				fileName=fileName.substring(fileName.indexOf("_")+1);
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.add("Content-Disposition","attachment;filename=\""
							+new String(fileName.getBytes("utf-8"),"ISO-8859-1")
							+"\"");				
			}
			
			entity=new ResponseEntity<byte[]>(IOUtils.toByteArray(in),
					headers,HttpStatus.CREATED);
		}catch(IOException e){
			e.printStackTrace();
			entity=new ResponseEntity<byte[]>
							(HttpStatus.INTERNAL_SERVER_ERROR);
		}finally{
			in.close();
		}
		return entity;
	}
	
	@RequestMapping(value="/deleteFile",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteFile(String fileName)
										throws Exception{
		
		logger.info("delete fileName : "+fileName);
		
		String formatName=fileName.substring(fileName.lastIndexOf(".")+1);
		MediaType mType=MediaUtils.getMediaType(formatName);
		
		if(mType!=null){
			String front=fileName.substring(0, 12);
			String end=fileName.substring(14);
			new File(uploadPath+(front+end).replace('/', File.separatorChar)).delete();			
		}
		new File(uploadPath+fileName.replace('/', File.separatorChar)).delete();
		
		return new ResponseEntity<String>("deleted",HttpStatus.OK);
	}
	
	@RequestMapping(value="/deleteAllFiles",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteFiles(@RequestParam("files[]") String[] files)
										throws Exception{
		
		logger.info("delete all files: "+files);
		
		if(files==null || files.length==0){
			return new ResponseEntity<String>("deleted",HttpStatus.OK);
		}
		
		for(String fileName:files){
			String formatName=
					fileName.substring(fileName.lastIndexOf(".")+1);
			MediaType mType=MediaUtils.getMediaType(formatName);
			
			if(mType!=null){
				String front=fileName.substring(0, 12);
				String end=fileName.substring(14);
				new File(uploadPath+(front+end).replace('/',File.separatorChar))
											   .delete();				
			}
			
			new File(uploadPath+fileName.replace('/',File.separatorChar))
			                                   .delete();
			
		}
		return new ResponseEntity<String>("deleted",HttpStatus.OK);
	}
}







