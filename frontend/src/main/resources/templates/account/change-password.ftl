<#import "base.ftl" as home>
<#import "../components.ftl" as components>
<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<@layout.mainLayout formName="menu.account" withButtonResponsiveButton=true>
    <@home.homeLayout userDetails=userDetails>
        <div class="card mb-3 mb-lg-5">
            <div class="card-header">
                <h5 class="card-title">
                    <@spring.message code="account.password.title" />
                </h5>
            </div>

            <div class="card-body">
                <#if errors??>
                    <@components.formMessages errors=errors errorKey="account.error.change-password-title" />
                </#if>
                <#if info??>
                    <div class="alert alert-info" role="alert">
                        <h5 class="alert-heading">
                            <@spring.message code="account.success.title"/>
                        </h5>

                        <#list info as code>
                            <p class="text-inherit">
                                <@spring.messageText code=code text=code />
                            </p>
                        </#list>
                    </div>
                </#if>

                <form method="post" name="user-details-form" action="/account/change-password">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <input type="hidden" name="subjectId" value="${userDetails.subjectId}">
                    <div class="row form-group">
                        <label for="currentPassword" class="col-sm-3 col-form-label input-label">
                            <@spring.message code="account.password.current" />
                        </label>

                        <div class="col-sm-9">
                            <input
                                    type="password"
                                    class="form-control"
                                    name="currentPassword"
                                    id="currentPassword"
                                    placeholder="<@spring.message code="account.password.current-placeholder" />"
                                    aria-label="<@spring.message code="account.password.current-placeholder" />">
                        </div>
                    </div>

                    <div class="row form-group">
                        <label for="password" class="col-sm-3 col-form-label input-label">
                            <@spring.message code="account.password.new" />
                        </label>

                        <div class="col-sm-9">
                            <input
                                    type="password"
                                    class="form-control"
                                    name="password"
                                    id="password"
                                    placeholder="<@spring.message code="account.password.new-placeholder" />"
                                    aria-label="<@spring.message code="account.password.new-placeholder" />">
                        </div>
                    </div>

                    <div class="row form-group">
                        <label for="confirmPassword" class="col-sm-3 col-form-label input-label">
                            <@spring.message code="account.password.confirm" />
                        </label>

                        <div class="col-sm-9">
                            <div class="mb-3">
                                <input
                                        type="password"
                                        class="form-control"
                                        name="confirmPassword"
                                        id="confirmPassword"
                                        placeholder="<@spring.message code="account.password.confirm-placeholder" />"
                                        aria-label="<@spring.message code="account.password.confirm-placeholder" />">
                            </div>

                            <h5>
                                <@spring.message code="account.password.requirements" />
                            </h5>

                            <ul class="font-size-1">
                                <#list passwordRequirements as message, parm>
                                    <li>
                                        <@spring.messageArgs code=message args=[parm] />
                                    </li>
                                </#list>
                            </ul>
                        </div>
                    </div>

                    <div class="d-flex justify-content-end">
                        <a class="btn btn-white" href="javascript:;">Cancel</a>
                        <span class="mx-2"></span>
                        <button type="submit" class="btn btn-primary">Update Password</button>
                    </div>
                </form>
            </div>
        </div>
    </@home.homeLayout>
</@layout.mainLayout>
