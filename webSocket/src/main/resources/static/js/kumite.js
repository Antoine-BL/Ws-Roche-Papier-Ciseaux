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
            handle: (donnees) => {
                app.initLobby(donnees.parametres[0]);
            },
        },
        COMBATTRE: {
            handle: (donnees) => changerPosition(donnees.de, donnees.parametres[0]),
        },
        SIGNALER: {
            handle: (donnees) => signal(donnees.parametres[0], donnees.parametres[1]),
        },
        MATCH_STATE: {
            handle: (donnees) => {
                app.chrono = donnees.parametres[0];
                if (donnees.parametres.length >= 2) {
                    app.state = donnees.parametres[1];
                    app.message = donnees.parametres.length >= 3 ? app.message : donnees.parametres[2];
                }
                if (app.state !== 'DECIDE' && app.state !== 'START') {
                    app.$refs.blanc.attack = null;
                    app.$refs.rouge.attack = null
                }
            },
        },
        SALUER:{
            handle: (donnees) => {
                joueurSalue(donnees.de);
            }
        },
        POSITION: {
            handle: (donnees) => {
                joueurApproche(donnees.de);
            }
        },
        ATTAQUER: {
            handle: donnees => {
                const params = donnees.parametres;
                const objCible = donnees.de.roleCombat === "ROUGE" ? app.$refs.rouge : app.$refs.blanc;

                if (params.length >= 1) {
                    objCible.attack = params[0].toLowerCase();
                } else {
                    objCible.attack = 'rien';
                }
            },
        },
        QUITTER: {
            handle: (donnees) => {
                app.removeFrom(donnees.parametres[0]);
                if (donnees.de.courriel === app.user.courriel) {
                    app.dansLobby = false;
                    window.clearInterval(heartbeatIntervalId);
                    websocket.unsubscribeFrom(subscribeTopics.LOBBY);
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
                app.dansLobby = true;
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
            }
        }
    });

    function signal(signal, cible) {
        if (signal === "IPPON"){
            app.$refs.arbitre.ippon(cible.roleCombat.toLowerCase());
        }
    }

    function joueurApproche(joueur) {
        if (joueur.roleCombat === 'ROUGE') {
            app.$refs.rouge.classApproche = 'approche-r';
        } else if (joueur.roleCombat === 'BLANC') {
            app.$refs.blanc.classApproche = 'approche-l';
        }
    }

    function joueurSalue(joueur) {
        if (joueur.roleCombat === 'ROUGE') {
            app.$refs.rouge.saluer();
        } else if (joueur.roleCombat === 'BLANC') {
            app.$refs.blanc.saluer();
        }
    }

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
        }

        function handleCommand(command) {
            if (!command.donnees) return;
            const donnees = command.donnees;
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
                    websocket.subscribeTo(subscribeTopics.LOBBY, 'chat-lobby', 'Lobby', handleCommand);
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