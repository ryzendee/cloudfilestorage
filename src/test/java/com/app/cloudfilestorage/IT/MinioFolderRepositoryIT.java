package com.app.cloudfilestorage.IT;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.exception.MinioObjectExistsException;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioFolderRepository;
import io.minio.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MinioFolderRepositoryIT extends ITBase {

    @Autowired
    private MinioFolderRepository minioFolderRepository;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @BeforeEach
    void setUp() throws Exception {
        deleteAll();
    }


    //TODO
    @DisplayName("Create empty folder: folder name already exists")
    @Test
    void createEmptyFolder_existsFolderName_throwMinioObjectExistsEx() throws Exception {
        String folderName = "folder/";
        putEmptyFolderWithName(folderName);

        assertThatThrownBy(() -> minioFolderRepository.createEmptyFolder(folderName))
                .isInstanceOf(MinioObjectExistsException.class);
    }

    @DisplayName("Create empty folder: should create only single empty folder")
    @Test
    void createEmptyFolder_nonExistsFolderName_shouldCreateFolder() throws Exception {
        String folderName = "folder/subfolder/";
        minioFolderRepository.createEmptyFolder(folderName);

        List<Item> actualFiles = findAllWithList();
        assertThat(actualFiles).hasSize(1);

        Item item = actualFiles.getFirst();
        assertThat(item.objectName()).isEqualTo(folderName);
    }

    @DisplayName("Save all: should save files")
    @Test
    void saveAll_nonExistsFolder_shouldSaveFiles() throws Exception {
        MinioSaveDataDto firstFile = getMinioSaveDataWithName("folder/first.txt");
        MinioSaveDataDto secondFile = getMinioSaveDataWithName("folder/second.txt");
        MinioSaveDataDto thirdFile = getMinioSaveDataWithName("folder/third.txt");
        List<MinioSaveDataDto> folderWithFiles = List.of(firstFile, secondFile, thirdFile);

        minioFolderRepository.saveAll(folderWithFiles);

        List<Item> actualFiles = findAllWithList();
        assertThat(actualFiles).extracting(Item::objectName)
                .containsExactlyInAnyOrder(firstFile.objectName(), secondFile.objectName(), thirdFile.objectName());
    }

    @DisplayName("Delete folder by path: should delete folder")
    @Test
    void deleteFolderByPath_existsFolderWithFiles_shouldDelete() throws Exception {
        MultipartFile firstFile = getMockMultipartFileWithName("folder/subfolder/text.txt");
        MultipartFile secondFile = getMockMultipartFileWithName("folder/second-file.txt");
        List<MultipartFile> fileList = List.of(firstFile, secondFile);
        putListOfFiles(fileList);

        minioFolderRepository.deleteFolderByPath("folder/");

        List<Item> actualFiles = findAllWithList();
        assertThat(actualFiles).isEmpty();
    }

    @DisplayName("Rename folder: should rename folder")
    @Test
    void renameFolder_nonExistsFolderName_shouldRenameFolder() throws Exception {
        MultipartFile firstFile = getMockMultipartFileWithName("folder/oldfolder/first.txt");
        putFile(firstFile);

        minioFolderRepository.renameFolder("folder/oldfolder/", "/folder/updatedFolder/");

        List<Item> actualFiles = findAllWithList();
        assertThat(actualFiles).extracting(Item::objectName)
                .containsExactly("folder/updatedFolder/first.txt");
    }

    @DisplayName("Rename folder: throws ex because folder name already exists")
    @Test
    void renameFolder_nonExistsFolderName_throwsMinioObjectExistsEx() throws Exception {
        MultipartFile firstFile = getMockMultipartFileWithName("folder/oldfolder/first.txt");
        putFile(firstFile);

        assertThatThrownBy(() -> minioFolderRepository.renameFolder("folder/oldfolder/", "/folder/oldfolder/"))
                .isInstanceOf(MinioObjectExistsException.class);

    }

    @DisplayName("Find all folders by path: should return only folders")
    @Test
    void findAllFolderByPath_existsFolders_shouldReturnOnlyFolders() throws Exception {
        MultipartFile firstFile = getMockMultipartFileWithName("folder/first-subfolder/first.txt");
        MultipartFile secondFile = getMockMultipartFileWithName("folder/second.txt");
        List<MultipartFile> fileList = List.of(firstFile, secondFile);
        putListOfFiles(fileList);

        List<MinioObject> actualFolders = minioFolderRepository.findAllFoldersByPath("folder/");
        assertThat(actualFolders).hasSize(1);

        MinioObject folder = actualFolders.getFirst();
        assertThat(folder.getPath()).isEqualTo("folder/first-subfolder/");
    }

//    @DisplayName("Find all folders by path recursive: should return only folders")
//    @Test
//    void findAllFolderByPathRecursive_existsFolders_shouldReturnOnlyFolders() throws Exception {
//        MultipartFile firstFile = getMockMultipartFileWithName("folder/first-subfolder/first.txt");
//        MultipartFile secondFile = getMockMultipartFileWithName("folder/second.txt");
//        MultipartFile thirdFile = getMockMultipartFileWithName("folder/first-subfolder/second-subfolder/third.txt");
//        List<MultipartFile> fileList = List.of(firstFile, secondFile, thirdFile);
//        putListOfFiles(fileList);
//
//        List<MinioObject> actualFolders = minioFolderRepository.findAllFoldersByPathRecursive("folder/");
//        assertThat(actualFolders).hasSize(2);
//        assertThat(actualFolders).extracting(MinioObject::getPath)
//                .containsExactlyInAnyOrder("folder/first-subfolder/", "folder/first-subfolder/second-subfolder/");
//    }

//    @DisplayName("Download folder by path: should return stream with files")
//    @Test
//    void downloadFolderByPath_existsFolderInPath_shouldDownload() throws Exception {
//        String firstFilename = "folder/subfolder/first-file";
//        String secondFileName = "folder/subfolder/second-file";
//        MultipartFile firstFile = getMockMultipartFileWithName(firstFilename);
//        MultipartFile secondFile = getMockMultipartFileWithName(secondFileName);
//        List<MultipartFile> fileList = List.of(firstFile, secondFile);
//        putListOfFiles(fileList);
//
//        ByteArrayOutputStream folderOutputStream = minioFolderRepository.downloadFolderByPath("folder/");
//
//        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(folderOutputStream.toByteArray()))) {
//            ZipEntry entry;
//            while ((entry = zis.getNextEntry()) != null) {
//                if (firstFilename.equals(entry.getName())) {
//                    byte[] buffer = new byte[1024];
//                    int length = zis.read(buffer);
//                    byte[] actualContent = new byte[length];
//                    System.arraycopy(buffer, 0, actualContent, 0, length);
//                    assertThat(firstFile.getBytes()).containsOnly(actualContent);
//                } else if (secondFileName.equals(entry.getName())) {
//                    byte[] buffer = new byte[1024];
//                    int length = zis.read(buffer);
//                    byte[] actualContent = new byte[length];
//                    System.arraycopy(buffer, 0, actualContent, 0, length);
//                    assertThat(secondFile.getBytes()).containsOnly(actualContent);
//                } else {
//                    fail("Unexpected file in zip: " + entry.getName());
//                }
//                zis.closeEntry();
//            }
//        }
//    }


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

    private void putListOfFiles(List<MultipartFile> fileList) throws Exception {
        for (MultipartFile file : fileList) {
            putFile(file);
        }
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

    private void putEmptyFolderWithName(String name) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(name)
                        .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                        .build()
        );
    }

    private MinioSaveDataDto getMinioSaveDataWithName(String objectName) {
       return new MinioSaveDataDto(objectName, new ByteArrayInputStream(new byte[0]), 0);
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
