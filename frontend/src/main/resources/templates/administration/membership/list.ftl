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
                ajaxSrc="membership/get-data"
            />

            $("#export-google").click(function() {
                $("#export-open").hide();
                $('#export-status').html('<p class="spinner-border"></p><p>Exporting to your Google Drive, please wait...</p>');
                $('#export-modal').modal();
                $.ajax({
                    url: "/administration/membership-report/all/export",
                    type: "GET",
                    "contentType": "application/json",
                    beforeSend: function (request) {
                        request.setRequestHeader(header, token);
                    }
                }).error(function(jqXHR, textStatus, errorMessage) {
                    $('#export-status')
                        .html("<p><b>Export error!</b></p><p>An error occured performing export.</p><p>Please contact administrator</p>");
                }).done(function (data) {
                    $('#export-status')
                        .html("<p><b>Export complete!</b></p><p>Saved to your private Google Drive folder, to share move the sheet to appropriate shared drive.</p><p>Click below to open.</p>");
                    $('#export-open').attr("href", data.location);
                    $("#export-open").show();
                });
            });
        }
    </script>
</#macro>
<#macro export>
    <div class="d-sm-flex justify-content-sm-end align-items-sm-center">
        <a id="export-google" class="btn btn-soft-success mb-2" href="javascript:">
            <img
                    class="avatar avatar-xss avatar-4by3 mr-2"
                    src="${resourcePrefix}/front/admin/assets/svg/brands/google-sheets.svg"
                    alt="<@spring.message code="membership.export" />">
            <@spring.message code="membership.export" />
        </a>
    </div>
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
        buttons=export
    />

    <div id="export-modal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="false">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <!-- Header -->
                <div class="modal-top-cover bg-primary text-center">
                    <figure class="position-absolute right-0 bottom-0 left-0" style="margin-bottom: -1px;">
                        <svg preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" viewBox="0 0 1920 100">
                            <path fill="#fff" d="M0,0c0,0,934.4,93.4,1920,0v100.1H0L0,0z"/>
                        </svg>
                    </figure>

                    <div class="modal-close">
                        <button type="button" class="btn btn-icon btn-sm btn-ghost-light" data-dismiss="modal" aria-label="Close">
                            <svg width="16" height="16" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
                                <path fill="currentColor" d="M11.5,9.5l5-5c0.2-0.2,0.2-0.6-0.1-0.9l-1-1c-0.3-0.3-0.7-0.3-0.9-0.1l-5,5l-5-5C4.3,2.3,3.9,2.4,3.6,2.6l-1,1 C2.4,3.9,2.3,4.3,2.5,4.5l5,5l-5,5c-0.2,0.2-0.2,0.6,0.1,0.9l1,1c0.3,0.3,0.7,0.3,0.9,0.1l5-5l5,5c0.2,0.2,0.6,0.2,0.9-0.1l1-1 c0.3-0.3,0.3-0.7,0.1-0.9L11.5,9.5z"/>
                            </svg>
                        </button>
                    </div>
                </div>
                <!-- End Header -->

                <div class="modal-top-cover-avatar border-0">
                    <img class="avatar avatar-lg avatar-centered" src="${resourcePrefix}/front/admin/assets/svg/brands/google-sheets.svg" alt="Sheets">
                </div>

                <div class="modal-body border-top-0">
                    <div id="export-status" class="text-center">
                        ...
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-white" data-dismiss="modal">
                        <@spring.message code="membership.export-cancel" />
                    </button>
                    <a id="export-open" class="btn btn-primary" target="_blank">
                        <@spring.message code="membership.open-export" />
                    </a>
                </div>
            </div>
        </div>
    </div>
</@layout.mainLayout>