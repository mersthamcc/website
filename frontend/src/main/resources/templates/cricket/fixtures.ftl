<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro imageScripts>
    <script>

    </script>
</#macro>

<@layout.mainLayout script=imageScripts headers=headers>
    <div class="container space-2">
        <div class="row justify-content-lg-between">
            <div class="col-md-3">
                <@components.teamList activeTeam=activeTeam season=season teams=teams />
            </div>

            <div class="col-md-9">
                <div class="container">
                    <div class="profile-cover">
                        <div class="profile-cover-img-wrapper">
                            <img class="profile-cover-img" src="${resourcePrefix}/front/assets/img/1920x400/img1.jpg" alt="Image Description">
                        </div>
                    </div>

                    <div class="d-sm-flex align-items-lg-center pt-1 px-3 pb-3">
                        <#if false>
                            <div class="mb-2 mb-sm-0 mr-4">
                                <img class="avatar avatar-xl profile-cover-avatar shadow-soft" src="${resourcePrefix}/front/assets/svg/brands/capsule.svg" alt="Image Description">
                            </div>
                        </#if>

                        <div class="media-body">
                            <div class="row">
                                <div class="col-lg mb-1">
                                    <h1 class="h2 mb-1">${activeTeam.name}</h1>

                                    <div class="d-flex align-items-center">
                                        <#if activeTeam.captain??>
                                            <span class="ml-1 mb-2">
                                                <i class="fa fa-user"></i>
                                            </span>
                                            <span class="font-size-1 ml-2 mb-2">${activeTeam.captain.name}</span>
                                        </#if>
                                    </div>
                                </div>

                                <div class="col-lg-auto align-self-lg-end text-lg-right">
                                    <div class="hs-unfold">
                                        <a class="js-hs-unfold-invoker btn btn-sm btn-primary dropdown-toggle" href="javascript:;"
                                           data-hs-unfold-options='{
                                               "target": "#calendarSubscription",
                                               "type": "css-animation",
                                               "event": "hover"
                                             }'>
                                            <i class="fas fa-calendar-plus fa-sm mr-1"></i>&nbsp;
                                            <@spring.message code="calendar.add" />
                                        </a>

                                        <div id="calendarSubscription" class="hs-unfold-content dropdown-menu">
                                            <a class="dropdown-item active" href="webcal:${baseUrl?keep_after(":")}/feeds/fixtures/${activeTeam.id?c}.ical">
                                                <@spring.message code="calendar.add-to-device" />
                                            </a>
                                            <a class="dropdown-item" href="https://calendar.google.com/calendar/render?cid=webcal:${baseUrl?keep_after(":")?replace("/", "%2F")}%2Ffeeds%2Ffixtures%2F${activeTeam.id?c}.ical">
                                                <@spring.message code="calendar.add-to-google" />
                                            </a>
                                            <a class="dropdown-item" href="/feeds/fixtures/${activeTeam.id?c}.ical">
                                                <@spring.message code="calendar.download" />
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-lg mb-3 mb-lg-0">
                                    <#list activeTeam.league as league>
                                        <div class="d-flex align-items-center">
                                            <span><i class="fa fa-trophy"></i> ${league.name}</span>
                                            <span class="font-weight-bold text-dark ml-2">${league.getTeamRank(activeTeam.id)}</span>
                                            <#assign points=league.getTeamPoints(activeTeam.id) />
                                            <#if points gt 0>
                                                <span class="font-size-1 ml-1">(${points} points)</span>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <#list activeTeam.fixtures as fixture>
                    <div class="card card-bordered card-frame card-hover-shadow mb-3 <#if fixture.friendly>nonleague<#else>league</#if>">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-sm-3">
                                    <span class="dateBoxFixture dateBoxBig">
                                        <span class="month">${fixture.date.month}</span>
                                        <span class="day">${fixture.date.dayOfMonth}</span>
                                    </span>
                                </div>
                                <div class="col-sm-9">
                                    <div>
                                        <h3>
                                            <a href="/fixtures/${season?c}/${activeTeam.id?c}/${fixture.id?c}">${fixture.opposition}</a>
                                        </h3>
                                        <ul class="list-group list-group-flush">
                                            <#if fixture.startTime?? && fixture.startTime?length gt 0>
                                                <li class="list-group-item">
                                                    <i class="fa fa-clock"></i>&nbsp;
                                                    ${fixture.startTime}
                                                </li>
                                            </#if>
                                            <li class="list-group-item">
                                                <#if fixture.friendly>
                                                    <i class="fa fa-handshake"></i>&nbsp;
                                                    ${fixture.matchType!""}
                                                <#else>
                                                    <i class="fa fa-trophy"></i>&nbsp;
                                                    ${fixture.matchType!""} - ${fixture.competitionName!""}
                                                </#if>

                                            </li>
                                            <li class="list-group-item">
                                                <i class="fa fa-compass"></i>&nbsp;
                                                ${fixture.venue}
                                            </li>
                                            <#if fixture.result?? && fixture.result != "">
                                                <li class="list-group-item">
                                                    <i class="fa fa-medal"></i>&nbsp;
                                                    ${fixture.result}
                                                </li>
                                                <li class="list-group-item">
                                                    <div class="row">
                                                        <div class="col-1 align-middle">
                                                            <i class="fa fa-columns"></i>
                                                        </div>
                                                        <div class="col-11">
                                                            <ul class="list-group list-group-flush">
                                                                <li class="list-group-item">
                                                                    ${fixture.firstInningsTeamName} ${fixture.firstInningsRuns?c}-${fixture.firstInningsWickets?c} (${fixture.firstInningsOvers?c} overs)
                                                                    <#if fixture.firstInningsDeclared>
                                                                        <@spring.message code="fixtures.declared" />
                                                                    </#if>
                                                                </li>
                                                                <li class="list-group-item">
                                                                    ${fixture.secondInningsTeamName} ${fixture.secondInningsRuns?c}-${fixture.secondInningsWickets?c} (${fixture.secondInningsOvers?c} overs)
                                                                    <#if fixture.secondInningsDeclared>
                                                                        <@spring.message code="fixtures.declared" />
                                                                    </#if>
                                                                </li>
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </li>
                                            </#if>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
    </div>

</@layout.mainLayout>