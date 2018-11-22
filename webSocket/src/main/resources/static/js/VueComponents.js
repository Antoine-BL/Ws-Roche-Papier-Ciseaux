Vue.component('app-nav', {
    props: ['user', 'currentPage'],
    template: '<b-navbar type="dark" variant="dark" class="align-items-center justify-content-between">\n' +
        '    <b-navbar-brand href="/" class="align-items-center">' +
                '<img class="profile-petit" src="/images/websocket_logo_white.png"/> ' +
                'Projet Websocket' +
            '</b-navbar-brand>\n' +
        '    <b-navbar-toggle target="navbarSupportedContent"></b-navbar-toggle>\n' +
        '    <b-collapse is-nav id="navbarSupportedContent">\n' +
        '        <b-navbar-nav class="ml-auto align-items-center">\n' +
        '            <app-nav-item v-if="currentPage != \'dojo\'" text="Dojo" destination="/"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'ecole\'" text="Notre École" destination="/ecole"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'kumite\' && user" text="Kumite" destination="/kumite"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'passage\' && user && (user.role.role == \'Venerable\' || user.role.role == \'Sensei\')" text="Passage de Grades" destination="/passage"></app-nav-item>' +
        '            <app-nav-item v-if="currentPage != \'passage\' && user && (user.role.role == \'Venerable\' || user.role.role == \'Sensei\')" text="Gestion des Membres" destination="/passage"></app-nav-item>' +
        '            <a v-if="user == null" class="nav-item active btn btn-success" id="connexion" href="/connexion" role="button">Se connecter</a>' +
        '            <app-profile v-if="user" v-bind:user="user"></app-profile>' +
        '        </b-navbar-nav>\n' +
        '    </b-collapse>\n' +
        '</b-navbar>'
});

Vue.component('app-nav-item', {
    props: ['text', 'destination'],
    template: '<b-nav-item v-bind:href="destination" class="nav-link active">{{text}}</b-nav-item>'
});


Vue.component('app-profile', {
    props: ['user'],
    template:
'                <b-dropdown class="bg-dark text-light" menu-class="bg-dark, text-light" variant="dark" type="dark">\n' +
'                    <img slot="button-content" v-bind:src="\'/api/avatars/\' + user.avatarId" class="profile profile-petit" width="96" height="96"/>\n' +
'                    <b-dropdown-header>Connecté en tant que: {{user.alias}}</b-dropdown-header>\n' +
'                    <div class="dropdown-item-text" >Rôle: {{user.role.role}}</div>\n' +
'                    <div class="dropdown-item-text" >Groupe: {{user.groupe.groupe}}</div>\n' +
'                    <div class="dropdown-item-text" >Points: {{user.points}}</div>\n' +
'                    <div class="dropdown-item-text" >Crédits: {{user.credits}}</div>\n' +
'                    <div class="dropdown-divider"></div>\n' +
'                    <b-dropdown-item href="/monCompte" variant="danger">Gérer mon compte</b-dropdown-item>\n' +
'                    <b-dropdown-item href="/deconnexion" variant="danger">Déconnexion</b-dropdown-item>\n' +
'                </b-dropdown>\n'
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
       '<div class="chat-message-header d-flex flex-grow-1"><strong class="flex-grow-1">{{message.de.alias}} {{message.de.role.role}} {{message.de.groupe.groupe}} <span v-if="message.mention">[{{message.mention}}]</span></strong><small><img class="icon m-2 d-inline-block" src="/open-iconic/svg/clock.svg">{{timeMessage}}</small></div>'+
       '<div class="chat-message-text">{{message.texte}}</div>' +
       '</div>' +
       '</div>'
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
    data: function () { return {
        classSalue: '',
        classApproche: '',
        attack: null,
        drapeau: null,
        classDrapeau: '',
        flagRight: false,
        afficherAttaque: false,
    }},
    template:
        '<div v-on:click="click" ' +
        'v-bind:class="[\'col-2\',\'col-xl-1\', disabled ? \'slot-disabled\' : \'slot\',\'transitions\', classSalue, classApproche]">' +
            '<img v-if="drapeau" v-bind:class="classDrapeau"/>' +
           '<img v-bind:src="attack ? \'/images/\' + attack + \'.png\' : (user == null ? \'/images/anonyme.jpg\' : \'/api/avatars/\' + user.avatarId)"/>' +
            '<img v-if="drapeau" v-bind:class="classDrapeau"/>' +
            '<span v-if="user && (user.roleCombat === \'ROUGE\' || user.roleCombat === \'BLANC\' || user.roleCombat === \'ARBITRE\')">' +
            '{{user.groupe.groupe}}' +
            '</span>'+
        '</div>',
    methods: {
        click: function (e) {
            if (!this.user && !this.disabled) {
                this.$emit('move-to', this);
            }
        },
        saluer: function() {
            let bowClass;
            let unbowClass;

            if (this.role === "blanc") {
                bowClass = 'bow-r';
                unbowClass = 'unbow-r';
            } else if (this.role === "rouge") {
                bowClass = 'bow-l';
                unbowClass = 'unbow-l';
            }

            this.classSalue = bowClass;
            window.setTimeout(() => this.classSalue = unbowClass, 1000)
        },
        ippon: function(gagnant) {
            let flagUpClass;
            let flagDownClass;

            if (gagnant === "blanc") {
                flagUpClass = 'flag-up-l';
                flagDownClass = 'flag-down-l';
                this.flagRight = false;
            } else if (gagnant === "rouge") {
                flagUpClass = 'flag-up-r';
                flagDownClass = 'flag-down-r';
                this.flagRight = true;
            }

            this.classDrapeau = flagDownClass;
            window.setTimeout(() => {
                this.classDrapeau = flagUpClass;
            }, 1000);
        }
    }
});

Vue.component('app-membre-ecole', {
    props: ['utilisateur'],
    template: '<div class="m-1">' +
        '<img class="profile profile-petit d-inline-block" v-bind:src="\'/api/avatars/\' + utilisateur.avatarId"/>' +
        '<span class="ml-2 mr-2">{{utilisateur.alias}}, {{utilisateur.role.role}}, {{utilisateur.groupe.groupe}}, {{utilisateur.courriel}}</span>' +
        '</div>'
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
                    '<div v-if="state == \'DECIDE\'" class="btn-group">' +
                        '<button ' +
                            'v-for="att in decisions"' +
                            'v-on:click="choixverd = att.value"' +
                            'v-bind:class="[\'btn\', \'btn-secondary\', choixverd == att.value ? \'active\' : \'\']" >' +
                            '{{att.text}}' +
                        '</button>' +
                        '<button class="btn btn-primary" v-on:click="decision">IPPON!</button>' +
                    '</div>' +
                    '<div v-if="state == \'EXIT\'" class="form-group">' +
                        '<button class="btn btn-primary" v-on:click="$emit(\'rester\')">Rester?</button>' +
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
                        '<button ' +
                        'v-for="att in attaques"' +
                        'v-on:click="choixatt = att.value" ' +
                        'v-bind:class="[\'btn\', \'btn-secondary\', choixatt == att.value ? \'active\' : \'\']" > ' +
                            '{{att.text}}' +
                        '</button>' +
                        '<button class="btn btn-primary" v-on:click="attaque">Attaquer</button>' +
                    '</div>' +
                '</div>' +
            '</div>' +
        '</div>',
    data: function() {
        return {
            choixatt: "PAPIER",
            choixverd: "NUL",
            attaques: [
                { text: 'Roche', value: 'ROCHE' },
                { text: 'Papier', value: 'PAPIER' },
                { text: 'Ciseaux', value: 'CISEAUX' },
            ],
            decisions: [
                { text: 'Blanc', value: 'BLANC' },
                { text: 'Nul', value: 'NUL' },
                { text: 'Rouge', value: 'ROUGE' },
            ],
        }
    },
    methods: {
        debuterMatch: function() {
            this.$emit('debuter-match');
        },
        decision: function() {
            this.$emit('signaler', 'IPPON', this.choixverd);
        },
        attaque: function() {
            this.$emit('attaque', this.choixatt);
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