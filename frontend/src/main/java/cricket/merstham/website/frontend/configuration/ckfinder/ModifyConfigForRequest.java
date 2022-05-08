package cricket.merstham.website.frontend.configuration.ckfinder;

import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.event.GetConfigForRequestEvent;
import com.cksource.ckfinder.exception.InvalidRequestException;
import com.cksource.ckfinder.listener.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Named
public class ModifyConfigForRequest implements Listener<GetConfigForRequestEvent> {

    private static final List<String> ALLOWED_SECTIONS = List.of("news");
    public static final String DEFAULT_BACKEND = "default";

    private final HttpServletRequest request;
    private final String baseDirectory;

    @Autowired
    public ModifyConfigForRequest(
            HttpServletRequest request,
            @Value("${resources.base-directory}") String baseDirectory) {
        this.request = request;
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void onApplicationEvent(GetConfigForRequestEvent event) {

        var uuid = request.getParameter("uuid");
        var section = request.getParameter("section");

        if (!(ALLOWED_SECTIONS.contains(section) && isValidUniqueId(uuid))) {
            throw new InvalidRequestException("Invalid request");
        } else {
            var baseDir = Paths.get(format("%s/resources/%s/%s", baseDirectory, section, uuid));
            if (Files.notExists(baseDir)) {
                try {
                    Files.createDirectory(baseDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Config config = event.getConfig();

            ArrayList<Config.ResourceType> resourceTypes = new ArrayList<>();
            int i = 0;
            resourceTypes.add(
                    resourceType(
                            i,
                            "Images",
                            format("This %s item's images", section),
                            DEFAULT_BACKEND,
                            format("/resources/%s/%s/images", section, uuid),
                            "jpeg,jpg,png,gif,svg",
                            null,
                            0));
            i = i + 1;
            resourceTypes.add(
                    resourceType(
                            i,
                            "Files",
                            format("This %s item's attachments", section),
                            DEFAULT_BACKEND,
                            format("/resources/%s/%s/files", section, uuid),
                            "csv,doc,docx,mov,mp3,mp4,ods,odt,pdf,ppt,pptx,swf,xls,xlsx",
                            null,
                            0));
            i = i + 1;
            resourceTypes.add(
                    resourceType(
                            i,
                            "WebGlobalImages",
                            "Website Global Images",
                            DEFAULT_BACKEND,
                            "/resources/statics/images",
                            "jpeg,jpg,png,gif",
                            null,
                            0));

            i = i + 1;
            resourceTypes.add(
                    resourceType(
                            i,
                            "WebGlobalFiles",
                            "Website Global Attachments",
                            DEFAULT_BACKEND,
                            "/resources/statics/files",
                            "csv,doc,docx,mov,mp3,mp4,ods,odt,pdf,ppt,pptx,swf,xls,xlsx",
                            null,
                            0));
            config.setResourceTypes(resourceTypes);
        }
    }

    private boolean isValidUniqueId(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException ignored) {
            return uuid.matches("[0-9a-f]{1,14}.[0-9]{1,8}");
        }
    }

    private Config.ResourceType resourceType(
            int index,
            String name,
            String label,
            String backend,
            String directory,
            String allowedExtensions,
            String deniedExtensions,
            int maxSize) {
        Config.ResourceType resourceType = new Config.ResourceType();
        resourceType.setName(format("%d-%s", index, name));
        resourceType.setLabel(label);
        resourceType.setBackend(backend);
        resourceType.setDirectory(directory);
        resourceType.setAllowedExtensions(allowedExtensions);
        resourceType.setDeniedExtensions(deniedExtensions);
        resourceType.setMaxSize(maxSize);
        resourceType.setLazyLoaded(true);
        return resourceType;
    }
}
