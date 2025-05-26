<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import '../../components.ftl' as components>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        function onPageLoad() {
            const token = $("meta[name='_csrf']").attr("content");
            const header = $("meta[name='_csrf_header']").attr("content");

            $("#sync-start").click(function() {
                $('#sync-status').html('<p class="spinner-border"></p><p>Synchonising fixtures to Google calendar, please wait...</p>');
                $('#sync-modal').modal();
                $.ajax({
                    url: "/administration/system/sync/calendar",
                    type: "POST",
                    data: $("#sync-start-form").serialize()
                }).error(function(jqXHR, textStatus, errorMessage) {
                    $('#sync-status')
                        .html("<p><b>Sychronisation error!</b></p><p>An error occured performing synchronisation.</p><p>Please contact administrator</p>");
                }).done(function (data) {
                    $('#sync-status')
                        .html("<p><b>Synchonisation complete!</b></p><p>Please check the calendar to verify results</p>");
                });
            });
        }
    </script>
</#macro>

<@layout.mainLayout script=dataScript>
    <div class="row">
        <div class="col-lg-12">
            <@admin.form action="/administration/system/sync/calendar" id="sync-start-form">
                <@admin.card title="menu.admin-sync-calendar">
                    <@admin.adminFormField name="startDate" data="" type="date" localeCategory="sync-calendar" />
                </@admin.card>

                <@components.buttonGroup>
                    <a href="/administration/" class="btn btn-bg-success transition-3d-hover">
                        <@spring.message code="sync-calendar.cancel" />
                    </a>
                    <button type="reset" class="btn btn-light transition-3d-hover" name="reset">
                        <@spring.messageText code="sync-calendar.reset" text="Reset" />
                    </button>&nbsp;&nbsp;
                    <button type="button" id="sync-start" class="btn btn-primary transition-3d-hover" name="action" value="save">
                        <@spring.message code="sync-calendar.start" />
                    </button>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>

    <div id="sync-modal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="false">
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

                <div class="modal-top-cover-icon border-0">
                    <span class="icon icon-lg icon-light icon-circle icon-centered shadow-soft">
                        <i class="tio-calendar"></i>
                    </span>
                </div>

                <div class="modal-body border-top-0">
                    <div id="sync-status" class="text-center">
                        ...
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-white" data-dismiss="modal">
                        <@spring.message code="sync-calendar.close" />
                    </button>
                </div>
            </div>
        </div>
    </div>
</@layout.mainLayout>