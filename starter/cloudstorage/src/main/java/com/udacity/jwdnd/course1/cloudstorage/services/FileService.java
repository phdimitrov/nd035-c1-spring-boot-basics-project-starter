package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public boolean isFileNameAvailable(String fileName, Integer userId) {
        final List<File> files = fileMapper.findAllMetaByFileNameAndUserId(fileName, userId);
        return files == null || files.isEmpty();
    }

    public List<File> getFilesMetaByUserId(Integer userId) {
        return fileMapper.findAllMetaByUserId(userId);
    }

    public File getFile(Integer fileId) {
        return fileMapper.findByFileId(fileId);
    }

    public void saveFile(File file) {
        fileMapper.insert(file);
    }

    public void deleteFile(Integer fileId) {
        fileMapper.delete(fileId);
    }

}
