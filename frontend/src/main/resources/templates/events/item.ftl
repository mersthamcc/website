<#import "../base.ftl" as layout>
<@layout.mainLayout>
    <div class="container space-1">
        <#if event.banner?has_content>
            <div class="profile-cover mb-5">
                <div class="profile-cover-img-wrapper">
                    <img class="profile-cover-img" src="${event.banner}">
                </div>
            </div>
        </#if>

        <div class="row justify-content-lg-between">
            <div class="w-lg-60 mx-lg-auto">
                <div class="mb-4">
                    <h1 class="h2">${event.title}</h1>
                </div>

                ${event.body}
            </div>


            <div class="col-lg-3">
                <div class="card-body-centered d-flex flex-column">
                    <span class="dateBoxFixture dateBoxBig">
                        <span class="month">${event.displayDate.month}</span>
                        <span class="day">${event.displayDate.dayOfMonth}</span>
                    </span>
                    <span class="h3 text-secondary space-top-1"><i class="far fa-clock text-primary"></i> ${event.displayDate.hour}:${event.displayDate.minute?string["00"]}</span>
                    <span class="h3 text-secondary space-bottom-2"><i class="far fa-globe text-primary"></i> ${event.location}</span>

                    <#if event.callToActionLink?has_content>
                        <a class="btn btn-primary" href="${event.callToActionLink}">${event.callToActionDescription}</a>
                    </#if>
                </div>
            </div>
        </div>
    </div>
</@layout.mainLayout>
