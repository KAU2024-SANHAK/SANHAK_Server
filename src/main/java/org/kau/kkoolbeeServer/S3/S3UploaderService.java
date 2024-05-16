package org.kau.kkoolbeeServer.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

   /* public String upload(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        File convertedFile = convertMultiPartToFile(file);
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
        convertedFile.delete(); // 임시 파일 삭제

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }*/
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String fileHash = calculateFileHash(file);
        String fileName = fileHash + "-" + file.getOriginalFilename();

        if (!amazonS3Client.doesObjectExist(bucketName, fileName)) {
            File convertedFile = convertMultiPartToFile(file);
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
            convertedFile.delete();
        }

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    private String calculateFileHash(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = file.getBytes();
        byte[] hash = digest.digest(fileBytes);
        return Hex.encodeHexString(hash);
    }




    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File convertedFile = new File(tempDir,file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }
    /*public void deleteFileFromS3(String fileUrl) {
        // URL에서 파일 이름 추출
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        // 파일 삭제
        amazonS3Client.deleteObject(bucketName, fileName);

    }*/
}
