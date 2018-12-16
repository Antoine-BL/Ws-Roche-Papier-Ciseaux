let app;
$(document).ready(function (){
    app = new Vue({
        el: '#app',
        data: {
            user: null,
            eligiblesCeinture: null,
            eligiblesRole: null,
        },
        methods: {
            reussir: function (index, key, id) {
                const eleve = app.eligiblesCeinture[key].splice(index, 1)[0];
                enregistrerExam(eleve, true);
                promotionCeinture(eleve);
            },
            echouer: function(index, key, id) {
                const eleve = app.eligiblesCeinture[key].splice(index, 1)[0];
                enregistrerExam(eleve, false);
            },
            reussirRole: function (index, key, id) {
                const eleve = app.eligiblesCeinture[key].splice(index, 1)[0];
                promotionRole(eleve);
            },
            echouerRole: function(index, key, id) {
                app.eligiblesCeinture[key].splice(index, 1);
            },
            reussirAncien: function(id, index) {
                app.eligiblesRole.Ancien.splice(index, 1);
                promotionRole({id: id});
            },
            reussirSensei: function(id, index) {
                app.eligiblesRole.Sensei.splice(index, 1);
                promotionRole({id: id});
            },
            echouerSensei: function(id, index) {
                app.eligiblesRole.Demotion.splice(index, 1);
                demotionRole(id);
            },
        }
    });

    function enregistrerExam(eleve, reussi) {
        $.ajax({
            url: "/api/examens",
            'type': 'POST',
            data: JSON.stringify({
                professeur: app.user.courriel,
                eleve: eleve.courriel,
                reussi: reussi,
                temps: new Date().getTime(),
            }),
            complete: initialiserListes,
            'contentType': 'application/json; charset=utf-8',
            'dataType': 'json',
        });
    }

    function demotionRole(id) {
        $.ajax({
            url: "/api/compte/demotion/" + id,
            'type': 'POST',
            complete: initialiserListes,
        });
    }

    function promotionCeinture(data) {
        $.ajax({
            url: "/api/compte/ceinture/" + data.courriel,
            'type': 'POST',
            complete: initialiserListes,
        });
    }

    function promotionRole(data) {
        $.ajax({
            url: "/api/compte/role/" +  data.courriel,
            'type': 'POST',
            complete: initialiserListes,
        });
    }

    $.ajax("/api/monCompte", {
        success: (data) => app.user = data,
    });
    initialiserListes();

    function initialiserListes() {
        console.log('initaliserListes');

        $.ajax("/api/examens/eligibles/groupe", {
            success: (data) => {
                app.eligiblesCeinture = data;
            }
        });

        $.ajax("/api/examens/eligibles/role", {
            success: (data) => {
                app.eligiblesRole = data;
            }
        });

        app.$forceUpdate();
    }
});