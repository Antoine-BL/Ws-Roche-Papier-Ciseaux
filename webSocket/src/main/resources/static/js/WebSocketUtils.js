class WebSocketClient {
    constructor(readFrom, displayTo, user){
        this.readFrom = readFrom;
        this.displayTo = displayTo;
        this.stompClient = null;
        this.user = user;
    }

    static get COMMAND_PREFIX() {
        return '/';
    }

    static get PARAM_SEPARATOR() {
        return ' ';
    }

    static serializeMessage(message){
        return JSON.stringify(message);
    }

    serializeCommand(commande){
        return JSON.stringify({
            parametres: commande.params,
            typeCommande: commande.name.toUpperCase(),
            de: this.user,
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

    showMessage(message, cssClass) {
        this.displayTo.append( '<div>' + message.texte + '</div>');
    }

    get isCommand() {
        try {
            return this.read().indexOf(WebSocketClient.COMMAND_PREFIX) === 0;
        } catch (e) {
            return false;
        }
    }

    readCommand() {
        if (!this.isCommand) throw 'not a command!';
        const input = this.read();

        const commandNoPrefix = input.substring(WebSocketClient.COMMAND_PREFIX.length);
        const params = commandNoPrefix.split(WebSocketClient.PARAM_SEPARATOR);

        const COMMAND_NAME = 0;

        return {
            name: params[COMMAND_NAME],
            params: params.slice(COMMAND_NAME + 1)
        };
    }

    sendCommandTo(topic, message) {
        message = message ? message : this.readCommand();

        this.stompClient.send(
            topic,
            {},
            this.serializeCommand(message),
        );
    }

    sendTo(topic, message) {
        message = message ? message : {
            de: this.user,
            texte: this.read()
        };
        this.stompClient.send(
            topic,
            {},
            WebSocketClient.serializeMessage(message)
        );
    }

    clear() {
        this.readFrom.val('')
    }

    read() {
        return this.readFrom.val();
    }

    subscribeTo(topic, cssClass) {
        this.stompClient.subscribe(topic, this.generateSubscribeHandler(cssClass));
    }

    generateSubscribeHandler(cssClass) {
        return (message) => {
            this.showMessage(JSON.parse(message.body), cssClass);
        }
    }
}