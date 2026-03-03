document.addEventListener('DOMContentLoaded', () => {
    // Hashtag highlighting in composer
    const composer = document.querySelector('.post-composer textarea');
    if (composer) {
        composer.addEventListener('input', (e) => {
            // In a real app, we'd use a contenteditable div to style hashtags in real-time.
            // For this demo, we'll log detection.
            const hashtags = e.target.value.match(/#[a-z0-9]+/gi);
            if (hashtags) {
                console.log('Detected hashtags:', hashtags);
            }
        });
    }

    // Toast Notifications
    window.showToast = (message, type = 'success') => {
        const toast = document.createElement('div');
        toast.className = `glass animate-fade-in`;
        toast.style = `position: fixed; bottom: 2rem; right: 2rem; padding: 1rem 2rem; z-index: 9999; border-left: 4px solid ${type === 'success' ? '#10b981' : '#ef4444'}; background: rgba(15,23,42,0.9);`;
        toast.innerHTML = `<i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i> ${message}`;
        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 3000);
    };

    // Auto-search dropdown (mock)
    const searchInput = document.querySelector('.search-bar input');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            if (e.target.value.length > 2) {
                console.log('Searching for:', e.target.value);
            }
        });
    }
});
