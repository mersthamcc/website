<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>

<#macro dataScript>

</#macro>

<@layout.mainLayout script=dataScript>
    <div class="row">
        <div class="col-lg-12">
            <@admin.form  action="/administration/contacts/save">
                <@admin.card title="menu.admin-contact-details">
                    <input type="hidden" name="id" value="${contact.id?long?c}" />
                    <@admin.formErrors errors=errors![] errorKey="contact.errorSaving"/>
                    <@admin.adminFormField name="name" data=contact.name!"" required=true type="text" localeCategory="contact" />
                    <@admin.adminFormField name="position" data=contact.position!"" required=true type="text" localeCategory="contact" />
                    <@admin.adminFormField name="sortOrder" data=contact.sortOrder?c!"" required=true type="number" localeCategory="contact" />
                    <@admin.adminSelectField name="category" data=(contact.category.id?c)!"" required=true localeCategory="contact" options=categories/>
                </@admin.card>

                <@admin.card title="menu.admin-contact-methods">
                    <#list methods as method>
                        <@admin.adminFormField name="${method}" data=(contact_methods[method])!"" required=false type="text" localeCategory="contact" />
                    </#list>
                </@admin.card>

                <@components.buttonGroup>
                    <a href="/administration/contacts" class="btn btn-bg-success transition-3d-hover">
                        <@spring.message code="contact.cancel" />
                    </a>
                    <button type="reset" class="btn btn-light transition-3d-hover" name="reset">
                        <@spring.messageText code="contact.reset" text="Reset" />
                    </button>&nbsp;&nbsp;
                    <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
                        <@spring.message code="contact.save" />
                        <i class="fa fa-check-circle"></i>
                    </button>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>
</@layout.mainLayout>
