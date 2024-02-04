<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>

<#macro dataScript>
    <script>
        ClassicEditor
            .create(document.querySelector('#content-editor'), {
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
                        section: 'pages',
                        startupPath: '0-WebGlobalImages:/',
                        rememberLastFolder: false,
                        pass: 'section'
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
                CKEditorInspector.attach(editor);
            })
            .catch( error => {
                console.error(error);
            });
    </script>
</#macro>

<@layout.mainLayout script=dataScript>
    <div class="row">
        <div class="col-lg-12">
            <@admin.form  action="/administration/page/save">
                <@admin.card title="menu.admin-page-new">
                    <@admin.formErrors errors=errors![] errorKey="page.errorSaving"/>
                    <@admin.adminFormField name="slug" data=page.slug!"" required=true type="text" localeCategory="page" />
                    <@admin.adminFormField name="title" data=page.title!"" required=true type="text" localeCategory="page" />
                    <@admin.adminFormField name="sortOrder" data=page.sortOrder?c!"" required=true type="number" localeCategory="page" />
                    <@admin.adminCkEditorField name="content" data=page.content!"" required=true type="text" localeCategory="page" rows=40/>
                </@admin.card>

                <@components.buttonGroup>
                    <a href="/administration/page" class="btn btn-bg-success transition-3d-hover">
                        <@spring.message code="page.cancel" />
                    </a>
                    <button type="reset" class="btn btn-light transition-3d-hover" name="reset">
                        <@spring.messageText code="page.reset" text="Reset" />
                    </button>&nbsp;&nbsp;
                    <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
                        <@spring.message code="page.save" />
                        <i class="fa fa-check-circle"></i>
                    </button>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>
</@layout.mainLayout>
