// ===== auth.js — Session Cookie Authentication =====
// NO JWT. NO localStorage tokens. The server manages the session (JSESSIONID cookie).
// We only cache the user object in localStorage for instant navbar display.
// The real auth state lives on the server and is verified on every page load.

const AUTH_CACHE_KEY = 'shw-user';

function getCurrentUser() {
    try {
        const raw = localStorage.getItem(AUTH_CACHE_KEY);
        return raw ? JSON.parse(raw) : null;
    } catch {
        return null;
    }
}

function setCurrentUser(user) {
    if (user) localStorage.setItem(AUTH_CACHE_KEY, JSON.stringify(user));
    else localStorage.removeItem(AUTH_CACHE_KEY);
}

function isLoggedIn() {
    return Boolean(getCurrentUser());
}

function isAdmin() {
    const user = getCurrentUser();
    // Backend UserDTO returns role as a string, e.g. "ADMIN" or "USER"
    return Boolean(user && user.role && String(user.role).toUpperCase() === 'ADMIN');
}

function isBlocked() {
    const user = getCurrentUser();
    return Boolean(user && user.enabled === false);
}

/**
 * Calls /api/users/me to verify the browser's session cookie is valid.
 * Returns the user object or null if session expired / not logged in.
 */
async function checkAuth() {
    try {
        const user = await API.getCurrentUser();
        setCurrentUser(user);
        return user;
    } catch (err) {
        setCurrentUser(null);
        return null;
    }
}

/**
 * Calls /api/users/logout, invalidates server session, clears cache.
 */
async function doLogout() {
    try {
        await API.logout();
    } catch (e) {
        console.warn('Logout API failed (session may already be expired):', e);
    }
    setCurrentUser(null);
    window.location.href = '/index.html';
}

/**
 * Patches the navbar after Bolt's renderHeader() has drawn it.
 * Hides/shows Login, Register, Profile, Logout, Create Ad, Admin links.
 */
function updateNavbar() {
    const user = getCurrentUser();
    const nav = document.querySelector('nav, .navbar, .site-nav, #header-root');
    if (!nav) return;

    const loginLink    = nav.querySelector('a[href="/login.html"], .nav-login');
    const registerLink = nav.querySelector('a[href="/register.html"], .nav-register');
    const logoutBtn    = nav.querySelector('.nav-logout, #nav-logout, button[onclick*="logout"]');
    const profileLink  = nav.querySelector('a[href="/profile.html"], .nav-profile, #nav-profile');
    const createLink   = nav.querySelector('a[href="/create-ad.html"], .nav-create');
    const adminLink    = nav.querySelector('a[href="/admin/pending-ads.html"], .nav-admin, a[href^="/admin"]');

    if (user) {
        if (loginLink)    loginLink.style.display    = 'none';
        if (registerLink) registerLink.style.display = 'none';

        if (logoutBtn) {
            logoutBtn.style.display = '';
            logoutBtn.onclick = (e) => { e.preventDefault(); doLogout(); };
        }
        if (profileLink) {
            profileLink.style.display = '';
            profileLink.textContent = user.fullName || user.username;
        }
        if (createLink) createLink.style.display = '';
        if (adminLink)  adminLink.style.display = isAdmin() ? '' : 'none';
    } else {
        if (loginLink)    loginLink.style.display    = '';
        if (registerLink) registerLink.style.display = '';
        if (logoutBtn)    logoutBtn.style.display    = 'none';
        if (profileLink)  profileLink.style.display  = 'none';
        if (createLink)   createLink.style.display   = 'none';
        if (adminLink)    adminLink.style.display    = 'none';
    }
}

function requireAuth(redirectUrl) {
    if (!isLoggedIn()) {
        const current = window.location.pathname.split('/').pop() || 'index.html';
        window.location.href = (redirectUrl || '/login.html') + `?redirect=${encodeURIComponent(current)}`;
        return false;
    }
    return true;
}

function requireAdmin() {
    if (!requireAuth('/login.html')) return false;
    if (!isAdmin()) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

// Backward compatibility: Bolt's index.html calls initAuth()
window.initAuth = checkAuth;

// Auto-run on every page: verify session, then patch navbar
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => checkAuth().then(updateNavbar));
} else {
    checkAuth().then(updateNavbar);
}

// Globals
window.getCurrentUser = getCurrentUser;
window.setCurrentUser = setCurrentUser;
window.isLoggedIn     = isLoggedIn;
window.isAdmin        = isAdmin;
window.isBlocked      = isBlocked;
window.checkAuth      = checkAuth;
window.doLogout       = doLogout;
window.updateNavbar   = updateNavbar;
window.requireAuth    = requireAuth;
window.requireAdmin   = requireAdmin;
window.logout = doLogout;