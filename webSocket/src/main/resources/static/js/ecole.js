var app;
$(document).ready(() => {
    app = new Vue({
        el: '#app',
        data: {
            user: null,
            venerables: null,
            senseis: null,
            anciens: null,
            nouveaux: null,
            deshonorables: null,
        },
        methods: {
            supprimer: function(id) {
                $.ajax({
                    url: "/api/comptes/" + id,
                    'type': 'DELETE',
                    complete: reinitialiserListe,
                    'contentType': 'application/json; charset=utf-8',
                    'dataType': 'json',
                });
            }
        }
    });

    $.ajax("/api/monCompte", {
        success: (data) => app.user = data
    });

    reinitialiserListe();

    function reinitialiserListe() {
        $.ajax("/api/comptes", {
            success: (data) => {
                const deshonorables = [];

                for (let i = 0;i < data.length; i++){
                    compte = data[i];
                    if (compte.deshonore) {
                        deshonorables.push(data.splice(i, 1));
                        i--;
                    }
                }

                app.deshonorables = deshonorables;
                app.venerables = data.filter(e => e.role.role === "Venerable");
                app.senseis = data.filter(e => e.role.role === "Sensei");
                app.anciens = data.filter(e => e.role.role === "Ancien");
                app.nouveaux = data.filter(e => e.role.role === "Nouveau");
            }
        });
    }
});