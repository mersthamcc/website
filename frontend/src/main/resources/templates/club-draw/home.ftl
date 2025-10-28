<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<#macro headers>

</#macro>

<@layout.mainLayout headers=headers>
    <div class="container space-top-2 space-bottom-1">
        <div class="w-lg-80 text-center mx-lg-auto">
            <div class="mb-5 mb-md-11">
                <h1 class="display-4">
                    <@spring.message code="club-draw.title" />
                </h1>
                <p class="lead">
                    <@spring.message code="club-draw.lead-line" />
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
                        ${members}
                    </span>
                    <span>
                        <@spring.message code="club-draw.member-count" />
                    </span>
                </div>
                <!-- End Stats -->
            </div>

            <div class="col-sm-4 col-lg-3 mb-7 mb-sm-0">
                <!-- Stats -->
                <div class="text-center">
                    <span class="d-block display-4 font-size-md-down-4 text-dark text-uppercase mb-0">
                        &pound;${prize_fund}
                    </span>
                    <span>
                        <@spring.message code="club-draw.prize-fund" />
                    </span>
                </div>
                <!-- End Stats -->
            </div>

            <div class="col-sm-4 col-lg-3">
                <!-- Stats -->
                <div class="text-center">
                    <span class="d-block display-4 font-size-md-down-4 text-dark text-uppercase mb-0">
                        &pound;${first_prize}
                    </span>
                    <span>
                        <@spring.message code="club-draw.first-prize" />
                    </span>
                </div>
                <!-- End Stats -->
            </div>
        </div>
    </div>
    <!-- End Stats Section -->
    <div class="container">
        <div class="w-lg-65 mx-lg-auto">
            <hr class="my-0">
        </div>
    </div>

    <div class="container space-2">
        <div class="row justify-content-lg-between">
            <div class="col-lg-4 mb-5 mb-lg-0">
                <h2>
                    ${page.title!""}
                </h2>
            </div>
            <div class="col-lg-6">
                ${page.content!""}
            </div>
        </div>
    </div>
</@layout.mainLayout>