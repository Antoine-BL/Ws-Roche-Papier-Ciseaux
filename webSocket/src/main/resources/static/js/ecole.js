var app;
console.log('help');

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
        },
    });

    $.ajax("/api/monCompte", {
        success: (data) => app.user = data
    });

    reinitialiserListe();

    function reinitialiserListe() {
        $.ajax("/api/comptes", {
            success: (data) => {
                const deshonorables = [];
                console.log(data);

                for (let i = 0;i < data.length; i++){
                    compte = data[i];
                    if (compte.deshonore) {
                        deshonorables.push(data.splice(i, 1)[0]);
                        i--;
                    }
                }
                console.log(data);
                app.deshonorables = deshonorables;
                app.venerables = data.filter(e => e.role.role === "VENERABLE");
                app.senseis = data.filter(e => e.role.role === "SENSEI");
                app.anciens = data.filter(e => e.role.role === "ANCIEN");
                app.nouveaux = data.filter(e => e.role.role === "NOUVEAU");
            }
        });
    }
});