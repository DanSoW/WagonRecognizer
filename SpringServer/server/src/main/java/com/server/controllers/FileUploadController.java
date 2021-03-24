package com.server.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {
	//класс-контроллер, для загрузки изображения на сервер
	
	private static final String nameDirectory = "Images"; //название директории, в которой будет находиться изображение

	@RequestMapping(value="/upload", method=RequestMethod.POST) //обработка POST-запроса загрузки изображения
	public @ResponseBody String handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file
			) {
		File f = new File(nameDirectory);
		if(!f.exists()) {
			f.mkdir();
		}
		
		if(!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				File f2 = new File(nameDirectory + "\\" + name);
				BufferedOutputStream stream = 
						new BufferedOutputStream(new FileOutputStream(new File(nameDirectory + "\\" + name)));
				stream.write(bytes);
                stream.close();
                return f2.getAbsolutePath();
			}catch(Exception e) {}
		}
		return "";
	}
}
