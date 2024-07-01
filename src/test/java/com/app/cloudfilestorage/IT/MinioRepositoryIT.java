package com.app.cloudfilestorage.IT;

import com.app.cloudfilestorage.dto.MinioSaveDataDto;
import com.app.cloudfilestorage.exception.MinioRepositoryException;
import com.app.cloudfilestorage.models.MinioObject;
import com.app.cloudfilestorage.repository.MinioRepository;
import io.minio.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
public class MinioRepositoryIT {

    private static final String POSTGRES_IMAGE = "postgres:15.6";

    private static final String MINIO_IMAGE = "minio/minio:RELEASE.2024-02-17T01-15-57Z.fips";
    private static final String MINIO_ENDPOINT = "minio.endpoint";
    private static final String MINIO_USERNAME = "minio.username";
    private static final String MINIO_PASSWORD = "minio.password";

    private static final String BASE_PATH = "users-1-files/";
    @Autowired
    private MinioRepository minioRepository;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Container
    static final MinIOContainer minIOContainer = new MinIOContainer(MINIO_IMAGE);

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE);


    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add(MINIO_ENDPOINT, minIOContainer::getS3URL);
        registry.add(MINIO_USERNAME, minIOContainer::getUserName);
        registry.add(MINIO_PASSWORD, minIOContainer::getPassword);
    }

    @BeforeEach
    void setUp() throws Exception {
        //Getting and deleting all files in bucket
        Iterable<Result<Item>> minioImages = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build()
        );

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

    @Test
    void createEmptyFolder_validPath_shouldCreateEmptyFolder() throws Exception {
        String folderName = "folder/";
        String testPath = BASE_PATH + folderName;
        minioRepository.createEmptyFolder(testPath);

        Iterable<Result<Item>> resultIterable = getAll(BASE_PATH);

        Result<Item> resultItem = resultIterable.iterator().next();
        Item folder = resultItem.get();
        assertThat(folder.isDir()).isTrue();
        assertThat(folder.objectName()).isEqualTo(testPath);
    }



    @Test
    void downloadByPath_existsObject_shouldDownload() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("test-filename");
        putFile(mockFile);

        InputStream inputStream = minioRepository.downloadByPath(mockFile.getOriginalFilename());
        assertThat(inputStream).isNotEmpty();
    }

    @Test
    void downloadByPath_nonExistsObject_throwsMinioRepositoryEx() throws Exception {
        MultipartFile mockFile = getMockMultipartFileWithName("test-filename");
        putFile(mockFile);

        assertThatThrownBy(() -> minioRepository.downloadByPath("invalid-path"))
                .isInstanceOf(MinioRepositoryException.class);
    }

    @Test
    void saveAll_nonEmptyList_shouldSaveAll() throws Exception {
        MinioSaveDataDto firstMinioSaveDataDto = getSaveObjectWithName(BASE_PATH + "first");
        MinioSaveDataDto secondMinioSaveDataDto = getSaveObjectWithName(BASE_PATH + "second");
        minioRepository.saveAll(List.of(firstMinioSaveDataDto, secondMinioSaveDataDto));

        Iterable<Result<Item>> iterable = getAll(BASE_PATH);
        for (Result<Item> itemResult : iterable) {
            Item item = itemResult.get();
            assertThat(item.objectName()).isIn(firstMinioSaveDataDto.objectName(), secondMinioSaveDataDto.objectName());
        }
    }

    @Test
    void save_withObject_shouldSave() throws Exception {
        String objectName = BASE_PATH + "test";
        MinioSaveDataDto minioSaveDataDto = getSaveObjectWithName(objectName);
        minioRepository.saveObject(minioSaveDataDto);

        Iterable<Result<Item>> iterable = getAll(BASE_PATH);

        for (Result<Item> itemResult : iterable) {
            Item item = itemResult.get();
            assertThat(item.objectName()).isEqualTo(minioSaveDataDto.objectName());
        }
    }

    @Test
    void deleteAllRecursive() throws Exception {
        String prefix = "folder/";
        MultipartFile mockFile = getMockMultipartFileWithName(prefix + "/subfolder/" + "test-filename");
        putFile(mockFile);

        minioRepository.deleteAllRecursive(prefix);
        Iterable<Result<Item>> iterable = getAll(prefix);

        boolean hasNext = iterable.iterator().hasNext();
        assertThat(hasNext).isFalse();
    }

    @Test
    void renameAllRecursive() throws Exception {
        String oldPath = "folder/";
        String updatedPath = "updated-folder/";
        MultipartFile mockFile = getMockMultipartFileWithName(oldPath + "text.txt");
        putFile(mockFile);

        minioRepository.renameAllRecursive(oldPath, updatedPath);

        Iterable<Result<Item>> oldIterable = getAll(oldPath);
        boolean hasNextOnOldPath = oldIterable.iterator().hasNext();
        assertThat(hasNextOnOldPath).isFalse();

        Iterable<Result<Item>> updated = getAll(updatedPath);
        Item item = updated.iterator().next().get();
        assertThat(item.objectName()).isEqualTo(updatedPath + "text.txt");
    }

    @Test
    void downloadByPathAll_existsFolderInPath_shouldDownload() throws Exception {
        String firstFilename = "folder/subfolder/first-file";
        String secondFileName = "folder/subfolder/second-file";
        MultipartFile firstMockFile = getMockMultipartFileWithName(firstFilename);
        MultipartFile secondMockFile = getMockMultipartFileWithName(secondFileName);
        List<MultipartFile> files = List.of(firstMockFile, secondMockFile);
        putListOfFiles(files);

        byte[] bytes = minioRepository.downloadByPathAll(BASE_PATH, "folder");
        assertThat(bytes.length).isGreaterThan(0);

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (firstFilename.equals(entry.getName())) {
                    byte[] buffer = new byte[1024];
                    int length = zis.read(buffer);
                    byte[] actualContent = new byte[length];
                    System.arraycopy(buffer, 0, actualContent, 0, length);
                    assertThat(firstMockFile.getBytes()).containsOnly(actualContent);
                } else if (secondFileName.equals(entry.getName())) {
                    byte[] buffer = new byte[1024];
                    int length = zis.read(buffer);
                    byte[] actualContent = new byte[length];
                    System.arraycopy(buffer, 0, actualContent, 0, length);
                    assertThat(secondMockFile.getBytes()).containsOnly(actualContent);
                } else {
                    fail("Unexpected file in zip: " + entry.getName());
                }
                zis.closeEntry();
            }
        }
    }

    @Test
    void findAll_existsFiles_returnsListWithMinioObjects() throws Exception {
        String path = "folder/subfolder/";
        MultipartFile firstMockFile = getMockMultipartFileWithName(path + "first-file");
        MultipartFile secondMockFile = getMockMultipartFileWithName(path + "second-file");
        List<MultipartFile> files = List.of(firstMockFile, secondMockFile);
        putListOfFiles(files);

        List<MinioObject> minioObjectList = minioRepository.findAll(path);
        assertThat(minioObjectList).hasSize(files.size());

        for (MinioObject minioObject : minioObjectList) {
            boolean found = files.stream()
                    .map(MultipartFile::getOriginalFilename)
                    .anyMatch(fileName -> fileName.equals(minioObject.getPath()));
            assertThat(found).isTrue();
        }
    }

    private void putListOfFiles(List<MultipartFile> files) throws Exception {
        List<SnowballObject> snowballObjectList = mapListOfFilesToSnowballObjects(files);

        minioClient.uploadSnowballObjects(
                UploadSnowballObjectsArgs.builder()
                        .bucket(bucketName)
                        .objects(snowballObjectList)
                        .build()
        );
    }

    private List<SnowballObject> mapListOfFilesToSnowballObjects(List<MultipartFile> files) throws Exception {
        List<SnowballObject> snowballObjectList = new ArrayList<>();
        for (MultipartFile file : files) {
            SnowballObject snowballObject = new SnowballObject(
                    Objects.requireNonNull(file.getOriginalFilename()),
                    file.getInputStream(),
                    file.getSize(),
                    null
            );
            snowballObjectList.add(snowballObject);
        }

        return snowballObjectList;
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

    private Iterable<Result<Item>> getAll(String prefix) throws Exception {
        return minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(bucketName)
                        .prefix(prefix)
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

    private MinioSaveDataDto getSaveObjectWithName(String name) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(name.getBytes());
        return new MinioSaveDataDto(
                name,
                inputStream,
                inputStream.available()
        );
    }

}

