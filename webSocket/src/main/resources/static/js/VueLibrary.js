Vue.component('app-nav', {
    props: ['user', 'currentPage'],
    template: '<nav class="navbar navbar-expand-lg navbar-dark bg-dark text-light align-items-center justify-content-between">\n' +
        '    <div class="navbar-brand d-inline-flex align-items-center"><img class="profile-petit" src="/images/websocket_logo_white.png"/> Projet Websocket</div>\n' +
        '    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">\n' +
        '        <span class="navbar-toggler-icon"></span>\n' +
        '    </button>\n' +
        '    <div class="collapse navbar-collapse" id="navbarSupportedContent">\n' +
        '        <ul class="navbar-nav ml-auto align-items-center">\n' +
        '            <app-nav-item v-if="currentPage != \'dojo\'" text="Dojo" destination="/"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'ecole\'" text="Notre École" destination="/ecole"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'kumite\' && user" text="Kumite" destination="/kumite"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'passage\' && user && (user.role == \'VENERABLE\' || user.role == \'SENSEI\')" text="Passage de Grades" destination="/passage"></app-nav-item>' +
        '            <a v-if="user == null" class="nav-item active btn btn-success mr-auto" id="connexion" href="/connexion" role="button">Se connecter</a>' +
        '            <app-profile v-if="user" v-bind:user="user"></app-profile>' +
        '        </ul>\n' +
        '    </div>\n' +
        '</nav>'
});

Vue.component('app-nav-item', {
    props: ['text', 'destination'],
    template: '<li class="nav-link"><a v-bind:href="destination" class="nav-link active">{{text}}</a></li>'
});

Vue.component('app-profile', {
    props: ['user'],
    template: '<li class="nav-item dropdown">\n' +
        '                <a class="text-light dropdown-toggle" role="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">\n' +
        '                    <img v-bind:src="\'/api/avatars/\' + user.avatarId" class="profile profile-petit" width="96" height="96"/>\n' +
        '                </a>\n' +
        '                <div class="dropdown-menu dropdown-menu-right bg-dark text-light" aria-labelledby="dropdownMenuButton">\n' +
        '                    <h5 class="dropdown-header font-weight-bold  text-light">Connecté en tant que: {{user.alias}}</h5>\n' +
        '                    <div class="dropdown-item-text text-light" >Rôle: {{user.role}}</div>\n' +
        '                    <div class="dropdown-item-text  text-light" >Groupe: {{user.groupe}}</div>\n' +
        '                    <div class="dropdown-item-text  text-light" >Crédits: {{user.credits}}</div>\n' +
        '                    <div class="dropdown-item-text  text-light" >Points: {{user.points}}</div>\n' +
        '                    <div class="dropdown-divider text-light"></div>\n' +
        '                    <form action="/deconnexion" method="GET">\n' +
        '                        <button class="dropdown-item text-light deconnexion" type="submit">Déconnexion</button>\n' +
        '                    </form>\n' +
        '                </div>\n' +
        '            </li>'
});