<#import "/spring.ftl" as spring />
<#import "../../../admin-base.ftl" as layout>
<#import "../../../components.ftl" as components>
<#import "../../../admin-components.ftl" as admin>

<#macro dataScript>

</#macro>

<@layout.mainLayout script=dataScript>
    <div class="row">
        <div class="col-lg-12">
            <@admin.form  action="/administration/system/static-data/save">
                <input type="hidden" name="id" value="${data.id!}">
                <@admin.card title="menu.admin-system-data-new">
                    <@admin.formErrors errors=errors![] errorKey="static-data.errorSaving"/>
                    <@admin.adminFormField name="path" data=data.path!"" required=true type="text" localeCategory="static-data" />
                    <@admin.adminFormField name="statusCode" data=data.statusCode!200?c required=true type="number" localeCategory="static-data" />
                    <@admin.adminFormField name="contentType" data=data.contentType!"" required=true type="text" localeCategory="static-data" />
                    <@admin.adminCkEditorField name="content" data=data.content!"" required=true type="text" localeCategory="static-data" rows=60/>
                </@admin.card>

                <@components.buttonGroup>
                    <a href="/administration/system/static-data" class="btn btn-bg-success transition-3d-hover">
                        <@spring.message code="static-data.cancel" />
                    </a>
                    <button type="reset" class="btn btn-light transition-3d-hover" name="reset">
                        <@spring.messageText code="static-data.reset" text="Reset" />
                    </button>&nbsp;&nbsp;
                    <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
                        <@spring.message code="static-data.save" />
                        <i class="fa fa-check-circle"></i>
                    </button>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>
</@layout.mainLayout>
