<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        function onPageLoad() {
            <@admin.adminSspTableScript
                id="venueTable"
                selectable=true
                columns=columns
                ajaxSrc="venue/get-data"
            />
        };
    </script>
</#macro>
<@layout.mainLayout script=dataScript>
    <@admin.adminSspTableCard
        id="venueTable"
        selectable=true
        searchable=true
        defaultPageLength=50
        pageLengths=[10,50,100]
        title="menu.admin-venue-list"
        columns=columns
    />
</@layout.mainLayout>
