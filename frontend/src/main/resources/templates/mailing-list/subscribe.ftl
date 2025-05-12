<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro subscribeScripts>
</#macro>

<@layout.mainLayout script=loginScripts headers=headers formName="menu.login">
    <@components.panel>
        <div id="mc_embed_shell" class="form-horizontal w-md-75 w-lg-60 mx-md-auto">
            <div id="mc_embed_signup" class="card mb-3 mb-lg-5">
                <link href="//cdn-images.mailchimp.com/embedcode/classic-061523.css" rel="stylesheet" type="text/css">
                <form
                        action="${config.mailingList.postUrl}"
                        method="post"
                        id="mc-embedded-subscribe-form"
                        name="mc-embedded-subscribe-form"
                        class="validate"
                        target="_blank">
                    <!-- Body -->
                    <div class="card-body">
                        <div id="mc_embed_signup_scroll form-group"><h2>Subscribe to Mailing List</h2>
                            <div class="indicates-required">
                                <span class="asterisk">*</span> indicates required
                            </div>
                            <div class="mc-field-group">
                                <label for="mce-EMAIL">Email Address <span class="asterisk">*</span></label>
                                <input type="email" name="EMAIL" class="required email" id="mce-EMAIL" required="" value="">
                            </div>
                            <div id="mce-responses" class="clear">
                                <div class="response" id="mce-error-response" style="display: none;"></div>
                                <div class="response" id="mce-success-response" style="display: none;"></div>
                            </div>
                            <div aria-hidden="true" style="position: absolute; left: -5000px;">
                                <input type="text" name="${config.mailingList.hiddenFieldName}" tabindex="-1" value="">
                            </div>
                            <div hidden="">
                                <input type="hidden" name="tags" value="${config.mailingList.tags}">
                            </div>

                            <div class="row align-items-center mb-5">
                                <div class="col-sm-6 mb-3 mb-sm-0">
                                    <input type="submit" name="subscribe" id="mc-embedded-subscribe" class="btn btn-primary transition-3d-hover" value="Subscribe">
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
                <script type="text/javascript" src="//s3.amazonaws.com/downloads.mailchimp.com/js/mc-validate.js"></script>
                <script type="text/javascript">(function($) {window.fnames = new Array(); window.ftypes = new Array();fnames[0]='EMAIL';ftypes[0]='email';fnames[1]='FNAME';ftypes[1]='text';fnames[2]='LNAME';ftypes[2]='text';}(jQuery));var $mcj = jQuery.noConflict(true);</script></div>
        </div>
    </@components.panel>
</@layout.mainLayout>
