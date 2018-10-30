var app;
$(document).ready(() => {
    Vue.component('app-membre-ecole', {
        props: ['utilisateur'],
        template: '<li>' +
            '<img class="profile profile-petit d-inline-block" v-bind:src="\'/api/avatars/\' + utilisateur.avatarId"/>' +
            '<span>{{utilisateur.alias}}, {{utilisateur.role}}, {{utilisateur.groupe}}, {{utilisateur.courriel}}</span>' +
            '</li>'
    });

    app = new Vue({
        el: '#app',
        data: {
            user: null,
            venerables: null,
            senseis: null,
            anciens: null,
            nouveaux: null
        }
    });

    $.ajax("/api/monCompte", {
        success: (data) => app.user = data
    });

    $.ajax("/api/comptes", {
        success: (data) => {
            app.venerables = data.filter(e => e.role === "VENERABLE");
            app.senseis = data.filter(e => e.role === "SENSEI");
            app.anciens = data.filter(e => e.role === "ANCIEN");
            app.nouveaux = data.filter(e => e.role === "NOUVEAU");
        }
    });
});