/**
 * 个人空间（登录后主页）
 */
(function () {
    'use strict';

    var TOKEN_KEY = 'love_token';
    var USER_KEY = 'love_user';

    if (!localStorage.getItem(TOKEN_KEY)) {
        window.location.replace('index.html');
        return;
    }

    var user = null;
    try {
        user = JSON.parse(localStorage.getItem(USER_KEY) || 'null');
    } catch (e) {
        user = null;
    }

    var name = (user && (user.nickname || user.username)) || '朋友';
    var avatar = (user && user.avatar) || '一';

    document.getElementById('userAvatar').textContent = avatar;
    document.getElementById('userName').textContent = name;

    // 问候语
    var hour = new Date().getHours();
    var greeting;
    if (hour < 5) {
        greeting = '夜深了';
    } else if (hour < 11) {
        greeting = '早安';
    } else if (hour < 14) {
        greeting = '午安';
    } else if (hour < 18) {
        greeting = '下午好';
    } else {
        greeting = '晚上好';
    }

    var greetingEl = document.getElementById('greeting');
    greetingEl.textContent = greeting + '，';
    var nameSpan = document.createElement('span');
    nameSpan.textContent = name;
    greetingEl.appendChild(nameSpan);
    greetingEl.appendChild(document.createTextNode('。'));

    // 日期
    var d = new Date();
    var week = ['日', '一', '二', '三', '四', '五', '六'][d.getDay()];
    document.getElementById('today').textContent =
        d.getFullYear() + ' 年 ' + (d.getMonth() + 1) + ' 月 ' + d.getDate() + ' 日 · 星期' + week;

    // Toast
    var toastWrap = document.getElementById('toastWrap');

    function showToast(message) {
        var toast = document.createElement('div');
        toast.className = 'toast';
        toast.textContent = message;
        toastWrap.appendChild(toast);
        window.setTimeout(function () {
            toast.classList.add('out');
            window.setTimeout(function () {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 260);
        }, 2200);
    }

    // 入口占位
    var entries = document.querySelectorAll('.entry');
    for (var i = 0; i < entries.length; i++) {
        entries[i].addEventListener('click', function () {
            showToast(this.getAttribute('data-feature') + '功能正在准备中');
        });
    }

    // 退出登录
    document.getElementById('logoutBtn').addEventListener('click', function () {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        window.location.href = 'index.html';
    });
})();
