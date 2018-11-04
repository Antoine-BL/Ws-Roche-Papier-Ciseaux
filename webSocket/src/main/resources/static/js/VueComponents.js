Vue.component('app-nav', {
    props: ['user', 'currentPage'],
    template: '<nav class="navbar navbar-expand-lg navbar-dark bg-dark text-light align-items-center justify-content-between">\n' +
        '    <a href="/" class="navbar-brand d-inline-flex align-items-center"><img class="profile-petit" src="/images/websocket_logo_white.png"/> Projet Websocket</a>\n' +
        '    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">\n' +
        '        <span class="navbar-toggler-icon"></span>\n' +
        '    </button>\n' +
        '    <div class="collapse navbar-collapse" id="navbarSupportedContent">\n' +
        '        <ul class="navbar-nav ml-auto align-items-center">\n' +
        '            <app-nav-item v-if="currentPage != \'dojo\'" text="Dojo" destination="/"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'ecole\'" text="Notre École" destination="/ecole"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'kumite\' && user" text="Kumite" destination="/kumite"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'passage\' && user && (user.role == \'Venerable\' || user.role == \'Sensei\')" text="Passage de Grades" destination="/passage"></app-nav-item>' +
        '            <a v-if="user == null" class="nav-item active btn btn-success mr-auto" id="connexion" href="/connexion" role="button">Se connecter</a>' +
        '            <app-profile v-if="user" v-bind:user="user"></app-profile>' +
        '        </ul>\n' +
        '    </div>\n' +
        '</nav>'
});

Vue.component('app-nav-item', {
    props: ['text', 'destination'],
    template: '<li class="nav-link"><a v-bind:href="destination" class="nav-link active">{{text}}</a></li>'
});

Vue.component('app-chat-message', {
   props: ['message'],
    data: function() { return {
           now: new Date()
        }
    },
    computed: {
        timeMessage: function () {
            const delta = this.$data.now.getTime() - this.message.creation;
            const ONE_SECOND = 1000;
            const ONE_MINUTE = 60 * ONE_SECOND;
            const ONE_HOUR = 60 * ONE_MINUTE;
            const ONE_DAY = 24 * ONE_HOUR;

            if (this.intervalId) {
                clearInterval(this.intervalId);
            }
            this.intervalId = setInterval(() => {
                this.now = new Date()
            }, delta*5);

            if (delta < ONE_MINUTE) {
                return 'Il y a ' + Math.round(delta / ONE_SECOND) + ' secondes';
            } else if (delta < ONE_HOUR) {
                return 'Il y a ' + Math.round(delta / ONE_MINUTE) + ' minutes';
            } else if (delta < ONE_DAY) {
                return 'Il y a ' + Math.round(delta / ONE_HOUR) + ' heures';
            } else {
                const date = new Date(this.message.creatiom);
                return date.getFullYear() + '-' + date.getMonth() + '-' + date.getDate();
            }
        }
    },
   template: '<div v-bind:class="[\'chat-message\', message.fromSelf ? \'self-chat\' : \'other-chat\', \'d-flex\', \'border-bottom\', message.css]">' +
       '<img v-bind:src="message.avatarUrl" class="profile profile-petit"/>' +
       '<div class="d-inline-block chat-message-content flex-grow-1 p-1">' +
       '<div class="chat-message-header d-flex flex-grow-1"><strong class="flex-grow-1">{{message.de.alias}} {{message.de.role}} {{message.de.groupe}} <span v-if="message.mention">[{{message.mention}}]</span></strong><small><img class="icon m-2 d-inline-block" src="/open-iconic/svg/clock.svg">{{timeMessage}}</small></div>'+
       '<div class="chat-message-text">{{message.texte}}</div>' +
       '</div>' +
       '</div>'
});

Vue.component('app-profile', {
    props: ['user'],
    template: '<li class="nav-item dropdown">\n' +
        '                <a class="text-light dropdown-toggle" role="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">\n' +
        '                    <img v-bind:src="\'/api/avatars/\' + user.avatarId" class="profile profile-petit" width="96" height="96"/>\n' +
        '                </a>\n' +
        '                <div class="dropdown-menu dropdown-menu-right bg-dark text-light" aria-labelledby="dropdownMenuButton">\n' +
        '                    <h5 class="dropdown-header font-weight-bold  text-light">Connecté en tant que: {{user.alias}}</h5>\n' +
        '                    <div class="dropdown-item-text text-light" >Rôle: {{user.role}}</div>\n' +
        '                    <div class="dropdown-item-text  text-light" >Groupe: {{user.groupe}}</div>\n' +
        '                    <div class="dropdown-divider text-light"></div>\n' +
        '                    <form action="/deconnexion" method="GET">\n' +
        '                        <button class="dropdown-item text-light deconnexion" type="submit">Déconnexion</button>\n' +
        '                    </form>\n' +
        '                </div>\n' +
        '            </li>'
});

Vue.component('app-rangee', {
    props: ['initialUsers', 'role', 'disabled'],
    template: '<div class="row justify-content-center">' +
        '<app-slot v-on:move-to="moveTo"' +
        ' v-for="(u, i) in users"' +
        ' v-bind:user="u"' +
        ' v-bind:index="i"' +
        ' v-bind:role="role"' +
        ' v-bind:disabled="disabled">' +
        '</app-slot>' +
        '</div>',
    data : function () { return {
            users: this.initialUsers,
        };
    },
    methods: {
        moveTo: function (e) {
            if (!this.user) {
                this.$emit('move-to-child', e);
            }
        }
    }
});

Vue.component('app-slot', {
    props: ['user', 'index', 'role', 'disabled'],
    template: '<div v-on:click="click" v-bind:class="[\'col-2\',\'col-xl-1\', disabled ? \'slot-disabled\' : \'slot\']"><img v-bind:src="user == null ? \'/images/anonyme.jpg\' : \'/api/avatars/\' + user.avatarId"></div>',
    methods: {
        click: function (e) {
            if (!this.user && !this.disabled) {
                this.$emit('move-to', this);
            }
        }
    }
});

Vue.component('app-controls', {
    props: ['role', 'state'],
    template:
        '<div class="w-100 align-items-center row flex-column border p-1">' +
            '<h4>Controles pour {{role}}</h4>' +
            '<div>' +
                '<div v-if="role == \'ARBITRE\' && state == \'OVER\'" class="inline-form"></div>' +
                    '<button v-if="state == \'OVER\'" v-on:click="debuterMatch " class="btn btn-primary">Débuter match</button>' +
                '</div>' +
                '<div v-if="role == \'ARBITRE\' && state != \'OVER\'" class="inline-form">' +
                    '<div v-if="state == \'WAITING\'" class="btn-group">' +
                        '<button v-on:click="signaler(\'REI\')" class="btn btn-primary">REI!</button>' +
                    '</div>' +
                    '<div v-if="state == \'READY\'" class="btn-group">' +
                        '<button v-on:click="signaler(\'HAJIME\')" class="btn btn-primary">HAJIME!</button>' +
                    '</div>' +
                    '<div v-if="state == \'DECIDE\'" class="form-group">' +
                        '<b-form-radio-group ' +
                            'buttons ' +
                            'v-model="decision" ' +
                            'buttons-variant="primary" ' +
                            ':options="attaques"> ' +
                        '</b-form-radio-group>' +
                        '<button class="btn btn-primary">IPPON!</button>' +
                    '</div>' +
                    '<div v-if="state == \'EXIT\'" class="form-group">' +
                        '<button class="btn btn-primary">Rester?</button>' +
                    '</div>' +
                '</div>' +
                '<div v-if="role == \'ROUGE\' || role == \'BLANC\'" class="inline-form">' +
                    '<h4 v-if="state == \'DECIDE\'">Attente de la décision de l\'arbitre...</h4>' +
                    '<h4 v-if="state == \'EXIT\'">Vous pouvez maintenant saluer votre adversaire et regagner l\'aire d\'attente</h4>' +
                    '<div v-if="state == \'READY\' || state == \'EXIT\'" class="btn-group">' +
                        '<button class="btn btn-primary" v-on:click="saluer">Saluer</button>' +
                        '<button v-if="state == \'READY\'" v-on:click="position(\'TATAMI\')" class="btn btn-primary">Entrer sur le tatami</button>' +
                    '</div>' +
                    '<div v-if="state == \'START\'" class="btn-group">' +
                        '<b-form-radio-group ' +
                                'buttons ' +
                                'v-model="attaque" ' +
                                'buttons-variant="primary" ' +
                                ':options="attaques">' +
                        '</b-form-radio-group>' +
                        '<button class="btn btn-primary" v-on:click="attaque">Attaquer</button>' +
                    '</div>' +
                '</div>' +
            '</div>' +
        '</div>',
    data: function() {
        return {
            attaque: "PAPIER",
            decision: "NUL",
            attaques: [
                { text: 'Roche', value: 'ROCHE' },
                { text: 'Papier', value: 'PAPIER' },
                { text: 'Ciseaux', value: 'CISEAUX' },
            ],
            decisions: [
                { text: 'Rouge', value: 'ROUGE' },
                { text: 'Nul', value: 'NUL' },
                { text: 'Blanc', value: 'BLANC' },
            ],
        }
    },
    computed: {
        btnStates () {
            return this.buttons.map(btn => btn.state)
        }
    },
    methods: {
        debuterMatch: function() {
            this.$emit('debuter-match');
        },
        decision: function() {
            this.$emit('signal', 'IPPON', this.decision);
        },
        attaque: function() {
            this.$emit('attaque', this.attaque);
        },
        position: function(position) {
            this.$emit('position', position);
        },
        saluer: function () {
            this.$emit('saluer');
        },
        signaler : function (signal) {
            this.$emit('signaler', signal);
        },
    }
});