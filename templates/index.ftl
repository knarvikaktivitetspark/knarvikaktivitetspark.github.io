<#import "page.ftl" as page>
<@page.template>

<div class="container-fluid px-3 pt-5 text-center bg-skate text-light">
    <#include "index-header.ftl">
</div>

<div id="om" class="link-fix"></div>
<div class="container-fluid bg-light px-3 py-5 text-center border-bottom">
    <div class="container mx-auto text-center">
        <#include "index-om.ftl">
        <#include "index-om-design.ftl">
        <#include "index-om-plan.ftl">
    </div>
</div>

<div id="bidra" class="link-fix"></div>
<div class="container text-center">
    <#include "index-bidra.ftl">
</div>

<div id="sponsorer" class="link-fix"></div>
<div class="container my-5 text-center border-top pt-5">
    <#include "index-sponsorer.ftl">
</div>

<div id="kontakt" class="link-fix"></div>
<div class="container-fluid bg-dark text-light py-5 text-center">
    <div class="container">
        <#include "index-kontakt.ftl">
    </div>
</div>

</@page.template>