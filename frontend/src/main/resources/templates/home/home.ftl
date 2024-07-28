<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<#macro headers>
    <link rel="stylesheet" href="${resourcePrefix}/front/assets/vendor/slick-carousel/slick/slick.css">
</#macro>

<#macro sliderScripts>
    <script >
        // INITIALIZATION OF SLICK CAROUSEL
        // =======================================================
        $('#heroSliderNav').on('init', function (event, slick) {
            $(slick.$slider).find('.slick-pagination-line-progress .slick-pagination-line-progress-helper').each(function() {
                $(this).css({
                    transitionDuration: (slick.options.autoplaySpeed - slick.options.speed) + 'ms'
                });
            });

            setTimeout(function() {
                $(slick.$slider).addClass('slick-dots-ready');
            });
        });

        $('#heroSliderNav').one('beforeChange', function (event, slick) {
            $(slick.$slider).find('.slick-pagination-line-progress .slick-pagination-line-progress-helper').each(function() {
                $(this).css({
                    transitionDuration: (slick.options.autoplaySpeed + slick.options.speed) + 'ms'
                });
            });
        });


        // INITIALIZATION OF SLICK CAROUSEL
        // =======================================================
        $('.js-slick-carousel').each(function() {
            var slickCarousel = $.HSCore.components.HSSlickCarousel.init($(this));
        });

        $(window).on('resize', function() {
            $('#heroSliderNav').slick('setPosition');
        });
    </script>
</#macro>

<#macro slide image title cta cta_link image_position="center" author="" avatar="">
    <div class="js-slide d-flex gradient-x-overlay-sm-dark bg-img-hero min-h-620rem"
         style="background-image: url(${image}); background-position: ${image_position};">
        <div class="container d-flex align-items-center min-h-620rem">
            <div class="w-lg-40 mr-5">
                <#if author?has_content>
                    <div class="media align-items-center mb-3"
                         data-hs-slick-carousel-animation="fadeInUp">
                        <#if avatar?has_content>
                            <div class="avatar avatar-sm avatar-circle mr-3">
                                <img class="avatar-img" src="${avatar}" alt="${author}" />
                            </div>
                        </#if>
                        <div class="media-body">
                            <span class="text-white">${author}</span>
                        </div>
                    </div>
                </#if>

                <div class="mb-5">
                    <h3 class="h1 font-weight-bold text-white"
                        data-hs-slick-carousel-animation="fadeInUp"
                        data-hs-slick-carousel-animation-delay="150">${title}</h3>
                </div>
                <a class="btn btn-primary btn-wide transition-3d-hover" href="${cta_link}"
                   data-hs-slick-carousel-animation="fadeInUp"
                   data-hs-slick-carousel-animation-delay="300">${cta} <i class="fas fa-angle-right fa-sm ml-1"></i></a>
            </div>
        </div>
    </div>
</#macro>

<@layout.mainLayout script=sliderScripts headers=headers>
    <div class="position-relative">
        <div id="heroSlider" class="js-slick-carousel slick"
             data-hs-slick-carousel-options='{
                   "vertical": false,
                   "verticalSwiping": false,
                   "autoplay": true,
                   "autoplaySpeed": 5000,
                   "dots": true,
                   "dotsClass": "slick-pagination slick-pagination-white d-lg-none position-absolute bottom-0 right-0 left-0 mb-3",
                   "asNavFor": "#heroSliderNav",
                   "responsive": [
                     {
                       "breakpoint": 576,
                       "settings": {
                         "vertical": false,
                         "verticalSwiping": false
                       }
                     }
                   ]
                 }'>

            <@slide
                image="${resourcePrefix}/mcc/img/club/clubhouse-stumps.png"
                image_position="center top"
                title=springMacroRequestContext.getMessage("home.welcome-long", [config.clubName])
                cta=springMacroRequestContext.getMessage("home.read-more")
                cta_link="/about"
                author=springMacroRequestContext.getMessage("home.founded")
            />

            <#assign images=["${resourcePrefix}/mcc/img/club/owzat.png", "${resourcePrefix}/mcc/img/club/aerial.jpeg", "${resourcePrefix}/mcc/img/club/fielding.png"]>
            <#list home.topNews as news>
                <@slide
                    image=images[news?index]
                    title=news.title
                    cta=springMacroRequestContext.getMessage("home.read-story")
                    cta_link="/news${news.path}"
                    author=news.displayPublishDate
                />
            </#list>
        </div>

        <div class="container slick-pagination-line-wrapper content-centered-y right-0 left-0">
            <div class="content-centered-y right-0 mr-3">
                <div id="heroSliderNav" class="js-slick-carousel slick slick-pagination-line max-w-27rem ml-auto"
                     data-hs-slick-carousel-options='{
                   "vertical": true,
                   "verticalSwiping": true,
                   "autoplay": true,
                   "autoplaySpeed": 5000,
                   "slidesToShow": ${home.topNews?size + 1},
                   "isThumbs": true,
                   "asNavFor": "#heroSlider"
                 }'>
                    <div class="js-slide my-3">
                        <span class="text-white">
                            <@spring.message code="home.welcome" />
                        </span>

                        <span class="slick-pagination-line-progress">
                          <span class="slick-pagination-line-progress-helper"></span>
                        </span>
                    </div>
                    <#list home.topNews as news>
                        <div class="js-slide my-3">
                            <span class="text-white">${news.title}</span>

                            <span class="slick-pagination-line-progress">
                              <span class="slick-pagination-line-progress-helper"></span>
                            </span>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    </div>

    <div class="container space-1">
        <div class="row justify-content-lg-between">
            <div class="col-lg-8">
                <div class="mb-5 mt-5">
                    <h1 class="mb-3">
                        <#if page?? && page.title??>
                            ${home.content.title}
                        </#if>
                    </h1>

                    ${home.content.getAbstract(3)}

                    <a class="btn btn-primary btn-wide transition-3d-hover" href="/register">
                        <@spring.messageText code="home.join" text="Join Now" />
                    </a>
                    <a class="btn btn-link btn-wide" href="/contacts">
                        <@spring.messageText code="home.contact" text="Contact us" /> <i class="fas fa-angle-right fa-sm ml-1"></i>
                    </a>
                </div>
            </div>
            <div class="col-lg-4">
                <@layout.fixtureWidget fixtures=home.upcomingFixtures title="home.upcoming-fixtures" />
            </div>
    </div>
</@layout.mainLayout>
