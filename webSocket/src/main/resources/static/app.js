var app
$(document).ready(() => {
    let wsu;
    let envoyerPub = true;
    let user = null;
    let pretAConnecter = false;

    app = new Vue({
        el: '#app',
        data: {
            user: null,
            messages: [],
            connecte: false,
            options: ["Public"],
            selected: "Public",
        },
        computed: {
            canSend: function() {
                if (!this.user || !this.connecte) return false;

                if (this.selected === "Public") {
                    return this.user.role.role === "ANCIEN" || this.user.role.role === "SENSEI" || this.user.role.role === "VENERABLE";
                } else if (this.selected === "Privé") {
                    return this.user;
                }
            }
        },
        methods: {
            send: function() {
                if (envoyerPub) {
                    wsu.sendTo(inputTopics.PUBLIC);
                } else {
                    wsu.sendTo(inputTopics.PRIVATE);
                }
                wsu.clear();
            },
            connecter: function() {
                if (pretAConnecter) {
                    wsu.connect('/webSocket', subscribe);
                }
                if (user) {
                    this.options.push("Privé");
                }
            }
        }
    });

    $.ajax("/api/monCompte", {
        success: (data) => {
            user = data;
            wsu = new WebSocketClient($('#tbMessage'), app.messages, user, app);
            app.user = data;
            pretAConnecter = true;
        }
    });

    const inputTopics = Object.freeze({
        PUBLIC: '/app/public/chat',
        PRIVATE: '/app/private/chat',
    });

    const outputTopics = Object.freeze({
        PUBLIC: '/topic/public/chat',
        PRIVATE: '/topic/private/chat',
    });

    $("#frmEnvoyer").on('submit', function (e) {
        e.preventDefault();
    });

    function subscribe() {
        app.connecte = true;
        wsu.subscribeTo(outputTopics.PUBLIC, '', 'public');
        if (app.user) {
            wsu.subscribeTo(outputTopics.PRIVATE, '', 'privé');
        }
    }

    $("#menuDD a").click(function(){
        const text = $(this).text();
        const ddl =  $("#ddBtn");
        ddl.text(text);
        ddl.val(text);
        envoyerPub = text === 'Public';
    });
});