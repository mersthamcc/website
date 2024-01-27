<#import "/spring.ftl" as spring />
<#import "../../mail/email-template.ftl" as email />

<@email.template header="Thank you">

    <@email.baseMembershipConfirmation season=season basket=basket order=order translations=discountTranslations />

    <@email.paragraph>
        You have paid, in full, using credit/debit card.
    </@email.paragraph>

    <@email.paragraph>
        You will also receive a confirmation e-mail from Stripe.
    </@email.paragraph>

</@email.template>