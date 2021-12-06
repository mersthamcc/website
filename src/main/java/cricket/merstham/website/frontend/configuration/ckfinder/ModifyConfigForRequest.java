package cricket.merstham.website.frontend.configuration.ckfinder;

import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.event.GetConfigForRequestEvent;
import com.cksource.ckfinder.listener.Listener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;

import static java.lang.String.format;

@Named
public class ModifyConfigForRequest implements Listener<GetConfigForRequestEvent> {

    private HttpServletRequest request;

    @Autowired
    public ModifyConfigForRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void onApplicationEvent(GetConfigForRequestEvent event) {

        var uuid = request.getParameter("uuid");
        var section = request.getParameter("section");

        Config config = event.getConfig();
        ArrayList<Config.ResourceType> resourceTypes = new ArrayList<>();
        int i = 0;
        //        resourceTypes.add(resourceType(
        //                i,
        //                "Files",
        //                "This items files",
        //                "default",
        //                format("/resources/%s/%s/files", section, uuid),
        //                "csv,doc,docx,mov,mp3,mp4,ods,odt,pdf,ppt,pptx,swf,xls,xlsx",
        //                null,
        //                0));
        //        i = i + 1;
        resourceTypes.add(
                resourceType(
                        i,
                        "Images",
                        "This items images",
                        "default",
                        format("/resources/%s/%s/images", section, uuid),
                        "jpeg,jpg,png,gif,svg",
                        null,
                        0));
        i = i + 1;
        resourceTypes.add(
                resourceType(
                        i,
                        "WebGlobalImages",
                        "Global Images",
                        "default",
                        "/resources/statics/images",
                        "jpeg,jpg,png,gif",
                        null,
                        0));

        config.setResourceTypes(resourceTypes);
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
