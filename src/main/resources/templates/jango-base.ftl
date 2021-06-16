<#import "/spring.ftl" as spring />
<#import "components.ftl" as components />

<#macro defaultHeaders>

</#macro>

<#macro defaultScripts>

</#macro>

<#macro mainLayout headers=defaultHeaders script=defaultScripts>
<!DOCTYPE html>
<!--[if IE 9]>
<html lang="en" class="ie9 no-js">
<![endif]-->
<!--[if !IE]><!-->
<html lang="en">
    <!--<![endif]-->
    <!-- BEGIN HEAD -->
    <head>
        <meta charset="utf-8"/>
        <title></title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
        <meta http-equiv="Content-type" content="text/html; charset=utf-8">
        <meta content="" name="description"/>
        <meta content="" name="author"/>
        <!-- BEGIN GLOBAL MANDATORY STYLES -->
        <link href='//fonts.googleapis.com/css?family=Roboto+Condensed:300italic,400italic,700italic,400,300,700&amp;subset=all' rel='stylesheet' type='text/css'>
        <link href="${resourcePrefix}/assets/plugins/socicon/socicon.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/bootstrap-social/bootstrap-social.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/simple-line-icons/simple-line-icons.min.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/animate/animate.min.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <!-- END GLOBAL MANDATORY STYLES -->

        <!-- BEGIN: BASE PLUGINS  -->
        <link href="${resourcePrefix}/assets/plugins/revo-slider/css/settings.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/revo-slider/css/layers.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/revo-slider/css/navigation.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/cubeportfolio/css/cubeportfolio.min.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/owl-carousel/assets/owl.carousel.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/plugins/slider-for-bootstrap/css/slider.css" rel="stylesheet" type="text/css"/>
        <!-- END: BASE PLUGINS -->


        <!-- BEGIN THEME STYLES -->
        <link href="${resourcePrefix}/assets/mcc/css/plugins.css" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/mcc/css/components.css" id="style_components" rel="stylesheet" type="text/css"/>
        <link href="${resourcePrefix}/assets/mcc/css/themes/blue2.css" rel="stylesheet" id="style_theme" type="text/css"/>
        <link href="${resourcePrefix}/assets/mcc/css/custom.css" rel="stylesheet" type="text/css"/>
        <!-- END THEME STYLES -->
        <link rel="shortcut icon" href="favicon.ico"/>

        <#if headers?is_directive><@headers /><#else>${headers}</#if>
    </head>

    <body
            class="c-layout-header-fixed c-layout-header-mobile-fixed c-layout-header-topbar c-layout-header-topbar-collapse"
            data-google-analytics-key="${config.googleAnalyticsKey}"
            data-cookie-controller-optional-cookies=""
            data-cookie-controller-api-key="${config.cookies.apiKey}"
            data-cookie-controller-product-code="${config.cookies.productCode}"
    >

    <!-- BEGIN: HEADER -->
    <header class="c-layout-header c-layout-header-4 c-layout-header-default-mobile" data-minimize-offset="80">
        <div class="c-topbar c-topbar-light">
            <div class="container">
                <!-- BEGIN: INLINE NAV -->
                <nav class="c-top-menu c-pull-left">
                    <ul class="c-icons c-theme-ul">
                        <li><a href="//www.twitter.com/${config.social.twitter.handle}"><i class="icon-social-twitter"></i></a></li>
                        <li><a href="//www.facebook.com/${config.social.facebook.handle}"><i class="icon-social-facebook"></i></a></li>
                        <li><a href="tel:${config.phoneNumber}"><i class="fa fa-phone"></i><span>&nbsp;&nbsp;${config.phoneNumber}</span></a></li>
                        <#if config.playCricket.enabled>
                            <li><a href="https://${config.playCricket.site}.play-cricket.com"><i class="fa fa-globe"></i><span>&nbsp;&nbsp;Play-Cricket</span></a></li>
                        </#if>
                    </ul>
                </nav>
                <!-- END: INLINE NAV -->
                <!-- BEGIN: INLINE NAV -->
                <@components.topmenu topMenu=topMenu user=user userMenu=userMenu />
                <!-- END: INLINE NAV -->
            </div>
        </div>
        <div class="c-navbar">
            <div class="container">
                <div class="c-navbar-wrapper clearfix">
                    <!-- BEGIN: BRAND -->
                    <div class="c-brand c-pull-left">
                        <a href="/" class="c-logo">
                            <h1 class="c-desktop-logo">
                                <img src="${resourcePrefix}${config.logo}" alt="${config.clubName}">
                                ${config.clubName}
                            </h1>
                            <h1 class="c-desktop-logo-inverse" style="margin-top: 0px;">
                                ${config.clubName}
                            </h1>
                            <h1 class="c-mobile-logo">
                                <img src="${resourcePrefix}${config.logo}" alt="${config.clubName}">
                                ${config.clubName}
                            </h1>
                        </a>
                        <button class="c-hor-nav-toggler" type="button" data-target=".c-mega-menu">
                            <span class="c-line"></span>
                            <span class="c-line"></span>
                            <span class="c-line"></span>
                        </button>
                        <button class="c-topbar-toggler" type="button">
                            <i class="fa fa-ellipsis-v"></i>
                        </button>
                        <button class="c-search-toggler" type="button">
                            <i class="fa fa-search"></i>
                        </button>
                        <button class="c-cart-toggler" type="button">
                            <i class="icon-handbag"></i> <span class="c-cart-number c-theme-bg">2</span>
                        </button>
                    </div>
                    <!-- END: BRAND -->
                    <!-- BEGIN: QUICK SEARCH -->
                    <form class="c-quick-search" action="#">
                        <input type="text" name="query" placeholder="Search..." value="" class="form-control" autocomplete="off">
                        <span class="c-theme-link">&times;</span>
                    </form>
                    <!-- END: QUICK SEARCH -->
                    <!-- BEGIN: HOR NAV -->
                    <!-- BEGIN: LAYOUT/HEADERS/MEGA-MENU -->
                    <!-- BEGIN: MEGA MENU -->
                    <!-- Dropdown menu toggle on mobile: c-toggler class can be applied to the link arrow or link itself depending on toggle mode -->
                    <nav class="c-mega-menu c-pull-right c-mega-menu-dark c-mega-menu-dark-mobile c-fonts-uppercase-reset c-fonts-bold-reset">
                        <ul class="nav navbar-nav c-theme-nav">
                            <#list mainMenu as item>
                                <#if item.onActivePath(currentRoute)>
                                    <#assign classes>c-active</#assign>
                                <#else>
                                    <#assign classes></#assign>
                                </#if>
                                <li class="c-menu-type-classic ${classes}">
                                    <a href="${item.destinationUrl}" class="c-link dropdown-toggle">
                                        <@spring.messageArgs code="menu.${item.name}" args=item.argumentValues />
                                        <span class="c-arrow c-toggler"></span>
                                    </a>
                                    <#if item.children??>
                                        <ul class="dropdown-menu c-menu-type-classic c-pull-left">
                                            <#list item.children as subitem>
                                                <#if subitem.onActivePath(currentRoute)>
                                                    <#assign classes>c-active</#assign>
                                                <#else>
                                                    <#assign classes></#assign>
                                                </#if>
                                                <#if subitem.children??>
                                                    <li class="dropdown-submenu ${classes}">
                                                        <a href="javascript:;">
                                                            <@spring.messageArgs code="menu.${subitem.name}" args=subitem.argumentValues />
                                                            <span class="c-arrow c-toggler"></span>
                                                        </a>
                                                        <ul class="dropdown-menu c-pull-right">
                                                            <#list subitem.children as leafItem>
                                                                <#if leafItem.onActivePath(currentRoute)>
                                                                    <#assign classes>c-active</#assign>
                                                                <#else>
                                                                    <#assign classes></#assign>
                                                                </#if>
                                                                <li class="${classes}">
                                                                    <a href="${leafItem.destinationUrl}">
                                                                        <@spring.messageArgs code="menu.${leafItem.name}" args=leafItem.argumentValues />
                                                                    </a>
                                                                </li>
                                                            </#list>
                                                        </ul>
                                                    </li>
                                                <#else>
                                                    <li class="${classes}">
                                                        <a href="${subitem.destinationUrl}">
                                                            <@spring.messageArgs code="menu.${subitem.name}" args=subitem.argumentValues />
                                                        </a>
                                                    </li>
                                                </#if>
                                            </#list>
                                        </ul>
                                    </#if>
                                </li>
                            </#list>
                            <li class="c-search-toggler-wrapper">
                                <a  href="#" class="c-btn-icon c-search-toggler"><i class="fa fa-search"></i></a>
                            </li>
                        </ul>
                    </nav>
                    <!-- END: MEGA MENU --><!-- END: LAYOUT/HEADERS/MEGA-MENU -->
                    <!-- END: HOR NAV -->
                </div>
            </div>
        </div>
    </header>
    <div class="modal fade c-content-login-form" id="forget-password-form" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content c-square">
                <div class="modal-header c-no-border">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
                </div>
                <div class="modal-body">

                </div>
                <div class="modal-footer c-no-border">
                    <span class="c-text-account">Don't Have An Account Yet ?</span>
                    <a href="javascript:;" data-toggle="modal" data-target="#signup-form" data-dismiss="modal" class="btn c-btn-dark-1 btn c-btn-uppercase c-btn-bold c-btn-slim c-btn-border-2x c-btn-square c-btn-signup">Signup!</a>
                </div>
            </div>
        </div>
    </div>

    <div class="c-layout-page">
        <div class="c-layout-breadcrumbs-1 c-bordered c-bordered-both c-fonts-uppercase-reset c-fonts-bold-reset">
            <div class="container">
                <div class="c-page-title c-pull-left">
                    <h3 class="c-font-sbold c-font-uppercase">
                        <@spring.messageArgsText
                            code="menu.${currentRoute.name}"
                            args=currentRoute.argumentValues
                            text=pageTitle!""
                        />
                    </h3>
                </div>
                <ul class="c-page-breadcrumbs c-theme-nav c-pull-right c-fonts-regular">
                    <#if breadcrumbs??>
                        <#list breadcrumbs as breadcrumb>
                            <#if breadcrumb?has_next>
                                <li>
                                    <a href="${breadcrumb.destinationUrl}">
                                        <@spring.messageArgs code="menu.${breadcrumb.name}" args=breadcrumb.argumentValues />
                                    </a>
                                </li>
                                <li>/</li>
                            <#else>
                                <li class="c-state_active"><@spring.messageArgs code="menu.${breadcrumb.name}" args=breadcrumb.argumentValues /></li>
                            </#if>
                        </#list>
                    </#if>
                </ul>
            </div>
        </div>
        <div class="c-content-box c-size-md c-bg-white">
            <div class="container">
                <#nested "content">
            </div>
        </div>
    </div>

    <footer class="c-layout-footer c-layout-footer-7">

        <div class="container">

            <div class="c-prefooter">

                <div class="c-body">
                    <div class="row">
                        <div class="col-md-4 col-sm-6 col-xs-12">
                            <div class="row">
                                <div class="c-content-title-1 c-title-md">
                                    <h2 class="c-title c-font-uppercase">Quick Links</h2>
                                </div>
                                <ul class="c-links c-theme-ul">
                                    <li><a href="#">About Jango</a></li>
                                    <li><a href="#">Privacy Policy</a></li>
                                    <li><a href="#">Terms &	Conditions</a></li>
                                    <li><a href="#">Delivery</a></li>
                                    <li><a href="#">Promotions</a></li>
                                    <li><a href="#">News</a></li>
                                </ul>
                                <ul class="c-links c-theme-ul">
                                    <li><a href="#">Blogs</a></li>
                                    <li><a href="#">Projects</a></li>
                                    <li><a href="#">Clients</a></li>
                                    <li><a href="#">Services</a></li>
                                    <li><a href="#">Features</a></li>
                                    <li><a href="#">Stats</a></li>
                                </ul>
                            </div>
                            <div class="row">
                                <div class="c-content-title-1 c-title-md">
                                    <h2 class="c-title c-font-uppercase">Membership Subs</h2>
                                </div>
                                <p class="c-text c-font-16 c-font-regular">Merstham Cricket Club now operates an online membership registration form, ALL members (playing members and social) should register annually.</p>
                                <p class="c-text c-font-16 c-font-regular">There are a number of easy payment options including Direct Debit, PayPal, Cash/Cheque and Bank Transfer.</p>
                                <p class="c-text c-font-16 c-font-regular">Please <a href="/register">click here</a> to complete your registration form for the current season.</p>
                            </div>
                            <div class="row">
                                <div class="c-content-title-1 c-title-md">
                                    <h2 class="c-title c-font-uppercase">ECB Clubmark</h2>
                                </div>
                                <p class="c-text c-font-16 c-font-regular">
                                    Merstham Cricket Club is proud to be <a href="/pages/clubmark">ECB Clubmark Accredited</a>
                                </p>
                            </div>
                        </div>
                        <div class="col-md-5 col-sm-6 col-xs-12">
                            <#if config.social.twitter.handle?has_content>
                                <div class="c-content-title-1 c-title-md">
                                    <h2 class="c-title c-font-uppercase">Latest Tweets</h2>
                                    <div class="c-line-left hide"></div>
                                </div>
                                <div class="c-twitter">
                                    <a class="twitter-timeline" href="https://twitter.com/${config.social.twitter.handle}" data-theme="dark" data-widget-id="437554408700641280" data-tweet-limit="3" data-chrome="noheader nofooter noscrollbar noborders transparent">Latest Tweets by ${config.social.twitter.handle}...</a>
                                </div>
                            </#if>
                        </div>
                        <div class="col-md-3 col-sm-12 col-xs-12">
                            <div class="c-content-title-1 c-title-md">
                                <h2 class="c-title c-font-uppercase">Contact</h2>
                            </div>

                            <p class="c-text c-font-16 c-font-regular">For general information please contact our <a href="/feeds/contactcard/41" class="webcontacts fancybox.ajax">Information Manager</a></p>
                            <p class="c-text c-font-16 c-font-regular">If you are interested in joining to play adult cricket then please contact our <a href="/feeds/contactcard/36" class="webcontacts fancybox.ajax">Club Captain</a></p>
                            <p class="c-text c-font-16 c-font-regular">For enquiries relating to Colts (junior) cricket please contact our <a href="/feeds/contactcard/81" class="webcontacts fancybox.ajax">Junior Co-ordinator</a></p>

                            <div class="c-content-title-1 c-title-md">
                                <h2 class="c-title c-font-uppercase">Fundraising & Donations</h2>
                            </div>
                            <p class="c-text c-font-16 c-font-regular">
                                <a href="//www.easyfundraising.org.uk/causes/mersthamcc/">EasyFundrasing</a> offer you a way to raise much needed funds for Merstham Cricket Club as you shop online.
                            </p>
                            <!-- START BANNER -->
                            <div class="margin-bottom-20">
                                <a href="//www.easyfundraising.org.uk/causes/mersthamcc/" target="_blank"><img src="${resourcePrefix}/assets/mcc/img/EF-Logo-01.png" border="0"  height="60"/></a>
                            </div>
                            <!-- END BANNER -->
                            <p class="c-text c-font-16 c-font-regular">
                                Merstham Cricket Club is now available as a cause on <a href="https://www.justgiving.com/merstham-cc/" target="_blank">JustGiving</a>, if you are taking part in an event and wish to raise
                                money for the club please use JustGiving to host your fundraising page.
                            </p>
                            <p class="c-text c-font-16 c-font-regular">
                                Alternatively, to simply donate money to the club, please do so via JustGiving by <a href="https://www.justgiving.com/4w350m3/donation/direct/charity/542336" target="_blank">clicking here</a> (don't forget to tick the gift-aid button)!
                            </p>
                            <div class="margin-bottom-20">
                                <a href="https://www.justgiving.com/merstham-cc/donate/?utm_source=website_cid542336&utm_medium=buttons&utm_content=merstham-cc&utm_campaign=donate_white"><img src="https://www.justgiving.com/charities/content/images/logo-buttons/white/donate_white.gif" alt="Donate with JustGiving" /></a>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="c-line"></div>

                <div class="c-foot">
                    <div class="row">
                        <div class="col-md-7">
                            <div class="c-content-title-1 c-title-md">
                                <h3 class="c-title c-font-uppercase c-font-bold">About Merstham<span class="c-theme-font">CC</span></h3>
                                <div class="c-line-left hide"></div>
                            </div>
                            <p class="c-text c-font-16 c-font-regular">Tolerare unus ducunt ad brevis buxum. Est alter buxum, cesaris. Eheu, lura! Racanas crescere in emeritis oenipons! Ubi est rusticus repressor? Lixa grandis clabulare est. Eposs tolerare, tanquam fatalis.</p>
                        </div>
                        <div class="col-md-5">
                            <div class="c-content-title-1 c-title-md">
                                <h3 class="c-title c-font-uppercase c-font-bold">Subscribe to Mailing List</h3>
                                <div class="c-line-left hide"></div>
                            </div>

                            <p class="c-text c-font-16 c-font-regular">Subscribe to our monthly eNewsletter and other mails to stay up to date with the latest events at the club!</p>

                            <form action="/feeds/mail" method="post">
                                <div class="input-group input-group-lg c-square">
                                    <input type="text" class="c-input form-control c-square c-theme" placeholder="Your Email Here"/>
                                    <span class="input-group-btn">
                                                <button class="btn c-theme-btn c-theme-border c-btn-square c-btn-uppercase c-font-16" type="button">Manage Subscription</button>
                                            </span>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="c-line"></div>

                <div class="c-head">
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="c-left">
                                <div class="socicon">
                                    <a href="//www.facebook.com/MersthamCC" class="socicon-btn socicon-btn-circle socicon-solid c-font-dark-1 c-theme-on-hover socicon-facebook tooltips" data-original-title="Facebook" data-container="body"></a>
                                    <a href="//www.twitter.com/@mersthammagics" class="socicon-btn socicon-btn-circle socicon-solid c-font-dark-1 c-theme-on-hover socicon-twitter tooltips" data-original-title="Twitter" data-container="body"></a>
                                    <a href="#" class="socicon-btn socicon-btn-circle socicon-solid c-font-dark-1 c-theme-on-hover socicon-youtube tooltips" data-original-title="Youtube" data-container="body"></a>
                                    <a href="#" class="socicon-btn socicon-btn-circle socicon-solid c-font-dark-1 c-theme-on-hover socicon-tumblr tooltips" data-original-title="Tumblr" data-container="body"></a>
                                    <a href="/feeds/subscribe" class="socicon-btn socicon-btn-circle socicon-solid c-font-dark-1 c-theme-on-hover socicon-rss tooltips" data-original-title="RSS" data-container="body"></a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-12">
                            <div class="c-right">
                                <h3 class="c-title c-font-uppercase c-font-bold">Download Mobile App</h3>
                                <div class="c-icons">
                                    <a href="#" class="c-font-30 c-font-green-1 socicon-btn socicon-android tooltips" data-original-title="Android" data-container="body"></a>
                                    <a href="#" class="c-font-30 c-font-grey-3 socicon-btn socicon-apple tooltips" data-original-title="Apple" data-container="body"></a>
                                    <a href="#" class="c-font-30 c-font-blue-3 socicon-btn socicon-windows tooltips" data-original-title="Windows" data-container="body"></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

        </div>

        <div class="c-postfooter c-bg-dark-2">
            <div class="container">
                <div class="row">
                    <div class="col-md-6 col-sm-12 c-col">
                        <p class="c-copyright c-font-grey">&copy; ${.now?string["yyyy"]} ${config.clubName}
                            <span class="c-font-grey-3">All Rights Reserved.</span>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </footer>

    <div class="c-layout-go2top" style="display: none;">
        <i class="icon-arrow-up"></i>
    </div>

    <!-- BEGIN: CORE PLUGINS -->
    <!--[if lt IE 9]>
    <script src="${resourcePrefix}/assets/global/plugins/excanvas.min.js"></script>
    <![endif]-->
    <script src="${resourcePrefix}/assets/plugins/jquery.min.js" type="text/javascript" type="text/javascript" ></script>
    <script src="${resourcePrefix}/assets/plugins/jquery-migrate.min.js" type="text/javascript" type="text/javascript" ></script>
    <script src="${resourcePrefix}/assets/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript" type="text/javascript" ></script>
    <script src="${resourcePrefix}/assets/plugins/jquery.easing.min.js" type="text/javascript" ></script>
    <script src="${resourcePrefix}/assets/plugins/reveal-animate/wow.js" type="text/javascript" ></script>
    <script src="${resourcePrefix}/assets/mcc/js/scripts/reveal-animate/reveal-animate.js" type="text/javascript" ></script>
    <script src="${resourcePrefix}/assets/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>
    <!-- END: CORE PLUGINS -->

    <!-- BEGIN: LAYOUT PLUGINS -->
    <script src="${resourcePrefix}/assets/plugins/revo-slider/js/jquery.themepunch.tools.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/revo-slider/js/jquery.themepunch.revolution.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/revo-slider/js/extensions/revolution.extension.slideanims.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/revo-slider/js/extensions/revolution.extension.layeranimation.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/revo-slider/js/extensions/revolution.extension.navigation.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/revo-slider/js/extensions/revolution.extension.video.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/revo-slider/js/extensions/revolution.extension.parallax.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/cubeportfolio/js/jquery.cubeportfolio.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/owl-carousel/owl.carousel.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/counterup/jquery.waypoints.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/counterup/jquery.counterup.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/fancybox/jquery.fancybox.pack.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/smooth-scroll/jquery.smooth-scroll.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/typed/typed.min.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/slider-for-bootstrap/js/bootstrap-slider.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/plugins/js-cookie/js.cookie.js" type="text/javascript"></script>
    <!-- END: LAYOUT PLUGINS -->

    <!-- BEGIN: THEME SCRIPTS -->
    <script src="${resourcePrefix}/assets/base/js/components.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/base/js/components-shop.js" type="text/javascript"></script>
    <script src="${resourcePrefix}/assets/base/js/app.js" type="text/javascript"></script>
    <script>
        $(document).ready(function() {
            App.init();
        });
    </script>
    <!-- END: THEME SCRIPTS -->

    <!-- BEGIN: PAGE SCRIPTS -->
    <script>
        $(document).ready(function() {

            var slider = $('.c-layout-revo-slider .tp-banner');
            var cont = $('.c-layout-revo-slider .tp-banner-container');
            var height = (App.getViewPort().width < App.getBreakpoint('md') ? 400 : 620);

            var api = slider.show().revolution({
                sliderType:"standard",
                sliderLayout:"fullwidth",
                delay: 15000,
                autoHeight: 'off',
                gridheight:500,

                navigation: {
                    keyboardNavigation:"off",
                    keyboard_direction: "horizontal",
                    mouseScrollNavigation:"off",
                    onHoverStop:"on",
                    arrows: {
                        style:"circle",
                        enable:true,
                        hide_onmobile:false,
                        hide_onleave:false,
                        tmp:'',
                        left: {
                            h_align:"left",
                            v_align:"center",
                            h_offset:30,
                            v_offset:0
                        },
                        right: {
                            h_align:"right",
                            v_align:"center",
                            h_offset:30,
                            v_offset:0
                        }
                    },
                    touch:{
                        touchenabled:"on",
                        swipe_threshold: 75,
                        swipe_min_touches: 1,
                        swipe_direction: "horizontal",
                        drag_block_vertical: false
                    },
                },
                viewPort: {
                    enable:true,
                    outof:"pause",
                    visible_area:"80%"
                },

                shadow: 0,

                spinner: "spinner2",

                disableProgressBar:"on",

                fullScreenOffsetContainer: '.tp-banner-container',

                hideThumbsOnMobile: "on",
                hideNavDelayOnMobile: 1500,
                hideBulletsOnMobile: "on",
                hideArrowsOnMobile: "on",
                hideThumbsUnderResolution: 0,

            });
        }); //ready
    </script>
    <!-- END: PAGE SCRIPTS -->
    <!-- END: LAYOUT/BASE/BOTTOM -->

    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id={{ config.googleAnalyticsKey }}"></script>

    <!--  GOOGLE reCAPTCHA -->
    <script src='//www.google.com/recaptcha/api.js'></script>
    <!--  GOOGLE reCAPTCHA -->

    <script src="//cc.cdn.civiccomputing.com/9/cookieControl-9.x.min.js" type="text/javascript"></script>

    <script src="https://tymxcm8ksmvn.statuspage.io/embed/script.js" type="text/javascript"></script>

    <#if script?is_directive><@script /><#else>${script}</#if>
    </body>
</html>
</#macro>