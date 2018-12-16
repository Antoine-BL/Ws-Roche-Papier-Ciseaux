class Commande{
    constructor (type, params) {
        this.typeCommande = type;
        this.parametres = params;
    }
}
const debugDisabled = true;
let app;
let Commands;
$(document).ready(() => {
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
    Commands = Object.freeze({
        ROLE: {
            handle: (donnees) => app.initLobby(donnees.parametres[0]),
        },
        JOINDRE: {
            handle: (donnees) => app.initLobby(donnees.parametres[0]),
        },
        SIGNALER: {
            handle: (donnees) => app.afficherChoixArbitre(donnees.parametres[0]),
        },
        ATTAQUER: {
            handle: donnees => {
                const params = donnees.parametres;
                app.$refs.rouge.attack = params[0].toLowerCase();
                app.$refs.blanc.attack = params[1].toLowerCase();
            },
        },
        MATCH_STATE: {
            handle: (donnees) => donnees,
        },
        COMBAT: {
            handle: (donnees) => {
                app.$refs.rouge.attack = null;
                app.$refs.blanc.attack = null;
                app.$refs.arbitre.attack = null;

                $.ajax("/api/monCompte", {
                    success: (user) => {
                        app.user = user;
                    }
                });
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
            message: '',
            chrono: 0,
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
            },
            removeFrom: function(lobbyPos) {
                Roles[lobbyPos.role].remove(lobbyPos);
            },
            setUser: function (utilisateur, lobbyPos) {
                Roles[lobbyPos.role].set(utilisateur, lobbyPos)

                if (this.user.courriel === utilisateur.courriel) {
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
            rester: function() {
                websocket.sendCommandTo(sendTopics.COMMAND, new Commande('SIGNALER', ['RESTER']))
            },
            push: function (el) {
                this.messages.push(el);
                const container = $('#messagerie')[0];
                container.scrollTop = container.scrollHeight;
            },
            afficherChoixArbitre : function (choix) {
                app.$refs.arbitre.attack = choix.toLowerCase();
            }
        }
    });

    $.ajax("/api/monCompte", {
        success: initWebSocket
    });

    function initWebSocket(account){
        app.user = account;

        websocket = new WebSocketClient($('#tbMessage'), app, account, app);

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
            websocket.subscribeTo(subscribeTopics.LOBBY, 'chat-lobby', 'Lobby', handleCommand);
            app.dansLobby = true;
        }

        function handleCommand(command) {
            if (!command.donnees) return;
            const donnees = command.donnees;
            try {
                Commands[donnees.typeCommande].handle(donnees);
            } catch (e) {
                console.error("Unknown command: " + donnees.typeCommande);
            }
        }

        function send() {
            if (websocket.isCommand) {
                const commandName = websocket.readCommand().typeCommande.toUpperCase();

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