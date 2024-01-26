<#import "/spring.ftl" as spring />
<#import "../../mail/email-template.ftl" as email />

<@email.template header="Thank you">

    <@email.baseMembershipConfirmation season=season basket=basket order=order />

    <@email.paragraph>
        The following payment(s) have been scheduled:
    </@email.paragraph>

    <@email.table>
        <#list order.payment as payment>
            <@email.row
                left=(payment.date).format('dd/MM/yyyy')
                right=payment.amount?string.currency />
        </#list>
    </@email.table>

    <@email.paragraph>
        <b>Mandate cancellation</b>: please note the date of the last payment above, after this date no further payments will
        be taken and the mandate will automatically cancel shortly afterwards. Please do not cancel the mandate before all payments
        are taken.
    </@email.paragraph>

</@email.template>