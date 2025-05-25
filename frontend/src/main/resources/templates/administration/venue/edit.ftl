<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>

<#macro dataScript>
    <script>
        ClassicEditor
            .create(document.querySelector('#description-editor'), {
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
                        section: 'venues',
                        startupPath: '0-Images:/',
                        rememberLastFolder: false,
                        pass: 'section'
                    }
                }
            })
            .then(editor => {
                CKEditorInspector.attach(editor);
            })
            .catch( error => {
                console.error(error);
            });
        ClassicEditor
            .create(document.querySelector('#directions-editor'), {
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
                        section: 'venues',
                        startupPath: '0-Images:/',
                        rememberLastFolder: false,
                        pass: 'section'
                    }
                }
            })
            .then(editor => {
                CKEditorInspector.attach(editor);
            })
            .catch( error => {
                console.error(error);
            });
        ClassicEditor
            .create(document.querySelector('#address-editor'), {
                toolbar: [
                    'heading', '|',
                    'bold', 'italic', 'link', 'bulletedList', 'numberedList', 'blockQuote', '|',
                    'undo', 'redo', '|',
                    'sourceEditing']
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
            <@admin.form  action="/administration/venue/save">
                <@admin.card title="venue.details">
                    <@admin.formErrors errors=errors![] errorKey="venue.errorSaving"/>
                    <@admin.adminFormField name="slug" data=venue.slug!"" required=true type="text" localeCategory="venue" />
                    <@admin.adminFormField name="name" data=venue.name!"" required=true type="text" localeCategory="venue" />
                    <@admin.adminFormField name="sortOrder" data=venue.sortOrder?c!"" required=true type="number" localeCategory="venue" />
                    <@admin.adminFormField name="aliasFor" data=venue.aliasFor!"" required=false type="number" localeCategory="venue" />
                    <@admin.adminSwitchField name="showOnMenu" checked=venue.showOnMenu localeCategory="venue" />
                    <@admin.adminCkEditorField name="description" data=venue.description!"" required=false localeCategory="venue" rows=40/>
                    <@admin.adminCkEditorField name="directions" data=venue.directions!"" required=false localeCategory="venue" rows=40/>
                </@admin.card>

                <@admin.card title="venue.address-details">
                    <@admin.adminCkEditorField name="address" data=venue.address!"" required=true localeCategory="venue" />
                    <@admin.adminFormField name="postCode" data=venue.postCode!"" required=true type="text" localeCategory="venue" />
                </@admin.card>

                <@admin.card title="venue.google-maps">
                    <@admin.adminFormField name="latitude" data=venue.latitude!0?c required=false type="text" localeCategory="venue" />
                    <@admin.adminFormField name="longitude" data=venue.longitude!0?c required=false type="text" localeCategory="venue" />
                    <@admin.adminFormField name="marker" data=venue.marker!"" required=false type="text" localeCategory="venue" />
                </@admin.card>

                <@components.buttonGroup>
                    <a href="/administration/venues" class="btn btn-bg-success transition-3d-hover">
                        <@spring.message code="venue.cancel" />
                    </a>
                    <button type="reset" class="btn btn-light transition-3d-hover" name="reset">
                        <@spring.messageText code="venue.reset" text="Reset" />
                    </button>&nbsp;&nbsp;
                    <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
                        <@spring.message code="venue.save" />
                        <i class="fa fa-check-circle"></i>
                    </button>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>
</@layout.mainLayout>
