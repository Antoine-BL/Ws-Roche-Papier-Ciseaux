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
                eleve = app.eligiblesCeinture[key].splice(index, 1)[0];
                enregistrerExam(eleve, true);
                promotionCeinture(eleve);
            },
            echouer: function(index, key, id) {
                app.eligiblesCeinture[key].splice(index, 1);
                enregistrerExam(id, false);
            },
            reussirRole: function (index, key, id) {
                eleve = app.eligiblesCeinture[key].splice(index, 1)[0];
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
            }
        }
    });

    function enregistrerExam(eleve, reussi) {
        $.ajax({
            url: "/api/examens",
            'type': 'POST',
            data: JSON.stringify({
                professeur: app.user.id,
                eleve: eleve.id,
                reussi: reussi,
                temps: new Date().getTime(),
            }),
            'contentType': 'application/json; charset=utf-8',
            'dataType': 'json',
        });
    }

    function promotionCeinture(data) {
        $.post("/api/compte/ceinture/" + data.id)
    }

    function promotionRole(data) {
        $.post("/api/compte/role/" +  data.id)
    }

    $.ajax("/api/monCompte", {
        success: (data) => app.user = data,
    });

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
});