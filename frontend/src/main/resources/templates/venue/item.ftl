<#import "../base.ftl" as layout>

<#macro mapScripts>
    <script>(g=>{var h,a,k,p="The Google Maps JavaScript API",c="google",l="importLibrary",q="__ib__",m=document,b=window;b=b[c]||(b[c]={});var d=b.maps||(b.maps={}),r=new Set,e=new URLSearchParams,u=()=>h||(h=new Promise(async(f,n)=>{await (a=m.createElement("script"));e.set("libraries",[...r]+"");for(k in g)e.set(k.replace(/[A-Z]/g,t=>"_"+t[0].toLowerCase()),g[k]);e.set("callback",c+".maps."+q);a.src=`https://maps.${'$'}{c}apis.com/maps/api/js?`+e;d[q]=f;a.onerror=()=>h=n(Error(p+" could not load."));a.nonce=m.querySelector("script[nonce]")?.nonce||"";m.head.append(a)}));d[l]?console.warn(p+" only loads once. Ignoring:",g):d[l]=(f,...n)=>r.add(f)&&u().then(()=>d[l](f,...n))})
        ({
            key: "${config.googleMapsApiKey}",
            v: "weekly"
        });
    </script>

    <script>
        $(document).on('ready', async function() {
            const { Map } = await google.maps.importLibrary("maps");
            const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");

            $('.map').each(function(i, e) {
                const position = {
                    lat: $(e).data('lat'),
                    lng: $(e).data('long'),
                };

                const map = new Map(document.getElementById("map"), {
                    zoom: 16,
                    center: position,
                    mapId: `map_id_${'$'}{i}`,
                });

                new AdvancedMarkerElement({
                    map: map,
                    position: position,
                    title: $(e).data('marker'),
                });
            });
        });
    </script>
</#macro>

<@layout.mainLayout script=mapScripts>
    <!-- BEGIN GOOGLE MAP -->
    <div class="row">
        <div
                id="map"
                class="map ml-2 mr-2"
                data-lat="${venue.latitude}"
                data-long="${venue.longitude}"
                data-marker="${venue.marker}">
        </div>
    </div>
    <!-- END GOOGLE MAP -->

    <div class="container space-1">
        <div class="row justify-content-lg-between">
            <div class="col-md-9 col-sm-9">
                <h2>${venue.name}</h2>
                <div class="space20"></div>
                ${venue.description}
                <div class="space20"></div>
                <h2>Directions</h2>
                ${venue.directions}
            </div>

            <div class="col-md-3 col-sm-3">
                <h2>Our Address</h2>
                <address class="markeraddress">
                    ${venue.address}
                    <div class="bold">${venue.postCode}</div>
                </address>
            </div>
        </div>
    </div>
</@layout.mainLayout>
