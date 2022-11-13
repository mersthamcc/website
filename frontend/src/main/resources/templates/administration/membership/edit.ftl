<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>
<#macro dataScript>
    <script>
        function onPageLoad() {
            <@admin.adminTableScript id="subscriptionHistoryTable" />
            <@admin.adminTableScript id="paymentsTable" />
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
                <@admin.section title="membership.${section.section.key}" action="" buttons=detailsFormButtons>
                    <#list section.section.attribute as attribute>
                        <@admin.memberAdminField attribute=attribute data=data />
                    </#list>
                </@admin.section>
            </#list>

            <@admin.adminTableCard
                id="paymentsTable"
                selectable=false
                searchable=false
                defaultPageLength=10
                pageLengths=[1,5,15]
                title="membership.payments"
                columns=paymentsColumns
                data=payments
            />

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

            <@components.buttonGroup>
                <a href="/administration/membership" class="btn btn-bg-success transition-3d-hover">
                    <@spring.message code="membership.complete" />
                </a>
            </@components.buttonGroup>
        </div>
        <div class="col-lg-4">
            <div class="card">
                <div class="card-body text-center">
                    <!-- Avatar -->
                    <div class="avatar avatar-xl avatar-soft-dark avatar-circle avatar-border-lg avatar-centered mb-3">
                        <span class="avatar-initials">${data["given-name"]?cap_first[0]}${data["family-name"]?cap_first[0]}</span>
                        <span class="avatar-status avatar-sm-status avatar-status-success"></span>
                    </div>
                    <!-- End Avatar -->

                    <h1 class="page-header-title">
                        ${data["given-name"]} ${data["family-name"]}
                        <i class="tio-verified tio-lg text-primary"
                           data-toggle="tooltip" data-placement="top"
                           title="" data-original-title="Top endorsed"></i>
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
                                <a href="#">${data["email"]}</a>
                            </li>
                        </#if>

                        <#if data["phone"]??>
                            <li class="list-item">
                                <i class="tio-android-phone mr-1"></i>
                                <a href="#">${data["phone"]}</a>
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
                        <#--                  <li class="list-inline-item"><a class="badge badge-soft-secondary p-2" href="#">1st XI</a></li>-->
                    </ul>
                    <!-- End Badges -->
                </div>
                <!-- End Body -->

                <!-- Footer -->
                <div class="card-footer">
                    <div class="row justify-content-between align-items-center">
                        <div class="col-auto py-1">
                            <a class="font-size-sm text-body" href="#"></a>
                        </div>

                        <div class="col-auto py-1">
                        </div>
                    </div>
                </div>
                <!-- End Footer -->
            </div>
        </div>
    </div>

</@layout.mainLayout>