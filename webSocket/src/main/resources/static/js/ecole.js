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
            console.log(data);
            app.venerables = data.filter(e => e.role === "Venerable");
            app.senseis = data.filter(e => e.role === "Sensei");
            app.anciens = data.filter(e => e.role === "Ancien");
            app.nouveaux = data.filter(e => e.role === "Nouveau");
        }
    });
});