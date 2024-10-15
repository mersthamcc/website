package cricket.merstham.website.frontend.service;

import com.cksource.ckfinder.config.Config;
import cricket.merstham.website.frontend.model.ckfinder.File;
import cricket.merstham.website.frontend.model.ckfinder.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cksource.ckfinder.acl.Permission.FILE_CREATE;
import static com.cksource.ckfinder.acl.Permission.FILE_DELETE;
import static com.cksource.ckfinder.acl.Permission.FILE_RENAME;
import static com.cksource.ckfinder.acl.Permission.FILE_VIEW;
import static com.cksource.ckfinder.acl.Permission.FOLDER_CREATE;
import static com.cksource.ckfinder.acl.Permission.FOLDER_DELETE;
import static com.cksource.ckfinder.acl.Permission.FOLDER_VIEW;

@Service
public class S3Service {

    private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
    public static final String DELIMITER = "/";
    private final String bucketName;
    private final String region;
    private final S3Client client;

    @Autowired
    public S3Service(
            @Value("${resources.bucket}") String bucketName,
            @Value("${resources.region}") String region,
            @Value("${resources.api-endpoint:#{null}}") Optional<URI> endpoint) {
        this.bucketName = bucketName;
        this.region = region;
        var builder =
                S3Client.builder()
                        .credentialsProvider(DefaultCredentialsProvider.builder().build())
                        .region(Region.of(region));
        endpoint.ifPresent(
                e -> {
                    if (!e.toString().isBlank())
                        builder.endpointOverride(e)
                                .serviceConfiguration(
                                        S3Configuration.builder()
                                                .pathStyleAccessEnabled(true)
                                                .build());
                });
        this.client = builder.build();
    }

    public int createFolderIfNotExist(String folder) {
        try {
            var sanitizedFolder = getSanitizedFolder(folder);
            client.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(sanitizedFolder).build(),
                    RequestBody.empty());
            return 1;
        } catch (SdkException e) {
            LOG.warn("{} (this is probably OK)", e.getMessage());
            return 0;
        }
    }

    public List<Folder> getFolders(String prefix, Config.Backend backend) {
        var result =
                client.listObjectsV2(
                        ListObjectsV2Request.builder()
                                .bucket(bucketName)
                                .delimiter(DELIMITER)
                                .prefix(prefix)
                                .build());
        return result.commonPrefixes().stream()
                .map(
                        p -> {
                            var path = Paths.get(p.prefix().replaceFirst(prefix, ""));
                            return Folder.builder()
                                    .name(path.getName(0).toString())
                                    .hasChildren(path.getNameCount() > 1)
                                    .acl(defaultRights())
                                    .build();
                        })
                .filter(folder -> !backend.getHideFolders().contains(folder.getName()))
                .toList();
    }

    public List<File> getFiles(String prefix, Config.Backend backend) {
        var result =
                client.listObjectsV2(
                        ListObjectsV2Request.builder()
                                .bucket(bucketName)
                                .delimiter(DELIMITER)
                                .prefix(prefix)
                                .build());

        return result.contents().stream()
                .map(
                        o ->
                                File.builder()
                                        .name(Paths.get(o.key()).toFile().getName())
                                        .date(
                                                Long.parseLong(
                                                        LocalDateTime.ofInstant(
                                                                        o.lastModified(),
                                                                        ZoneId.systemDefault())
                                                                .format(
                                                                        DateTimeFormatter.ofPattern(
                                                                                "yyyyMMddHHmm"))))
                                        .size(o.size() / 1024)
                                        .build())
                .filter(file -> !backend.getHideFiles().contains(file.getName()))
                .toList();
    }

    public File getFile(String folder, String fileName) {
        try {
            var key = getSanitizedFileName(folder, fileName);
            var result =
                    client.getObject(
                            GetObjectRequest.builder().bucket(bucketName).key(key).build());

            return File.builder()
                    .name(fileName)
                    .date(
                            Long.parseLong(
                                    LocalDateTime.ofInstant(
                                                    result.response().lastModified(),
                                                    ZoneId.systemDefault())
                                            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))))
                    .size(result.readAllBytes().length / 1024)
                    .build();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public InputStream readFile(String folder, String fileName) {
        var key = getSanitizedFileName(folder, fileName);
        var result =
                client.getObjectAsBytes(
                        GetObjectRequest.builder().bucket(bucketName).key(key).build());
        return result.asInputStream();
    }

    public int renameFile(String folder, String fileName, String newName) {
        var sourceKey = getSanitizedFileName(folder, fileName);
        var destinationKey = getSanitizedFileName(folder, newName);
        try {
            client.copyObject(
                    CopyObjectRequest.builder()
                            .sourceBucket(bucketName)
                            .sourceKey(sourceKey)
                            .destinationBucket(bucketName)
                            .destinationKey(destinationKey)
                            .build());
        } catch (SdkException ex) {
            LOG.error("Error copying files");
            return 0;
        }
        return deleteFiles(List.of(sourceKey));
    }

    public int deleteFolder(String folder) {
        try {
            var sanitizedFolder = getSanitizedFolder(folder);
            client.deleteObject(
                    DeleteObjectRequest.builder().bucket(bucketName).key(sanitizedFolder).build());
            return 1;
        } catch (SdkException e) {
            LOG.warn("{} (this is probably OK)", e.getMessage());
            return 0;
        }
    }

    public int defaultRights() {
        return FOLDER_VIEW.getValue()
                | FOLDER_CREATE.getValue()
                | FOLDER_DELETE.getValue()
                | FILE_VIEW.getValue()
                | FILE_CREATE.getValue()
                | FILE_RENAME.getValue()
                | FILE_DELETE.getValue();
    }

    public int deleteFiles(List<String> files) {
        AtomicInteger deleted = new AtomicInteger();
        files.forEach(
                file -> {
                    try {
                        client.deleteObject(
                                DeleteObjectRequest.builder().bucket(bucketName).key(file).build());
                        deleted.getAndIncrement();
                    } catch (SdkException ex) {
                        LOG.error("Error deleting file", ex);
                    }
                });
        return deleted.get();
    }

    public int copyFiles(String folder, Map<String, String> files, boolean move) {
        AtomicInteger copied = new AtomicInteger();
        files.forEach(
                (file, sourceFolder) -> {
                    try {
                        var sourceKey = getSanitizedFileName(sourceFolder, file);
                        var destinationKey = getSanitizedFileName(folder, file);
                        client.copyObject(
                                CopyObjectRequest.builder()
                                        .sourceBucket(bucketName)
                                        .sourceKey(sourceKey)
                                        .destinationBucket(bucketName)
                                        .destinationKey(destinationKey)
                                        .build());
                        if (move) {
                            deleteFiles(List.of(sourceKey));
                        }
                        copied.getAndIncrement();
                    } catch (SdkException ex) {
                        LOG.error("Error deleting file", ex);
                    }
                });
        return copied.get();
    }

    public int upload(String folder, String filename, byte[] bytes) {
        try {
            var file = getSanitizedFileName(folder, filename);
            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(file)
                            .contentType(URLConnection.guessContentTypeFromName(filename))
                            .build(),
                    RequestBody.fromBytes(bytes));
            return 1;
        } catch (SdkException ex) {
            LOG.error("Error deleting file", ex);
            return 0;
        }
    }

    private String getSanitizedFolder(String folder) {
        var sanitizedFolder = folder;
        if (folder.startsWith(DELIMITER)) {
            sanitizedFolder = sanitizedFolder.replaceFirst(DELIMITER, "");
        }
        if (!sanitizedFolder.endsWith(DELIMITER)) {
            sanitizedFolder += DELIMITER;
        }
        return sanitizedFolder;
    }

    private String getSanitizedFileName(String folder, String fileName) {
        var path = Path.of(folder, fileName).toString();
        return path.startsWith(DELIMITER) ? path.replaceFirst(DELIMITER, "") : path;
    }
}
