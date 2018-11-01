class Commande{
    constructor (type, params, de) {
        this.typeCommande = type;
        this.parametres = params;
        this.de = de;
    }
}
const debugDisabled = true;

$(document).ready(() => {
    let heartbeatIntervalId;
    const subscribeTopics = Object.freeze({
        CHAT: '/topic/battle/chat',
        COMMAND: '/topic/battle/command',
        LOBBY: '/topic/battle/lobby',
    });
    const sendTopics = Object.freeze({
        CHAT: '/app/battle/chat',
        COMMAND: '/app/battle/command',
        HEARTBEAT: '/app/battle/heartbeat',
    });
    const commandes = Object.freeze({
        ROLE: 'ROLE',
        JOINDRE: 'JOINDRE',
        QUITTER: 'QUITTER',
    });
    const Roles = Object.freeze({
        SPECTATEUR: 'SPECTATEUR',
        COMBATTANT: 'COMBATTANT',
        ROUGE: 'ROUGE',
        ARBITRE: 'ARBITRE',
        BLANC: 'BLANC',
    });

    app = new Vue({
        el: '#app',
        data: {
            user: null,
            messages: [],
            spectateurs: [null, null, null, null, null, null, null, null, null, null, null, null],
            combattants: [null, null, null, null, null, null, null, null, null, null, null, null],
            rouge: null,
            arbitre: null,
            blanc: null,
            match: null,
        },
        methods: {
            handleMove: function(e) {
                moveTo(e);
            },
            initLobby: function(lobby){
                for (let i = 0; i < lobby.spectateurs.length; i++) {
                    app.spectateurs.splice(i, 1, lobby.spectateurs[i]);

                }
                for (let i = 0; i < lobby.combattants.length; i++) {
                    app.combattants.splice(i, 1, lobby.combattants[i]);
                }

                app.rouge = lobby.rouge;
                app.arbitre = lobby.arbitre;
                app.blanc = lobby.blanc;
                app.match = lobby.match;
            },
            removeFrom: function(lobbyPos) {
                if (lobbyPos.role === Roles.SPECTATEUR) {
                    app.spectateurs.splice(lobbyPos.position, 1, null);
                } else if (lobbyPos.role === Roles.COMBATTANT) {
                    app.combattants.splice(lobbyPos.position, 1, null);
                } else if (lobbyPos.role === Roles.ARBITRE) {
                    app.arbitre = null;
                } else if (lobbyPos.role === Roles.ROUGE) {
                    app.rouge = null;
                } else if (lobbyPos.role === Roles.BLANC) {
                    app.blanc = null;
                }
            },
            setUser: function (utilisateur, lobbyPos) {
                if (lobbyPos.role === Roles.SPECTATEUR) {
                    Vue.set(app.spectateurs, lobbyPos.position, utilisateur);
                } else if (lobbyPos.role === Roles.COMBATTANT) {
                    Vue.set(app.combattants, lobbyPos.position, utilisateur);
                } else if (lobbyPos.role === Roles.ARBITRE) {
                    app.arbitre = utilisateur;
                } else if (lobbyPos.role === Roles.ROUGE) {
                    app.rouge = utilisateur;
                } else if (lobbyPos.role === Roles.BLANC) {
                    app.blanc = utilisateur;
                }
            }
        }
    });

    let websocket;

    function moveTo(e) {
        console.log(e);
        websocket.sendCommandTo(sendTopics.COMMAND, new Commande(commandes.ROLE, [e.role, e.index], app.user))
    }

    $.ajax("/api/monCompte", {
        success: initWebSocket
    });

    function initWebSocket(data){
        const account = data;
        app.user = data;

        websocket = new WebSocketClient($('#tbMessage'), app.messages, account, app);

        $("#frmMessage").on('submit', function (e) {
            e.preventDefault();
        });

        $('#btnEnvoyer').click(() => send());

        websocket.connect('/webSocket', subscribe);

        function subscribe() {
            if (debugDisabled) websocket.disableDebug();
            setConnection(true);
            websocket.subscribeTo(subscribeTopics.CHAT, '');
            websocket.subscribeTo(subscribeTopics.COMMAND, 'chat-robot', 'Commande');
        }

        function handleCommand(command) {
            if (!command.donnees) return;

            const donnees = command.donnees;
            switch(donnees.typeCommande) {
                case commandes.JOINDRE:
                    app.initLobby(donnees.parametres[0]);
                    break;
                case commandes.ROLE:
                    changerRole(donnees.de, donnees.parametres[0], donnees.parametres[1], donnees.parametres[2]);
                    break;
                case commandes.QUITTER:
                    app.removeFrom(donnees[0]);
                    break;
            }
        }

        function changerRole(utilisateur, newPos, oldPos) {
            if (oldPos) {
                app.removeFrom(oldPos);
            }
            app.setUser(utilisateur, newPos);
        }

        function heartbeat() {
            websocket.sendTo(sendTopics.HEARTBEAT, {
                de: account,
                heartbeat: true,
            });
        }

        function send() {
            if (websocket.isCommand) {
                const commandName = websocket.readCommand().typeCommande.toUpperCase();
                if (commandName === "JOINDRE") {
                    heartbeatIntervalId = window.setInterval(heartbeat, 500);
                    websocket.subscribeTo(subscribeTopics.LOBBY, 'chat-lobby', 'Lobby', handleCommand)
                }
                else if (commandName === "QUITTER") {
                    window.clearInterval(heartbeatIntervalId);
                    websocket.unsubscribeFrom(subscribeTopics.LOBBY);
                }
                websocket.sendCommandTo(sendTopics.COMMAND);
            } else {
                websocket.sendTo(sendTopics.CHAT);
            }
            websocket.clear();
        }
    }

    setConnection(false);

    function setConnection(connected) {
        $('#msgAttente').toggle(!connected);
        $('#msgAttente').toggleClass('d-flex', !connected);
        $('#messagerie').toggle(connected);
        $('#btnEnvoyer').prop('disabled', !connected);
        $('#tbMessage').prop('disabled', !connected);
    }
});