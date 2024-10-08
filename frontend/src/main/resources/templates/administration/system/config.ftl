<#ftl output_format="HTML" auto_esc=false>
<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>

<@layout.mainLayout>
    <div class="row">
        <div class="col-lg-12">
            <@admin.form action="">
                <@admin.card title="system.profiles">
                    <ul class="unstyled">
                        <#list profiles as profile>
                            <li><code>${profile}</code></li>
                        </#list>
                    </ul>
                </@admin.card>

                <@admin.card title="system.configuration">
                    <#list properties?keys?sort as key>
                        <div class="row form-group">
                            <code class="col-md-4 control-label text-right">
                                ${key}
                            </code>
                            <pre class="col-md-8" style="white-space: pre-wrap;">${properties[key]?esc}</pre>
                        </div>
                    </#list>
                </@admin.card>

                <@admin.card title="system.environment">
                    <#list env?keys?sort as key>
                        <div class="row form-group">
                            <code class="col-md-4 control-label text-right">
                                ${key}
                            </code>
                            <pre class="col-md-8" style="white-space: pre-wrap;">${env[key]?esc}</pre>
                        </div>
                    </#list>
                </@admin.card>

                <@admin.card title="system.graph-profiles">
                    <ul class="unstyled">
                        <#list graphProfiles as profile>
                            <li><code>${profile}</code></li>
                        </#list>
                    </ul>
                </@admin.card>

                <@admin.card title="system.graph-configuration">
                    <#list graphProperties?keys?sort as key>
                        <div class="row form-group">
                            <code class="col-md-4 control-label text-right">
                                ${key}
                            </code>
                            <pre class="col-md-8" style="white-space: pre-wrap;">${graphProperties[key]?esc}</pre>
                        </div>
                    </#list>
                </@admin.card>

                <@admin.card title="system.graph-environment">
                    <#list graphEnvironment?keys?sort as key>
                        <div class="row form-group">
                            <code class="col-md-4 control-label text-right">
                                ${key}
                            </code>
                            <pre class="col-md-8" style="white-space: pre-wrap;">${graphEnvironment[key]?esc}</pre>
                        </div>
                    </#list>
                </@admin.card>

                <@components.buttonGroup>
                    <a href="/administration" class="btn btn-primary transition-3d-hover">
                        <@spring.message code="system.cancel" />
                    </a>
                </@components.buttonGroup>
            </@admin.form>
        </div>
    </div>
</@layout.mainLayout>