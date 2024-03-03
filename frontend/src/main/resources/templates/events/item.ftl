<#import "../base.ftl" as layout>
<@layout.mainLayout>
    <div class="container space-1">
        <#if event.banner??>
            <div class="profile-cover">
                <div class="profile-cover-img-wrapper">
                    <img class="profile-cover-img" src="${event.banner}" alt="Banner Image">
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
                <#if event.callToActionLink??>
                    <a class="btn btn-primary" href="${event.callToActionLink}">${event.callToActionDescription}</a>
                </#if>
            </div>
        </div>
    </div>
</@layout.mainLayout>
