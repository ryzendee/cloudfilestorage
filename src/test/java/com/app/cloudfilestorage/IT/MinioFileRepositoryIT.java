package com.app.cloudfilestorage.IT;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.exception.MinioObjectExistsException;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioFileRepository;
import io.minio.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

public class MinioFileRepositoryIT extends ITBase {

    @Autowired
    private MinioFileRepository minioFileRepository;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @BeforeEach
    void setUp() throws Exception {
        deleteAll();
    }

    @DisplayName("Find all by path: should return files from folder by path")
    @Test
    void findAllFiles_withFileInSubfolder_shouldReturnOnlyFromFolderInPath() throws Exception {
        String rootFolder = "folder/";
        MultipartFile firstFile = getMockMultipartFileWithName(rootFolder + "test.txt");
        MultipartFile secondFile = getMockMultipartFileWithName(rootFolder + "subfolder/test.txt");
        putFile(firstFile);
        putFile(secondFile);

        List<MinioObject> fileList = minioFileRepository.findAllFilesByPath(rootFolder);
        assertThat(fileList).extracting(MinioObject::getPath)
                .containsExactlyInAnyOrder(firstFile.getOriginalFilename());
    }

    @DisplayName("Find all by path recursive: should return files from all folders by path")
    @Test
    void findAllFilesByPathRecursive_existsFiles_shouldReturnListOfFiles() throws Exception {
        String rootFolder = "folder/";
        MultipartFile firstFile = getMockMultipartFileWithName(rootFolder + "test.txt");
        MultipartFile secondFile = getMockMultipartFileWithName(rootFolder + "subfolder/test.txt");
        putFile(firstFile);
        putFile(secondFile);

        List<MinioObject> fileList = minioFileRepository.findAllFilesByPathRecursive(rootFolder);
        assertThat(fileList).extracting(MinioObject::getPath)
                .containsExactlyInAnyOrder(firstFile.getOriginalFilename(), secondFile.getOriginalFilename());

    }

    @DisplayName("Save: should save file")
    @Test
    void saveFile_fileNotExists_shouldSave() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("folder/test.txt");
        MinioSaveDataDto saveDataDto = getMinioSaveDataFromMultipartFile(mockFile);

        minioFileRepository.saveFile(saveDataDto);

        List<Item> actualFiles = findAllWithList();
        assertThat(actualFiles).extracting(Item::objectName)
                .containsExactly(mockFile.getOriginalFilename());
    }

    @DisplayName("Save: throws ex because file with this objectName already exists")
    @Test
    void saveFile_existsFile_throwsMinioObjectExistsEx() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("folder/test.txt");
        putFile(mockFile);
        MinioSaveDataDto saveDataDto = getMinioSaveDataFromMultipartFile(mockFile);

        assertThatThrownBy(() -> minioFileRepository.saveFile(saveDataDto))
                .isInstanceOf(MinioObjectExistsException.class);
    }

    @DisplayName("Delete by name: file exists")
    @Test
    void deleteFileByObjectName_existsFile_shouldDelete() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("folder/test.txt");
        putFile(mockFile);

        minioFileRepository.deleteFileByObjectName(mockFile.getOriginalFilename());

        List<Item> actualFiles = findAllWithList();
        assertThat(actualFiles).isEmpty();
    }

    @DisplayName("Delete by objectName: file does not exists but exception should not be thrown")
    @Test
    void deleteFileByObjectName_nonExistsFile_shouldNotThrowEx() {
        minioFileRepository.deleteFileByObjectName("folder/test.txt");
    }

    @DisplayName("Download by objectName: file bytes and stream bytes should be equals")
    @Test
    void downloadFileByObjectName_existsFile_shouldDownloadFile() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("folder/test.txt");
        putFile(mockFile);

        try (InputStream fileStream = minioFileRepository.downloadFileByObjectName(mockFile.getOriginalFilename())) {
            assertThat(mockFile.getBytes()).isEqualTo(fileStream.readAllBytes());
        }
    }

    @DisplayName("Download by objectName: throws ex because file does not exists")
    @Test
    void downloadFileByObjectName_nonExistsFile_throwsMinioRepositoryEx() {
        assertThatThrownBy(() -> {
            InputStream fileStream = minioFileRepository.downloadFileByObjectName("folder/test.txt");

            if (fileStream != null) {
                fileStream.close();
                fail("Exists input stream, method must throw MinioRepositoryException because file nto found");
            }

        }).isInstanceOf(MinioRepositoryException.class);
    }

    @DisplayName("Rename: should rename file")
    @Test
    void renameFile_existsFile_shouldRenameFile() throws Exception {
        String currentFileName = "folder/file.txt";
        String updatedFileName = "folder/updated.txt";
        MultipartFile mockFile = getMockMultipartFileWithName(currentFileName);
        putFile(mockFile);

        minioFileRepository.renameFile(currentFileName, updatedFileName);

        List<Item> actualFiles = findAllWithList();
        assertThat(actualFiles).extracting(Item::objectName)
                .containsExactly(updatedFileName);
    }

    @DisplayName("Rename: throws ex because file with this name already exists")
    @Test
    void renameFile_existsName_throwsMinioObjectNameExistsEx() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("folder/test.txt");
        putFile(mockFile);

        assertThatThrownBy(() -> minioFileRepository.renameFile(mockFile.getOriginalFilename(), mockFile.getOriginalFilename()))
                .isInstanceOf(MinioObjectExistsException.class);
    }

    @DisplayName("Rename test: throws ex because file does not exists")
    @Test
    void renameFile_nonExistsFile_throwsMinioRepositoryException() {
        String currentFileName = "folder/file.txt";

        assertThatThrownBy(() -> minioFileRepository.renameFile(currentFileName, currentFileName))
                .isInstanceOf(MinioRepositoryException.class);
    }

    private void deleteAll() throws Exception {
        Iterable<Result<Item>> minioImages = findAllWithIterable();

        for (Result<Item> item : minioImages) {
            String objectName = item.get().objectName();
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        }
    }

    private List<Item> findAllWithList() throws Exception {
        var iterable = findAllWithIterable();

        List<Item> items = new ArrayList<>();
        for (var result : iterable) {
            items.add(result.get());
        }

        return items;
    }

    private Iterable<Result<Item>> findAllWithIterable() throws Exception {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build()
        );
    }

    private MinioSaveDataDto getMinioSaveDataFromMultipartFile(MultipartFile file) throws IOException {
        return new MinioSaveDataDto(file.getOriginalFilename(), file.getInputStream(), file.getSize());
    }

    private void putFile(MultipartFile file) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(file.getOriginalFilename())
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .build()
        );
    }

    private MultipartFile getMockMultipartFileWithName(String name) {
        return new MockMultipartFile(
                "file",
                name,
                "text/plain",
                "Content".getBytes(StandardCharsets.UTF_8)
        );
    }
}
