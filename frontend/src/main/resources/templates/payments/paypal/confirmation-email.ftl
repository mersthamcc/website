<#import "/spring.ftl" as spring />
<#import "../../mail/email-template.ftl" as email />

<@email.template header="Thank you">

    <@email.baseMembershipConfirmation season=season basket=basket order=order translations=discountTranslations />

    <#assign payment=order.payment[0] />
    <@email.paragraph>
        You have paid using PayPal and your PayPal reference is ${payment.reference}.
    </@email.paragraph>

    <@email.paragraph>
        You will also receive a confirmation e-mail from PayPal.
    </@email.paragraph>

</@email.template>