document.addEventListener('DOMContentLoaded', () => {
    // State management
    let isUploaded = false;
    let historyData = [];

    // DOM Elements - Upload Section
    const dropZone = document.getElementById('drop-zone');
    const fileInput = document.getElementById('file-input');
    const dropZoneDefault = document.getElementById('drop-zone-default');
    const dropZoneActive = document.getElementById('drop-zone-active');
    const dropZoneSuccess = document.getElementById('drop-zone-success');
    const uploadProgressBar = document.getElementById('upload-progress-bar');
    const uploadStatusText = document.getElementById('upload-status-text');
    const uploadPercentage = document.getElementById('upload-percentage');
    const selectedFileName = document.getElementById('selected-file-name');
    const selectedFileSize = document.getElementById('selected-file-size');
    const successFileName = document.getElementById('success-file-name');
    const reuploadBtn = document.getElementById('reupload-btn');
    const uploadCardWrapper = document.getElementById('upload-card-wrapper');

    // DOM Elements - Ask Section
    const askCardWrapper = document.getElementById('ask-card-wrapper');
    const askForm = document.getElementById('ask-form');
    const questionInput = document.getElementById('question-input');
    const askBtn = document.getElementById('ask-btn');
    const loadingSpinner = document.getElementById('loading-spinner');
    const answerCard = document.getElementById('answer-card');
    const displayQuestion = document.getElementById('display-question');
    const displayAnswer = document.getElementById('display-answer');
    const copyBtn = document.getElementById('copy-btn');
    const copyText = document.getElementById('copy-text');
    const copyNormalIcon = copyBtn.querySelector('.copy-normal');
    const copySuccessIcon = copyBtn.querySelector('.copy-success');

    // DOM Elements - History Sidebar
    const toggleHistoryBtn = document.getElementById('toggle-history-btn');
    const closeHistoryBtn = document.getElementById('close-history-btn');
    const historySidebar = document.getElementById('history-sidebar');
    const sidebarOverlay = document.getElementById('sidebar-overlay');
    const historyList = document.getElementById('history-list');
    const historySearch = document.getElementById('history-search');
    const historyEmptyState = document.getElementById('history-empty-state');

    // DOM Elements - Toast Notification
    const toastContainer = document.getElementById('toast-container');

    // Load initial history
    loadHistory();

    // ----------------------------------------------------
    // Drag & Drop Functionality
    // ----------------------------------------------------

    // Prevent defaults for all drag events
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    // Add/remove drag highlight classes
    ['dragenter', 'dragover'].forEach(eventName => {
        dropZone.addEventListener(eventName, () => {
            if (!isUploaded) dropZone.classList.add('dragover');
        }, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, () => {
            dropZone.classList.remove('dragover');
        }, false);
    });

    // Handle dropped files
    dropZone.addEventListener('drop', (e) => {
        if (isUploaded) return;
        const dt = e.dataTransfer;
        const files = dt.files;
        if (files.length > 0) {
            handleFileSelect(files[0]);
        }
    });

    // Handle file input selection
    fileInput.addEventListener('change', (e) => {
        if (isUploaded) return;
        const files = e.target.files;
        if (files.length > 0) {
            handleFileSelect(files[0]);
        }
    });

    // Reset upload state (re-upload different file)
    reuploadBtn.addEventListener('click', (e) => {
        e.stopPropagation(); // Avoid triggering file selection
        resetUpload();
    });

    function handleFileSelect(file) {
        // Validate file type
        if (file.type !== 'application/pdf' && !file.name.endsWith('.pdf')) {
            showToast('Please select a valid PDF file.', 'error');
            return;
        }

        // Limit size to 25MB
        const maxSize = 25 * 1024 * 1024;
        if (file.size > maxSize) {
            showToast('File is too large. Max limit is 25MB.', 'error');
            return;
        }

        // Show upload details and progress UI
        selectedFileName.textContent = file.name;
        selectedFileSize.textContent = formatBytes(file.size);
        
        dropZoneDefault.classList.add('hidden');
        dropZoneActive.classList.remove('hidden');
        fileInput.style.pointerEvents = 'none'; // Disable clicking during upload

        // Start upload
        uploadFile(file);
    }

    function uploadFile(file) {
        const formData = new FormData();
        formData.append('file', file);

        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/upload', true);

        // Upload progress listener
        xhr.upload.addEventListener('progress', (e) => {
            if (e.lengthComputable) {
                const percent = Math.round((e.loaded / e.total) * 100);
                updateProgress(percent);
            }
        });

        // Response handling
        xhr.onload = function() {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseText = xhr.responseText;
                
                // Spring Boot Controller catch block returns a 200 OK even on exception,
                // check response text to confirm success.
                if (responseText.includes('Upload failed:')) {
                    handleUploadError(responseText);
                } else {
                    handleUploadSuccess(file.name);
                }
            } else {
                handleUploadError('Server returned status: ' + xhr.status);
            }
        };

        xhr.onerror = function() {
            handleUploadError('Network error or connection refused.');
        };

        xhr.send(formData);
    }

    function updateProgress(percent) {
        uploadProgressBar.style.width = `${percent}%`;
        uploadPercentage.textContent = `${percent}%`;
        if (percent === 100) {
            uploadStatusText.textContent = 'Processing PDF...';
        } else {
            uploadStatusText.textContent = 'Uploading...';
        }
    }

    function handleUploadSuccess(fileName) {
        isUploaded = true;
        successFileName.textContent = fileName;
        
        dropZoneActive.classList.add('hidden');
        dropZoneSuccess.classList.remove('hidden');
        
        // Enable Question Section
        askCardWrapper.classList.remove('disabled');
        questionInput.removeAttribute('disabled');
        askBtn.removeAttribute('disabled');
        questionInput.focus();
        
        showToast('PDF uploaded and indexed successfully!', 'success');
    }

    function handleUploadError(message) {
        showToast(message, 'error');
        resetUpload();
    }

    function resetUpload() {
        isUploaded = false;
        fileInput.value = '';
        fileInput.style.pointerEvents = 'auto';
        
        // Reset UI components
        dropZoneDefault.classList.remove('hidden');
        dropZoneActive.classList.add('hidden');
        dropZoneSuccess.classList.add('hidden');
        uploadProgressBar.style.width = '0%';
        uploadPercentage.textContent = '0%';
        uploadStatusText.textContent = 'Uploading...';
        
        // Disable Question Section
        askCardWrapper.classList.add('disabled');
        questionInput.setAttribute('disabled', 'true');
        askBtn.setAttribute('disabled', 'true');
        questionInput.value = '';
        
        // Hide answer elements
        answerCard.classList.add('hidden');
    }

    // ----------------------------------------------------
    // Q&A Functionality
    // ----------------------------------------------------

    askForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const question = questionInput.value.trim();
        if (!question) {
            showToast('Please type a question first.', 'error');
            return;
        }

        // Hide old answers and show loader
        answerCard.classList.add('hidden');
        loadingSpinner.classList.remove('hidden');
        
        // Disable controls while querying
        questionInput.setAttribute('disabled', 'true');
        askBtn.setAttribute('disabled', 'true');

        // Call backend API: GET /ask?question=your_question
        fetch(`/ask?question=${encodeURIComponent(question)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                loadingSpinner.classList.add('hidden');
                
                // Enable controls
                questionInput.removeAttribute('disabled');
                askBtn.removeAttribute('disabled');
                
                // Show Answer
                displayQuestion.textContent = `Q: ${data.question}`;
                displayAnswer.textContent = data.answer;
                answerCard.classList.remove('hidden');
                answerCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
                
                // Refresh History list
                loadHistory();
            })
            .catch(error => {
                loadingSpinner.classList.add('hidden');
                questionInput.removeAttribute('disabled');
                askBtn.removeAttribute('disabled');
                showToast('Failed to get an answer: ' + error.message, 'error');
            });
    });

    // Copy to clipboard handler
    copyBtn.addEventListener('click', () => {
        const textToCopy = displayAnswer.textContent;
        if (!textToCopy) return;

        navigator.clipboard.writeText(textToCopy)
            .then(() => {
                // Show success state on copy button
                copyNormalIcon.classList.add('hidden');
                copySuccessIcon.classList.remove('hidden');
                copyText.textContent = 'Copied!';
                copyBtn.classList.add('btn-primary');
                copyBtn.classList.remove('btn-secondary');
                
                showToast('Answer copied to clipboard!', 'info');

                // Reset copy button icon after 2 seconds
                setTimeout(() => {
                    copyNormalIcon.classList.remove('hidden');
                    copySuccessIcon.classList.add('hidden');
                    copyText.textContent = 'Copy';
                    copyBtn.classList.remove('btn-primary');
                    copyBtn.classList.add('btn-secondary');
                }, 2000);
            })
            .catch(err => {
                showToast('Unable to copy answer: ' + err, 'error');
            });
    });

    // ----------------------------------------------------
    // History Panel Functionality
    // ----------------------------------------------------

    // Toggle history sidebar open/close
    toggleHistoryBtn.addEventListener('click', () => {
        historySidebar.classList.add('active');
        sidebarOverlay.classList.add('active');
        loadHistory();
    });

    closeHistoryBtn.addEventListener('click', closeSidebar);
    sidebarOverlay.addEventListener('click', closeSidebar);

    function closeSidebar() {
        historySidebar.classList.remove('active');
        sidebarOverlay.classList.remove('active');
    }

    function loadHistory() {
        fetch('/history')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Could not fetch question history.');
                }
                return response.json();
            })
            .then(data => {
                // Sort history in reverse order (newest at the top)
                historyData = data.sort((a, b) => b.id - a.id);
                renderHistory(historyData);
            })
            .catch(error => {
                console.error('History fetch error:', error);
            });
    }

    function renderHistory(items) {
        historyList.innerHTML = '';
        
        if (items.length === 0) {
            historyEmptyState.classList.remove('hidden');
            historyList.classList.add('hidden');
            return;
        }

        historyEmptyState.classList.add('hidden');
        historyList.classList.remove('hidden');

        items.forEach(item => {
            const li = document.createElement('li');
            li.className = 'history-item';
            
            const dateFormatted = item.askedAt ? formatTime(item.askedAt) : 'Recent';
            
            li.innerHTML = `
                <div class="history-item-question">${escapeHtml(item.question)}</div>
                <div class="history-item-answer">${escapeHtml(item.answer)}</div>
                <div class="history-item-meta">
                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:12px;height:12px;">
                        <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                    </svg>
                    <span>${dateFormatted}</span>
                </div>
            `;
            
            // Re-view past Q&A when clicking a history card
            li.addEventListener('click', () => {
                displayQuestion.textContent = `Q: ${item.question}`;
                displayAnswer.textContent = item.answer;
                
                // Activate/Enable elements to display the card nicely
                askCardWrapper.classList.remove('disabled');
                answerCard.classList.remove('hidden');
                
                // Scroll main workspace answer into view
                answerCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
                
                // If on mobile/tablet, close the drawer so user sees the answer immediately
                if (window.innerWidth <= 992) {
                    closeSidebar();
                }
                
                showToast('Viewing archived Q&A', 'info');
            });

            historyList.appendChild(li);
        });
    }

    // Search filter for history items
    historySearch.addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase().trim();
        if (!query) {
            renderHistory(historyData);
            return;
        }

        const filtered = historyData.filter(item => 
            item.question.toLowerCase().includes(query) || 
            item.answer.toLowerCase().includes(query)
        );
        
        renderHistory(filtered);
    });

    // ----------------------------------------------------
    // Utility Helpers
    // ----------------------------------------------------

    // Show toast message
    function showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        
        let iconSvg = '';
        if (type === 'success') {
            iconSvg = `<svg class="toast-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 11-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>`;
        } else if (type === 'error') {
            iconSvg = `<svg class="toast-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
            </svg>`;
        } else {
            iconSvg = `<svg class="toast-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>
            </svg>`;
        }

        toast.innerHTML = `
            <div class="toast-content">
                ${iconSvg}
                <span class="toast-msg">${escapeHtml(message)}</span>
            </div>
            <button class="toast-close" aria-label="Close toast">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
            </button>
        `;

        toastContainer.appendChild(toast);

        // Bind close button event
        toast.querySelector('.toast-close').addEventListener('click', () => {
            removeToast(toast);
        });

        // Auto expire after 4 seconds
        setTimeout(() => {
            removeToast(toast);
        }, 4000);
    }

    function removeToast(toast) {
        toast.classList.add('fade-out');
        // Wait for animation
        toast.addEventListener('transitionend', () => {
            toast.remove();
        });
    }

    // Helper: format bytes into reader-friendly sizes
    function formatBytes(bytes, decimals = 2) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const dm = decimals < 0 ? 0 : decimals;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
    }

    // Helper: format ISO dates nicely
    function formatTime(isoString) {
        try {
            const date = new Date(isoString);
            return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' ' + date.toLocaleDateString([], { month: 'short', day: 'numeric' });
        } catch (e) {
            return 'Recent';
        }
    }

    // Helper: escape HTML tags
    function escapeHtml(unsafe) {
        return unsafe
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
});
