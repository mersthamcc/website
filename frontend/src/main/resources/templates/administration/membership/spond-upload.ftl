<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script src="${resourcePrefix}/front/admin/assets/vendor/hs-file-attach/dist/hs-file-attach.min.js"></script>
    <script>
        function onPageLoad() {
            $('.js-file-attach').each(function () {
                const customFile = new HSFileAttach($(this)).init();
            });
        }
    </script>
</#macro>
<#macro detailsFormButtons>
    <button type="reset" class="btn btn-light transition-3d-hover" name="action">
        <@spring.messageText code="membership.reset" text="Reset" />
    </button>&nbsp;&nbsp;
    <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
        <@spring.message code="membership.save" />
        <i class="fa fa-check-circle"></i>
    </button>
</#macro>

<@layout.mainLayout script=dataScript>
    <div class="row">
        <div class="col-lg-12">
            <@admin.form action="/administration/membership/spond-upload" enctype="multipart/form-data">
                <@admin.card title="menu.admin-membership-spond-upload">
                    <div class="custom-file">
                        <input type="file" class="js-file-attach custom-file-input" id="spond-data" name="spond-data"
                               data-hs-file-attach-options='{
                                  "textTarget": "[for=\"spond-data\"]"
                               }'>
                        <label class="custom-file-label" for="spond-data">Choose file</label>
                    </div>
                </@admin.card>

                <@components.buttonGroup>
                    <a href="/administration/news" class="btn btn-bg-success transition-3d-hover">
                        <@spring.message code="news.cancel" />
                    </a>
                    <button type="reset" class="btn btn-light transition-3d-hover" name="reset">
                        <@spring.messageText code="news.reset" text="Reset" />
                    </button>&nbsp;&nbsp;
                    <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
                        <@spring.message code="news.save" />
                        <i class="fa fa-check-circle"></i>
                    </button>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>
</@layout.mainLayout>
