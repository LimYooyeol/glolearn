package com.glolearn.newbook.service.image;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ImageService {

    public void findImage(String directory, String filename, HttpServletResponse response) throws IOException;

    public String saveImage(MultipartFile multipartFile, String directory, String filename) throws IOException, InterruptedException;
}
