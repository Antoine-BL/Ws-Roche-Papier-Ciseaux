
class WebSocketClient {
    constructor(readFrom, displayTo){
        this.readFrom = readFrom;
        this.displayTo = displayTo;
        this.stompClient = null;
    }

    static serializeMessage(message){
        return JSON.stringify({
            texte: message,
            type: 'COURRIER',
        });
    }

    connect(ws, callback) {
        const socket = new SockJS(ws);
        this.stompClient = Stomp.over(socket);

        this.stompClient.connect({}, callback);
    }

    disconnect(callback) {
        this.stompClient.disconnect();
        this.stompClient = null;
        callback();
    }

    showMessage(message, displayTo, cssClass) {
        displayTo.append( message + '<br/>');
    }

    sendTo(topic) {
        this.stompClient.send(
            topic,
            {},
            WebSocketClient.serializeMessage(this.readFrom.val())
        );

        this.readFrom.val('');
    }

    subscribeTo(topic, cssClass) {
        this.stompClient.subscribe(topic, this.generateSubscribeHandler(cssClass));
    }

    generateSubscribeHandler(cssClass) {
        const fct = this.showMessage;
        const displayTo = this.displayTo;
        return function (message) {
            fct(JSON.parse(message.body), displayTo, cssClass);
        }
    }
}