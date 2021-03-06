package com.server.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.database.elements.DataElementImageFilePath;

//********************************************************************
//Контроллер для загрузки изображений с сервера на клиент и с клиента на сервер
//********************************************************************

@RestController
public class FileLoadController {
	
	//Название директории, хранящейся на сервере, где будут сохранены изображения загруженные с клиентской части приложения
	public static final String nameDirectory = "Images"; 

	
	//Загрузка изображений с клиентской части приложения на сервер
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public @ResponseBody String handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file
			) throws Exception {
		File f = new File(nameDirectory);
		if(!f.exists()) {
			f.mkdir();
		}
		
		if(!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				File fileData = new File(nameDirectory + "\\" + name);
				BufferedOutputStream stream = 
						new BufferedOutputStream(new FileOutputStream(fileData));
				stream.write(bytes);
                stream.close();
                return fileData.getAbsolutePath();
			}catch(Exception e) {
				throw new Exception("Не удалось загрузить файл!");
			}
		}
		
		throw new Exception("Нет входных данных!");
	}
	
	//Загрузка изображения с сервера на клиентскую часть приложения
	@RequestMapping(value = "/load/{file_name:.+}", method = RequestMethod.GET)
    public void getFile(@PathVariable("file_name") String fileName, HttpServletResponse response) throws Exception {
		File file = new File(nameDirectory + "\\" + fileName);
        if ((file.isFile()) && (file.exists())){
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            response.setContentType("multipart/form-data");
 
            try {
                Files.copy(file.toPath(), response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException e) {
                throw new Exception("Не удалось загрузить файл!");
            }
        }
        
        throw new Exception("Файла с данным именем нет на сервере!");
    }
	
	//Возвращает абсолютный путь к файлу, сохраненному на сервере или ошибку, в случае не существования файла на сервере
	@RequestMapping(value = "/load/filepath", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataElementImageFilePath getAbsoluteFilePath(@RequestParam String fileName) throws Exception {
		File file = new File(nameDirectory + "\\" + fileName);
        if (file.exists()){
            return new DataElementImageFilePath(file.getAbsolutePath());
        }
        
        throw new Exception("Файла с данным именем нет на сервере!");
    }
}
