<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        function onPageLoad() {
            <@admin.adminSspTableScript
                id="eventTable"
                selectable=true
                columns=columns
                ajaxSrc="event/get-data"
            />
        };
    </script>
</#macro>
<@layout.mainLayout script=dataScript>
    <@admin.adminSspTableCard
        id="eventTable"
        selectable=true
        searchable=true
        defaultPageLength=50
        pageLengths=[10,50,100]
        title="menu.admin-event-list"
        columns=columns
    />
</@layout.mainLayout>
