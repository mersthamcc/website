<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout formName="menu.register">
    <@components.panel>
        <form class="form-horizontal" method="post" name="action" action="/register/mailing-list">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <#if errors??>
                <@components.formMessages errors=errors errorKey="membership.errors" class="alert-danger"/>
            </#if>

            <@components.section title="Mailing List">
                <p>
                    <@spring.message code="membership.mailing-list-body" />
                </p>
                <div class="row form-group">
                    <label class="col-md-4 control-label">
                        <@spring.message code="membership.mailing-list" />
                    </label>
                    <div class="col-md-8">
                        <#list emails as email>
                            <div class="checkbox">
                                <label>
                                    <input
                                            type="checkbox"
                                            <#if email.subscribed>checked="checked"</#if>
                                            name="emailAddresses"
                                            value="${email.emailAddress}" />
                                    &nbsp;&nbsp;${email.emailAddress} &nbsp;
                                    <#if email.subscribed>
                                        <span class="link-underline ml-2">
                                            <@spring.message code="membership.mailing-list-subscribed" />
                                        </span>
                                    </#if>
                                    <#if email.pending>
                                        <span class="link-underline ml-2">
                                            <@spring.message code="membership.mailing-list-pending" />
                                        </span>
                                    </#if>
                                </label>
                            </div>
                        </#list>
                    </div>
                </div>
            </@components.section>

            <@components.buttonGroup>
                <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>
