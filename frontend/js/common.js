// ===== common.js — shared utilities, header rendering, helpers =====

//const CATEGORIES = ['Furniture', 'Electronics', 'Cameras', 'Music', 'Books', 'Bikes', 'Fashion', 'Watches', 'Decor', 'Audio'];
const CITIES = ['تهران', 'اصفهان', 'شیراز', 'مشهد', 'تبریز', 'کرج', 'اهواز', 'رشت', 'یزد', 'قم', 'زاهدان', 'اراک', 'کرمان', 'ساری', 'گرگان', 'بندرعباس'];

function esc(value = '') {
    return String(value).replace(/[&<>'"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[c]));
}

function money(value) {
    return `$${Number(value).toFixed(0)}`;
}

function initials(name = 'کاربر') {
    return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
}

function getQueryParam(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name) || '';
}

function toast(message) {
    const region = document.getElementById('toast-region') || (() => {
        const div = document.createElement('div');
        div.id = 'toast-region';
        document.body.appendChild(div);
        return div;
    })();
    const el = document.createElement('div');
    el.className = 'toast';
    el.textContent = message;
    region.appendChild(el);
    setTimeout(() => el.remove(), 2600);
}

function getLocalFavorites() {
    return JSON.parse(localStorage.getItem('reverb-favorites') || '[]');
}

function toggleLocalFavorite(id) {
    const favs = getLocalFavorites();
    const idx = favs.indexOf(id);
    if (idx >= 0) favs.splice(idx, 1);
    else favs.push(id);
    localStorage.setItem('reverb-favorites', JSON.stringify(favs));
    return favs.includes(id);
}

function isLocalFavorite(id) {
    return getLocalFavorites().includes(id);
}

// ===== Shared Header =====
function renderHeader(activeNav = '') {
    const user = getCurrentUser();
    const navHTML = `
    <nav class="main-nav">
      <a href="/index.html" class="${activeNav === 'home' ? 'active' : ''}">صفحه اصلی</a>
      <a href="/index.html#browse" class="${activeNav === 'browse' ? 'active' : ''}">مرور آگهی‌ها</a>
      <a href="/create-ad.html" class="${activeNav === 'sell' ? 'active' : ''}">ثبت آگهی</a>
    </nav>
  `;
    const accountHTML = user
        ? `
      <div class="menu-wrap">
        <button class="account-button" id="menu-toggle">
          <span class="avatar">${initials(user.fullName || user.username)}</span>
          <span>☰</span>
        </button>
        <div class="menu hidden" id="account-menu">
          <div class="menu-info">
            <p><strong>${esc(user.fullName || 'کاربر')}</strong></p>
            <p class="muted small">@${esc(user.username || '')}</p>
          </div>
          <button class="menu-item" onclick="window.location.href='/my-ads.html'">آگهی‌های من</button>
          <button class="menu-item" onclick="window.location.href='/favorites.html'">مورد علاقه‌ها</button>
          <button class="menu-item" onclick="window.location.href='/inbox.html'">صندوق پیام‌ها</button>
${isAdmin() ? '<button class="menu-item" onclick="window.location.href=\'/admin/pending-ads.html\'">پنل مدیریت</button>' : ''}          <button class="menu-item" onclick="logout()">خروج از حساب</button>
        </div>
      </div>
    `
        : `
      <a href="/login.html" class="outline-button" style="min-height:40px">ورود</a>
    `;

    return `
    <header class="app-header">
      <div class="header-inner">
        <a class="logo" href="/index.html">
          <span class="logo-mark">✦</span>
          <span>reverb<span class="logo-dot">.</span></span>
        </a>
        ${navHTML}
        <div class="header-actions">
          ${user ? '' : '<a href="/register.html" class="sell-button">ثبت‌نام</a>'}
          ${accountHTML}
        </div>
      </div>
    </header>
  `;
}

function attachHeaderEvents() {
    const toggle = document.getElementById('menu-toggle');
    const menu = document.getElementById('account-menu');
    if (toggle && menu) {
        toggle.addEventListener('click', (e) => {
            e.stopPropagation();
            menu.classList.toggle('hidden');
        });
        document.addEventListener('click', (e) => {
            if (!menu.contains(e.target) && e.target !== toggle) {
                menu.classList.add('hidden');
            }
        });
    }
}

// ===== Shared Footer =====
function renderFooter() {
    return `
    <footer class="site-footer">
      <div class="footer-inner">
        <div class="footer-brand">reverb<span>.</span> <small class="muted">بازارچه‌ای با حافظه</small></div>
        <span>© ۲۰۲۴ بازارچه Reverb</span>
      </div>
    </footer>
  `;
}

// ===== Listing Card =====
function listingCardHTML(item, index = 0) {
    const saved = isLocalFavorite(item.id);
    return `
    <article class="listing-card" style="animation-delay:${Math.min(index * 45, 500)}ms" onclick="window.location.href='/ad-detail.html?id=${item.id}'">
      <div class="image-box">
        <img class="listing-image" src="${esc(item.image || (item.imageUrls && item.imageUrls[0]) || '')}" alt="${esc(item.title)}" loading="lazy">
        <div class="image-overlay"></div>
        <button class="favorite-button ${saved ? 'saved' : ''}" data-fav-id="${item.id}" aria-label="${saved ? 'حذف از علاقه‌مندی' : 'افزودن به علاقه‌مندی'}">
          ${saved ? '♥' : '♡'}
        </button>
        <span class="category-pill">◇ ${esc(item.category)}</span>
      </div>
      <div class="listing-meta">
        <div class="listing-title-row">
          <h3 class="listing-title">${esc(item.title)}</h3>
          <span class="listing-price">${typeof item.price === 'string' ? item.price : money(item.price)}</span>
        </div>
        <p class="listing-location">${esc(item.location || item.city || '')}</p>
      </div>
    </article>
  `;
}

function attachFavoriteButtons() {
    document.querySelectorAll('[data-fav-id]').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.stopPropagation();
            const id = Number(btn.dataset.favId);
            const wasFav = isLocalFavorite(id);
            toggleLocalFavorite(id);
            if (isLoggedIn()) {
                try { await api.toggleFavorite(id, wasFav); } catch {}
            }
            btn.classList.toggle('saved');
            btn.textContent = wasFav ? '♡' : '♥';
            toast(wasFav ? 'از علاقه‌مندی‌ها حذف شد' : 'به علاقه‌مندی‌ها اضافه شد');
        });
    });
}

// ===== Loader =====
function showLoader(container) {
    container.innerHTML = '<div class="loader"><div class="spinner"></div></div>';
}

// ===== Init: fetch current user on every page =====
async function initAuth() {
    return checkAuth();
}

window.esc = esc;
window.money = money;
window.initials = initials;
window.getQueryParam = getQueryParam;
window.toast = toast;
window.getLocalFavorites = getLocalFavorites;
window.toggleLocalFavorite = toggleLocalFavorite;
window.isLocalFavorite = isLocalFavorite;
window.renderHeader = renderHeader;
window.attachHeaderEvents = attachHeaderEvents;
window.renderFooter = renderFooter;
window.listingCardHTML = listingCardHTML;
window.attachFavoriteButtons = attachFavoriteButtons;
window.showLoader = showLoader;
window.initAuth = initAuth;
/////window.CATEGORIES = CATEGORIES;
window.CITIES = CITIES;
