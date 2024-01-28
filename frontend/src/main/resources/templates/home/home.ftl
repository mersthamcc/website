<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

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

<@layout.mainLayout script=sliderScripts>
    <div id="heroSlider" class="js-slick-carousel slick"
         data-hs-slick-carousel-options='{
           "vertical": true,
           "verticalSwiping": true,
           "autoplay": true,
           "autoplaySpeed": 10000,
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

        <div class="js-slide d-flex gradient-x-overlay-sm-dark bg-img-hero min-h-620rem" style="background-image: url(${resourcePrefix}/mcc/img/1st-eleven.jpg); background-position-y: -400px;">
            <!-- News Block -->
            <div class="container flex align-items-center min-h-620rem" style="padding-top: 9em;">
                <div class="w-lg-100 mr-5">
                    <div class="media align-items-center mb-5">
                        <h3 class="h1 font-weight-bold text-white"
                            data-hs-slick-carousel-animation="fadeInUp"
                            data-hs-slick-carousel-animation-delay="150">Welcome to Merstham Cricket Club</h3>
                    </div>
                </div>
            </div>
            <!-- End News Block -->
        </div>
    </div>

    <div class="container space-1">
        <div class="row justify-content-lg-between">
            <div class="col-lg-12">
                <div class="mb-5 mt-5">
                    <h1 class="mb-3">
                        ${page.title}
                    </h1>

                    ${page.content}

                    <a class="btn btn-primary btn-wide transition-3d-hover" href="/register">
                        <@spring.messageText code="home.join" text="Join Now" />
                    </a>
                    <a class="btn btn-link btn-wide" href="/contacts">
                        <@spring.messageText code="home.contact" text="Contact us" /> <i class="fas fa-angle-right fa-sm ml-1"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</@layout.mainLayout>
