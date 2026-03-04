/**
 * Green Diamond Hotel - AI Chatbot Widget
 */
document.addEventListener('DOMContentLoaded', () => {
    const toggleBtn = document.getElementById('chatbot-toggle');
    const panel = document.getElementById('chatbot-panel');
    const messagesEl = document.getElementById('chatbot-messages');
    const inputEl = document.getElementById('chatbot-input');
    const sendBtn = document.getElementById('chatbot-send');
    const suggestionsEl = document.getElementById('chatbot-suggestions');
    const clearBtn = document.getElementById('chatbot-clear');

    let isOpen = false;
    let isLoading = false;
    let initialized = false;

    // ======================== TOGGLE ========================
    toggleBtn.addEventListener('click', () => {
        isOpen = !isOpen;
        toggleBtn.classList.toggle('open', isOpen);
        panel.classList.toggle('visible', isOpen);

        if (isOpen && !initialized) {
            initialize();
            initialized = true;
        }

        if (isOpen) {
            setTimeout(() => inputEl.focus(), 350);
        }
    });

    // ======================== INIT ========================
    function initialize() {
        // Welcome message
        appendMessage('bot', 'Xin chào! 👋 Tôi là trợ lý AI của Green Diamond Hotel. Tôi có thể giúp bạn tìm phòng phù hợp, tư vấn giá cả, và trả lời các thắc mắc. Hãy hỏi tôi bất cứ điều gì! 😊');

        // Load suggestions
        loadSuggestions();
    }

    // ======================== SEND MESSAGE ========================
    async function sendMessage(text) {
        if (!text || text.trim() === '' || isLoading) return;

        const userText = text.trim();
        inputEl.value = '';

        // Hide suggestions after first message
        if (suggestionsEl) {
            suggestionsEl.style.display = 'none';
        }

        // Append user message
        appendMessage('user', userText);

        // Show typing indicator
        const typingEl = showTyping();
        isLoading = true;
        sendBtn.disabled = true;

        try {
            const response = await fetch('/api/chatbot', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: userText })
            });

            if (!response.ok) {
                throw new Error('Server error: ' + response.status);
            }

            const data = await response.json();
            removeTyping(typingEl);
            appendMessage('bot', data.reply);

        } catch (error) {
            console.error('Chatbot error:', error);
            removeTyping(typingEl);
            appendMessage('bot', 'Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau. 🙏');
        } finally {
            isLoading = false;
            sendBtn.disabled = false;
            inputEl.focus();
        }
    }

    // Send on click
    sendBtn.addEventListener('click', () => sendMessage(inputEl.value));

    // Send on Enter
    inputEl.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage(inputEl.value);
        }
    });

    // ======================== SUGGESTIONS ========================
    async function loadSuggestions() {
        try {
            const response = await fetch('/api/chatbot/suggestions');
            const suggestions = await response.json();

            if (suggestionsEl && suggestions.length > 0) {
                suggestionsEl.innerHTML = '';
                suggestions.forEach(text => {
                    const btn = document.createElement('button');
                    btn.textContent = text;
                    btn.addEventListener('click', () => sendMessage(text));
                    suggestionsEl.appendChild(btn);
                });
            }
        } catch (error) {
            console.error('Error loading suggestions:', error);
        }
    }

    // ======================== CLEAR HISTORY ========================
    if (clearBtn) {
        clearBtn.addEventListener('click', async () => {
            try {
                await fetch('/api/chatbot/history', { method: 'DELETE' });
            } catch (e) { /* ignore */ }

            // Reset UI
            messagesEl.innerHTML = '';
            if (suggestionsEl) suggestionsEl.style.display = 'flex';
            initialized = false;
            initialize();
            initialized = true;
        });
    }

    // ======================== HELPERS ========================
    function appendMessage(role, text) {
        const msgDiv = document.createElement('div');
        msgDiv.className = `chat-msg ${role}`;

        const avatarDiv = document.createElement('div');
        avatarDiv.className = 'chat-msg-avatar';

        const avatarIcon = document.createElement('span');
        avatarIcon.className = 'material-symbols-outlined';
        avatarIcon.style.fontSize = '18px';
        avatarIcon.textContent = role === 'bot' ? 'smart_toy' : 'person';
        avatarDiv.appendChild(avatarIcon);

        const bubbleDiv = document.createElement('div');
        bubbleDiv.className = 'chat-msg-bubble';
        bubbleDiv.innerHTML = formatMessage(text);

        msgDiv.appendChild(avatarDiv);
        msgDiv.appendChild(bubbleDiv);
        messagesEl.appendChild(msgDiv);

        scrollToBottom();
    }

    function showTyping() {
        const msgDiv = document.createElement('div');
        msgDiv.className = 'chat-msg bot typing-msg';

        const avatarDiv = document.createElement('div');
        avatarDiv.className = 'chat-msg-avatar';
        const icon = document.createElement('span');
        icon.className = 'material-symbols-outlined';
        icon.style.fontSize = '18px';
        icon.textContent = 'smart_toy';
        avatarDiv.appendChild(icon);

        const bubbleDiv = document.createElement('div');
        bubbleDiv.className = 'chat-msg-bubble';

        const typingDots = document.createElement('div');
        typingDots.className = 'typing-indicator';
        typingDots.innerHTML = '<span></span><span></span><span></span>';
        bubbleDiv.appendChild(typingDots);

        msgDiv.appendChild(avatarDiv);
        msgDiv.appendChild(bubbleDiv);
        messagesEl.appendChild(msgDiv);

        scrollToBottom();
        return msgDiv;
    }

    function removeTyping(el) {
        if (el && el.parentNode) {
            el.parentNode.removeChild(el);
        }
    }

    function scrollToBottom() {
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    /**
     * Format tin nhắn: hỗ trợ **bold**, xuống dòng, emoji
     */
    function formatMessage(text) {
        if (!text) return '';

        // Escape HTML
        let formatted = text
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');

        // Bold: **text**
        formatted = formatted.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');

        // Italic: *text*
        formatted = formatted.replace(/\*(.+?)\*/g, '<em>$1</em>');

        // Line breaks
        formatted = formatted.replace(/\n/g, '<br>');

        return formatted;
    }
});

