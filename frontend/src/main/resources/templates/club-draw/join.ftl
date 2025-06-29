<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />
<#import "../components.ftl" as components />

<#macro headers>

</#macro>

<@layout.mainLayout headers=headers formName="club-draw.join">
    <@components.panel>
        <form class="form-horizontal" method="post" name="club-draw-form" id="club-draw-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <#if existingMandates?size != 0 >
                <@components.section title="Use existing mandate?">
                    <#list existingMandates as mandate>
                        <div class="form-group">
                            <div class="custom-control custom-radio">
                                <input
                                        type="radio"
                                        class="custom-control-input"
                                        name="mandate"
                                        id="mandateOptions${mandate.mandate.id}"
                                        value="${mandate.mandate.id}"
                                        <#if mandate?is_first>checked="checked"</#if> />
                                <label class="custom-control-label" for="mandateOptions${mandate.mandate.id}">
                                    <div class="text-body mb-1">
                                        <span><b>Bank:</b></span>
                                        <span>${mandate.customerBankAccount.bankName}</span>
                                    </div>
                                    <div class="text-body mb-1">
                                        <span><b>Account Number:</b></span>
                                        <span>******${mandate.customerBankAccount.accountNumberEnding}</span>
                                    </div>
                                    <div class="text-body mb-1">
                                        <span><b>Account Name:</b></span>
                                        <span>${mandate.customerBankAccount.accountHolderName}</span>
                                    </div>
                                    <div class="text-body mb-1">
                                        <span><b>Reference:</b></span>
                                        <span>${mandate.mandate.reference}</span>
                                    </div>
                                </label>
                            </div>
                        </div>
                    </#list>
                    <div class="form-group">
                        <div class="custom-control custom-radio">
                            <input type="radio" class="custom-control-input" name="mandate" id="mandateOptionsNew" value="new" />
                            <label class="custom-control-label" for="mandateOptionsNew">
                                <div class="text-body mb-1">
                                    Create new mandate
                                </div>
                            </label>
                        </div>
                    </div>
                </@components.section>
            </#if>

            <@components.buttonGroup>
                <a href="/register" class="btn btn-danger transition-3d-hover">
                    <@spring.message code="membership.cancel" />
                </a>&nbsp;
                <button type="submit" class="btn btn-primary btn-xlg transition-3d-hover" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>