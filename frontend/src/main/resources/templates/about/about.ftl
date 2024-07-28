<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<#macro headers>

</#macro>

<@layout.mainLayout headers=headers>
    <div class="container space-top-2 space-bottom-1">
        <div class="w-lg-80 text-center mx-lg-auto">
            <div class="mb-5 mb-md-11">
                <h1 class="display-4">
                    <@spring.message code="about.title" />
                </h1>
                <p class="lead">
                    <@spring.message code="about.lead-line" />
                </p>
            </div>
        </div>

        <div class="row mx-n2">
            <div class="col-6 col-md px-2 mb-3">
                <div class="h-250rem bg-img-hero rounded-lg" style="background-image: url(${resourcePrefix}/mcc/img/club/empty-patio.png); background-position: center;"></div>
            </div>
            <div class="col-md-3 d-none d-md-block px-2 mb-3">
                <div class="h-250rem bg-img-hero rounded-lg" style="background-image: url(${resourcePrefix}/mcc/img/club/stumps.png);"></div>
            </div>
            <div class="col-6 col-md px-2 mb-3">
                <div class="h-250rem bg-img-hero rounded-lg" style="background-image: url(${resourcePrefix}/mcc/img/club/backward-short-leg.png); background-position: center;"></div>
            </div>
        </div>
    </div>
    <!-- End Hero Section -->

    <!-- Stats Section -->
    <div class="container space-bottom-2">
        <div class="row justify-content-lg-center">
            <div class="col-sm-4 col-lg-3 mb-7 mb-sm-0">
                <!-- Stats -->
                <div class="text-center">
                    <span class="d-block display-4 font-size-md-down-4 text-dark text-uppercase mb-0">
                        ${about.members}
                    </span>
                    <span>
                        <@spring.message code="about.member-count" />
                    </span>
                </div>
                <!-- End Stats -->
            </div>

            <div class="col-sm-4 col-lg-3 mb-7 mb-sm-0">
                <!-- Stats -->
                <div class="text-center">
                    <span class="d-block display-4 font-size-md-down-4 text-dark text-uppercase mb-0">
                        ${about.fixtures}
                    </span>
                    <span>
                        <@spring.message code="about.fixture-count" />
                    </span>
                </div>
                <!-- End Stats -->
            </div>

            <div class="col-sm-4 col-lg-3">
                <!-- Stats -->
                <div class="text-center">
                    <span class="d-block display-4 font-size-md-down-4 text-dark text-uppercase mb-0">
                        ${about.wins}
                    </span>
                    <span>
                        <@spring.message code="about.win-count" />
                    </span>
                </div>
                <!-- End Stats -->
            </div>
        </div>
    </div>
    <!-- End Stats Section -->

    <#if about.general.content?has_content>
        <div class="container">
            <div class="w-lg-65 mx-lg-auto">
                <hr class="my-0">
            </div>
        </div>

        <div class="container space-2">
            <div class="row justify-content-lg-between">
                <div class="col-lg-4 mb-5 mb-lg-0">
                    <h2>
                        ${about.general.title}
                    </h2>
                </div>
                <div class="col-lg-6">
                    ${about.general.content}
                </div>
            </div>
        </div>
    </#if>

    <#if about.success.content?has_content>
        <div class="container">
            <div class="w-lg-65 mx-lg-auto">
                <hr class="my-0">
            </div>
        </div>

        <div class="container space-2 space-lg-3">
            <div class="row justify-content-lg-between">
                <div class="col-lg-4 mb-5 mb-lg-0">
                    <h2>
                        ${about.success.title}
                    </h2>
                </div>
                <div class="col-lg-6">
                    ${about.success.content}
                </div>
            </div>
        </div>
    </#if>

    <#if about.cricket.content?has_content>
        <div class="container">
            <div class="w-lg-65 mx-lg-auto">
                <hr class="my-0">
            </div>
        </div>

        <div class="container space-2">
            <div class="row mx-n2">
                <div class="col-6 col-md px-2 mb-1 mb-md-0">
                    <div class="h-250rem bg-img-hero rounded-lg" style="background-image: url(${resourcePrefix}/mcc/img/club/second-xi-23.png); background-position: center;"></div>
                </div>
                <div class="col-md-4 d-none d-md-block px-2 mb-3 mb-md-0">
                    <div class="h-250rem bg-img-hero rounded-lg" style="background-image: url(${resourcePrefix}/mcc/img/club/walking-cricket.png); background-position: center;"></div>
                </div>
                <div class="col-6 col-md px-2">
                    <div class="h-250rem bg-img-hero rounded-lg" style="background-image: url(${resourcePrefix}/mcc/img/club/women-in-kit.jpeg); background-position: center;"></div>
                </div>
                <div class="w-100"></div>
            </div>

            <div class="row justify-content-lg-between space-2">
                <div class="col-lg-4 mb-5 mb-lg-0">
                    <h2>
                        ${about.cricket.title}
                    </h2>
                </div>
                <div class="col-lg-6">
                    ${about.cricket.content}
                </div>
            </div>
        </div>
    </#if>

    <#if about.community.content?has_content>
        <div class="container">
            <div class="w-lg-65 mx-lg-auto">
                <hr class="my-0">
            </div>
        </div>

        <div class="container space-2 space-lg-3">
            <div class="row justify-content-lg-between">
                <div class="col-lg-4 mb-5 mb-lg-0">
                    <h2>
                        ${about.community.title}
                    </h2>
                </div>
                <div class="col-lg-6">
                    ${about.community.content}
                </div>
            </div>
        </div>
    </#if>
</@layout.mainLayout>