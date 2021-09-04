<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        function onPageLoad() {
        };
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
        <div class="col-lg-8">
            <#list form as section>
                <@admin.section title="membership.${section.section().key()}" action="" buttons=detailsFormButtons>
                    <#list section.section().attribute() as attribute>
                        <@admin.memberAdminField attribute=attribute data=data />
                    </#list>
                </@admin.section>
            </#list>

            <@components.buttonGroup>
                <a href="/administration/membership" class="btn btn-bg-success transition-3d-hover">
                    <@spring.message code="membership.complete" />
                </a>
            </@components.buttonGroup>
        </div>
    </div>

</@layout.mainLayout>