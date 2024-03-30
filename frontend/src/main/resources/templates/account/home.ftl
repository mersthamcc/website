<#import "base.ftl" as home>
<#import "../base.ftl" as layout>
<#import "../components.ftl" as components>
<#import "/spring.ftl" as spring />

<@layout.mainLayout formName="menu.account" withButtonResponsiveButton=true>
    <@home.homeLayout userDetails=userDetails>
        <form method="post" name="user-details-form" action="/account">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="subjectId" value="${userDetails.subjectId}">
            <div class="card mb-3 mb-lg-5">
                <div class="card-header">
                    <h5 class="card-title">
                        <@spring.message code="account.menu.account-home" />
                    </h5>
                </div>
                <div class="card-body">
                    <#if errors??>
                        <@components.formMessages errors=errors errorKey="account.error.title" />
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

                    <div class="row form-group">
                        <label for="givenNameLabel" class="col-sm-3 col-form-label input-label">
                            <@spring.message code="account.name" />
                        </label>

                        <div class="col-sm-9">
                            <div class="input-group">
                                <input type="text" class="form-control" name="givenName" required="required" id="givenNameLabel" placeholder="<@spring.message code="account.given-name" />" aria-label="${userDetails.givenName}" value="${userDetails.givenName}">
                                <input type="text" class="form-control" name="familyName" required="required" id="familyNameLabel" placeholder="<@spring.message code="account.family-name" />" aria-label="${userDetails.familyName}" value="${userDetails.familyName}">
                            </div>
                        </div>
                    </div>

                    <div class="row form-group">
                        <label for="emailLabel" class="col-sm-3 col-form-label input-label">
                            <@spring.message code="account.email" />
                        </label>

                        <div class="col-sm-9">
                            <input type="email" class="form-control" name="email" required="required" id="emailLabel" placeholder="<@spring.message code="account.email" />" aria-label="${userDetails.email}" value="${userDetails.email}">
                        </div>
                    </div>

                    <div class="js-add-field row form-group" data-hs-add-field-options="{
                            &quot;template&quot;: &quot;#addPhoneFieldTemplate&quot;,
                            &quot;container&quot;: &quot;#addPhoneFieldContainer&quot;,
                            &quot;defaultCreated&quot;: 0
                          }">
                        <label for="phoneLabel" class="col-sm-3 col-form-label input-label">
                            <@spring.message code="account.phone" />
                            <span class="input-label-secondary">(Optional)</span>
                        </label>

                        <div class="col-sm-9">
                            <div class="input-group align-items-center">
                                <input type="text" class="js-masked-input form-control" name="phoneNumber" id="phoneLabel" placeholder="+44 xxxx xxxxxx" aria-label="+44 xxxx xxxxxx" value="${userDetails.phoneNumber}" data-hs-mask-options="{
                                   &quot;template&quot;: &quot;+44 xxxx xxxxxx&quot;
                                 }" maxlength="16">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="card-footer d-flex justify-content-end">
                    <button class="btn btn-white" type="reset">
                        <@spring.message code="account.reset" />
                    </button>
                    <span class="mx-2"></span>
                    <button class="btn btn-primary" type="submit">
                        <@spring.message code="account.save" />
                    </button>
                </div>
            </div>
        </form>
    </@home.homeLayout>
</@layout.mainLayout>
