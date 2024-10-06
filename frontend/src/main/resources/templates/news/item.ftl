<#import "../base.ftl" as layout>
<#macro socialheaders>
    <meta name="twitter:card" content="summary" />
    <meta name="twitter:site" content="@${config.social.twitter.handle}" />
    <meta name="twitter:title" content="${news.title}" />
    <meta name="twitter:description" content="${news.getSocialDescription()!""}" />

    <meta property="og:type" content="article" />
    <meta property="og:title" content="${news.title}">
    <meta property="og:description" content="${news.getSocialDescription()!""}" />
    <meta property="og:locale" content="en_GB" />
    <meta property="og:url" content="${baseUrl}/news${news.path}" />
    <#if news.getSocialImage()??>
        <meta name="twitter:image" content="${news.getSocialImage()}" />
        <meta property="og:image" content="${news.getSocialImage()}">
    </#if>
</#macro>

<@layout.mainLayout headers=socialheaders>
    <div class="container space-1">
        <#if news.featureImageUrl?has_content>
            <div class="mb-5">
                <img
                        class="feature-image"
                        src="${news.featureImageUrl}"
                        alt="${news.title}">
            </div>
        </#if>

        <div class="row justify-content-lg-between">
            <div class="w-lg-60 mx-lg-auto">
                <div class="mb-4">
                    <h1 class="h2">${news.title}</h1>
                </div>

                <div class="border-top border-bottom py-4 mb-5">
                    <div class="row align-items-md-center">
                        <div class="col-md-7 mb-5 mb-md-0">
                            <div class="media align-items-center">
                                <div class="avatar avatar-soft-dark avatar-circle avatar-border-lg avatar-centered">
                                    <span class="avatar-initials">${news.authorInitials}</span>
                                </div>
                                <div class="media-body font-size-1 ml-3">
                                    <span class="h6">${news.author}</span>
                                    <span class="d-block text-muted">${news.displayPublishDate}</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-5">
<#--                            <div class="d-flex justify-content-md-end align-items-center">-->
<#--                                <span class="d-block small font-weight-bold text-cap mr-2">Share:</span>-->

<#--                                <a class="btn btn-xs btn-icon btn-soft-secondary rounded-circle ml-2" href="#">-->
<#--                                    <i class="fab fa-facebook-f"></i>-->
<#--                                </a>-->
<#--                                <a class="btn btn-xs btn-icon btn-soft-secondary rounded-circle ml-2" href="#">-->
<#--                                    <i class="fab fa-twitter"></i>-->
<#--                                </a>-->
<#--                                <a class="btn btn-xs btn-icon btn-soft-secondary rounded-circle ml-2" href="#">-->
<#--                                    <i class="fab fa-instagram"></i>-->
<#--                                </a>-->
<#--                                <a class="btn btn-xs btn-icon btn-soft-secondary rounded-circle ml-2" href="#">-->
<#--                                    <i class="fab fa-telegram"></i>-->
<#--                                </a>-->
<#--                            </div>-->
                        </div>
                    </div>
                </div>

                ${news.body}
            </div>

            <div class="col-lg-3">

            </div>
        </div>
    </div>
</@layout.mainLayout>