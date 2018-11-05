class Commande{
    constructor (type, params) {
        this.typeCommande = type;
        this.parametres = params;
    }
}
const debugDisabled = true;
let app;
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
    const Commands = Object.freeze({
        ROLE: {
            handle: (donnees) => changerRole(donnees.de, donnees.parametres[0], donnees.parametres[1]),
        },
        JOINDRE: {
            handle: (donnees) => app.initLobby(donnees.parametres[0]),
        },
        COMBATTRE: {
            handle: (donnees) => changerPosition(donnees.de, donnees.parametres[0]),
        },
        SIGNALER: {
            handle: (donnees) => signal(donnees.parametres[0], donnees.parametres[1]),
        },
        MATCH_STATE: {
            handle: (donnees) => app.state = donnees.parametres[0],
        },
        SALUER:{
            handle: (donnees) => joueurSalue(donnees.de),
        },
        POSITION: {
            handle: f => f,
        },
        ATTAQUER: {
            handle: f => f,
        },
        QUITTER: {
            handle: (donnees) => {
                app.removeFrom(donnees.parametres[0]);
                if (donnees.de.courriel === app.user.courriel) {
                    app.dansLobby = false;
                }
            },
        },
    });
    const Roles = Object.freeze({
        SPECTATEUR: {
            val: 'SPECTATEUR',
            remove: (lobbyPos) => app.spectateurs.splice(lobbyPos.position, 1, null),
            set: (utilisateur, lobbyPos) => Vue.set(app.spectateurs, lobbyPos.position, utilisateur),
        },
        COMBATTANT: {
            val: 'COMBATTANT',
            remove: (lobbyPos) => app.combattants.splice(lobbyPos.position, 1, null),
            set: (utilisateur, lobbyPos) => Vue.set(app.combattants, lobbyPos.position, utilisateur),
        },
        ROUGE: {
            val: 'ROUGE',
            remove: () => app.rouge = null,
            set: (utilisateur) => app.rouge = utilisateur,
        },
        ARBITRE: {
            val: 'ARBITRE',
            remove: () => app.arbitre = null,
            set: (utilisateur) => app.arbitre = utilisateur,
        },
        BLANC:  {
            val: 'BLANC',
            remove: () => app.blanc = null,
            set: (utilisateur) => app.blanc = utilisateur,
        },
    });
    let websocket;

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
            state: 'OVER',
            dansLobby: false,
        },
        methods: {
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
                app.state = lobby.match ? lobby.match.state : 'OVER';
                app.dansLobby = true;
            },
            removeFrom: function(lobbyPos) {
                Roles[lobbyPos.role].remove(lobbyPos);
            },
            setUser: function (utilisateur, lobbyPos) {
                Roles[lobbyPos.role].set(utilisateur, lobbyPos)

                if (this.user.courriel === utilisateur.courriel) {
                    console.log('setUser');
                    this.user.roleCombat = lobbyPos.role;
                }
            },
            debuterMatch: function() {
                websocket.sendCommandTo(sendTopics.COMMAND, new Commande('COMBATTRE', []))
            },
            attaque: function(attaque) {
                websocket.sendCommandTo(sendTopics.COMMAND, new Commande('ATTAQUER', [attaque]))
            },
            position: function(position) {
                websocket.sendCommandTo(sendTopics.COMMAND, new Commande('POSITION', [position]))
            },
            saluer: function () {
                websocket.sendCommandTo(sendTopics.COMMAND, new Commande('SALUER', []))
            },
            signaler : function (signal, cible) {
                params = [signal];
                if (cible) {
                    params.push(cible);
                }

                websocket.sendCommandTo(sendTopics.COMMAND, new Commande('SIGNALER', params))
            },
            role: function(e) {
                websocket.sendCommandTo(sendTopics.COMMAND, new Commande('ROLE', [e.role, e.index]))
            },
        }
    });

    function changerRole(utilisateur, newPos, oldPos) {
        if (oldPos) {
            app.removeFrom(oldPos);
        }
        app.setUser(utilisateur, newPos);
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
            console.log(donnees);
            Commands[donnees.typeCommande].handle(donnees);
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
                    app.dansLobby = false;
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