class WebSocketClient {
    constructor(readFrom, displayTo, user){
        this.readFrom = readFrom;
        this.displayTo = displayTo;
        this.stompClient = null;
        this.justSent = false;
        this.user = user;
        this.subscriptions = new Map();
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
        commande.de = this.user;
        return JSON.stringify(commande);
    }

    disableDebug() {
        this.stompClient.debug = () => {};
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

    showMessage(message, cssClass, mention) {
        if (!message.de) {
            message.de = WebSocketClient.ANONYMOUS_USER;
            message.avatarUrl = '/images/anonyme.jpg';
            message.fromSelf = this.justSent;
        } else {
            message.avatarUrl = '/api/avatars/' + message.de.avatarId;
            message.fromSelf = this.user && message.de.courriel === this.user.courriel;
        }

        message.css = cssClass ? cssClass : '';
        message.mention = mention;

        this.displayTo.push(message);
        this.justSent = false;
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
            typeCommande: params[COMMAND_NAME].toUpperCase(),
            parametres: params.slice(COMMAND_NAME + 1)
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
        this.justSent = true;
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

    subscribeTo(topic, cssClass, mention, callback) {
        const subscription = this.stompClient.subscribe(topic, this.generateSubscribeHandler(cssClass, mention, callback));
        this.subscriptions.set(topic, subscription);
    }

    unsubscribeFrom(topic) {
        if (this.subscriptions.has(topic))
            this.subscriptions.get(topic).unsubscribe();
    }

    generateSubscribeHandler(cssClass, mention, callback) {
        return (message) => {
            message = JSON.parse(message.body);
            if (message.texte && message.texte !== '') {
                this.showMessage(message, cssClass, mention);
            }
            if (callback) {
                callback(message);
            }
        }
    }
}