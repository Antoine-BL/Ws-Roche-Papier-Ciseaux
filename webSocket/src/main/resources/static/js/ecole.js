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
        }
    });

    $.ajax("/api/monCompte", {
        success: (data) => app.user = data
    });

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
            app.venerables = data.filter(e => e.role === "Venerable");
            app.senseis = data.filter(e => e.role === "Sensei");
            app.anciens = data.filter(e => e.role === "Ancien");
            app.nouveaux = data.filter(e => e.role === "Nouveau");
        }
    });
});