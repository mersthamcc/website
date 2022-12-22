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
                <@components.teamList activeTeam=activeTeam season=season teams=teams/>
            </div>

            <div class="col-md-9">
                <div class="mx-lg-auto">
                    <div class="mb-4">
                        <h1 class="h2">
                            ${activeTeam.name}
                            <@spring.message code="fixtures.versus_short" />
                            ${fixture.opposition}
                        </h1>
                    </div>

                    <div class="border-top py-4 mb-1">
                        <div class="row align-items-md-center">
                            <div class="col-md-7 mb-1 mb-md-0">
                                <div class="media align-items-center">
                                    <div class="media-body font-size-1 ml-3">
                                        <span class="d-block text-muted">
                                            <i class="fa fa-calendar"></i>&nbsp;
                                            ${(fixture.date).format('E, MMM dd yyyy')}
                                        </span>
                                        <#if fixture.startTime??>
                                            <span class="d-block text-muted">
                                                <i class="fa fa-clock"></i>&nbsp;
                                                        ${fixture.startTime}
                                            </span>
                                        </#if>
                                        <span class="d-block text-muted">
                                                <i class="fa fa-globe"></i>&nbsp;
                                                        ${fixture.venue}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-5">
                                <div class="d-flex justify-content-md-end align-items-center">
                                    <a class="btn btn-xs btn-icon btn-soft-secondary rounded-circle ml-2" href="#">
                                        <i class="fa fa-table"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <#if fixture.result?? && fixture.result != "">
                        <div class="border-top py-4 mb-1">
                            <div class="row align-items-md-center">
                                <div class="col">
                                    <h6>${fixture.result!""}</h6>
                                </div>
                            </div>
                            <div class="row align-items-md-center">
                                <div class="col-1 align-middle">
                                    <i class="fa fa-handshake"></i>
                                </div>
                                <div class="col-11">
                                    <ul class="list-group list-group-flush">
                                        <li class="list-group-item">
                                            ${fixture.toss}
                                        </li>
                                    </ul>
                                </div>
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
                        </div>

                        <div class="text-center">
                            <ul class="nav nav-segment nav-pills scrollbar-horizontal mb-7" role="tablist">
                                <li class="nav-item">
                                    <a class="nav-link active" id="first-innings-tab" data-toggle="pill" href="#first-innings" role="tab" aria-controls="first-innings" aria-selected="true">
                                        <@spring.messageArgs code="fixtures.innings" args=[fixture.firstInningsTeamName] />
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link" id="second-innings-tab" data-toggle="pill" href="#second-innings" role="tab" aria-controls="second-innings" aria-selected="false">
                                        <@spring.messageArgs code="fixtures.innings" args=[fixture.secondInningsTeamName] />
                                    </a>
                                </li>
                                <#if fixture.points??>
                                    <li class="nav-item">
                                        <a class="nav-link" id="pills-three-code-features-example1-tab" data-toggle="pill" href="#points" role="tab" aria-controls="points" aria-selected="false">
                                            Points
                                        </a>
                                    </li>
                                </#if>
                            </ul>
                        </div>

                        <div class="tab-content">
                            <div class="tab-pane fade show active" id="first-innings" role="tabpanel" aria-labelledby="first-innings">
                                <div class="row align-items-md-center">
                                    <@components.inningsScoreCard fixture=fixture innings=1 />
                                </div>
                                <div class="row align-items-md-center">
                                    <@components.inningsBowlingSummary fixture=fixture innings=1 />
                                </div>
                            </div>

                            <div class="tab-pane fade" id="second-innings" role="tabpanel" aria-labelledby="second-innings">
                                <div class="row align-items-md-center">
                                    <@components.inningsScoreCard fixture=fixture innings=2 />
                                </div>
                                <div class="row align-items-md-center">
                                    <@components.inningsBowlingSummary fixture=fixture innings=2 />
                                </div>
                            </div>

                            <div class="tab-pane fade" id="points" role="tabpanel" aria-labelledby="points">

                            </div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    </div>
</@layout.mainLayout>
