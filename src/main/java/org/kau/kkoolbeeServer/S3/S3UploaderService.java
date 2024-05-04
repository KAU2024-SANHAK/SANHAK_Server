package org.kau.kkoolbeeServer.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class S3UploaderService {

    private final AmazonS3 amazonS3Client;
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    public S3UploaderService(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String upload(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        File convertedFile = convertMultiPartToFile(file);
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
        convertedFile.delete(); // 임시 파일 삭제

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File convertedFile = new File(tempDir,file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }
}
