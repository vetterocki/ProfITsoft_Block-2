package org.example.block2.service;

import org.example.block2.web.dto.UploadStatistics;
import org.springframework.web.multipart.MultipartFile;

public interface JsonUploader {
  UploadStatistics uploadJson(MultipartFile json);
}
