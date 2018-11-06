var app;
$(document).ready(() => {
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