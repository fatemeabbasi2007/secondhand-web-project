// ===== api.js — Real Spring Boot Backend (Session Cookie Auth) =====
//
// CRITICAL: This file assumes the frontend is served by Spring Boot
// from the same origin (localhost:8080). Therefore we use:
//   - credentials: 'include'  → sends the JSESSIONID cookie with every request
//   - NO Authorization header → the backend uses HttpSession, not JWT
//   - NO localStorage token   → session is managed by the server + browser cookies
//
// If you open HTML files directly as file://, this will NOT work.
// You must access the site through http://localhost:8080

const API_BASE = '';

// ------------------------------------------------------------------
// Unified request helper
// ------------------------------------------------------------------
async function apiRequest(path, options = {}) {
    const url = `${API_BASE}${path}`;

    const defaultHeaders = {
        'Content-Type': 'application/json',
    };

    const response = await fetch(url, {
        ...options,
        headers: { ...defaultHeaders, ...(options.headers || {}) },
        credentials: 'include', // ← REQUIRED for session cookie auth
    });

    // Parse JSON if possible, otherwise fall back to text
    let data = null;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        try { data = await response.json(); } catch (e) { data = null; }
    } else {
        try { data = await response.text(); } catch (e) { data = null; }
    }

    //if (!response.ok) {
        // Backend returns ErrorResponse { message: "..." } on failures
    //    const message = (data && data.message) || data || `خطای سرور (${response.status})`;
    //    throw new Error(message);
    //}
    if (!response.ok) {
        // Safely extract string messages from Spring Boot response objects
        let message = `خطای سرور (${response.status})`;

        if (typeof data === 'string' && data.trim()) {
            message = data;
        } else if (data && typeof data === 'object') {
            message = data.message || data.error || JSON.stringify(data);
        }

        throw new Error(message);
    }

    return data;
}

// ------------------------------------------------------------------
// Hardcoded categories (backend has no CategoryController yet)
// ------------------------------------------------------------------
function getCategories() {
    return apiRequest('/api/categories');
}

function getCategoryAttributes(categoryId) {
    return apiRequest(`/api/categories/${encodeURIComponent(categoryId)}/attributes`);
}


// ------------------------------------------------------------------
// AUTH  (/api/users)
// ------------------------------------------------------------------

/**
 * POST /api/users/login
 * Body: { username, password }
 * Returns: User object (backend also sets HttpSession)
 */
function login(username, password) {
    return apiRequest('/api/users/login', {
        method: 'POST',
        body: JSON.stringify({ username, password })
    });
}

/**
 * POST /api/users/register
 * Body: { username, password, email, phoneNum, fullName }
 * Returns: MessageResponse { message: "done successfully" }
 */
function register({ username, password, email, phoneNum, fullName }) {
    return apiRequest('/api/users/register', {
        method: 'POST',
        body: JSON.stringify({ username, password, email, phoneNum, fullName })
    });
}

/**
 * POST /api/users/logout
 * Invalidates the server session.
 */
function logout() {
    return apiRequest('/api/users/logout', { method: 'POST' });
}

/**
 * GET /api/users/me
 * Returns: UserDTO { id, username, email, phoneNum, fullName, role, enabled,
 *                    favoriteAdIds, averageRating, totalRatingsCount }
 */
function getCurrentUser() {
    return apiRequest('/api/users/me');
}

// ------------------------------------------------------------------
// ADVERTISEMENTS  (/api/advertisements)
// ------------------------------------------------------------------

/**
 * GET /api/advertisements/search?keyword=&categoryId=&city=&minPrice=&maxPrice
 * Returns: List<AdSearchDTO>
 */
function searchAds({ keyword, categoryId, city, minPrice, maxPrice } = {}) {
    const params = new URLSearchParams();
    if (keyword)    params.append('keyword', keyword);
    if (categoryId) params.append('categoryId', categoryId);
    if (city)       params.append('city', city);
    if (minPrice != null) params.append('minPrice', minPrice);
    if (maxPrice != null) params.append('maxPrice', maxPrice);

    const qs = params.toString();
    return apiRequest(`/api/advertisements/search${qs ? '?' + qs : ''}`);
}

/**
 * GET /api/advertisements/{id}
 * Returns: AdvertisementDetailDTO
 */
function getAdDetail(id) {
    return apiRequest(`/api/advertisements/${id}`);
}

/**
 * POST /api/advertisements/create
 * Body: Advertisement { title, description, price, city, categoryId,
 *                       imageUrls[], specificAttributes }
 * Returns: MessageResponse
 */
function createAd(advertisement) {
    return apiRequest('/api/advertisements/create', {
        method: 'POST',
        body: JSON.stringify(advertisement)
    });
}

/**
 * PUT /api/advertisements/own/{advertisementId}
 * Body: Advertisement (full updated object)
 */
function updateOwnAd(adId, advertisement) {
    return apiRequest(`/api/advertisements/own/${adId}`, {
        method: 'PUT',
        body: JSON.stringify(advertisement)
    });
}

/**
 * DELETE /api/advertisements/own/{adId}
 */
function deleteOwnAd(adId) {
    return apiRequest(`/api/advertisements/own/${adId}`, {
        method: 'DELETE'
    });
}

/**
 * PATCH /api/advertisements/own/{adId}/sold
 */
function markAdAsSold(adId) {
    return apiRequest(`/api/advertisements/own/${adId}/sold`, {
        method: 'PATCH'
    });
}
const getMyAds = () => apiRequest('/api/advertisements/own');

// ------------------------------------------------------------------
// FAVORITES  (/api/users/{userId}/favorites/...)
// ------------------------------------------------------------------

/**
 * GET /api/users/{userId}/favorites
 * Returns: List<Advertisement>
 */
function getFavorites(userId) {
    return apiRequest(`/api/users/${userId}/favorites`);
}

/**
 * POST /api/users/{userId}/favorites/{adId}
 */
function addFavorite(userId, adId) {
    return apiRequest(`/api/users/${userId}/favorites/${adId}`, {
        method: 'POST'
    });
}

/**
 * DELETE /api/users/{userId}/favorites/{adId}
 */
function removeFavorite(userId, adId) {
    return apiRequest(`/api/users/${userId}/favorites/${adId}`, {
        method: 'DELETE'
    });
}

// ------------------------------------------------------------------
// CHAT  (/api/chats)
// ------------------------------------------------------------------

/**
 * GET /api/chats/conversations
 * Returns: List<ConversationPreviewDTO>
 */
function getConversations() {
    return apiRequest('/api/chats/conversations');
}

/**
 * GET /api/chats/conversations/{conversationId}/messages
 * Returns: List<Message>
 */
function getChatMessages(conversationId) {
    return apiRequest(`/api/chats/conversations/${conversationId}/messages`);
}

/**
 * POST /api/chats/send?advertisementId={id}
 * Body: Message { text: "..." }  (backend expects Message object; we send minimal fields)
 */
function sendMessage(advertisementId, message) {
    return apiRequest(`/api/chats/send?advertisementId=${encodeURIComponent(advertisementId)}`, {
        method: 'POST',
        body: JSON.stringify(message)
    });
}

// ------------------------------------------------------------------
// REVIEWS / RATINGS  (/api/ratings)
// ------------------------------------------------------------------

/**
 * GET /api/ratings/user/{sellerId}/reviews
 * Returns: List<Review>
 */
function getUserReviews(sellerId) {
    return apiRequest(`/api/ratings/user/${sellerId}/reviews`);
}

/**
 * POST /api/ratings/submit/{advertisementId}
 * Body: ReviewDTO { score, comment }
 */
function submitReview(advertisementId, { score, comment }) {
    return apiRequest(`/api/ratings/submit/${advertisementId}`, {
        method: 'POST',
        body: JSON.stringify({ score, comment })
    });
}

// ------------------------------------------------------------------
// ADMIN  (/api/advertisements/admin/...  +  /api/users/admin/...)
// ------------------------------------------------------------------

/**
 * GET /api/advertisements/admin/pending
 * Returns: List<AdminPendingAdDTO>
 */
function getPendingAds() {
    return apiRequest('/api/advertisements/admin/pending');
}

/**
 * POST /api/advertisements/admin/{id}/approve
 */
function approveAd(adId) {
    return apiRequest(`/api/advertisements/admin/${adId}/approve`, {
        method: 'POST'
    });
}

/**
 * POST /api/advertisements/admin/{id}/reject?reason=...
 */
function rejectAd(adId, reason) {
    return apiRequest(
        `/api/advertisements/admin/${adId}/reject?reason=${encodeURIComponent(reason)}`,
        { method: 'POST' }
    );
}

/**
 * DELETE /api/advertisements/admin/{id}
 */
function deleteAdByAdmin(adId) {
    return apiRequest(`/api/advertisements/admin/${adId}`, {
        method: 'DELETE'
    });
}

/**
 * GET /api/users/admin/all-users
 * Returns: List<User>
 */
function getAllUsers() {
    return apiRequest('/api/users/admin/all-users');
}

/**
 * PATCH /api/users/admin/block/{userId}
 */
function blockUser(userId) {
    return apiRequest(`/api/users/admin/block/${userId}`, {
        method: 'PATCH'
    });
}

/**
 * PATCH /api/users/admin/unblock/{userId}
 */
function unblockUser(userId) {
    return apiRequest(`/api/users/admin/unblock/${userId}`, {
        method: 'PATCH'
    });
}
const getActiveAds = () => apiRequest('/api/advertisements/search');

// ------------------------------------------------------------------
// Expose everything to the global window object (vanilla JS, no modules)
// ------------------------------------------------------------------
const CATEGORIES = []; // placeholder — will be populated by getCategories()
window.API = {
    getCategories,
    getCategoryAttributes,
    login, register, logout, getCurrentUser,
    searchAds, getAdDetail, createAd, updateOwnAd, deleteOwnAd, markAdAsSold,
    getFavorites, addFavorite, removeFavorite,
    getConversations, getChatMessages, sendMessage,
    getUserReviews, submitReview,getMyAds,getActiveAds,
    getPendingAds, approveAd, rejectAd, deleteAdByAdmin,
    getAllUsers, blockUser, unblockUser,CATEGORIES
};