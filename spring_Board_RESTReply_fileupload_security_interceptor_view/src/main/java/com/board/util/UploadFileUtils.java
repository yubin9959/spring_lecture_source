package com.board.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

public class UploadFileUtils {
	
	private static final Logger logger=
			LoggerFactory.getLogger(UploadFileUtils.class);
	
	public static String calcPath(String uploadPath)throws Exception{
		Calendar cal=Calendar.getInstance();
		
		String yearPath=File.separator+cal.get(Calendar.YEAR);
		String monthPath=yearPath+File.separator+
				new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);
		String datePath=monthPath+File.separator+
				new DecimalFormat("00").format(cal.get(Calendar.DATE));
		
		makeDir(uploadPath,yearPath,monthPath,datePath);
		
		logger.info(datePath);
		
		return datePath;
	} 
	
	public static void makeDir(String uploadPath,String... paths){
		if(new File(paths[paths.length-1]).exists()){
			return;
		}
		for(String path:paths){
			File dirPath=new File(uploadPath+path);
			if(!dirPath.exists()){
				dirPath.mkdirs();
			}
		}
	}
	
	//아이콘 형태
	public static String makeIcon(String uploadPath,
								  String path,
								  String fileName) throws Exception{
		String iconName=uploadPath+path+File.separator+fileName;
		
		return iconName.substring(uploadPath.length()).
				replace(File.separatorChar, '/');
	}
	
	//썸네일 형태
	public static String makeThumbnail(String uploadPath,
									  String path,
									  String fileName)throws Exception{
		BufferedImage sourceImg=
				ImageIO.read(new File(uploadPath+path,fileName));
		
		BufferedImage destImg=Scalr.resize(sourceImg, 
								Scalr.Method.AUTOMATIC,
								Scalr.Mode.FIT_TO_HEIGHT,100);
		
		String thumbnailName=uploadPath+path+File.separator+"s_"+fileName;
		File newFile=new File(thumbnailName);
		String formatName=fileName.substring(fileName.lastIndexOf(".")+1);
		ImageIO.write(destImg, formatName.toUpperCase(),newFile);
		
		return thumbnailName.substring(uploadPath.length()).
				replace(File.separatorChar,'/');
	}
	
	
	//uploadFile 저장
	public static String uploadFile(String uploadPath,
									String originalName,
									byte[] fileData)throws Exception{
		
		//중복파일명 해결..
		UUID uid=UUID.randomUUID();
		String saveName=uid.toString().replace("-", "")+"_"+originalName;
				
		//저장 경로..
		String savePath=calcPath(uploadPath);
		
		//저장..
		File target=new File(uploadPath+savePath,saveName);
		FileCopyUtils.copy(fileData, target);
		
		logger.info(target.getAbsolutePath());
		
		//썸네일 이미지/파일...
		String uploadFileName=null;
		String formatName=originalName.substring(originalName.lastIndexOf(".")+1);
		if(MediaUtils.getMediaType(formatName)!=null){
			//썸네일 형태로 보여주기
			uploadFileName=makeThumbnail(uploadPath,savePath,saveName);
		}else{
			//텍스트 형태로 보여주기
			uploadFileName=makeIcon(uploadPath,savePath,saveName);
		}
		return uploadFileName;
		
	}
	
}







