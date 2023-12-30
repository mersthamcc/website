package cricket.merstham.website.frontend.configuration.ckfinder;

import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.exception.InvalidRequestException;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Named
public class CkFinderRequestConfiguration {

    private static final List<String> ALLOWED_SECTIONS = List.of("news", "events", "pages");
    public static final String DEFAULT_BACKEND = "default";

    public List<Config.ResourceType> resourcesForRequest(Map<String, String> request) {

        var uuid = request.get("uuid");
        var section = request.get("section");

        if (!(ALLOWED_SECTIONS.contains(section) && isValidUniqueId(uuid))) {
            throw new InvalidRequestException("Invalid request");
        } else {
            ArrayList<Config.ResourceType> resourceTypes = new ArrayList<>();
            int i = 0;
            if (nonNull(uuid) && !uuid.isBlank()) {
                resourceTypes.add(
                        resourceType(
                                i,
                                "Images",
                                format("This %s item's images", section),
                                DEFAULT_BACKEND,
                                format("/%s/%s/images", section, uuid),
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
                                format("/%s/%s/files", section, uuid),
                                "csv,doc,docx,mov,mp3,mp4,ods,odt,pdf,ppt,pptx,swf,xls,xlsx",
                                null,
                                0));
                i = i + 1;
            }
            resourceTypes.add(
                    resourceType(
                            i,
                            "WebGlobalImages",
                            "Website Global Images",
                            DEFAULT_BACKEND,
                            "/statics/images",
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
                            "/statics/files",
                            "csv,doc,docx,mov,mp3,mp4,ods,odt,pdf,ppt,pptx,swf,xls,xlsx",
                            null,
                            0));
            return resourceTypes;
        }
    }

    private boolean isValidUniqueId(String uuid) {
        if (isNull(uuid) || uuid.isBlank()) return true;
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException ignored) {
            return uuid.matches("[0-9a-f]{1,14}.\\d{1,8}");
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
