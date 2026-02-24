<#import "/spring.ftl" as spring />
<#import "../../../admin-base.ftl" as layout>
<#import "../../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        function onPageLoad() {
            <@admin.adminSspTableScript
                id="dataTable"
                selectable=true
                columns=columns
                ajaxSrc="static-data/get-data"
            />
        };
    </script>
</#macro>
<@layout.mainLayout script=dataScript>
    <@admin.adminSspTableCard
        id="dataTable"
        selectable=true
        searchable=true
        defaultPageLength=50
        pageLengths=[10,50,100]
        title="menu.admin-system-data-list"
        columns=columns
    />
</@layout.mainLayout>
