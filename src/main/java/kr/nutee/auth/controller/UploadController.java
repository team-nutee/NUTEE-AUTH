package kr.nutee.auth.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import kr.nutee.auth.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RefreshScope
@RequestMapping(path = "/auth/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class UploadController {

    private final S3Service s3Service;

    /*
        이미지 S3에 업로드
     */
    @PostMapping("")
    public List<String> uploadImages(MultipartHttpServletRequest mtfRequest) {
        List<MultipartFile> fileList = mtfRequest.getFiles("images");
        List<String> srcList = new ArrayList<>();
        for (MultipartFile files : fileList) {
            try {
                String imgPath = s3Service.upload(files);
                srcList.add(imgPath);
            } catch (IllegalStateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
        return srcList;
    }
}
