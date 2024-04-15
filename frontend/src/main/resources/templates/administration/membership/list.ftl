<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        function playCricketLink(row, type, set, meta) {
            if (row.data.identifiers && row.data.identifiers.includes("PLAYER_ID")) {
                return `<div class="avatar mr-3">
                            <img class="avatar-img" src="${resourcePrefix}/mcc/img/play-cricket-small.png" alt="Linked to PlayCricket">
                        </div>`;
            }
            return "";
        }

        function onPageLoad() {
            <@admin.adminSspTableScript
                id="memberTable"
                selectable=true
                columns=memberColumns
                ajaxSrc="membership/get-data"
            />
        };
    </script>
</#macro>
<@layout.mainLayout script=dataScript>
    <@admin.adminSspTableCard
        id="memberTable"
        selectable=true
        searchable=true
        defaultPageLength=50
        pageLengths=[10,50,100]
        title="membership.member-details"
        columns=memberColumns
    />
</@layout.mainLayout>