<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>

<#macro dataScript>
    <script>
        ClassicEditor
            .create(document.querySelector('#body-editor'), {
                toolbar: [
                    'heading', '|',
                    'bold', 'italic', 'link', 'bulletedList', 'numberedList', 'blockQuote', '|',
                    'readMore', '|',
                    'undo', 'redo', '|',
                    'ckfinder', 'mediaEmbed', '|',
                    'sourceEditing'],
                ckfinder: {
                    uploadUrl: '/administration/components/ckfinder/connector?command=QuickUpload&type=Files&responseType=json',
                    options: {
                        connectorPath: '/administration/components/ckfinder/connector',
                        section: 'events',
                        startupPath: '0-Images:/',
                        rememberLastFolder: false,
                        uuid: '${event.uuid}',
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
            <@admin.form  action="/administration/event/save">
                <@admin.card title="menu.admin-event-new">
                    <input type="hidden" name="id" value="${event.id?long?c}" />
                    <input type="hidden" name="uuid" value="${event.uuid}" />
                    <@admin.formErrors errors=errors![] errorKey="event.errorSaving"/>
                    <@admin.adminDateTimeField name="eventDate" data=event.eventDate localeCategory="event" />
                    <@admin.adminFormField name="title" data=event.title!"" required=true type="text" localeCategory="event" />
                    <@admin.adminFormField name="location" data=event.location!"" required=true type="text" localeCategory="event" />
                    <@admin.adminCkEditorField name="body" data=event.body!"" required=true type="text" localeCategory="event" rows=40 />
                </@admin.card>

                <@components.buttonGroup>
                    <a href="/administration/event" class="btn btn-bg-success transition-3d-hover">
                        <@spring.message code="event.cancel" />
                    </a>
                    <button type="reset" class="btn btn-light transition-3d-hover" name="reset">
                        <@spring.messageText code="event.reset" text="Reset" />
                    </button>&nbsp;&nbsp;
                    <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
                        <@spring.message code="event.save" />
                        <i class="fa fa-check-circle"></i>
                    </button>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>
</@layout.mainLayout>
