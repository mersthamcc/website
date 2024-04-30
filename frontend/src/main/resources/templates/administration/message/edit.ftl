<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>

<#macro dataScript>
    <script>
        const debug = ${debug?c};
        ClassicEditor
            .create(document.querySelector('#messageText-editor'), {
                toolbar: [
                    'bold', 'italic', 'link', '|',
                    'undo', 'redo', '|',
                    'sourceEditing']
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
            <@admin.form action=message.key>
                <@admin.card title="message.edit">
                    <input type="hidden" name="key" value="${message.key}" />
                    <@admin.formErrors errors=errors![] errorKey="message.errorSaving"/>
                    <@admin.adminSelectField
                        name="messageClass"
                        data=message.messageClass!""
                        required=true
                        localeCategory="message"
                        options={
                            "alert-info": "Info",
                            "alert-warning": "Warning",
                            "alert-danger": "Danger"
                        }
                    />
                    <@admin.adminCkEditorField
                        name="messageText"
                        data=message.messageText!""
                        required=true
                        type="text"
                        localeCategory="message"
                        rows=1
                    />
                    <@admin.adminSwitchField name="enabled" checked=message.enabled!false localeCategory="message" />
                </@admin.card>

                <@components.buttonGroup>
                    <button type="reset" class="btn btn-light transition-3d-hover" name="reset">
                        <@spring.messageText code="message.reset" text="Reset" />
                    </button>&nbsp;&nbsp;
                    <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
                        <@spring.message code="message.save" />
                        <i class="fa fa-check-circle"></i>
                    </button>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>
</@layout.mainLayout>