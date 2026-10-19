/**
 * 后端接口封装
 * 统一处理：Base URL、JSON 序列化、Authorization、业务错误
 */
(function () {
    'use strict';

    var TOKEN_KEY = 'love_token';
    var base = (window.LOVE_CONFIG.apiBase || '').replace(/\/+$/, '');

    function request(path, options) {
        var opts = options || {};
        var auth = opts.auth !== false;
        var headers = Object.assign(
            { 'Content-Type': 'application/json' },
            opts.headers || {}
        );

        if (auth) {
            var token = localStorage.getItem(TOKEN_KEY);
            if (token) {
                headers['Authorization'] = 'Bearer ' + token;
            }
        }

        var init = {
            method: opts.method || 'GET',
            headers: headers
        };
        if (opts.body !== undefined) {
            init.body = JSON.stringify(opts.body);
        }

        return fetch(base + path, init).then(function (res) {
            return res.json().catch(function () {
                return null;
            }).then(function (body) {
                if (!res.ok || !body || body.code !== 0) {
                    var message = body && body.message ? body.message : '请求失败（' + res.status + '）';
                    var error = new Error(message);
                    error.code = body && typeof body.code === 'number' ? body.code : res.status;
                    error.status = res.status;
                    throw error;
                }
                return body.data;
            });
        }, function () {
            var error = new Error('无法连接服务器，请确认后端已启动');
            error.code = -1;
            throw error;
        });
    }

    window.LoveApi = {
        login: function (username, password, remember) {
            return request('/api/auth/login', {
                method: 'POST',
                auth: false,
                body: { username: username, password: password, remember: !!remember }
            });
        },

        register: function (username, password, nickname) {
            return request('/api/auth/register', {
                method: 'POST',
                auth: false,
                body: { username: username, password: password, nickname: nickname }
            });
        },

        me: function () {
            return request('/api/auth/me');
        },

        logout: function () {
            return request('/api/auth/logout', { method: 'POST' });
        },

        checkUsername: function (username) {
            return request('/api/auth/check-username?username=' + encodeURIComponent(username), {
                auth: false
            });
        }
    };
})();
