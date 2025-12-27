package com.example.backend.Services;

import com.example.backend.Mappers.UserInfoMapper;
import com.example.backend.POJO.DO.UserInfoDO;
import com.example.backend.POJO.Result;
import com.example.backend.units.FileUploadUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("UserInfoService")
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Result getProfile(String Userid){
        Result result = new Result();
        UserInfoDO userInfoDO = userInfoMapper.selectByUserId(Userid);
        result.setData(userInfoDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("get Profile success");
        return result;
    }

    @Override
    public Result updateInfo(UserInfoDO userInfoDO){
        Result result = new Result();
        userInfoMapper.update(userInfoDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("update Profile success");
        return result;
    }

    private static final String BASE_UPLOAD_DIR = "../assets/";

    @Override
    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("The file is empty.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only supports image format");
        }

        LocalDate now = LocalDate.now();
        String relativeDir = now.getYear() + "/" + String.format("%02d", now.getMonthValue()) + "/";

        Path uploadDir = Paths.get(BASE_UPLOAD_DIR, relativeDir);
        Files.createDirectories(uploadDir);

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }

        byte[] fileBytes = file.getBytes();
        String hash = FileUploadUtils.bytesToHex(FileUploadUtils.sha256(fileBytes));
        String fileName = "avatar_" + hash.substring(0, 12) + ext;

        Path filePath = uploadDir.resolve(fileName);

        if (!Files.exists(filePath)) {
            Files.write(filePath, fileBytes);
        }

        return "http://localhost:8080/assets/" + relativeDir + fileName;
    }
}

