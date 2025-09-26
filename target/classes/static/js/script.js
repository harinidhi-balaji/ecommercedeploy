// ECommerce Application JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize all components
    initializeComponents();

    // Auto-hide alerts after 5 seconds
    autoHideAlerts();

    // Initialize tooltips
    initializeTooltips();

    // Add loading states to forms
    initializeFormSubmissions();

    // Initialize product search
    initializeProductSearch();

    // Initialize cart functionality
    initializeCartFunctionality();
});

/**
 * Initialize all JavaScript components
 */
function initializeComponents() {
    console.log('ECommerce Application initialized');

    // Add fade-in animation to cards
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        setTimeout(() => {
            card.classList.add('fade-in');
        }, index * 100);
    });
}

/**
 * Auto-hide alert messages after 5 seconds
 */
function autoHideAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
}

/**
 * Initialize Bootstrap tooltips
 */
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Add loading states to form submissions
 */
function initializeFormSubmissions() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                const originalText = submitBtn.innerHTML;
                submitBtn.innerHTML = '<span class="spinner"></span> Processing...';
                submitBtn.disabled = true;

                // Re-enable button after 3 seconds in case of network issues
                setTimeout(() => {
                    submitBtn.innerHTML = originalText;
                    submitBtn.disabled = false;
                }, 3000);
            }
        });
    });
}

/**
 * Initialize product search functionality
 */
function initializeProductSearch() {
    const searchInput = document.getElementById('productSearch');
    if (searchInput) {
        let searchTimeout;

        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const searchTerm = this.value.trim();

            if (searchTerm.length >= 2) {
                searchTimeout = setTimeout(() => {
                    searchProducts(searchTerm);
                }, 500);
            } else if (searchTerm.length === 0) {
                // Clear search results
                window.location.href = '/products';
            }
        });
    }
}

/**
 * Search products with AJAX
 */
function searchProducts(searchTerm) {
    const url = `/products?search=${encodeURIComponent(searchTerm)}`;
    window.location.href = url;
}

/**
 * Initialize cart functionality
 */
function initializeCartFunctionality() {
    // Quantity increment/decrement buttons
    const quantityControls = document.querySelectorAll('.quantity-controls');
    quantityControls.forEach(control => {
        const decreaseBtn = control.querySelector('.btn-decrease');
        const increaseBtn = control.querySelector('.btn-increase');
        const quantityInput = control.querySelector('input[type="number"]');

        if (decreaseBtn && increaseBtn && quantityInput) {
            decreaseBtn.addEventListener('click', () => {
                let currentValue = parseInt(quantityInput.value);
                if (currentValue > 1) {
                    quantityInput.value = currentValue - 1;
                    updateCartItem(quantityInput);
                }
            });

            increaseBtn.addEventListener('click', () => {
                let currentValue = parseInt(quantityInput.value);
                const maxStock = parseInt(quantityInput.getAttribute('max'));
                if (!maxStock || currentValue < maxStock) {
                    quantityInput.value = currentValue + 1;
                    updateCartItem(quantityInput);
                }
            });
        }
    });

    // Add to cart buttons
    const addToCartForms = document.querySelectorAll('form[action*="/cart/add"]');
    addToCartForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const quantity = form.querySelector('input[name="quantity"]').value;
            const productName = form.closest('.card').querySelector('.card-title').textContent;

            // Show success message immediately
            showNotification(`Added ${quantity} x ${productName} to cart!`, 'success');
        });
    });
}

/**
 * Update cart item quantity
 */
function updateCartItem(quantityInput) {
    const form = quantityInput.closest('form');
    if (form) {
        // Add loading state
        quantityInput.disabled = true;

        // Submit form after a short delay
        setTimeout(() => {
            form.submit();
        }, 300);
    }
}

/**
 * Show notification message
 */
function showNotification(message, type = 'info') {
    const alertContainer = document.querySelector('.container');
    if (alertContainer) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        alertContainer.insertBefore(alertDiv, alertContainer.firstChild);

        // Auto-hide after 3 seconds
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alertDiv);
            bsAlert.close();
        }, 3000);
    }
}

/**
 * Format currency values
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

/**
 * Confirm deletion with custom message
 */
function confirmDelete(itemName) {
    return confirm(`Are you sure you want to delete "${itemName}"? This action cannot be undone.`);
}

/**
 * Image loading error handler
 */
function handleImageError(img) {
    img.style.display = 'none';
    const placeholder = img.parentElement.querySelector('.image-placeholder') ||
                       document.createElement('div');
    placeholder.className = 'image-placeholder';
    placeholder.innerHTML = '<i class="bi bi-image"></i>';
    img.parentElement.appendChild(placeholder);
}

/**
 * Validate form inputs
 */
function validateForm(form) {
    let isValid = true;
    const requiredInputs = form.querySelectorAll('input[required], select[required], textarea[required]');

    requiredInputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('is-invalid');
            isValid = false;
        } else {
            input.classList.remove('is-invalid');
        }
    });

    return isValid;
}

/**
 * Initialize data tables for admin pages
 */
function initializeDataTables() {
    const tables = document.querySelectorAll('.data-table');
    tables.forEach(table => {
        // Add sorting functionality
        const headers = table.querySelectorAll('th[data-sort]');
        headers.forEach(header => {
            header.style.cursor = 'pointer';
            header.addEventListener('click', () => {
                sortTable(table, header.dataset.sort);
            });
        });
    });
}

/**
 * Sort table by column
 */
function sortTable(table, column) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const isNumeric = table.querySelector(`th[data-sort="${column}"]`).dataset.type === 'number';

    rows.sort((a, b) => {
        const aValue = a.querySelector(`td:nth-child(${getColumnIndex(table, column)})`).textContent.trim();
        const bValue = b.querySelector(`td:nth-child(${getColumnIndex(table, column)})`).textContent.trim();

        if (isNumeric) {
            return parseFloat(aValue) - parseFloat(bValue);
        } else {
            return aValue.localeCompare(bValue);
        }
    });

    // Clear tbody and append sorted rows
    tbody.innerHTML = '';
    rows.forEach(row => tbody.appendChild(row));
}

/**
 * Get column index for sorting
 */
function getColumnIndex(table, column) {
    const headers = table.querySelectorAll('th');
    for (let i = 0; i < headers.length; i++) {
        if (headers[i].dataset.sort === column) {
            return i + 1;
        }
    }
    return 1;
}

/**
 * Initialize admin dashboard charts (if needed)
 */
function initializeDashboardCharts() {
    // This would be the place to initialize charts
    // Using libraries like Chart.js or D3.js
    console.log('Dashboard charts would be initialized here');
}

/**
 * Handle file upload preview
 */
function handleFileUpload(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('imagePreview');
            if (preview) {
                preview.src = e.target.result;
                preview.style.display = 'block';
            }
        };
        reader.readAsDataURL(input.files[0]);
    }
}

/**
 * Initialize lazy loading for images
 */
function initializeLazyLoading() {
    const images = document.querySelectorAll('img[data-src]');

    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.classList.remove('lazy');
                    imageObserver.unobserve(img);
                }
            });
        });

        images.forEach(img => imageObserver.observe(img));
    } else {
        // Fallback for browsers without IntersectionObserver
        images.forEach(img => {
            img.src = img.dataset.src;
        });
    }
}

/**
 * Utility function to debounce function calls
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}