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
                    <@admin.adminCkEditorField name="body" data=news.body!"" required=true type="text" localeCategory="news" rows=40/>
                    <@admin.adminSwitchField name="draft" checked=news.draft!false localeCategory="news" />
                </@admin.card>
                <@admin.card title="Social Media">
                    <#if true>
                        <@admin.adminSwitchField name="publishToFacebook" checked=news.publishToFacebook!false localeCategory="news" />
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