<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<@layout.mainLayout formName="membership.confirmation">
    <@components.panel>
        <@components.section title="Bank Transfer">
            <@spring.message code="payments.bank-confirmation" />
        </@components.section>

        <@components.section title="Bank Details">
            <@spring.message code="payments.bank-arrange-transfer" />
            <div class="row">
                <div class="col-md-4">
                    <b>Account Name</b>
                </div>
                <div class="col-md-8">
                    ${bankAccountName}
                </div>
            </div>
            <div class="row">
                <div class="col-md-4">
                    <b>Account Number</b>
                </div>
                <div class="col-md-8">
                    ${bankAccountNumber}
                </div>
            </div>
            <div class="row">
                <div class="col-md-4">
                    <b>Sort Code</b>
                </div>
                <div class="col-md-8">
                    ${bankAccountSortCode}
                </div>
            </div>
            <div class="row">
                <div class="col-md-4">
                    <b>Payment Reference</b>
                </div>
                <div class="col-md-8">
                    ${orderReference}
                </div>
            </div>
        </@components.section>

        <@components.buttonGroup>
            <a class="btn btn-success btn-xlg transition-3d-hover" href="/">
                <@spring.message code="membership.complete" />
                <i class="fa fa-icon-home"></i>
            </a>
        </@components.buttonGroup>
    </@components.panel>
</@layout.mainLayout>
