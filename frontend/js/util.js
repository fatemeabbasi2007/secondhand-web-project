function esc(value = '') {
    return String(value).replace(/[&<>'"]/g, ch => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[ch]));
}
function money(value) {
    return Number(value).toLocaleString('fa-IR') + ' تومان';
}
function initials(name = 'کاربر') {
    return name.split(' ').map(w => w[0]).join('').slice(0, 2);
}