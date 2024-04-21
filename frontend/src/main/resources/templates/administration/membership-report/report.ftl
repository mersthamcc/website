<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        const CURRENT_YEAR = new Date().getFullYear();
        function playCricketLink(row, type, set, meta) {
            if (row.data.identifiers && row.data.identifiers.includes("PLAYER_ID")) {
                return `<div class="avatar mr-3">
                            <img class="avatar-img" src="${resourcePrefix}/mcc/img/play-cricket-small.png" alt="Linked to PlayCricket">
                        </div>`;
            }
            return "";
        }

        function tags(row, type, set, meta) {
            let tags = [];
            if (row.data.declarations && row.data.declarations.includes("OPENAGE")) {
                tags.push(`<span class="badge badge-success"><@spring.message code="membership.open-age-allowed" /></span>`);
            }
            if (row.data.declarations && !row.data.declarations.includes("PHOTOS-MARKETING")) {
                tags.push(`<span class="badge badge-warning"><@spring.message code="membership.no-photos" /></span>`);
            }
            if (row.data.declarations && !row.data.declarations.includes("PHOTOS-COACHING")) {
                tags.push(`<span class="badge badge-secondary"><@spring.message code="membership.no-coaching-photo" /></span>`);
            }
            return tags.join("<br/>");
        }

        function unpaid(row, type, set, meta) {
            if (row.data.mostRecentSubscription < CURRENT_YEAR || !row.data.received) {
                return `<span class="badge badge-danger"><@spring.message code="membership.unpaid" /></span>`
            }
            return "";
        }

        function onPageLoad() {
            <@admin.adminSspTableScript
                id="memberTable"
                selectable=true
                columns=memberColumns
                ajaxSrc=report + "/get-data"
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