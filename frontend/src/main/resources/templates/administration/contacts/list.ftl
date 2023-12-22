<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        function onPageLoad() {
            <@admin.adminSspTableScript
                id="contactTable"
                selectable=true
                columns=columns
                ajaxSrc="contacts/get-data"
            />
        };
    </script>
</#macro>
<@layout.mainLayout script=dataScript>
    <@admin.adminSspTableCard
        id="contactTable"
        selectable=true
        searchable=true
        defaultPageLength=50
        pageLengths=[10,50,100]
        title="menu.admin-contact-list"
        columns=columns
    />
</@layout.mainLayout>
