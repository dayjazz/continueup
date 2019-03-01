package com.daychen.controller;

import com.daychen.entity.MultipartFileParam;
import com.daychen.entity.ResultStatus;
import com.daychen.entity.ResultVo;
import com.daychen.service.StorageService;
import com.daychen.utils.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/index/")
public class IndexController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StorageService storageService;

    @RequestMapping(value = "checkFileMd5", method = RequestMethod.POST)
    public Object checkFileMd5(String md5) throws IOException {
        //判断文件有没有传过
        Object processingObj = redisTemplate.opsForHash().get(Constants.FILE_UPLOAD_STATUS, md5);
        if (processingObj == null) {
            return new ResultVo(ResultStatus.NO_HAVE);
        }
        String processingStr = processingObj.toString();
        boolean processing = Boolean.parseBoolean(processingStr);
        Object value = redisTemplate.opsForValue().get(Constants.FILE_MD5_KEY + md5);
        //文件已存在
        if (processing) {
            return new ResultVo(ResultStatus.IS_HAVE, value);
        }else{  //文件没传完
            File confFile = new File(value.toString());
            byte[] completeList = FileUtils.readFileToByteArray(confFile);
            List<String> missChunkList = new LinkedList<>();
            for (int i = 0; i < completeList.length; i++) {
                if (completeList[i] != Byte.MAX_VALUE) {
                    missChunkList.add(i + "");
                }
            }
            return new ResultVo<>(ResultStatus.ING_HAVE, missChunkList);
        }
    }

    @RequestMapping(value = "fileUpload", method = RequestMethod.POST)
    public ResponseEntity fileUpload(MultipartFileParam param, HttpServletRequest request) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            try {
                storageService.uploadFileByMappedByteBuffer(param);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok().body("上传成功。");
    }

}
