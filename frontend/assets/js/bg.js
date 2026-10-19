/**
 * 背景粒子层：缓慢上升的爱心与光点，带鼠标视差
 */
(function () {
    'use strict';

    var canvas = document.getElementById('bg');
    if (!canvas) {
        return;
    }

    var ctx = canvas.getContext('2d');
    var W = 0;
    var H = 0;
    var DPR = 1;
    var particles = [];
    var mouse = { x: 0.5, y: 0.5, tx: 0.5, ty: 0.5 };
    var COLORS = [
        'rgba(255, 126, 182, ',
        'rgba(106, 108, 240, ',
        'rgba(94, 214, 192, ',
        'rgba(255, 190, 120, '
    ];

    function resize() {
        DPR = Math.min(window.devicePixelRatio || 1, 2);
        W = window.innerWidth;
        H = window.innerHeight;
        canvas.width = Math.round(W * DPR);
        canvas.height = Math.round(H * DPR);
        canvas.style.width = W + 'px';
        canvas.style.height = H + 'px';
        ctx.setTransform(DPR, 0, 0, DPR, 0, 0);
    }

    function makeParticle(initial) {
        var isHeart = Math.random() < 0.38;
        return {
            x: Math.random() * W,
            y: initial ? Math.random() * H : H + 40,
            size: isHeart ? 6 + Math.random() * 12 : 1.2 + Math.random() * 2.6,
            speed: 8 + Math.random() * 22,
            drift: (Math.random() - 0.5) * 12,
            phase: Math.random() * Math.PI * 2,
            alpha: isHeart ? 0.1 + Math.random() * 0.16 : 0.16 + Math.random() * 0.28,
            color: COLORS[Math.floor(Math.random() * COLORS.length)],
            heart: isHeart,
            rot: (Math.random() - 0.5) * 0.4,
            rotV: (Math.random() - 0.5) * 0.12,
            depth: 0.4 + Math.random() * 0.9
        };
    }

    function init() {
        particles = [];
        var count = Math.min(58, Math.round((W * H) / 26000));
        for (var i = 0; i < count; i++) {
            particles.push(makeParticle(true));
        }
    }

    function heartPath(x, y, s) {
        ctx.beginPath();
        ctx.moveTo(x, y + s * 0.32);
        ctx.bezierCurveTo(x, y, x - s * 0.5, y - s * 0.22, x - s * 0.5, y + s * 0.14);
        ctx.bezierCurveTo(x - s * 0.5, y + s * 0.42, x, y + s * 0.6, x, y + s * 0.78);
        ctx.bezierCurveTo(x, y + s * 0.6, x + s * 0.5, y + s * 0.42, x + s * 0.5, y + s * 0.14);
        ctx.bezierCurveTo(x + s * 0.5, y - s * 0.22, x, y, x, y + s * 0.32);
        ctx.closePath();
    }

    var last = 0;

    function frame(ts) {
        var dt = Math.min(0.05, (ts - last) / 1000 || 0.016);
        last = ts;

        ctx.clearRect(0, 0, W, H);

        mouse.x += (mouse.tx - mouse.x) * 0.05;
        mouse.y += (mouse.ty - mouse.y) * 0.05;
        var offsetX = (mouse.x - 0.5) * 26;
        var offsetY = (mouse.y - 0.5) * 18;

        for (var i = 0; i < particles.length; i++) {
            var p = particles[i];
            p.y -= p.speed * dt;
            p.phase += dt * 0.6;
            p.x += Math.sin(p.phase) * p.drift * dt;
            p.rot += p.rotV * dt;

            if (p.y < -50) {
                particles[i] = makeParticle(false);
                continue;
            }

            var px = p.x + offsetX * p.depth;
            var py = p.y + offsetY * p.depth;

            ctx.save();
            ctx.globalAlpha = p.alpha;
            ctx.fillStyle = p.color + '1)';

            if (p.heart) {
                ctx.translate(px, py);
                ctx.rotate(p.rot);
                heartPath(0, 0, p.size);
                ctx.fill();
            } else {
                ctx.beginPath();
                ctx.arc(px, py, p.size, 0, Math.PI * 2);
                ctx.fill();
            }

            ctx.restore();
        }

        requestAnimationFrame(frame);
    }

    window.addEventListener('resize', function () {
        resize();
        init();
    });

    window.addEventListener('pointermove', function (e) {
        mouse.tx = e.clientX / window.innerWidth;
        mouse.ty = e.clientY / window.innerHeight;
    });

    resize();
    init();
    requestAnimationFrame(frame);
})();
