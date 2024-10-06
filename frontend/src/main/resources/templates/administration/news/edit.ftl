<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>

<#macro dataScript>
    <script>
        const debug = ${debug?c};
        ClassicEditor
            .create(document.querySelector('#body-editor'), {
                toolbar: [
                    'heading', '|',
                    'bold', 'italic', 'link', 'bulletedList', 'numberedList', 'blockQuote', '|',
                    'undo', 'redo', '|',
                    'ckfinder', 'mediaEmbed', '|',
                    'sourceEditing'],
                ckfinder: {
                    uploadUrl: '/administration/components/ckfinder/connector?command=QuickUpload&type=Files&responseType=json',
                    options: {
                        connectorPath: '/administration/components/ckfinder/connector',
                        section: 'news',
                        startupPath: '0-Images:/',
                        rememberLastFolder: false,
                        uuid: '${news.uuid}',
                        pass: 'section,uuid'
                    }
                },
                mention: {
                    feeds: [
                        {
                            marker: '@',
                            feed: ['@Chris', '@Richard'],
                            minimumCharacters: 1
                        }
                    ]
                }
            })
            .then(editor => {
                if (debug) CKEditorInspector.attach(editor);
            })
            .catch( error => {
                console.error(error);
            });

        $(document).ready(function (){
            $("#featureImageUrl-browse").click(function () {
                CKFinder.modal({
                    chooseFiles: true,
                    connectorPath: '/administration/components/ckfinder/connector',
                    section: 'news',
                    startupPath: '0-Images:/',
                    rememberLastFolder: false,
                    uuid: '${news.uuid}',
                    pass: 'section,uuid',
                    onInit: function(finder) {
                        finder.on('files:choose', function(event) {
                            let file = event.data.files.first();
                            $("#item-featureImageUrl").val(file.getUrl());
                            $("#image-featureImageUrl").attr("src", $("#item-featureImageUrl").val());
                        });
                        finder.on('file:choose:resizedImage', function(event) {
                            $("#item-featureImageUrl").val(event.data.resizedUrl);
                            $("#image-featureImageUrl").attr("src", $("#item-featureImageUrl").val());
                        });
                    }
                });
            });
            if($("#item-featureImageUrl").val() !== "") {
                $("#image-featureImageUrl").attr("src", $("#item-featureImageUrl").val());
            }
        });
    </script>
</#macro>

<@layout.mainLayout script=dataScript>
    <div class="row">
        <div class="col-lg-12">
            <@admin.form  action="/administration/news/save">
                <@admin.card title="menu.admin-news-new">
                    <input type="hidden" name="id" value="${news.id?long?c}" />
                    <input type="hidden" name="uuid" value="${news.uuid}" />
                    <input type="hidden" name="createdDate" value="${news.createdDate.format("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")}" />
                    <input type="hidden" name="publishDate" value="${news.publishDate.format("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")}" />
                    <@admin.formErrors errors=errors![] errorKey="news.errorSaving"/>
                    <@admin.adminFormDisplayField name="createdDate" data=news.createdDate.format()?datetime.iso?string["dd/MM/yyyy HH:mm"] localeCategory="news" />
                    <@admin.adminFormField name="title" data=news.title!"" required=true type="text" localeCategory="news" />
                    <@admin.adminFormField name="author" data=news.author!"" required=true type="text" localeCategory="news" />
                    <@admin.adminFormField name="featureImageUrl" data=news.featureImageUrl!"" required=false type="hidden" localeCategory="news">
                        <div id="attachFilesLabel" class="js-dropzone dropzone-custom custom-file-boxed">
                            <div class="dz-message custom-file-boxed-label">
                                <img
                                        id="image-featureImageUrl"
                                        class="mb-3 feature-image"
                                        src="${resourcePrefix}/front/admin/assets/img/400x400/img2.jpg"
                                        alt="Browse for images" />

                                <p class="mb-2"></p>

                                <button type="button" class="btn btn-sm btn-white" name="featureImageUrl-browse" id="featureImageUrl-browse">
                                    <@spring.messageText code="news.browse" text="Browse" />
                                </button>
                            </div>
                        </div>
                    </@admin.adminFormField>

                    <@admin.adminCkEditorField name="body" data=news.body!"" required=true type="text" localeCategory="news" rows=40/>
                    <@admin.adminSwitchField name="draft" checked=news.draft!false localeCategory="news" />
                </@admin.card>
                <@admin.card title="Social Media">
                    <#if true>
                        <@admin.adminSwitchField name="publishToFacebook" checked=news.publishToFacebook!false localeCategory="news" />
                        <@admin.adminSwitchField name="publishToInstagram" checked=news.publishToInstagram!false localeCategory="news" />
                        <@admin.adminSwitchField name="publishToTwitter" checked=news.publishToTwitter!false localeCategory="news" />
                        <@admin.adminFormField name="socialSummary" data=news.socialSummary!"" required=false type="text" localeCategory="news" />
                    <#else>
                        <div class="alert alert-soft-primary" role="alert">
                            <h5 class="alert-heading">Disabled</h5>
                            <hr />
                            Automatic posting to social media currently not available.
                        </div>
                    </#if>
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