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
        return JSON.stringify({
            texte: message
        });
    }

    serializeCommand(commande){
        console.log(commande);
        console.log(this.user);
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

    showMessage(message, displayTo, cssClass) {
        displayTo.append( message.texte + '<br/>');
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

        this.clear();
    }

    sendTo(topic, message) {
        message = message ? message : this.read();

        this.stompClient.send(
            topic,
            {},
            WebSocketClient.serializeMessage(message)
        );

        this.clear();
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
        const fct = this.showMessage;
        const displayTo = this.displayTo;
        return function (message) {
            fct(JSON.parse(message.body), displayTo, cssClass);
        }
    }
}