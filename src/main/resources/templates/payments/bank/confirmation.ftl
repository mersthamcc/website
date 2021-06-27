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
            <table>
                <tr>
                    <th>Account Name</th>
                    <td>${bankAccountName}</td>
                </tr>
                <tr>
                    <th>Account Number</th>
                    <td>${bankAccountNumber}</td>
                </tr>
                <tr>
                    <th>Sort Code</th>
                    <td>${bankAccountSortCode}</td>
                </tr>
            </table>
        </@components.section>

        <@components.buttonGroup>
            <a class="btn btn-success btn-xlg transition-3d-hover" href="/">
                <@spring.message code="membership.complete" />
                <i class="fa fa-icon-home"></i>
            </a>
        </@components.buttonGroup>
    </@components.panel>
</@layout.mainLayout>
