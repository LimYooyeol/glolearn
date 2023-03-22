package com.glolearn.newbook.service.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class AwsS3Service implements ImageService {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    // 이미지 불러오기
    public void findImage(String directory, String filename, HttpServletResponse response) throws IOException {
        InputStream inputStream = amazonS3Client.getObject(bucket, directory + "/" + filename + ".PNG").getObjectContent();
        IOUtils.copy(inputStream, response.getOutputStream());
        inputStream.close();
    }

    // 이미지 저장하기
    // https://stackoverflow.com/questions/50248710/upload-multipart-file-to-aws-without-saving-it-locally
    public String saveImage(MultipartFile multipartFile, String directory, String filename) throws IOException, InterruptedException {
        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(multipartFile.getContentType());
        data.setContentLength(multipartFile.getSize());
        amazonS3Client.putObject(bucket, directory + "/" + filename + ".PNG", multipartFile.getInputStream(), data);

        multipartFile.getInputStream().close();

        return directory + "/" + filename;
    }
}
