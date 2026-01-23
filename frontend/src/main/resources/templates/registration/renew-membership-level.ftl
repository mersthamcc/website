<#import "../base.ftl" as layout>
<#import "../components.ftl" as components />
<#import "/spring.ftl" as spring />

<@layout.mainLayout formName="membership.choose-membership-type">
    <@components.panel>
        <@components.section title="membership.continue-with-membership-level-title">
            <p>
                <@spring.message code="membership.continue-with-membership-level" />
            </p>
            <p>
                <@spring.message code="membership.check-eligibility" />
            </p>
            <div class="row position-relative z-index-2 mx-n2 mb-5">
                <@components.pricelistItem
                    item=item
                    category=item.memberCategory
                    _csrf=_csrf
                    subscriptionId=subscriptionId
                    buttonTitle="membership.yes"
                    negativeButtonTitle="membership.change-type"
                />
            </div>
            <p>
                <b>*</b>&nbsp;<@spring.message code="membership.eligibility-footnote" />
            </p>
        </@components.section>
        <@components.buttonGroup>
            <a href="/register" class="btn btn-danger transition-3d-hover">
                <@spring.message code="membership.cancel" />
            </a>&nbsp;
        </@components.buttonGroup>
    </@components.panel>
</@layout.mainLayout>