$(document).ready(() => {
    const inputTopics = Object.freeze({
        CHAT: '/topic/battle/chat',
    });
    const outputTopics = Object.freeze({
        CHAT: '/app/battle/chat',
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
    }

    function setConnection(connected) {
        $('#msgAttente').toggle(!connected);
        $('#msgAttente').removeClass('d-flex');
        $('#messagerie').toggle(connected);
        $('#btnEnvoyer').prop('disabled', !connected);
        $('#tbMessage').prop('disabled', !connected);
    }

    function send() {
        websocket.sendTo(outputTopics.CHAT);
    }
});