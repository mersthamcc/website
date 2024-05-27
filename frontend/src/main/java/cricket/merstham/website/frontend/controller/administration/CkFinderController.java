package cricket.merstham.website.frontend.controller.administration;

import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.config.loader.ConfigLoader;
import com.cksource.ckfinder.error.ErrorCode;
import com.cksource.ckfinder.exception.CKFinderException;
import com.cksource.ckfinder.image.Image;
import com.cksource.ckfinder.image.ImageSize;
import com.cksource.ckfinder.image.Thumb;
import com.cksource.ckfinder.utils.FormatUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.shared.utils.DataURI;
import cricket.merstham.website.frontend.configuration.ckfinder.CkFinderRequestConfiguration;
import cricket.merstham.website.frontend.model.ckfinder.CopyFilesResponse;
import cricket.merstham.website.frontend.model.ckfinder.CreateFolderResponse;
import cricket.merstham.website.frontend.model.ckfinder.CurrentFolder;
import cricket.merstham.website.frontend.model.ckfinder.DeleteFilesResponse;
import cricket.merstham.website.frontend.model.ckfinder.DeleteFolderResponse;
import cricket.merstham.website.frontend.model.ckfinder.Error;
import cricket.merstham.website.frontend.model.ckfinder.ErrorResponse;
import cricket.merstham.website.frontend.model.ckfinder.File;
import cricket.merstham.website.frontend.model.ckfinder.FileUploadResponse;
import cricket.merstham.website.frontend.model.ckfinder.GetFileUrlResponse;
import cricket.merstham.website.frontend.model.ckfinder.GetFilesResponse;
import cricket.merstham.website.frontend.model.ckfinder.GetFoldersResponse;
import cricket.merstham.website.frontend.model.ckfinder.GetResizedImagesResponse;
import cricket.merstham.website.frontend.model.ckfinder.ImageConfig;
import cricket.merstham.website.frontend.model.ckfinder.ImageInfoResponse;
import cricket.merstham.website.frontend.model.ckfinder.InitResponse;
import cricket.merstham.website.frontend.model.ckfinder.MoveFilesResponse;
import cricket.merstham.website.frontend.model.ckfinder.RenameFileResponse;
import cricket.merstham.website.frontend.model.ckfinder.ResourceType;
import cricket.merstham.website.frontend.model.ckfinder.SaveImageResponse;
import cricket.merstham.website.frontend.service.S3Service;
import jakarta.ws.rs.core.UriBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cricket.merstham.website.frontend.configuration.ckfinder.CkFinderRequestConfiguration.DEFAULT_BACKEND;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(path = CkFinderController.CONNECTOR_PATH)
public class CkFinderController {

    private static final Logger LOG = LogManager.getLogger(CkFinderController.class);

    public static final String CONNECTOR_PATH = "/administration/components/ckfinder/connector";

    private static final String CHARS =
            "123456789ABCDEFGHJKLMNPQRSTUVWXYZ"; // pragma: allowlist secret
    private static final int[] POSITIONS = new int[] {1, 8, 17, 22, 3, 13, 11, 20, 5, 24, 27};
    public static final String CK_CSRF_TOKEN = "ckCsrfToken";

    private final Config config;
    private final CkFinderRequestConfiguration ckFinderRequestConfiguration;
    private final S3Service service;

    private final ObjectMapper objectMapper;

    @Autowired
    public CkFinderController(
            ConfigLoader configLoader,
            CkFinderRequestConfiguration ckFinderRequestConfiguration,
            S3Service service,
            ObjectMapper objectMapper)
            throws Exception {
        this.config = configLoader.loadConfig();
        this.ckFinderRequestConfiguration = ckFinderRequestConfiguration;
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(
            method = GET,
            params = {"command=Init"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public InitResponse init(@RequestParam Map<String, String> params) {
        String ln = "";
        String lc = config.getLicenseKey().replace("-", "");
        if (!lc.isEmpty()) {
            int pos = CHARS.indexOf(lc.charAt(2)) % 5;
            if (pos == 1 || pos == 2) {
                ln = config.getLicenseName();
            }
        }

        var resourceTypes = ckFinderRequestConfiguration.resourcesForRequest(params);
        resourceTypes.forEach(
                resourceType -> service.createFolderIfNotExist(resourceType.getDirectory()));
        return InitResponse.builder()
                .resourceTypes(
                        resourceTypes.stream()
                                .map(
                                        rt ->
                                                ResourceType.builder()
                                                        .name(rt.getName())
                                                        .label(rt.getLabel())
                                                        .allowedExtensions(
                                                                extensionList(
                                                                        rt.getAllowedExtensions()))
                                                        .deniedExtensions(
                                                                extensionList(
                                                                        rt.getDeniedExtensions()))
                                                        .lazyLoad(rt.isLazyLoaded())
                                                        .hasChildren(false)
                                                        .hash(sha256(rt))
                                                        .url(urlFor(rt))
                                                        .acl(service.defaultRights())
                                                        .build())
                                .toList())
                .enabled(true)
                .uploadCheckImages(false)
                .s(ln)
                .c(processLicenseKey(lc))
                .images(
                        ImageConfig.builder()
                                .max(
                                        imageSize(
                                                config.getImagesConfig().getMaxWidth(),
                                                config.getImagesConfig().getMaxHeight()))
                                .sizes(
                                        config.getImagesConfig().getSizes().entrySet().stream()
                                                .collect(
                                                        Collectors.toMap(
                                                                Map.Entry::getKey,
                                                                s ->
                                                                        imageSize(
                                                                                s.getValue()
                                                                                        .getWidth(),
                                                                                s.getValue()
                                                                                        .getHeight()))))
                                .build())
                .thumbs(
                        config.getThumbnailsConfig().getSizes().stream()
                                .map(t -> imageSize(t.getWidth(), t.getHeight()))
                                .toList())
                .build();
    }

    @RequestMapping(
            method = GET,
            params = {"command=GetFolders"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public GetFoldersResponse getFolders(@RequestParam Map<String, String> params) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) ->
                        GetFoldersResponse.builder()
                                .resourceType(params.get("type"))
                                .currentFolder(
                                        CurrentFolder.builder()
                                                .acl(service.defaultRights())
                                                .path(currentFolder)
                                                .url(currentFolderUrl(resourceType, currentFolder))
                                                .build())
                                .folders(
                                        service.getFolders(
                                                folder,
                                                config.getBackendConfig(
                                                        resourceType.getBackendName())))
                                .build());
    }

    @RequestMapping(
            method = GET,
            params = {"command=GetFiles"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public GetFilesResponse getFiles(@RequestParam Map<String, String> params) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) ->
                        GetFilesResponse.builder()
                                .resourceType(params.get("type"))
                                .currentFolder(
                                        CurrentFolder.builder()
                                                .acl(service.defaultRights())
                                                .path(currentFolder)
                                                .url(currentFolderUrl(resourceType, currentFolder))
                                                .build())
                                .files(
                                        service.getFiles(
                                                folder,
                                                config.getBackendConfig(
                                                        resourceType.getBackendName())))
                                .build());
    }

    @RequestMapping(
            method = GET,
            params = {"command=GetResizedImages"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public GetResizedImagesResponse getResizedImages(@RequestParam Map<String, String> params) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    InputStream in = service.readFile(folder, params.get("fileName"));
                    Image image = Image.create(in);
                    return GetResizedImagesResponse.builder()
                            .resourceType(params.get("type"))
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .originalSize(
                                    format(
                                            "{0,number,#}x{1,number,#}",
                                            image.getSize().getWidth(),
                                            image.getSize().getHeight()))
                            .resized(Map.of())
                            .build();
                });
    }

    @RequestMapping(
            method = GET,
            params = {"command=ImagePreview"})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public byte[] preview(@RequestParam Map<String, String> params) throws IOException {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    InputStream in = service.readFile(folder, params.get("fileName"));
                    Image image = Image.create(in);
                    var requestedSize = FormatUtils.parseImageSize(params.get("size"));
                    var adjustedSize = getAdjustedSize(requestedSize);
                    image.resize(adjustedSize);
                    Thumb thumb = Thumb.fromImage(image);
                    return thumb.getInputStream().readAllBytes();
                });
    }

    @RequestMapping(
            method = GET,
            params = {"command=Thumbnail"})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public byte[] thumbnail(@RequestParam Map<String, String> params) throws IOException {
        return preview(params);
    }

    @RequestMapping(
            method = GET,
            params = {"command=DownloadFile"})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public byte[] downloadFile(@RequestParam Map<String, String> params) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    InputStream in = service.readFile(folder, params.get("fileName"));
                    return in.readAllBytes();
                });
    }

    @RequestMapping(
            method = POST,
            params = {"command=CreateFolder"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public CreateFolderResponse createFolder(
            @RequestParam Map<String, String> params,
            @CookieValue(CK_CSRF_TOKEN) String csrfToken) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    validateRequest(params, csrfToken);
                    var newFolder = params.get("newFolderName");
                    return CreateFolderResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .newFolder(newFolder)
                            .created(
                                    service.createFolderIfNotExist(
                                            Paths.get(folder, newFolder).toString()))
                            .build();
                });
    }

    @RequestMapping(
            method = POST,
            params = {"command=DeleteFolder"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public DeleteFolderResponse deleteFolder(
            @RequestParam Map<String, String> params,
            @CookieValue(CK_CSRF_TOKEN) String csrfToken) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    validateRequest(params, csrfToken);
                    return DeleteFolderResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .deleted(service.deleteFolder(folder))
                            .build();
                });
    }

    @RequestMapping(
            method = POST,
            params = {"command=DeleteFiles"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public DeleteFilesResponse deleteFiles(
            @RequestParam Map<String, String> params,
            @CookieValue(CK_CSRF_TOKEN) String csrfToken) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    var json = getAndValidateJson(params, csrfToken);

                    var files =
                            StreamSupport.stream(json.path("files").spliterator(), false)
                                    .map(
                                            n ->
                                                    Paths.get(
                                                                    folder,
                                                                    n.get("folder").asText(),
                                                                    n.get("name").asText())
                                                            .toString())
                                    .toList();

                    return DeleteFilesResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .deleted(service.deleteFiles(files))
                            .build();
                });
    }

    @RequestMapping(
            method = POST,
            params = {"command=RenameFile"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public RenameFileResponse renameFile(
            @RequestParam Map<String, String> params,
            @CookieValue(CK_CSRF_TOKEN) String csrfToken) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    validateRequest(params, csrfToken);
                    var fileName = params.get("fileName");
                    var newName = params.get("newFileName");
                    return RenameFileResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .name(fileName)
                            .newName(newName)
                            .renamed(service.renameFile(folder, fileName, newName))
                            .build();
                });
    }

    @RequestMapping(
            method = POST,
            params = {"command=SaveImage"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public SaveImageResponse saveImage(
            @RequestParam Map<String, String> params,
            @CookieValue(CK_CSRF_TOKEN) String csrfToken) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    validateRequest(params, csrfToken);

                    var fileName = params.get("fileName");
                    var content = params.get("content");
                    var data = DataURI.parse(content);

                    var result = service.upload(folder, fileName, data.getContent());
                    File file = service.getFile(folder, fileName);
                    return SaveImageResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .saved(result)
                            .date(file.getDate())
                            .size(file.getSize())
                            .build();
                });
    }

    @RequestMapping(
            method = POST,
            params = {"command=FileUpload"},
            consumes = {MULTIPART_FORM_DATA_VALUE},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public FileUploadResponse fileUpload(
            @RequestParam Map<String, String> params,
            @CookieValue(CK_CSRF_TOKEN) String csrfToken,
            @RequestParam("upload") MultipartFile file) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    validateRequest(params, csrfToken);
                    return FileUploadResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .fileName(file.getOriginalFilename())
                            .uploaded(
                                    service.upload(
                                            folder, file.getOriginalFilename(), file.getBytes()))
                            .build();
                });
    }

    @RequestMapping(
            method = POST,
            params = {"command=QuickUpload"},
            consumes = {MULTIPART_FORM_DATA_VALUE},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public FileUploadResponse quickUpload(
            @RequestParam Map<String, String> params,
            @CookieValue(CK_CSRF_TOKEN) String csrfToken,
            @RequestParam("upload") MultipartFile file) {
        return fileUpload(params, csrfToken, file);
    }

    @RequestMapping(
            method = GET,
            params = {"command=GetFileUrl"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public GetFileUrlResponse getFileUrl(@RequestParam Map<String, String> params) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    var fileName = params.get("fileName");
                    var folderUri = currentFolderUrl(resourceType, currentFolder);
                    return GetFileUrlResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(folderUri)
                                            .build())
                            .url(folderUri.resolve(fileName))
                            .build();
                });
    }

    @RequestMapping(
            method = GET,
            params = {"command=ImageInfo"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public ImageInfoResponse imageInfo(@RequestParam Map<String, String> params) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    var fileName = params.get("fileName");
                    var image = Image.create(service.readFile(folder, fileName));
                    return ImageInfoResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .height(image.getHeight())
                            .width(image.getWidth())
                            .build();
                });
    }

    @RequestMapping(
            method = POST,
            params = {"command=CopyFiles"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public CopyFilesResponse copyFiles(
            @RequestParam Map<String, String> params,
            @CookieValue(CK_CSRF_TOKEN) String csrfToken) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    Map<String, String> files = getFilesToCopyOrMove(params, csrfToken);

                    return CopyFilesResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .copied(service.copyFiles(folder, files, false))
                            .build();
                });
    }

    @RequestMapping(
            method = POST,
            params = {"command=MoveFiles"},
            produces = {APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_' + #params.get(\"section\").toUpperCase())")
    public MoveFilesResponse moveFiles(
            @RequestParam Map<String, String> params,
            @CookieValue(CK_CSRF_TOKEN) String csrfToken) {
        return executeCommand(
                params,
                (resourceType, currentFolder, folder) -> {
                    Map<String, String> files = getFilesToCopyOrMove(params, csrfToken);

                    return MoveFilesResponse.builder()
                            .resourceType(resourceType.getName())
                            .currentFolder(
                                    CurrentFolder.builder()
                                            .acl(service.defaultRights())
                                            .path(currentFolder)
                                            .url(currentFolderUrl(resourceType, currentFolder))
                                            .build())
                            .moved(service.copyFiles(folder, files, true))
                            .build();
                });
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        int errorCode = ErrorCode.UNKNOWN;
        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof CKFinderException ckfinderException) {
            errorCode = ckfinderException.getErrorCode();
            httpStatus = ckfinderException.getHttpStatus();
        }

        return ResponseEntity.status(httpStatus)
                .body(
                        ErrorResponse.builder()
                                .error(
                                        Error.builder()
                                                .message(ex.getMessage())
                                                .number(errorCode)
                                                .exceptionName(ex.getClass().getSimpleName())
                                                .build())
                                .build());
    }

    private Map<String, String> getFilesToCopyOrMove(
            @RequestParam Map<String, String> params, @CookieValue(CK_CSRF_TOKEN) String csrfToken)
            throws JsonProcessingException {
        var json = getAndValidateJson(params, csrfToken);

        return StreamSupport.stream(json.path("files").spliterator(), false)
                .distinct()
                .collect(
                        Collectors.toMap(
                                n -> n.get("name").asText(),
                                n ->
                                        Paths.get(
                                                        getResourceType(
                                                                        params,
                                                                        n.get("type").asText())
                                                                .getDirectory(),
                                                        n.get("folder").asText())
                                                .toString()));
    }

    @FunctionalInterface
    private interface CkFinderFunction<T> {
        T getResult(Config.ResourceType resourceType, String currentFolder, String folder)
                throws IOException;
    }

    private <T> T executeCommand(Map<String, String> params, CkFinderFunction<T> callable) {
        try {
            var resourceType = getResourceType(params);
            var currentFolder = params.get("currentFolder");
            var folder = getFolder(resourceType.getDirectory(), currentFolder);

            return callable.getResult(resourceType, currentFolder, folder);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException("Error running CKFinder command", ex);
        }
    }

    private String getFolder(String base, String currentFolder) {
        var root = config.getBackendConfig("default").getRoot();
        var folder = new StringBuilder(root);
        if (!(root.endsWith("/") || base.startsWith("/"))) folder.append("/");
        folder.append(base);
        if (!(base.endsWith("/") || currentFolder.startsWith("/"))) folder.append("/");
        folder.append(currentFolder);
        return folder.toString();
    }

    private Config.ResourceType getResourceType(Map<String, String> params) {
        return getResourceType(params, params.get("type"));
    }

    private Config.ResourceType getResourceType(Map<String, String> params, String name) {
        var resourceTypes = ckFinderRequestConfiguration.resourcesForRequest(params);
        return resourceTypes.stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private URI currentFolderUrl(Config.ResourceType resourceType, String currentFolder) {
        var builder = UriBuilder.fromUri(urlFor(resourceType)).path("/");
        return builder.path(currentFolder).build();
    }

    private URI urlFor(Config.ResourceType rt) {
        var base = config.getBackendConfig(DEFAULT_BACKEND);
        var builder = UriBuilder.fromUri(base.getBaseUrl()).path(base.getRoot());
        return builder.path(rt.getDirectory()).path("/").build();
    }

    private String sha256(Config.ResourceType rt) {
        try {
            var base = format("{0}/{1}", rt.getName(), rt.getDirectory());
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; ++i) {
                String hex = Integer.toHexString(255 & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String extensionList(Set<String> extensions) {
        if (isNull(extensions)) return "";
        return String.join(",", extensions);
    }

    private String imageSize(int width, int height) {
        return format("{0,number,#}x{1,number,#}", width, height);
    }

    private String processLicenseKey(String licenseKey) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < POSITIONS.length; ++i) {
            int pos = POSITIONS[i];
            if (pos < licenseKey.length()) {
                stringBuilder.append(licenseKey.charAt(pos));
            }
        }

        return stringBuilder.toString();
    }

    private ImageSize getAdjustedSize(ImageSize requestedSize) {
        List<ImageSize> allowedSizes = config.getThumbnailsConfig().getSizes();
        Iterator<ImageSize> iterator = allowedSizes.iterator();

        ImageSize imageSize;
        do {
            if (!iterator.hasNext()) {
                return allowedSizes.get(allowedSizes.size() - 1);
            }

            imageSize = iterator.next();
        } while (imageSize.getWidth() < requestedSize.getWidth()
                || imageSize.getHeight() < requestedSize.getHeight());

        return imageSize;
    }

    private static void validateRequest(Map<String, String> params, String csrfToken) {
        if (!Objects.equals(csrfToken, params.get(CK_CSRF_TOKEN))) {
            throw new CKFinderException("Bad CSRF Token", ErrorCode.ACCESS_DENIED);
        }
    }

    private JsonNode getAndValidateJson(Map<String, String> params, String csrfToken)
            throws JsonProcessingException {
        var jsonData = params.get("jsonData");

        if (isNull(jsonData) || jsonData.isBlank()) {
            throw new CKFinderException("JsonData missing from request", ErrorCode.INVALID_REQUEST);
        }

        var json = objectMapper.readValue(jsonData, JsonNode.class);
        if (!Objects.equals(csrfToken, json.get(CK_CSRF_TOKEN).asText())) {
            throw new CKFinderException("Bad CSRF Token", ErrorCode.ACCESS_DENIED);
        }
        return json;
    }
}
