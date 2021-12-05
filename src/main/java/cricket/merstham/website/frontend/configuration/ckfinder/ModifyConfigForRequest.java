package cricket.merstham.website.frontend.configuration.ckfinder;

import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.event.GetConfigForRequestEvent;
import com.cksource.ckfinder.listener.Listener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Named
public class ModifyConfigForRequest implements Listener<GetConfigForRequestEvent> {

    private HttpServletRequest request;

    @Autowired
    public ModifyConfigForRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void onApplicationEvent(GetConfigForRequestEvent event) {

        var id = request.getParameter("id");
        var section = request.getParameter("section");

        Config config = event.getConfig();
        ArrayList<Config.ResourceType> resourceTypes = new ArrayList<>();
        resourceTypes.add(resourceType(
                "Files",
                "default",
                "/files",
                "7z,aiff,asf,avi,bmp,csv,doc,docx,fla,flv,gif,gz,gzip,jpeg,jpg,mid,mov,mp3,mp4,mpc,mpeg,mpg,ods,odt,pdf,png,ppt,pptx,qt,ram,rar,rm,rmi,rmvb,rtf,sdc,swf,sxc,sxw,tar,tgz,tif,tiff,txt,vsd,wav,wma,wmv,xls,xlsx,zip",
                null,
                0));
        resourceTypes.add(resourceType(
                "Images",
                "default",
                "/images",
                "bmp,jpeg,jpg,png",
                null,
                0));
        config.setResourceTypes(resourceTypes);
    }


    private Config.ResourceType resourceType(String name, String backend, String directory, String allowedExtensions, String deniedExtensions, int maxSize) {
        Config.ResourceType resourceType = new Config.ResourceType();
        resourceType.setName(name);
        resourceType.setBackend(backend);
        resourceType.setDirectory(directory);
        resourceType.setAllowedExtensions(allowedExtensions);
        resourceType.setDeniedExtensions(deniedExtensions);
        resourceType.setMaxSize(maxSize);
        resourceType.setLazyLoaded(true);
        return resourceType;
    }
}
