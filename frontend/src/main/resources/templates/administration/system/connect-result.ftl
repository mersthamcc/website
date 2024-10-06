<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>

<@layout.mainLayout>
    <div class="card-body card-body-centered py-10">
        <div class="text-center">
            <@admin.form action="">
                <@admin.card title="menu.admin-system-oauth">
                    <@admin.adminFormDisplayField name="name" data=name!"" localeCategory="system" />
                    <@admin.adminFormDisplayField name="code" data=code!"" localeCategory="system" />
                    <@admin.adminFormDisplayField name="state" data=state!"" localeCategory="system" />
                    <@admin.adminFormDisplayField name="success" data=result.success?c!"" localeCategory="system" />
                    <@admin.adminFormDisplayField name="message" data=result.message!"" localeCategory="system" />
                </@admin.card>

                <@components.buttonGroup>
                    <a href="/administration" class="btn btn-secondary transition-3d-hover mr-2">
                        <@spring.message code="system.cancel" />
                    </a>

                    <a href="/administration/system/oauth/${name}" class="btn btn-primary transition-3d-hover">
                        <@spring.message code="system.retry" />
                    </a>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>
</@layout.mainLayout>