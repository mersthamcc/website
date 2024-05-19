<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>

<@layout.mainLayout headers=headers>
    <div class="container space-2">
        <#list dates as date>
            <div class="w-md-80 w-lg-50 text-center mx-md-auto mb-5 mb-md-9">
                <span class="d-block small font-weight-bold text-cap mb-2">
                    ${(date).format("EEEE")}
                </span>
                <h2 class="h1">
                    ${(date).format("MMMM dd")}
                </h2>
            </div>

            <div class="row mb-5">
                <#list selection as fixture>
                    <#if fixture.date == date>
                        <div class="col-md-6 col-lg-4 mb-4 mb-md-5 mb-lg-0">
                            <div class="card shadow-soft h-100 transition-3d-hover">
                                <div class="card-body pt-7 px-7">
                                    <h3 class="text-center">
                                        <a href="/fixtures/2024/${fixture.team.id?c}">${fixture.team.name}</a>
                                    </h3>
                                    <h6 class="text-center">
                                        <@spring.message code="fixtures.versus_short" />
                                    </h6>
                                    <h5 class="text-center">${fixture.opposition}</h5>
                                    <div class="text-body">
                                        <div class="list-group list-group-lg list-group-flush list-group-no-gutters">
                                            <#if (fixture.players?? && fixture.players?size > 0) >
                                                <#list fixture.players as player>
                                                    <div class="list-group-item">
                                                        <div class="media">
                                                            <div class="media-body">
                                                                <div class="row">
                                                                    <div class="col-sm mb-1 mb-sm-0">
                                                                        <h6 class="mb-0">
                                                                            ${player.name}
                                                                            <#if player.captain>
                                                                                <span class="badge badge-soft-primary ml-1">
                                                                                    <@spring.message code="fixtures.captain" />
                                                                                </span>
                                                                            </#if>
                                                                            <#if player.wicketKeeper>
                                                                                <span class="badge badge-soft-secondary ml-1">
                                                                                    <@spring.message code="fixtures.wicket-keeper" />
                                                                                </span>
                                                                            </#if>
                                                                        </h6>
    <#--                                                                    <small><strong>This years stats:</strong> </small>-->
    <#--                                                                    <small>|</small>-->
    <#--                                                                    <small><strong>This years stats:</strong></small>-->
                                                                    </div>

                                                                    <div class="col-sm-auto">
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </#list>
                                            <#else>
                                                <div class="list-group-item">
                                                    <div class="media">
                                                        <div class="media-body">
                                                            <div class="row">
                                                                <div class="col-sm mb-1 mb-sm-0">
                                                                    <h6 class="mb-0">
                                                                        <@spring.message code="fixtures.no-selection" />
                                                                    </h6>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                            </#if>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-footer border-0 pt-0 pb-7 px-7">
                                    <#if fixture.homeAway == "HOME">
                                        <span class="font-size-sm">
                                            <i class="fas fa-home fa-sm ml-1"></i>
                                            <@spring.message code="fixtures.home" />
                                        </span>
                                    <#else>
                                        <span class="font-size-sm">
                                            <i class="fas fa-globe fa-sm ml-1"></i>
                                            <@spring.message code="fixtures.away" />
                                        </span>
                                    </#if>
                                </div>
                            </div>
                        </div>
                    </#if>
                </#list>
            </div>
            <#if date?has_next>
                <span class="divider divider-text mb-5">
                    <i class="fas fa-calendar">&nbsp;</i>
                </span>
            </#if>
        </#list>
    </div>
</@layout.mainLayout>