/**
 * 登录页交互
 */
(function () {
    'use strict';

    var config = window.LOVE_CONFIG;
    var api = window.LoveApi;

    var authCard = document.getElementById('authCard');
    var authView = document.getElementById('authView');
    var welcomeView = document.getElementById('welcomeView');
    var segmented = document.getElementById('segmented');
    var segButtons = segmented.querySelectorAll('.seg-btn');
    var loginForm = document.getElementById('loginForm');
    var registerForm = document.getElementById('registerForm');
    var cardTitle = document.getElementById('cardTitle');
    var cardSub = document.getElementById('cardSub');
    var toastWrap = document.getElementById('toastWrap');
    var successOverlay = document.getElementById('successOverlay');

    var currentUser = null;
    var busy = false;

    var TAB_COPY = {
        login: { title: '欢迎回来', sub: '登录后继续你的记录' },
        register: { title: '开始记录', sub: '创建账号，留下属于你的第一页' }
    };

    /* ================= Toast ================= */
    function showToast(message, type) {
        var toast = document.createElement('div');
        toast.className = 'toast ' + (type || 'info');

        var dot = document.createElement('span');
        dot.className = 'dot';

        var text = document.createElement('span');
        text.textContent = message;

        toast.appendChild(dot);
        toast.appendChild(text);
        toastWrap.appendChild(toast);

        window.setTimeout(function () {
            toast.classList.add('out');
            window.setTimeout(function () {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 280);
        }, 2800);
    }

    /* ================= 表单状态 ================= */
    function markInvalid(input) {
        var field = input.closest('.field');
        if (!field) {
            return;
        }
        field.classList.add('invalid');
        input.oninput = function () {
            field.classList.remove('invalid');
            input.oninput = null;
        };
    }

    function clearInvalid() {
        var fields = authCard.querySelectorAll('.field.invalid');
        for (var i = 0; i < fields.length; i++) {
            fields[i].classList.remove('invalid');
        }
    }

    function setBusy(button, value) {
        busy = value;
        button.classList.toggle('loading', value);
        button.disabled = value;
    }

    function shakeCard() {
        authCard.classList.remove('shake');
        // 强制重排以重启动画
        void authCard.offsetWidth;
        authCard.classList.add('shake');
    }

    /* ================= 视图切换 ================= */
    function switchTab(tab) {
        segmented.setAttribute('data-active', tab);
        for (var i = 0; i < segButtons.length; i++) {
            segButtons[i].classList.toggle('active', segButtons[i].getAttribute('data-tab') === tab);
        }
        loginForm.hidden = tab !== 'login';
        registerForm.hidden = tab !== 'register';
        cardTitle.textContent = TAB_COPY[tab].title;
        cardSub.textContent = TAB_COPY[tab].sub;
        clearInvalid();
    }

    function showAuth(tab) {
        welcomeView.hidden = true;
        authView.hidden = false;
        switchTab(tab || 'login');
    }

    function showWelcome(user) {
        currentUser = user;
        authView.hidden = true;
        welcomeView.hidden = false;
        document.getElementById('welcomeAvatar').textContent = user.avatar || '💘';
        document.getElementById('welcomeName').textContent = user.nickname || user.username;
    }

    /* ================= 登录成功后的处理 ================= */
    function onAuthSuccess(data) {
        localStorage.setItem('love_token', data.token);
        localStorage.setItem('love_user', JSON.stringify(data.user));

        // 同步游戏内档案，进入游戏后直接显示当前昵称与头像
        localStorage.setItem('heartcatch.user', JSON.stringify({
            name: data.user.nickname || data.user.username,
            avatar: data.user.avatar || '💘',
            createdAt: data.user.createdAt || Date.now()
        }));
        localStorage.setItem('heartcatch.onboarded', '1');

        currentUser = data.user;
        successOverlay.classList.add('show');
        successOverlay.setAttribute('aria-hidden', 'false');

        window.setTimeout(function () {
            window.location.href = config.appUrl;
        }, 1150);
    }

    /* ================= 事件委托 ================= */
    authCard.addEventListener('click', function (e) {
        var eye = e.target.closest('.eye');
        if (eye) {
            var input = document.getElementById(eye.getAttribute('data-target'));
            if (input) {
                var visible = input.type === 'text';
                input.type = visible ? 'password' : 'text';
                eye.textContent = visible ? '显示' : '隐藏';
                eye.setAttribute('aria-label', visible ? '显示密码' : '隐藏密码');
            }
            return;
        }

        var segment = e.target.closest('.seg-btn');
        if (segment) {
            switchTab(segment.getAttribute('data-tab'));
            return;
        }

        var jumper = e.target.closest('[data-goto]');
        if (jumper) {
            switchTab(jumper.getAttribute('data-goto'));
        }
    });

    /* ================= 登录 ================= */
    loginForm.addEventListener('submit', function (e) {
        e.preventDefault();
        if (busy) {
            return;
        }

        var userInput = document.getElementById('loginUser');
        var passInput = document.getElementById('loginPass');
        var username = userInput.value.trim();
        var password = passInput.value;

        if (!username) {
            markInvalid(userInput);
            showToast('请输入用户名', 'error');
            return;
        }
        if (!password) {
            markInvalid(passInput);
            showToast('请输入密码', 'error');
            return;
        }

        var button = document.getElementById('loginBtn');
        setBusy(button, true);

        api.login(username, password, document.getElementById('remember').checked)
            .then(function (data) {
                setBusy(button, false);
                onAuthSuccess(data);
            })
            .catch(function (err) {
                setBusy(button, false);
                showToast(err.message || '登录失败，请稍后再试', 'error');
                shakeCard();
            });
    });

    /* ================= 注册 ================= */
    registerForm.addEventListener('submit', function (e) {
        e.preventDefault();
        if (busy) {
            return;
        }

        var userInput = document.getElementById('regUser');
        var nickInput = document.getElementById('regNick');
        var passInput = document.getElementById('regPass');
        var pass2Input = document.getElementById('regPass2');

        var username = userInput.value.trim();
        var nickname = nickInput.value.trim();
        var password = passInput.value;
        var confirm = pass2Input.value;

        if (!username) {
            markInvalid(userInput);
            showToast('请输入用户名', 'error');
            return;
        }
        if (!/^[a-zA-Z0-9_]{3,20}$/.test(username)) {
            markInvalid(userInput);
            showToast('用户名为 3-20 位字母、数字或下划线', 'error');
            return;
        }
        if (password.length < 6 || password.length > 32) {
            markInvalid(passInput);
            showToast('密码长度为 6-32 位', 'error');
            return;
        }
        if (password !== confirm) {
            markInvalid(pass2Input);
            showToast('两次输入的密码不一致', 'error');
            return;
        }

        var button = document.getElementById('registerBtn');
        setBusy(button, true);

        api.register(username, password, nickname)
            .then(function (data) {
                setBusy(button, false);
                showToast('账号创建成功', 'success');
                onAuthSuccess(data);
            })
            .catch(function (err) {
                setBusy(button, false);
                showToast(err.message || '注册失败，请稍后再试', 'error');
                shakeCard();
            });
    });

    /* ================= 已登录视图操作 ================= */
    document.getElementById('continueBtn').addEventListener('click', function () {
        window.location.href = config.appUrl;
    });

    document.getElementById('logoutBtn').addEventListener('click', function () {
        var button = this;
        button.disabled = true;
        api.logout()
            .catch(function () {
                // Token 可能已过期，仍然清除本地状态
            })
            .then(function () {
                localStorage.removeItem('love_token');
                localStorage.removeItem('love_user');
                currentUser = null;
                button.disabled = false;
                showAuth('login');
                showToast('已退出登录', 'info');
            });
    });

    /* ================= 启动：检测已有登录态 ================= */
    function bootstrap() {
        if (!localStorage.getItem('love_token')) {
            return;
        }
        api.me()
            .then(function (user) {
                showWelcome(user);
            })
            .catch(function () {
                localStorage.removeItem('love_token');
            });
    }

    switchTab('login');
    bootstrap();
})();
