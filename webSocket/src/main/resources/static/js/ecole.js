var app
$(document).ready(() => {
    app = new Vue({
        el: '#app',
        data: {
            user: null
        }
    });

    $.ajax("/api/monCompte", {
        success: (data) => app.user = data
    });
});