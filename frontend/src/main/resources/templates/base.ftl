<#import "/spring.ftl" as spring />
<#import "components.ftl" as components />

<#macro defaultHeaders>

</#macro>

<#macro defaultScripts>

</#macro>

<#macro mainLayout formName="" headers=defaultHeaders script=defaultScripts>
    <!DOCTYPE html>
    <html lang="en">
        <head>
            <!-- Title -->
            <title>
                ${config.clubName} -
                <#if pageTitle??>
                    ${pageTitle}
                <#else>
                    <@spring.messageArgsText
                        code="menu.${currentRoute.name}"
                        args=currentRoute.argumentValues
                        text=currentRoute.name
                    />
                </#if>
            </title>
            <!-- Required Meta Tags Always Come First -->
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

            <!-- Favicon -->
            <link rel="shortcut icon" href="${resourcePrefix}${config.favicon}">

            <!-- Font -->
            <link href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&display=swap" rel="stylesheet">

            <!-- CSS Implementing Plugins -->
            <link rel="stylesheet" href="${resourcePrefix}/front/assets/vendor/fontawesome/css/all.min.css">
            <link rel="stylesheet" href="${resourcePrefix}/front/assets/vendor/hs-mega-menu/dist/hs-mega-menu.min.css">
            <link rel="stylesheet" href="${resourcePrefix}/front/assets/vendor/dzsparallaxer/dzsparallaxer.css">
            <link rel="stylesheet" href="${resourcePrefix}/front/assets/vendor/cubeportfolio/css/cubeportfolio.min.css">
            <link rel="stylesheet" href="${resourcePrefix}/front/assets/vendor/aos/dist/aos.css">

            <!-- CSS Front Template -->
            <link rel="stylesheet" href="${resourcePrefix}/front/assets/css/theme.min.css">

            <link rel="stylesheet" href="${resourcePrefix}/mcc/css/custom.css">

            <#if headers?is_directive><@headers /><#else>${headers}</#if>
        </head>

        <body
            data-google-analytics-key="${config.googleAnalyticsKey}"
            data-cookie-controller-optional-cookies=""
            data-cookie-controller-api-key="${config.cookies.apiKey}"
            data-cookie-controller-product-code="${config.cookies.productCode}"
        >
            <!-- ========== HEADER ========== -->
            <header id="header" class="header header-box-shadow-on-scroll header-show-hide"
                    data-hs-header-options='{"fixMoment": 1000, "fixEffect": "slide"}'>
                <!-- Search -->
                <div id="searchPushTop" class="search-push-top">
                    <div class="container position-relative">
                        <div class="search-push-top-content pt-3">
                            <!-- Close Button -->
                            <div class="search-push-top-close-btn">
                                <div class="hs-unfold">
                                    <a class="js-hs-unfold-invoker btn btn-icon btn-xs btn-soft-secondary mt-2 mr-2" href="javascript:;"
                                       data-hs-unfold-options='{"target": "#searchPushTop", "type": "jquery-slide", "contentSelector": ".search-push-top"}'>
                                        <svg width="10" height="10" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
                                            <path fill="currentColor" d="M11.5,9.5l5-5c0.2-0.2,0.2-0.6-0.1-0.9l-1-1c-0.3-0.3-0.7-0.3-0.9-0.1l-5,5l-5-5C4.3,2.3,3.9,2.4,3.6,2.6l-1,1 C2.4,3.9,2.3,4.3,2.5,4.5l5,5l-5,5c-0.2,0.2-0.2,0.6,0.1,0.9l1,1c0.3,0.3,0.7,0.3,0.9,0.1l5-5l5,5c0.2,0.2,0.6,0.2,0.9-0.1l1-1 c0.3-0.3,0.3-0.7,0.1-0.9L11.5,9.5z"/>
                                        </svg>
                                    </a>
                                </div>
                            </div>
                            <!-- End Close Button -->

                            <!-- Input -->
                            <form class="input-group">
                                <input type="search" class="form-control" placeholder="Search Front" aria-label="Search Front">
                                <div class="input-group-append">
                                    <button type="button" class="btn btn-primary">Search</button>
                                </div>
                            </form>
                            <!-- End Input -->

                        </div>
                    </div>
                </div>
                <!-- End Search -->

                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <strong>New website</strong> Not all features are available yet, full service will be available soon. Membership registration is available now.
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>

                <div class="header-section">
                    <!-- Topbar -->
                    <div class="container header-hide-content pt-2">
                        <div class="d-flex align-items-center">
                            <a href="//www.twitter.com/${config.social.twitter.handle}"
                               class="nav-link font-size-1 py-2 pl-0"
                               target="_blank">
                                <i class="fab fa-twitter"></i>
                            </a>
                            <a href="//www.facebook.com/${config.social.facebook.handle}"
                               class="nav-link font-size-1 py-2 pl-0"
                               target="_blank">
                                <i class="fab fa-facebook-f"></i>
                            </a>
                            <a href="tel:${config.phoneNumber}"
                               class="nav-link font-size-1 py-2 pl-0">
                                <i class="fas fa-phone"></i>
                                ${config.phoneNumber}
                            </a>
                            <#if config.playCricket.enabled>
                                <a href="https://${config.playCricket.site}.play-cricket.com"
                                   class="nav-link font-size-1 py-2 pl-0"
                                   target="_blank">
                                    <i class="fas fa-globe"></i>
                                    Visit us on Play-Cricket
                                </a>
                            </#if>

                            <@components.topmenu topMenu=topMenu user=user />

                            <ul class="list-inline ml-2 mb-0">
                                <!-- Search -->
<#--                                <li class="list-inline-item">-->
<#--                                    <div class="hs-unfold">-->
<#--                                        <a class="js-hs-unfold-invoker btn btn-xs btn-icon btn-ghost-secondary" href="javascript:;"-->
<#--                                           data-hs-unfold-options='{"target": "#searchPushTop", "type": "jquery-slide", "contentSelector": ".search-push-top"}'>-->
<#--                                            <i class="fas fa-search"></i>-->
<#--                                        </a>-->
<#--                                    </div>-->
<#--                                </li>-->
                                <!-- End Search -->

                                <!-- Account Login -->
                                <li class="list-inline-item">
                                    <@components.userMenu user=user userMenu=userMenu />
                                </li>
                                <!-- End Account Login -->
                            </ul>
                        </div>
                    </div>
                    <!-- End Topbar -->

                    <div id="logoAndNav" class="container">
                        <!-- Nav -->
                        <nav class="js-mega-menu navbar navbar-expand-lg">
                            <!-- Logo -->
                            <a class="navbar-brand" href="/" aria-label="${config.clubName}">
                                <img src="${resourcePrefix}${config.logo}" alt="${config.clubName}">
                            </a>
                            <!-- End Logo -->

                            <!-- Responsive Toggle Button -->
                            <button type="button" class="navbar-toggler btn btn-icon btn-sm rounded-circle"
                                    aria-label="Toggle navigation"
                                    aria-expanded="false"
                                    aria-controls="navBar"
                                    data-toggle="collapse"
                                    data-target="#navBar">
                                <span class="navbar-toggler-default">
                                  <svg width="14" height="14" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
                                    <path fill="currentColor" d="M17.4,6.2H0.6C0.3,6.2,0,5.9,0,5.5V4.1c0-0.4,0.3-0.7,0.6-0.7h16.9c0.3,0,0.6,0.3,0.6,0.7v1.4C18,5.9,17.7,6.2,17.4,6.2z M17.4,14.1H0.6c-0.3,0-0.6-0.3-0.6-0.7V12c0-0.4,0.3-0.7,0.6-0.7h16.9c0.3,0,0.6,0.3,0.6,0.7v1.4C18,13.7,17.7,14.1,17.4,14.1z"/>
                                  </svg>
                                </span>
                                <span class="navbar-toggler-toggled">
                                  <svg width="14" height="14" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
                                    <path fill="currentColor" d="M11.5,9.5l5-5c0.2-0.2,0.2-0.6-0.1-0.9l-1-1c-0.3-0.3-0.7-0.3-0.9-0.1l-5,5l-5-5C4.3,2.3,3.9,2.4,3.6,2.6l-1,1 C2.4,3.9,2.3,4.3,2.5,4.5l5,5l-5,5c-0.2,0.2-0.2,0.6,0.1,0.9l1,1c0.3,0.3,0.7,0.3,0.9,0.1l5-5l5,5c0.2,0.2,0.6,0.2,0.9-0.1l1-1 c0.3-0.3,0.3-0.7,0.1-0.9L11.5,9.5z"/>
                                  </svg>
                                </span>
                            </button>
                            <!-- End Responsive Toggle Button -->

                            <!-- Navigation -->
                            <@components.navMenu mainMenu=mainMenu />
                            <!-- End Navigation -->
                        </nav>
                        <!-- End Nav -->
                    </div>
                </div>
            </header>
            <!-- ========== END HEADER ========== -->

            <!-- ========== MAIN CONTENT ========== -->
            <main id="content" role="main" class="<#if formName!="">bg-light</#if>">
                <#if formName == "">
                    <@components.breadcrumbs breadcrumbs=breadcrumbs currentRoute=currentRoute />
                <#else>
                    <@components.formHeader
                        formName=formName
                        resourcePrefix=resourcePrefix
                        breadcrumbs=breadcrumbs
                        currentRoute=currentRoute />
                </#if>
                <#nested />
            </main>
            <!-- ========== END MAIN CONTENT ========== -->

            <!-- ========== FOOTER ========== -->
            <footer class="bg-dark">
                <div class="container">
                    <div class="space-top-2 space-bottom-1 space-bottom-lg-2">
                        <div class="row justify-content-lg-between">
                            <div class="col-lg-3 ml-lg-auto mb-5 mb-lg-0">
                                <!-- Logo -->
                                <div class="mb-4">
                                    <a href="/" aria-label="${config.clubName}">
                                        <img class="brand" src="${resourcePrefix}${config.logo}" alt="${config.clubName}">
                                    </a>
                                </div>
                                <!-- End Logo -->

                                <!-- Nav Link -->
                                <ul class="nav nav-sm nav-x-0 nav-white flex-column">
                                    <li class="nav-item">
                                        <a class="nav-link media" href="/findus/qs">
                                          <span class="media">
                                            <span class="fas fa-location-arrow mt-1 mr-2"></span>
                                            <span class="media-body">
                                              ${config.clubName}, ${config.clubAddress}
                                            </span>
                                          </span>
                                        </a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link media" href="tel:${config.phoneNumber}">
                                          <span class="media">
                                            <span class="fas fa-phone-alt mt-1 mr-2"></span>
                                            <span class="media-body">
                                              ${config.phoneNumber}
                                            </span>
                                          </span>
                                        </a>
                                    </li>
                                </ul>
                                <!-- End Nav Link -->
                            </div>

                            <div class="col-6 col-md-3 col-lg mb-5 mb-lg-0">
                                <h5 class="text-white">
                                  <@spring.messageText code="footer.club" text="Club" />
                                </h5>

                                <!-- Nav Link -->
                                <ul class="nav nav-sm nav-x-0 nav-white flex-column">
                                    <li class="nav-item"><a class="nav-link" href="#"><@spring.messageText code="footer.about" text="About" /></a></li>
                                    <li class="nav-item"><a class="nav-link" href="#"><@spring.messageText code="footer.membership" text="Membership" /></a></li>
                                    <li class="nav-item"><a class="nav-link" href="#"><@spring.messageText code="footer.contact" text="Contacts" /></a></li>
                                    <li class="nav-item"><a class="nav-link" href="#"><@spring.messageText code="footer.policies" text="Policies" /></a></li>
                                </ul>
                                <!-- End Nav Link -->
                            </div>

                            <div class="col-6 col-md-3 col-lg mb-5 mb-lg-0">
                                <h5 class="text-white"><@spring.messageText code="footer.funding" text="Funding" /></h5>

                                <!-- Nav Link -->
                                <ul class="nav nav-sm nav-x-0 nav-white flex-column">
                                    <li class="nav-item"><a class="nav-link" href="${config.fundraising.justgiving}"><@spring.messageText code="footer.donate" text="Donate" /></a></li>
                                    <li class="nav-item"><a class="nav-link" href="${config.fundraising.easyfundraising}"><@spring.messageText code="footer.easyfundraising" text="EasyFundraising" /></a></li>
                                </ul>
                                <!-- End Nav Link -->
                            </div>

                            <div class="col-6 col-md-3 col-lg">
                                <h5 class="text-white"><@spring.messageText code="footer.resources" text="Resources" /></h5>

                                <!-- Nav Link -->
                                <ul class="nav nav-sm nav-x-0 nav-white flex-column">
                                    <li class="nav-item">
                                        <a class="nav-link" href="/account">
                                            <span class="media align-items-center">
                                              <i class="fa fa-user-circle mr-2"></i>
                                              <span class="media-body"><@spring.messageText code="footer.account" text="Account" /></span>
                                            </span>
                                        </a>
                                    </li>
                                </ul>
                                <!-- End Nav Link -->
                            </div>
                        </div>
                    </div>

                    <hr class="opacity-xs my-0">

                    <div class="space-1">
                        <div class="row align-items-md-center mb-7">
                            <div class="col-md-6 mb-4 mb-md-0">
                                <!-- Nav Link -->
                                <ul class="nav nav-sm nav-white nav-x-sm align-items-center">
                                    <li class="nav-item">
                                        <a class="nav-link" href="/pages/privacy">Privacy Policy</a>
                                    </li>
                                    <li class="nav-item opacity mx-3">&#47;</li>
                                    <li class="nav-item">
                                        <a class="nav-link" href="/pages/terms">Terms</a>
                                    </li>
                                    <li class="nav-item opacity mx-3">&#47;</li>
                                    <li class="nav-item">
                                        <a class="nav-link" href="/pages/cookies">Cookies</a>
                                    </li>
                                </ul>
                                <!-- End Nav Link -->
                            </div>

                            <div class="col-md-6 text-md-right">
                                <ul class="list-inline mb-0">
                                    <!-- Social Networks -->
                                    <li class="list-inline-item">
                                      <a class="btn btn-xs btn-icon btn-soft-light" href="//www.twitter.com/${config.social.twitter.handle}">
                                        <i class="fab fa-twitter"></i>
                                      </a>
                                    </li>
                                    <li class="list-inline-item">
                                        <a class="btn btn-xs btn-icon btn-soft-light" href="//www.facebook.com/${config.social.facebook.handle}">
                                            <i class="fab fa-facebook-f"></i>
                                        </a>
                                    </li>
                                    <li class="list-inline-item">
                                        <a class="btn btn-xs btn-icon btn-soft-light" href="//github.com/mersthamcc/website">
                                            <i class="fab fa-github"></i>
                                        </a>
                                    </li>
                                    <!-- End Social Networks -->
                                </ul>
                            </div>
                        </div>

                        <!-- Copyright -->
                        <div class="w-md-75 text-lg-center mx-lg-auto">
                            <p class="text-white opacity-sm small">&copy; ${config.clubName} ${.now?string('yyyy')}. All rights reserved.</p>
                        </div>
                        <!-- End Copyright -->
                    </div>
                </div>
            </footer>
            <!-- ========== END FOOTER ========== -->

            <!-- Go to Top -->
            <a class="js-go-to go-to position-fixed" href="javascript:;" style="visibility: hidden;"
               data-hs-go-to-options='{
                   "offsetTop": 700,
                   "position": {
                     "init": {
                       "right": 15
                     },
                     "show": {
                       "bottom": 15
                     },
                     "hide": {
                       "bottom": -15
                     }
                   }
                 }'>
                <i class="fas fa-angle-up"></i>
            </a>
            <!-- End Go to Top -->

            <!-- JS Global Compulsory  -->
            <script src="${resourcePrefix}/front/assets/vendor/jquery/dist/jquery.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/jquery-migrate/dist/jquery-migrate.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/bootstrap/dist/js/bootstrap.bundle.min.js"></script>

            <!-- JS Implementing Plugins -->
            <script src="${resourcePrefix}/front/assets/vendor/hs-header/dist/hs-header.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/hs-go-to/dist/hs-go-to.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/hs-unfold/dist/hs-unfold.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/hs-mega-menu/dist/hs-mega-menu.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/hs-show-animation/dist/hs-show-animation.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/hs-sticky-block/dist/hs-sticky-block.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/hs-counter/dist/hs-counter.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/appear/dist/appear.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/cubeportfolio/js/jquery.cubeportfolio.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/jquery-validation/dist/jquery.validate.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/dzsparallaxer/dzsparallaxer.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/typed.js/lib/typed.min.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/aos/dist/aos.js"></script>
            <script src="${resourcePrefix}/front/assets/vendor/slick-carousel/slick/slick.js"></script>


            <!-- JS Front -->
            <script src="${resourcePrefix}/front/assets/js/theme.min.js"></script>

            <!-- JS Plugins Init. -->
            <script>
                $(document).on('ready', function () {
                    // INITIALIZATION OF HEADER
                    // =======================================================
                    var header = new HSHeader($('#header')).init();


                    // INITIALIZATION OF MEGA MENU
                    // =======================================================
                    var megaMenu = new HSMegaMenu($('.js-mega-menu'), {
                        desktop: {
                            position: 'left'
                        }
                    }).init();


                    // INITIALIZATION OF UNFOLD
                    // =======================================================
                    var unfold = new HSUnfold('.js-hs-unfold-invoker').init();


                    // INITIALIZATION OF TEXT ANIMATION (TYPING)
                    // =======================================================
                    var typed = $.HSCore.components.HSTyped.init(".js-text-animation");


                    // INITIALIZATION OF AOS
                    // =======================================================
                    AOS.init({
                        duration: 650,
                        once: true
                    });


                    // INITIALIZATION OF FORM VALIDATION
                    // =======================================================
                    $('.js-validate').each(function() {
                        $.HSCore.components.HSValidation.init($(this), {
                            rules: {
                                confirmPassword: {
                                    equalTo: '#password'
                                }
                            }
                        });
                    });

                    // INITIALIZATION OF SHOW ANIMATIONS
                    // =======================================================
                    $('.js-animation-link').each(function () {
                        var showAnimation = new HSShowAnimation($(this)).init();
                    });


                    // INITIALIZATION OF COUNTER
                    // =======================================================
                    $('.js-counter').each(function() {
                        var counter = new HSCounter($(this)).init();
                    });


                    // INITIALIZATION OF GO TO
                    // =======================================================
                    $('.js-go-to').each(function () {
                        var goTo = new HSGoTo($(this)).init();
                    });
                });
            </script>



            <!-- Global site tag (gtag.js) - Google Analytics -->
            <#if config.googleAnalyticsKey?? && config.googleAnalyticsKey != "">
              <script async src="https://www.googletagmanager.com/gtag/js?id=${config.googleAnalyticsKey}"></script>
            </#if>

            <!--  GOOGLE reCAPTCHA -->
            <script src='//www.google.com/recaptcha/api.js'></script>
            <!--  GOOGLE reCAPTCHA -->

            <#if config.cookies.apiKey?? && config.cookies.apiKey != "">
              <script src="https://cc.cdn.civiccomputing.com/9/cookieControl-9.9.2.min.js" type="text/javascript"></script>
            </#if>

            <script src="//tymxcm8ksmvn.statuspage.io/embed/script.js" type="text/javascript"></script>
            <script
                    type="text/javascript"
                    src="//mersthamcricketclub.atlassian.net/s/d41d8cd98f00b204e9800998ecf8427e-T/sb53l8/b/24/bc54840da492f9ca037209037ef0522a/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?locale=en-US&collectorId=e8e9adb2">
            </script>

            <script type="text/javascript">
                function startMail(user,domain) {
                    let locationstring = "mailto:" + user + "@" + domain;
                    window.location = locationstring;
                }

                function startCall(country,code,local) {
                    if ( code.substring(0,1) == "0"){
                        code = code.substring(1);
                    }
                    let locationstring = "tel:" + country + code + local;
                    window.location = locationstring;
                }
            </script>

            <#if script?is_directive><@script /><#else>${script}</#if>
        </body>
    </html>
</#macro>
