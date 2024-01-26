<#import "/spring.ftl" as spring />
<#import "../../mail/email-template.ftl" as email />

<@email.template header="Thank you">

    <@email.baseMembershipConfirmation season=season basket=basket order=order />

    <@email.paragraph>
        You have opted to pay by bank transfer. Please arrange a transfer to the following details at quoting reference ${order.webReference}.
    </@email.paragraph>

    <@email.table>
        <@email.row left="Account Name" right=bankAccountName />
        <@email.row left="Account Number" right=bankAccountNumber />
        <@email.row left="Sort Code" right=bankAccountSortCode />
        <@email.row left="Reference" right=order.webReference />
        <@email.row left="Amount" right=basket.basketTotal?string.currency />
    </@email.table>

</@email.template>