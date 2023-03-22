package com.glolearn.newbook.service.image;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class LocalService implements ImageService{

    @Override
    public void findImage(String directory, String filename, HttpServletResponse response) throws IOException {
        File image = new File("C:/Users/duf70/OneDrive/바탕 화면/glo_image/" + directory + "/" + filename + ".PNG");

        FileInputStream fileInputStream = new FileInputStream(image);
        IOUtils.copy(fileInputStream, response.getOutputStream());
        fileInputStream.close();
        response.getOutputStream().close();
    }

    @Override
    public String saveImage(MultipartFile multipartFile, String directory, String filename) throws IOException, InterruptedException {

        File base = new File("C:/Users/duf70/OneDrive/바탕 화면/glo_image/" + directory);
        if(!base.exists()) {base.mkdir();}

        File file = new File(base + "/" + filename + ".PNG");

        multipartFile.transferTo(file);

        return directory + "/" + filename;
    }
}
