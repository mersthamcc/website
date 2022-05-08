<#import "../base.ftl" as layout>
<@layout.mainLayout>
    <div class="d-lg-flex position-relative">
        <div class="container d-lg-flex align-items-lg-center space-top-2 space-lg-0 min-vh-lg-100">
            <!-- Content -->
            <div class="w-md-100">
                <div class="row">
                    <div class="col-lg-5">
                        <div class="mb-5 mt-11">
                            <h1 class="display-4 mb-3">
                                Hello World
                            </h1>
                        </div>

                        <a class="btn btn-primary btn-wide transition-3d-hover" href="./page-login-simple.html">Get Started</a>
                        <a class="btn btn-link btn-wide" href="#">Learn More <i class="fas fa-angle-right fa-sm ml-1"></i></a>
                    </div>
                </div>
            </div>
            <!-- End Content -->

            <!-- SVG Shape -->
            <figure class="col-lg-7 col-xl-6 d-none d-lg-block position-absolute top-0 right-0 pr-0 ie-main-hero">
                <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" viewBox="0 0 1137.5 979.2">
                    <path fill="#F9FBFF" d="M565.5,957.4c81.1-7.4,155.5-49.3,202.4-115.7C840,739.8,857,570,510.7,348.3C-35.5-1.5-4.2,340.3,2.7,389
              c0.7,4.7,1.2,9.5,1.7,14.2l29.3,321c14,154.2,150.6,267.8,304.9,253.8L565.5,957.4z"></path>
                    <defs>
                        <path id="mainHeroSVG1" d="M1137.5,0H450.4l-278,279.7C22.4,430.6,24.3,675,176.8,823.5l0,0C316.9,960,537.7,968.7,688.2,843.6l449.3-373.4V0z"></path>
                    </defs>
                    <clipPath id="mainHeroSVG2">
                        <use xlink:href="#mainHeroSVG1"></use>
                    </clipPath>
                    <g transform="matrix(1 0 0 1 0 0)" clip-path="url(#mainHeroSVG2)">
                        <image width="750" height="750" xlink:href="${resourcePrefix}/mcc/img/familyfunday.jpg" transform="matrix(1.4462 0 0 1.4448 52.8755 0)"></image>
                    </g>
                </svg>
            </figure>
            <!-- End SVG Shape -->
        </div>
    </div>
</@layout.mainLayout>