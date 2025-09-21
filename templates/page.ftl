<#macro template>
<!DOCTYPE html><!DOCTYPE html>
<html lang="en" data-bs-theme="light">

<head>
 <meta charset="utf-8">
 <meta name="viewport" content="width=device-width, initial-scale=1">
 <meta name="description" content="">
 <title>Knarvik Aktivitetspark</title>
 <link href="bootstrap.min.css" rel="stylesheet">
 <link rel="stylesheet" href="bootstrap-icons.min.css">
 <link href="styles.css" rel="stylesheet">
 <script src="bootstrap.bundle.js"></script>
</head>

<body>
 <nav class="navbar navbar-expand-lg fixed-top bg-white">
   <div class="container">
     <a class="navbar-brand" href="#"><img src="logo.svg"></a>
     <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse"
       aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
       <span class="navbar-toggler-icon"></span>
     </button>
     <div class="collapse navbar-collapse bg-white p-3" id="navbarCollapse">
       <ul class="navbar-nav me-auto">
         <li class="nav-item"><a class="nav-link" href="#om">Om prosjektet</a></li>
         <li class="nav-item"><a class="nav-link" href="#bidra">Bidra</a></li>
         <li class="nav-item"><a class="nav-link" href="#sponsorer">Sponsorer</a></li>
         <li class="nav-item"><a class="nav-link" href="#kontakt">Kontakt oss</a></li>
       </ul>
     </div>
   </div>
 </nav>

 <div class="navbar-clear"></div>

 <#nested>

 <script>
   document.addEventListener('DOMContentLoaded', function () {
     const navLinks = document.querySelectorAll('.navbar-collapse .nav-link');
     const navbarCollapse = document.querySelector('.navbar-collapse');

     navLinks.forEach(link => {
       link.addEventListener('click', function () {
         // Check if the navbar is currently shown (collapsed)
         if (navbarCollapse.classList.contains('show')) {
           // Remove the 'show' class to hide it
           navbarCollapse.classList.remove('show');
         }
       });
     });
   });
 </script>

</body>

</html>
</#macro>