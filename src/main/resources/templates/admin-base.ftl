<#import "/spring.ftl" as spring />
<#import "components.ftl" as components />

<#macro defaultHeaders>

</#macro>

<#macro defaultScripts>

</#macro>
<#macro mainLayout headers=defaultHeaders script=defaultScripts>
	<!DOCTYPE html>
	<html lang="en">
		<head>
			<!-- Required Meta Tags Always Come First -->
			<meta charset="utf-8">
			<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
			<meta name="_csrf" content="${_csrf.token}"/>
			<meta name="_csrf_header" content="${_csrf.headerName}"/>

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

			<!-- Favicon -->
			<link rel="shortcut icon" href="${resourcePrefix}${config.favicon}">

			<!-- Font -->
			<link href="//fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&display=swap" rel="stylesheet">

			<!-- CSS Implementing Plugins -->
			<link rel="stylesheet" href="${resourcePrefix}/front/admin/assets/vendor/icon-set/style.css">
			<link rel="stylesheet" href="${resourcePrefix}/front/admin/assets/vendor/select2/dist/css/select2.min.css">
			<!-- CSS Front Template -->
			<link rel="stylesheet" href="${resourcePrefix}/front/admin/assets/css/theme.min.css">

			<#if headers?is_directive><@headers /><#else>${headers}</#if>
		</head>
		<body class="bg-light">
			<!-- Search Form -->
			<div id="searchDropdown" class="hs-unfold-content dropdown-unfold search-fullwidth d-md-none">
				<form class="input-group input-group-merge input-group-borderless">
					<div class="input-group-prepend">
						<div class="input-group-text">
							<i class="tio-search"></i>
						</div>
					</div>

					<input class="form-control rounded-0" type="search" placeholder="Search in front" aria-label="Search in front">

					<div class="input-group-append">
						<div class="input-group-text">
							<div class="hs-unfold">
								<a class="js-hs-unfold-invoker" href="javascript:;"
								   data-hs-unfold-options='{
										 "target": "#searchDropdown",
										 "type": "css-animation",
										 "animationIn": "fadeIn",
										 "hasOverlay": "rgba(46, 52, 81, 0.1)",
										 "closeBreakpoint": "md"
									   }'>
									<i class="tio-clear tio-lg"></i>
								</a>
							</div>
						</div>
					</div>
				</form>
			</div>
			<!-- End Search Form -->

			<!-- ========== HEADER ========== -->
			<header id="header" class="navbar navbar-expand-lg navbar-fixed-lg navbar-container navbar-light">
				<div class="navbar-nav-wrap">
					<div class="navbar-brand-wrapper">
						<!-- Logo -->
						<a class="navbar-brand" href="/administration" aria-label="Admin Home">
							<img class="navbar-brand-logo" src="${resourcePrefix}${config.logo}" alt="${config.clubName}">
						</a>
						<!-- End Logo -->
					</div>

					<div class="navbar-nav-wrap-content-left">
						<!-- Search Form -->
						<div class="d-none d-lg-block">
							<form class="position-relative">
								<!-- Input Group -->
								<div class="input-group input-group-merge navbar-input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">
											<i class="tio-search"></i>
										</div>
									</div>
									<input type="search" class="js-form-search form-control" placeholder="Search in front" aria-label="Search in front"
										   data-hs-form-search-options='{
											   "clearIcon": "#clearSearchResultsIcon",
											   "dropMenuElement": "#searchDropdownMenu",
											   "dropMenuOffset": 20,
											   "toggleIconOnFocus": true,
											   "activeClass": "focus"
											 }'>
									<a class="input-group-append" href="javascript:;">
										<span class="input-group-text">
										  <i id="clearSearchResultsIcon" class="tio-clear" style="display: none;"></i>
										</span>
									</a>
								</div>
								<!-- End Input Group -->

								<!-- Card Search Content -->
								<div id="searchDropdownMenu" class="hs-form-search-menu-content card dropdown-menu dropdown-card overflow-hidden">
									<!-- Body -->
									<div class="card-body-height py-3">
										<small class="dropdown-header mb-n2">Recent searches</small>

										<div class="dropdown-item bg-transparent text-wrap my-2">
											<span class="h4 mr-1">
												<a class="btn btn-xs btn-soft-dark btn-pill" href="../index.html">
												  Gulp <i class="tio-search ml-1"></i>
												</a>
											</span>
											<span class="h4">
												<a class="btn btn-xs btn-soft-dark btn-pill" href="../index.html">
												  Notification panel <i class="tio-search ml-1"></i>
												</a>
											</span>
										</div>

										<div class="dropdown-divider my-3"></div>

										<small class="dropdown-header mb-n2">Tutorials</small>

										<a class="dropdown-item my-2" href="../index.html">
											<div class="media align-items-center">
												<span class="icon icon-xs icon-soft-dark icon-circle mr-2">
												  <i class="tio-tune"></i>
												</span>

												<div class="media-body text-truncate">
													<span>How to set up Gulp?</span>
												</div>
											</div>
										</a>

										<a class="dropdown-item my-2" href="../index.html">
											<div class="media align-items-center">
												<span class="icon icon-xs icon-soft-dark icon-circle mr-2">
												  <i class="tio-paint-bucket"></i>
												</span>

												<div class="media-body text-truncate">
													<span>How to change theme color?</span>
												</div>
											</div>
										</a>

										<div class="dropdown-divider my-3"></div>

										<small class="dropdown-header mb-n2">Members</small>

										<a class="dropdown-item my-2" href="../index.html">
											<div class="media align-items-center">
												<img class="avatar avatar-xs avatar-circle mr-2" src="${resourcePrefix}/front/admin/assets/img/160x160/img10.jpg" alt="Image Description">
												<div class="media-body text-truncate">
													<span>Amanda Harvey <i class="tio-verified text-primary" data-toggle="tooltip" data-placement="top" title="Top endorsed"></i></span>
												</div>
											</div>
										</a>

										<a class="dropdown-item my-2" href="../index.html">
											<div class="media align-items-center">
												<img class="avatar avatar-xs avatar-circle mr-2" src="${resourcePrefix}/front/admin/assets/img/160x160/img3.jpg" alt="Image Description">
												<div class="media-body text-truncate">
													<span>David Harrison</span>
												</div>
											</div>
										</a>

										<a class="dropdown-item my-2" href="../index.html">
											<div class="media align-items-center">
												<div class="avatar avatar-xs avatar-soft-info avatar-circle mr-2">
													<span class="avatar-initials">A</span>
												</div>
												<div class="media-body text-truncate">
													<span>Anne Richard</span>
												</div>
											</div>
										</a>
									</div>
									<!-- End Body -->

									<!-- Footer -->
									<a class="card-footer text-center" href="../index.html">
										See all results
										<i class="tio-chevron-right"></i>
									</a>
									<!-- End Footer -->
								</div>
								<!-- End Card Search Content -->
							</form>
						</div>
						<!-- End Search Form -->
					</div>

					<!-- Secondary Content -->
					<div class="navbar-nav-wrap-content-right">
						<!-- Navbar -->
						<ul class="navbar-nav align-items-center flex-row">
							<li class="nav-item d-lg-none">
								<!-- Search Trigger -->
								<div class="hs-unfold">
									<a class="js-hs-unfold-invoker btn btn-icon btn-ghost-secondary rounded-circle" href="javascript:;"
									   data-hs-unfold-options='{
										   "target": "#searchDropdown",
										   "type": "css-animation",
										   "animationIn": "fadeIn",
										   "hasOverlay": "rgba(46, 52, 81, 0.1)",
										   "closeBreakpoint": "md"
										 }'>
										<i class="tio-search"></i>
									</a>
								</div>
								<!-- End Search Trigger -->
							</li>

							<li class="nav-item">
								<!-- Account -->
								<div class="hs-unfold">
									<a class="js-hs-unfold-invoker navbar-dropdown-account-wrapper" href="javascript:;"
													   data-hs-unfold-options='{
										   "target": "#accountNavbarDropdown",
										   "type": "css-animation"
										 }'>
										<div class="avatar avatar-sm avatar-circle">
											<img class="avatar-img"
												 src="//www.gravatar.com/avatar/${user.gravatarHash}?s=100&d=identicon"
												 alt="Image Description">
											<span class="avatar-status avatar-sm-status avatar-status-success"></span>
										</div>
									</a>

									<div id="accountNavbarDropdown"
										 class="hs-unfold-content dropdown-unfold dropdown-menu dropdown-menu-right navbar-dropdown-menu navbar-dropdown-account"
										 style="width: 16rem;">
										<div class="dropdown-item-text">
											<div class="media align-items-center">
												<div class="avatar avatar-sm avatar-circle mr-2">
													<img class="avatar-img"
														 src="//www.gravatar.com/avatar/${user.gravatarHash}?s=100&d=identicon"
														 alt="Image Description">
												</div>
												<div class="media-body">
													<span class="card-title h5">${user.givenName} ${user.familyName}</span>
													<span class="card-text">${user.email}</span>
												</div>
											</div>
										</div>

										<div class="dropdown-divider"></div>

										<a class="dropdown-item" href="#">
											<span class="text-truncate pr-2" title="Profile &amp; account">Profile &amp; account</span>
										</a>

										<a class="dropdown-item" href="#">
											<span class="text-truncate pr-2" title="Settings">Settings</span>
										</a>

										<div class="dropdown-divider"></div>

										<a class="dropdown-item" href="/logout">
											<span class="text-truncate pr-2"
												  title="<@spring.message code="menu.logout" />">
												<@spring.message code="menu.logout" />
											</span>
										</a>
									</div>
								</div>
								<!-- End Account -->
							</li>
						</ul>
						<!-- End Navbar -->
					</div>
					<!-- End Secondary Content -->
				</div>
			</header>
			<!-- ========== END HEADER ========== -->

			<!-- ========== MAIN CONTENT ========== -->
			<main id="content" role="main" class="main">
				<!-- Content -->
				<div class="bg-dark">
					<div class="content container-fluid" style="height: 25rem;">
						<!-- Page Header -->
						<div class="page-header page-header-light page-header-reset">
							<div class="row align-items-center">
								<div class="col">
									<h1 class="page-header-title">
										<#if pageTitle??>
											${pageTitle}
										<#else>
											<@spring.messageArgsText
											code="menu.${currentRoute.name}"
											args=currentRoute.argumentValues
											text=currentRoute.name
											/>
										</#if>
									</h1>
								</div>
								<div class="col-auto">
									<a class="btn btn-primary" href="#">My dashboard</a>
								</div>
							</div>
							<!-- End Row -->
						</div>
						<!-- End Page Header -->
					</div>
				</div>
				<!-- End Content -->

				<!-- Content -->
				<div class="content container-fluid" style="margin-top: -20rem;">
					<!-- Navbar Vertical -->
					<div class="navbar-expand-lg">
						<!-- Navbar Toggle -->
						<button type="button" class="navbar-toggler btn btn-block btn-white mb-3" aria-label="Toggle navigation" aria-expanded="false" aria-controls="navbarVerticalNavMenu" data-toggle="collapse" data-target="#navbarVerticalNavMenu">
							<span class="d-flex justify-content-between align-items-center">
								<span class="h5 mb-0">Nav menu</span>
								<span class="navbar-toggle-default">
									<i class="tio-menu-hamburger"></i>
								</span>
								<span class="navbar-toggle-toggled">
									<i class="tio-clear"></i>
								</span>
							</span>
						</button>
						<!-- End Navbar Toggle -->

						<aside id="navbarVerticalNavMenu" class="js-navbar-vertical-aside navbar navbar-vertical navbar-vertical-absolute navbar-vertical-detached navbar-shadow navbar-collapse collapse rounded-lg">
							<div class="navbar-vertical-container">
								<div class="navbar-vertical-footer-offset">
									<!-- Content -->
									<div class="navbar-vertical-content">
										<!-- Navbar Nav -->
										<ul class="navbar-nav navbar-nav-lg card-navbar-nav">
											<@components.adminMenuItems items=dashboardMenu/>

											<#list adminMenus as title, items>
												<li class="nav-item">
													<#assign sectionTitle>
														<@spring.messageText
															code="menu.${title}"
															text=title />
													</#assign>
													<small class="nav-subtitle" title="${sectionTitle}">
														${sectionTitle}
													</small>
													<small class="tio-more-horizontal nav-subtitle-replacer"></small>
												</li>
												<@components.adminMenuItems items=items/>
											</#list>
										</ul>
										<!-- End Navbar Nav -->
									</div>
									<!-- End Content -->

									<!-- Footer -->
									<div class="navbar-vertical-footer">
										<ul class="navbar-vertical-footer-list">
											<li class="navbar-vertical-footer-list-item">
												<a class="btn btn-icon btn-ghost-secondary rounded-circle"
												   	href="/"
													alt="Back to website">
													<i class="tio-home-vs-1-outlined "></i>
												</a>
											</li>

											<li class="navbar-vertical-footer-list-item">
												<!-- Other Links -->
												<div class="hs-unfold">
													<a class="js-hs-unfold-invoker btn btn-icon btn-ghost-secondary rounded-circle" href="javascript:;"
													   data-hs-unfold-options='{
														  "target": "#otherLinksDropdown",
														  "type": "css-animation",
														  "animationIn": "slideInDown",
														  "hideOnScroll": true
														 }'>
														<i class="tio-help-outlined"></i>
													</a>

													<div id="otherLinksDropdown" class="hs-unfold-content dropdown-unfold dropdown-menu navbar-vertical-footer-dropdown">
														<span class="dropdown-header">Help</span>
														<a class="dropdown-item" href="#">
															<i class="tio-book-outlined dropdown-item-icon"></i>
															<span class="text-truncate pr-2" title="Resources &amp; tutorials">Resources &amp; tutorials</span>
														</a>
														<div class="dropdown-divider"></div>
														<span class="dropdown-header">Contacts</span>
														<a class="dropdown-item" href="#">
															<i class="tio-chat-outlined dropdown-item-icon"></i>
															<span class="text-truncate pr-2" title="Contact support">Contact support</span>
														</a>
													</div>
												</div>
												<!-- End Other Links -->
											</li>
										</ul>
									</div>
									<!-- End Footer -->
								</div>
							</div>
						</aside>
					</div>
					<!-- End Navbar Vertical -->

					<!-- Sidebar Detached Content -->
					<div class="sidebar-detached-content mt-3 mt-lg-0">
						<#nested />
					</div>
					<!-- End Sidebar Detached Content -->
				</div>
				<!-- End Content -->
			</main>
			<!-- ========== END MAIN CONTENT ========== -->

			<!-- ========== SECONDARY CONTENTS ========== -->
			<!-- Keyboard Shortcuts -->
			<div id="keyboardShortcutsSidebar" class="hs-unfold-content sidebar sidebar-bordered sidebar-box-shadow">
				<div class="card card-lg sidebar-card">
					<div class="card-header">
						<h4 class="card-header-title">Keyboard shortcuts</h4>

						<!-- Toggle Button -->
						<a class="js-hs-unfold-invoker btn btn-icon btn-xs btn-ghost-dark ml-2" href="javascript:;"
						   data-hs-unfold-options='{
					  "target": "#keyboardShortcutsSidebar",
					  "type": "css-animation",
					  "animationIn": "fadeInRight",
					  "animationOut": "fadeOutRight",
					  "hasOverlay": true,
					  "smartPositionOff": true
					 }'>
							<i class="tio-clear tio-lg"></i>
						</a>
						<!-- End Toggle Button -->
					</div>

					<!-- Body -->
					<div class="card-body sidebar-body sidebar-scrollbar">
						<div class="list-group list-group-sm list-group-flush list-group-no-gutters mb-5">
							<div class="list-group-item">
								<h5 class="mb-1">Formatting</h5>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span class="font-weight-bold">Bold</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">b</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<em>italic</em>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">i</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<u>Underline</u>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">u</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<s>Strikethrough</s>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Alt</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">s</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span class="small">Small text</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">s</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<mark>Highlight</mark>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">e</kbd>
									</div>
								</div>
							</div>
						</div>

						<div class="list-group list-group-sm list-group-flush list-group-no-gutters mb-5">
							<div class="list-group-item">
								<h5 class="mb-1">Insert</h5>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Mention person <a href="#">(@Brian)</a></span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">@</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Link to doc <a href="#">(+Meeting notes)</a></span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">+</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<a href="#">#hashtag</a>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">#hashtag</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Date</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">/date</kbd>
										<kbd class="d-inline-block mb-1">Space</kbd>
										<kbd class="d-inline-block mb-1">/datetime</kbd>
										<kbd class="d-inline-block mb-1">/datetime</kbd>
										<kbd class="d-inline-block mb-1">Space</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Time</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">/time</kbd>
										<kbd class="d-inline-block mb-1">Space</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Note box</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">/note</kbd>
										<kbd class="d-inline-block mb-1">Enter</kbd>
										<kbd class="d-inline-block mb-1">/note red</kbd>
										<kbd class="d-inline-block mb-1">/note red</kbd>
										<kbd class="d-inline-block mb-1">Enter</kbd>
									</div>
								</div>
							</div>
						</div>

						<div class="list-group list-group-sm list-group-flush list-group-no-gutters mb-5">
							<div class="list-group-item">
								<h5 class="mb-1">Editing</h5>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Find and replace</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">r</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Find next</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">n</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Find previous</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">p</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Indent</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Tab</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Un-indent</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Shift</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Tab</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Move line up</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Shift</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1"><i class="tio-arrow-large-upward-outlined"></i></kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Move line down</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Shift</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1"><i class="tio-arrow-large-downward-outlined font-size-sm"></i></kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Add a comment</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Alt</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">m</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Undo</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">z</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Redo</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">y</kbd>
									</div>
								</div>
							</div>
						</div>

						<div class="list-group list-group-sm list-group-flush list-group-no-gutters">
							<div class="list-group-item">
								<h5 class="mb-1">Application</h5>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Create new doc</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Alt</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">n</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Present</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Shift</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">p</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Share</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Shift</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">s</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Search docs</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Shift</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">o</kbd>
									</div>
								</div>
							</div>
							<div class="list-group-item">
								<div class="row align-items-center">
									<div class="col-5">
										<span>Keyboard shortcuts</span>
									</div>
									<div class="col-7 text-right">
										<kbd class="d-inline-block mb-1">Ctrl</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">Shift</kbd> <small class="text-muted">+</small> <kbd class="d-inline-block mb-1">/</kbd>
									</div>
								</div>
							</div>
						</div>
					</div>
					<!-- End Body -->
				</div>
			</div>
			<!-- End Keyboard Shortcuts -->

			<!-- Activity -->
			<div id="activitySidebar" class="hs-unfold-content sidebar sidebar-bordered sidebar-box-shadow">
				<div class="card card-lg sidebar-card">
					<div class="card-header">
						<h4 class="card-header-title">Activity stream</h4>

						<!-- Toggle Button -->
						<a class="js-hs-unfold-invoker btn btn-icon btn-xs btn-ghost-dark ml-2" href="javascript:;"
						   data-hs-unfold-options='{
					"target": "#activitySidebar",
					"type": "css-animation",
					"animationIn": "fadeInRight",
					"animationOut": "fadeOutRight",
					"hasOverlay": true,
					"smartPositionOff": true
				   }'>
							<i class="tio-clear tio-lg"></i>
						</a>
						<!-- End Toggle Button -->
					</div>

					<!-- Body -->
					<div class="card-body sidebar-body sidebar-scrollbar">
						<!-- Step -->
						<ul class="step step-icon-sm step-avatar-sm">
							<!-- Step Item -->
							<li class="step-item">
								<div class="step-content-wrapper">
									<div class="step-avatar">
										<img class="step-avatar-img" src="${resourcePrefix}/front/admin/assets/img/160x160/img9.jpg" alt="Image Description">
									</div>

									<div class="step-content">
										<h5 class="mb-1">Iana Robinson</h5>

										<p class="font-size-sm mb-1">Added 2 files to task <a class="text-uppercase" href="#"><i class="tio-folder-bookmarked"></i> Fd-7</a></p>

										<ul class="list-group list-group-sm">
											<!-- List Item -->
											<li class="list-group-item list-group-item-light">
												<div class="row gx-1">
													<div class="col-6">
														<div class="media">
									<span class="mt-1 mr-2">
									  <img class="avatar avatar-xs" src="${resourcePrefix}/front/admin/assets/svg/brands/excel.svg" alt="Image Description">
									</span>
															<div class="media-body text-truncate">
																<span class="d-block font-size-sm text-dark text-truncate" title="weekly-reports.xls">weekly-reports.xls</span>
																<small class="d-block text-muted">12kb</small>
															</div>
														</div>
													</div>
													<div class="col-6">
														<div class="media">
									<span class="mt-1 mr-2">
									  <img class="avatar avatar-xs" src="${resourcePrefix}/front/admin/assets/svg/brands/word.svg" alt="Image Description">
									</span>
															<div class="media-body text-truncate">
																<span class="d-block font-size-sm text-dark text-truncate" title="weekly-reports.xls">weekly-reports.xls</span>
																<small class="d-block text-muted">4kb</small>
															</div>
														</div>
													</div>
												</div>
											</li>
											<!-- End List Item -->
										</ul>

										<small class="text-muted text-uppercase">Now</small>
									</div>
								</div>
							</li>
							<!-- End Step Item -->

							<!-- Step Item -->
							<li class="step-item">
								<div class="step-content-wrapper">
									<span class="step-icon step-icon-soft-dark">B</span>

									<div class="step-content">
										<h5 class="mb-1">Bob Dean</h5>

										<p class="font-size-sm mb-1">Marked <a class="text-uppercase" href="#"><i class="tio-folder-bookmarked"></i> Fr-6</a> as <span class="badge badge-soft-success badge-pill"><span class="legend-indicator bg-success"></span>"Completed"</span></p>

										<small class="text-muted text-uppercase">Today</small>
									</div>
								</div>
							</li>
							<!-- End Step Item -->

							<!-- Step Item -->
							<li class="step-item">
								<div class="step-content-wrapper">
									<div class="step-avatar">
										<img class="step-avatar-img" src="${resourcePrefix}/front/admin/assets/img/160x160/img3.jpg" alt="Image Description">
									</div>

									<div class="step-content">
										<h5 class="h5 mb-1">Crane</h5>

										<p class="font-size-sm mb-1">Added 5 card to <a href="#">Payments</a></p>

										<ul class="list-group list-group-sm">
											<li class="list-group-item list-group-item-light">
												<div class="row gx-1">
													<div class="col">
														<img class="img-fluid rounded ie-sidebar-activity-img" src="${resourcePrefix}/front/admin/assets/svg/illustrations/card-1.svg" alt="Image Description">
													</div>
													<div class="col">
														<img class="img-fluid rounded ie-sidebar-activity-img" src="${resourcePrefix}/front/admin/assets/svg/illustrations/card-2.svg" alt="Image Description">
													</div>
													<div class="col">
														<img class="img-fluid rounded ie-sidebar-activity-img" src="${resourcePrefix}/front/admin/assets/svg/illustrations/card-3.svg" alt="Image Description">
													</div>
													<div class="col-auto align-self-center">
														<div class="text-center">
															<a href="#">+2</a>
														</div>
													</div>
												</div>
											</li>
										</ul>

										<small class="text-muted text-uppercase">May 12</small>
									</div>
								</div>
							</li>
							<!-- End Step Item -->

							<!-- Step Item -->
							<li class="step-item">
								<div class="step-content-wrapper">
									<span class="step-icon step-icon-soft-info">D</span>

									<div class="step-content">
										<h5 class="mb-1">David Lidell</h5>

										<p class="font-size-sm mb-1">Added a new member to Front Dashboard</p>

										<small class="text-muted text-uppercase">May 15</small>
									</div>
								</div>
							</li>
							<!-- End Step Item -->

							<!-- Step Item -->
							<li class="step-item">
								<div class="step-content-wrapper">
									<div class="step-avatar">
										<img class="step-avatar-img" src="${resourcePrefix}/front/admin/assets/img/160x160/img7.jpg" alt="Image Description">
									</div>

									<div class="step-content">
										<h5 class="mb-1">Rachel King</h5>

										<p class="font-size-sm mb-1">Marked <a class="text-uppercase" href="#"><i class="tio-folder-bookmarked"></i> Fr-3</a> as <span class="badge badge-soft-success badge-pill"><span class="legend-indicator bg-success"></span>"Completed"</span></p>

										<small class="text-muted text-uppercase">Apr 29</small>
									</div>
								</div>
							</li>
							<!-- End Step Item -->

							<!-- Step Item -->
							<li class="step-item">
								<div class="step-content-wrapper">
									<div class="step-avatar">
										<img class="step-avatar-img" src="${resourcePrefix}/front/admin/assets/img/160x160/img5.jpg" alt="Image Description">
									</div>

									<div class="step-content">
										<h5 class="mb-1">Finch Hoot</h5>

										<p class="font-size-sm mb-1">Earned a "Top endorsed" <i class="tio-verified text-primary"></i> badge</p>

										<small class="text-muted text-uppercase">Apr 06</small>
									</div>
								</div>
							</li>
							<!-- End Step Item -->

							<!-- Step Item -->
							<li class="step-item">
								<div class="step-content-wrapper">
						<span class="step-icon step-icon-soft-primary">
						  <i class="tio-user"></i>
						</span>

									<div class="step-content">
										<h5 class="mb-1">Project status updated</h5>

										<p class="font-size-sm mb-1">Marked <a class="text-uppercase" href="#"><i class="tio-folder-bookmarked"></i> Fr-3</a> as <span class="badge badge-soft-primary badge-pill"><span class="legend-indicator bg-primary"></span>"In progress"</span></p>

										<small class="text-muted text-uppercase">Feb 10</small>
									</div>
								</div>
							</li>
							<!-- End Step Item -->
						</ul>
						<!-- End Step -->

						<a class="btn btn-block btn-white" href="javascript:;">View all <i class="tio-chevron-right"></i></a>
					</div>
					<!-- End Body -->
				</div>
			</div>
			<!-- End Activity -->

			<!-- Welcome Message Modal -->
			<div class="modal fade" id="welcomeMessageModal" tabindex="-1" role="dialog" aria-hidden="true">
				<div class="modal-dialog modal-dialog-centered" role="document">
					<div class="modal-content">
						<!-- Header -->
						<div class="modal-close">
							<button type="button" class="btn btn-icon btn-sm btn-ghost-secondary" data-dismiss="modal" aria-label="Close">
								<i class="tio-clear tio-lg"></i>
							</button>
						</div>
						<!-- End Header -->

						<!-- Body -->
						<div class="modal-body p-sm-5">
							<div class="text-center">
								<div class="w-75 w-sm-50 mx-auto mb-4">
									<img class="img-fluid" src="${resourcePrefix}/front/admin/assets/svg/illustrations/graphs.svg" alt="Image Description">
								</div>

								<h4 class="h1">Welcome to Front</h4>

								<p>We're happy to see you in our community.</p>
							</div>
						</div>
						<!-- End Body -->

						<!-- Footer -->
						<div class="modal-footer d-block text-center py-sm-5">
							<small class="text-cap mb-4">Trusted by the world's best teams</small>

							<div class="w-85 mx-auto">
								<div class="row justify-content-between">
									<div class="col">
										<img class="img-fluid ie-welcome-brands" src="${resourcePrefix}/front/admin/assets/svg/brands/gitlab-gray.svg" alt="Image Description">
									</div>
									<div class="col">
										<img class="img-fluid ie-welcome-brands" src="${resourcePrefix}/front/admin/assets/svg/brands/fitbit-gray.svg" alt="Image Description">
									</div>
									<div class="col">
										<img class="img-fluid ie-welcome-brands" src="${resourcePrefix}/front/admin/assets/svg/brands/flow-xo-gray.svg" alt="Image Description">
									</div>
									<div class="col">
										<img class="img-fluid ie-welcome-brands" src="${resourcePrefix}/front/admin/assets/svg/brands/layar-gray.svg" alt="Image Description">
									</div>
								</div>
							</div>
						</div>
						<!-- End Footer -->
					</div>
				</div>
			</div>
			<!-- End Welcome Message Modal -->
			<!-- ========== END SECONDARY CONTENTS ========== -->

			<!-- JS Global Compulsory  -->
			<script src="${resourcePrefix}/front/admin/assets/vendor/jquery/dist/jquery.min.js"></script>
			<script src="${resourcePrefix}/front/admin/assets/vendor/jquery-migrate/dist/jquery-migrate.min.js"></script>
			<script src="${resourcePrefix}/front/admin/assets/vendor/bootstrap/dist/js/bootstrap.bundle.min.js"></script>

			<!-- JS Implementing Plugins -->
			<script src="${resourcePrefix}/front/admin/assets/vendor/hs-navbar-vertical-aside/hs-navbar-vertical-aside.min.js"></script>
			<script src="${resourcePrefix}/front/admin/assets/vendor/hs-unfold/dist/hs-unfold.min.js"></script>
			<script src="${resourcePrefix}/front/admin/assets/vendor/hs-form-search/dist/hs-form-search.min.js"></script>
			<script src="${resourcePrefix}/front/admin/assets/vendor/hs-transform-tabs-to-btn/dist/hs-transform-tabs-to-btn.min.js"></script>
			<script src="${resourcePrefix}/front/admin/assets/vendor/hs-nav-scroller/dist/hs-nav-scroller.min.js"></script>
			<script src="${resourcePrefix}/front/admin/assets/vendor/select2/dist/js/select2.full.min.js"></script>
			<script src="${resourcePrefix}/front/admin/assets/vendor/datatables/media/js/jquery.dataTables.min.js"></script>
			<script src="${resourcePrefix}/front/admin/assets/vendor/datatables.net.extensions/select/select.min.js"></script>
			<!-- JS Front -->
			<script src="${resourcePrefix}/front/admin/assets/js/theme.min.js"></script>

			<#if script?is_directive><@script /><#else>${script}</#if>

			<!-- JS Plugins Init. -->
			<script>
				$(document).on('ready', function () {
					// INITIALIZATION OF NAVBAR VERTICAL NAVIGATION
					// =======================================================
					var sidebar = $('.js-navbar-vertical-aside').hsSideNav();


					// INITIALIZATION OF UNFOLD
					// =======================================================
					$('.js-hs-unfold-invoker').each(function () {
						var unfold = new HSUnfold($(this)).init();
					});


					// INITIALIZATION OF FORM SEARCH
					// =======================================================
					$('.js-form-search').each(function () {
						new HSFormSearch($(this)).init()
					});

					$('.js-select2-custom').each(function () {
						var select2 = $.HSCore.components.HSSelect2.init($(this));
					});

					if (typeof onPageLoad === "function") onPageLoad();
				});
			</script>

			<!-- IE Support -->
			<script>
				if (/MSIE \d|Trident.*rv:/.test(navigator.userAgent)) document.write('<script src="${resourcePrefix}/front/admin/assets/vendor/babel-polyfill/polyfill.min.js"><\/script>');
			</script>
		</body>
	</html>
</#macro>