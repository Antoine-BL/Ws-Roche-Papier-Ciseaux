$(document).ready(() => {
    const inputTopics = Object.freeze({
        CHAT: '/topic/battle/chat',
        COMMAND: '/topic/battle/command'
    });
    const outputTopics = Object.freeze({
        CHAT: '/app/battle/chat',
        COMMAND: '/app/battle/command',
    });
    const websocket = new WebSocketClient($('#tbMessage'), $('#messagerie'));
    $("#frmMessage").on('submit', function (e) {
        e.preventDefault();
    });

    setConnection(false);

    $('#btnEnvoyer').click(send);

    websocket.connect('/webSocket', subscribe);

    function subscribe() {
        setConnection(true);
        websocket.subscribeTo(inputTopics.CHAT, '');
        websocket.subscribeTo(inputTopics.COMMAND, '');
    }

    function setConnection(connected) {
        $('#msgAttente').toggle(!connected);
        $('#msgAttente').toggleClass('d-flex', !connected);
        $('#messagerie').toggle(connected);
        $('#btnEnvoyer').prop('disabled', !connected);
        $('#tbMessage').prop('disabled', !connected);
    }

    function send() {
        if (websocket.isCommand) {
            websocket.sendCommandTo(outputTopics.COMMAND);
        } else {
            websocket.sendTo(outputTopics.CHAT);
        }
    }
});