$(document).ready(() => {
    let heartbeatIntervalId;
    const subscribeTopics = Object.freeze({
        CHAT: '/topic/battle/chat',
        COMMAND: '/topic/battle/command',
    });
    const sendTopics = Object.freeze({
        CHAT: '/app/battle/chat',
        COMMAND: '/app/battle/command',
        HEARTBEAT: '/app/battle/heartbeat',
    });

    app = new Vue({
        el: '#app',
        data: {
            user: null
        }
    });

    $.ajax("/api/monCompte", {
        success: initWebSocket
    });

    function initWebSocket(data){
        const account = data;
        app.user = data;

        const websocket = new WebSocketClient($('#tbMessage'), $('#messagerie'), account);
        $("#frmMessage").on('submit', function (e) {
            e.preventDefault();
        });

        $('#btnEnvoyer').click(() => send());

        websocket.connect('/webSocket', subscribe);

        function subscribe() {
            setConnection(true);
            websocket.subscribeTo(subscribeTopics.CHAT, '');
            websocket.subscribeTo(subscribeTopics.COMMAND, '');
        }

        function heartbeat() {
            websocket.sendTo(sendTopics.HEARTBEAT, {
                de: account,
                heartbeat: true,
            });
        }

        function send() {
            if (websocket.isCommand) {
                const commandName = websocket.readCommand().name.toUpperCase()
                if (commandName === "JOINDRE")
                    heartbeatIntervalId = window.setInterval(heartbeat, 500);
                else if (commandName === "QUITTER")
                    window.clearInterval(heartbeatIntervalId);
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