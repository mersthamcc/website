package cricket.merstham.website.frontend.configuration.ckfinder;

import com.cksource.ckfinder.acl.AclRule;
import com.cksource.ckfinder.acl.Permission;
import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.config.loader.ConfigLoader;
import com.cksource.ckfinder.image.ImageSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.cksource.ckfinder.acl.Permission.FILE_CREATE;
import static com.cksource.ckfinder.acl.Permission.FILE_RENAME;
import static com.cksource.ckfinder.acl.Permission.FILE_VIEW;
import static com.cksource.ckfinder.acl.Permission.FOLDER_CREATE;
import static com.cksource.ckfinder.acl.Permission.FOLDER_DELETE;
import static com.cksource.ckfinder.acl.Permission.FOLDER_RENAME;
import static com.cksource.ckfinder.acl.Permission.FOLDER_VIEW;
import static com.cksource.ckfinder.acl.Permission.IMAGE_RESIZE;
import static com.cksource.ckfinder.acl.Permission.IMAGE_RESIZE_CUSTOM;

public class CkFinderConfigLoader implements ConfigLoader {

    private Config config;

    public CkFinderConfigLoader(
            String licenseName, String licenseKey, String resourceUrl, String resourcePath) {
        config = new Config();
        config.setLicenseName(licenseName);
        config.setLicenseKey(licenseKey);

        config.setServeStaticResources(false);
        config.setSecureImageUploads(false);
        config.setCsrfProtection(true);
        config.setRoleSessionAttribute("CKFinder_UserRole");

        Config.PrivateDir privateDir = new Config.PrivateDir();
        privateDir.setBackend("default");
        privateDir.setPath(".ckfinder/");
        config.setPrivateDir(privateDir);

        Config.Images images = new Config.Images();
        images.setMaxWidth(1600);
        images.setMaxHeight(1200);
        images.setQuality(80);
        images.setSizes(
                Map.of(
                        "small", imageSize(400, 320, 80),
                        "medium", imageSize(600, 480, 80),
                        "large", imageSize(800, 600, 80)));
        config.setImages(images);

        Config.Thumbnails thumbnails = new Config.Thumbnails();
        thumbnails.setEnabled(true);
        thumbnails.setSizes(
                List.of(imageSize(150, 150, 80), imageSize(300, 300, 80), imageSize(500, 500, 80)));

        config.setThumbnails(thumbnails);

        Config.Cache cache = new Config.Cache();
        cache.setImagePreview(86400);
        cache.setProxyCommand(0);
        cache.setThumbnails(31536000);
        config.setCache(cache);

        ArrayList<AclRule> rules = new ArrayList<>();
        rules.add(
                createRule(
                        "*",
                        "*",
                        "/",
                        List.of(
                                FOLDER_VIEW,
                                FOLDER_CREATE,
                                FOLDER_RENAME,
                                FOLDER_DELETE,
                                FILE_VIEW,
                                FILE_CREATE,
                                FILE_RENAME,
                                IMAGE_RESIZE,
                                IMAGE_RESIZE_CUSTOM)));
        config.setAccessControl(rules);

        Config.Backend backend = new Config.Backend();
        backend.setName("default");
        backend.setAdapter("s3");
        backend.setBaseUrl(resourceUrl);
        backend.setRoot(resourcePath);
        backend.setDisallowUnsafeCharacters(true);
        backend.setForceAscii(false);
        backend.setHideFolders(List.of(".*", "CVS", "__thumbs"));
        backend.setHideFiles(List.of(".*"));
        backend.setHtmlExtensions(List.of("html", "htm", "xml", "js"));
        backend.setOverwriteOnUpload(true);
        backend.setUseProxyCommand(false);
        ArrayList<Config.Backend> backends = new ArrayList<>();
        backends.add(backend);
        config.setBackends(backends);
    }

    private AclRule createRule(
            String role, String resourceType, String folder, List<Permission> permissions) {
        AclRule aclRule = new AclRule();
        aclRule.setRole(role);
        aclRule.setResourceType(resourceType);
        aclRule.setFolder(folder);
        Arrays.asList(Permission.values())
                .forEach(
                        permission ->
                                aclRule.setPermission(
                                        permission, permissions.contains(permission)));
        return aclRule;
    }

    private ImageSize imageSize(int width, int height, int quality) {
        ImageSize size = new ImageSize();
        size.setQuality(quality);
        size.setWidth(width);
        size.setHeight(height);
        return size;
    }

    @Override
    public Config loadConfig() throws Exception {
        return config;
    }
}
