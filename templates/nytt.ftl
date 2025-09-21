<#import "page.ftl" as page>
<@page.template>

<div class="container-fluid bg-light pb-5">
    <div class="container pt-5">
        <h1 class="display-2">Nyheter</h1>

        <#list newsEntries as newsEntry>
            <div class="container mt-5">
                <#import "nytt/${newsEntry.file}" as newsEntryContent>
                <h1 class="display-6">${newsEntry.date} ${newsEntryContent.title}</h1>
                ${newsEntryContent.content}
            </div>
        </#list>
    </div>
</div>

<div id="kontakt" class="link-fix"></div>
<div class="container-fluid bg-dark text-light py-5 text-center">
    <div class="container">
        <#include "index-kontakt.ftl">
    </div>
</div>

</@page.template>