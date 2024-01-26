<#import "/spring.ftl" as spring />
<#import "../../mail/email-template.ftl" as email />

<@email.template header="Thank you">

    <@email.baseMembershipConfirmation season=season basket=basket order=order />

</@email.template>