<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        function onPageLoad() {
        };
    </script>
</#macro>
<#macro detailsFormButtons>
    <button type="reset" class="btn btn-light transition-3d-hover" name="action">
        <@spring.messageText code="membership.reset" text="Reset" />
    </button>&nbsp;&nbsp;
    <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
        <@spring.message code="membership.save" />
        <i class="fa fa-check-circle"></i>
    </button>
</#macro>
<@layout.mainLayout script=dataScript>
    <div class="row">
        <div class="col-lg-8">
            <#list subscription.priceListItem.memberCategory.form as section>
                <@admin.section title="membership.${section.section.key}" action="" footer=detailsFormButtons>
                    <#list section.section.attribute as attribute>
                        <@admin.memberAdminField attribute=attribute data=data />
                    </#list>
                </@admin.section>
            </#list>

            <@admin.adminTableCard
                id="subscriptionHistoryTable"
                selectable=false
                searchable=false
                defaultPageLength=10
                pageLengths=[10,25,50]
                title="membership.history"
                columns=subscriptionHistoryColumns
                data=subscriptionHistory
            />

            <@admin.adminTableCard
                id="paymentsTable"
                selectable=false
                searchable=false
                defaultPageLength=10
                pageLengths=[10,25,50]
                title="membership.payments"
                columns=paymentsColumns
                data=payments
                rightHeader=orderLabel
            />

            <@components.buttonGroup>
                <a href="/administration/membership" class="btn btn-bg-success transition-3d-hover">
                    <@spring.message code="membership.complete" />
                </a>
            </@components.buttonGroup>
        </div>
        <div class="col-lg-4">
            <div class="card mb-3 mb-lg-5">
                <div class="card-body text-center">
                    <!-- Avatar -->
                    <div class="avatar avatar-xl avatar-soft-dark avatar-circle avatar-border-lg avatar-centered mb-3">
                        <span class="avatar-initials">${data["given-name"]?cap_first[0]}${data["family-name"]?cap_first[0]}</span>
                        <span class="avatar-status avatar-sm-status avatar-status-success"></span>
                    </div>
                    <!-- End Avatar -->

                    <h1 class="page-header-title">
                        ${data["given-name"]} ${data["family-name"]}
                        <#if member.subscribedThisYear && member.paidThisYear>
                            <i class="tio-verified tio-lg text-primary"
                               data-toggle="tooltip" data-placement="top"
                               title="" data-original-title="Fully Paid"></i>
                        </#if>
                    </h1>
                    <ul class="list-unstyled">
                        <li class="list-item">
                            <#if subscription.priceListItem.memberCategory.key == "social">
                                <i class="tio-beer-bottle mr-1"></i>
                            <#else>
                                <i class="tio-cricket mr-1"></i>
                            </#if>
                            <span>
                                <@spring.messageText
                                    code="membership.${subscription.priceListItem.memberCategory.key}"
                                    text=subscription.priceListItem.memberCategory.key />
                            </span>
                        </li>

                        <#if data["email"]??>
                            <li class="list-item">
                                <i class="tio-email-outlined mr-1"></i>
                                <a href="mailto:${data["email"]}">${data["email"]}</a>
                            </li>
                        </#if>

                        <#if data["phone"]??>
                            <li class="list-item">
                                <i class="tio-android-phone mr-1"></i>
                                <a href="tel:${data["phone"]}">${data["phone"]}</a>
                            </li>
                        </#if>

                        <li class="list-item">
                            <i class="tio-date-range mr-1"></i>
                            <span>Last Renewal: ${subscription.year?c}</span>
                        </li>
                    </ul>
                    <div class="mb-3">
                    </div>

                    <!-- Badges -->
                    <ul class="list-inline list-inline-m-1 mb-0">
<#--                        <li class="list-inline-item"><a class="badge badge-soft-secondary p-2" href="#">1st XI</a></li>-->
                    </ul>
                    <!-- End Badges -->
                </div>
                <!-- End Body -->

            </div>
            <#if player??>
                <@admin.section title="membership.play-cricket.linked" action="" footer=playerStats headerRight=playCricketUnlink footerClasses="">
                    <div class="media">
                        <div class="avatar mr-3">
                            <img class="avatar-img" src="${resourcePrefix}/mcc/img/play-cricket-small.png" alt="Linked to PlayCricket">
                        </div>
                        <div class="media-body">
                            <span class="d-block h5 text-hover-primary mb-0">${player.name}</span>
                            <span class="d-block font-size-sm text-body">ID: ${player.id?c}</span>
                            <span class="d-block font-size-sm text-body">
                                <a class="link link-underline" href="https://merstham.play-cricket.com/site_admin/users/${member.playerId}/details">
                                    <@spring.messageText code="membership.play-cricket.detail" text="View details" />
                                </a>
                                <span class="border-3"> | </span>
                                <a class="link link-underline" href="https://merstham.play-cricket.com/player_stats/batting/${member.playerId}">
                                    <@spring.messageText code="membership.play-cricket.stats" text="View stats" />
                                </a>
                            </span>
                        </div>
                    </div>
                </@admin.section>
            <#else>
                <@admin.section title="membership.play-cricket.link" action="${member.id}/play-cricket-link" footer=detailsFormButtons>
                    <select name="play-cricket-id" class="js-select2-custom custom-select" size="1" style="opacity: 0;"
                            data-hs-select2-options='{
                              "placeholder": "Select a player...",
                              "searchInputPlaceholder": "Search PlayCricket",
                              "minimumInputLength": 3,
                              "ajax": {
                                "url": "/administration/players/list",
                                "dataType": "json"
                              }
                            }'>
                    </select>
                </@admin.section>
            </#if>

            <@admin.section title="membership.sync-status.title" action="">
                <div class="list-group list-group-lg list-group-flush list-group-no-gutters">
                    <!-- List Item -->
                    <div class="list-group-item">
                        <div class="media">
                            <div class="avatar mr-3">
                                <img class="avatar-img" src="${resourcePrefix}/mcc/img/epos-now.png" alt="EposNow">
                            </div>
                            <div class="media-body">
                                <div class="row align-items-center">
                                    <div class="col-sm mb-1 mb-sm-0">
                                        <h6 class="mb-0">
                                            <@spring.message code="membership.sync-status.epos" />
                                        </h6>
                                        <#if member.eposId??>
                                            <span class="small">ID: ${member.eposId}</span>
                                        <#else>
                                            <span class="small">
                                                <@spring.message code="membership.sync-status.not-synced" />
                                            </span>
                                        </#if>
                                    </div>

                                    <div class="col-sm-auto">
                                        <#if member.eposId??>
                                            <span class="badge badge-soft-success ml-2">
                                                <@spring.message code="membership.sync-status.active"/>
                                            </span>
                                        <#else>
                                            <span class="badge badge-soft-danger ml-2">
                                                <@spring.message code="membership.sync-status.inactive"/>
                                            </span>
                                        </#if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- End List Item -->
                </div>
            </@admin.section>

            <#if owner??>
                <@admin.section title="membership.owner.title" action="">
                    <div class="list-group list-group-lg list-group-flush list-group-no-gutters">
                        <!-- List Item -->
                        <div class="list-group-item">
                            <div class="media">
                                <div class="avatar avatar-soft-dark avatar-circle avatar-border-lg avatar-centered mr-3">
                                    <span class="avatar-initials">${owner.givenName?cap_first[0]}${owner.familyName?cap_first[0]}</span>
                                    <#if owner.verified>
                                        <span class="avatar-status avatar-sm-status avatar-status-success"></span>
                                    </#if>
                                </div>
                                <div class="media-body">
                                    <div class="row align-items-center">
                                        <div class="col-sm mb-1 mb-sm-0">
                                            <h6 class="mb-0">
                                                ${owner.fullName}
                                            </h6>
                                            <div class="small">Email: ${owner.email}</div>
                                            <div class="small">Phone: ${owner.phoneNumber}</div>
                                        </div>

                                        <div class="col-sm-auto">
                                            <#if owner.enabled>
                                                <span class="badge badge-soft-success ml-2">
                                                    <@spring.message code="membership.owner.enabled"/>
                                                </span>
                                            <#else>
                                                <span class="badge badge-soft-danger ml-2">
                                                    <@spring.message code="membership.owner.disabled"/>
                                                </span>
                                            </#if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- End List Item -->
                    </div>
                </@admin.section>
            </#if>

            <#if linkedMembers?has_content>
                <@admin.section title="membership.linked-members.title" action="">
                    <div class="list-group list-group-lg list-group-flush list-group-no-gutters">
                        <#list linkedMembers as m>
                            <div class="list-group-item">
                                <div class="media">
                                    <div class="avatar avatar-soft-dark avatar-circle avatar-border-lg avatar-centered mr-3">
                                        <span class="avatar-initials">${m.givenName?cap_first[0]}${m.familyName?cap_first[0]}</span>
                                    </div>
                                    <div class="media-body">
                                        <div class="row align-items-center">
                                            <div class="col-sm mb-1 mb-sm-0">
                                                <h6 class="mb-0">
                                                    ${m.givenName} ${m.familyName}
                                                </h6>
                                                <div class="small">
                                                    <@spring.messageText code="membership.${m.lastSubsCategory}" text=m.lastSubsCategory />
                                                </div>
                                            </div>

                                            <div class="col-sm-auto">
                                                <a class="js-edit btn btn-sm btn-white" href="/administration/membership/get-data/${m.id}">
                                                    <i class="js-edit-icon tio-edit"></i> Edit
                                                </a>&nbsp;
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    </div>
                </@admin.section>
            </#if>
        </div>
    </div>
</@layout.mainLayout>

<#macro playCricketUnlink>
    <a class="link link-underline" href="${member.id}/delete-play-cricket-link">
        <@spring.messageText code="membership.play-cricket.unlink" text="Unlink" />
    </a>
</#macro>

<#macro playerStats>
    <div class="row">
        <div class="col">
            <span class="h3">${player.fixturesThisYear}</span>
            <span class="d-block font-size-sm">
                <@spring.messageText code="membership.play-cricket.this-year" text="This Year" />
            </span>
        </div>

        <div class="col column-divider">
            <span class="h3">${player.fixturesLastYear}</span>
            <span class="d-block font-size-sm">
                <@spring.messageText code="membership.play-cricket.last-year" text="Last Year" />
            </span>
        </div>

        <div class="col column-divider">
            <span class="h3">
                <#if player.earliestDate??>
                    ${(player.earliestDate).format('yyyy')!"?"}
                <#else>
                    ?
                </#if>
            </span>
            <span class="d-block font-size-sm">
                <@spring.messageText code="membership.play-cricket.first-game" text="Year of first game" />
            </span>
        </div>
    </div>
</#macro>

<#macro orderLabel>
    <div class="h2">
        <span class="badge badge-pill badge-soft-primary">
            ${subscription.order.webReference}
        </span>
    </div>
</#macro>
